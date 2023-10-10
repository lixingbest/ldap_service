package com.plzy.ldap.modules.sudo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzy.ldap.framework.ldap.protocol.LDAPResponse;
import com.plzy.ldap.framework.ldap.service.LDAPRemoteService;
import com.plzy.ldap.modules.sudo.dto.SudoRefDTO;
import com.plzy.ldap.modules.sudo.dto.SudoRuleDTO;
import com.plzy.ldap.modules.sudo.service.SudoRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SudoRuleServiceImpl implements SudoRuleService {

    @Autowired
    private LDAPRemoteService rpcService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List list(Long domainId) {

        LDAPResponse resp = rpcService.request(domainId,"{" +
                "    \"method\": \"sudorule_find\"," +
                "    \"params\": [" +
                "        [" +
                "            \"\"" +
                "        ]," +
                "        {" +
                "            \"pkey_only\": true," +
                "            \"sizelimit\": 0," +
                "            \"version\": \"2.237\"" +
                "        }" +
                "    ]" +
                "}");
        List list = (List) resp.getResult().get("result");
        StringBuilder in = new StringBuilder();
        for (Object item : list) {
            Map it = (Map) item;
            String cn = ((List) it.get("cn")).get(0) + "";

            in.append("{" +
                    "                \"method\": \"sudorule_show\"," +
                    "                \"params\": [" +
                    "                    [" +
                    "                        \"").append(cn).append("\"" +
                    "                    ]," +
                    "                    {" +
                    "                        \"no_members\": true" +
                    "                    }" +
                    "                ]" +
                    "            },");
        }
        if(in.length() > 1) {
            in = in.deleteCharAt(in.length() - 1);
        }

        String cmd2 = "";
        LDAPResponse resp2 = rpcService.request(domainId,"{" +
                "    \"method\": \"batch\"," +
                "    \"params\": [" +
                "        [" +
                "            " + in.toString() +
                "        ]," +
                "        {" +
                "            \"version\": \"2.237\"" +
                "        }" +
                "    ]" +
                "}");

        List list2 = (List) resp2.getResult().get("results");
        List result = new ArrayList();
        for(Object item : list2){
            Map i = (Map)item;
            Map record = (Map)i.get("result");
            // 额外添加domainId字段，因为前端需要区分此规则的所属域
            record.put("domainId", domainId);
            result.add(record);
        }

        return result;
    }

    @Override
    public void save(Long domainId,SudoRuleDTO rule) {

        try{
            rpcService.request(domainId,"{" +
                    "    \"method\": \"sudorule_add\"," +
                    "    \"params\": [" +
                    "        []," +
                    objectMapper.writeValueAsString(rule) +
                    "    ]" +
                    "}");
        }catch (Exception e){
            log.error("保存sudorule时出现错误：", e);
        }
    }

    @Override
    public void delete(Long domainId,String cn) {
        rpcService.request(domainId,"{" +
                "    \"method\": \"batch\"," +
                "    \"params\": [" +
                "        [" +
                "            {" +
                "                \"method\": \"sudorule_del\"," +
                "                \"params\": [[\"" +
                cn + "\"]," +
                "                    {}" +
                "                ]" +
                "            }" +
                "        ]," +
                "        {" +
                "            \"version\": \"2.237\"" +
                "        }" +
                "    ]" +
                "}");
    }

    @Override
    public void addUserGroup(Long domainId,SudoRefDTO ref) {

        String list = "";
        for(String group : ref.getGroupList().split(",")){
            list += "\""+group+"\",";
        }
        list = list.substring(0, list.length() - 1);
        rpcService.request(domainId,"{\"method\":\"sudorule_add_user\",\"params\":[[\""+ref.getRuleCN()+"\"],{\"group\":["+list+"],\"version\":\"2.237\"}]}");
    }

    @Override
    public void addHost(Long domainId,SudoRefDTO ref) {

        String list = "";
        for(String group : ref.getGroupList().split(",")){
            list += "\""+group+"\",";
        }
        list = list.substring(0, list.length() - 1);
        rpcService.request(domainId,"{\"method\":\"sudorule_add_host\",\"params\":[[\""+ref.getRuleCN()+"\"],{\"host\":["+list+"],\"version\":\"2.237\"}]}");
    }

    @Override
    public void addAllowCmdGroup(Long domainId,SudoRefDTO ref) {

        String list = "";
        for(String group : ref.getGroupList().split(",")){
            list += "\""+group+"\",";
        }
        list = list.substring(0, list.length() - 1);
        rpcService.request(domainId,"{\"method\":\"sudorule_add_allow_command\",\"params\":[[\""+ref.getRuleCN()+"\"],{\"sudocmdgroup\":["+list+"],\"version\":\"2.237\"}]}");
    }

    @Override
    public void addDenyCommand(Long domainId,SudoRefDTO ref) {

        String list = "";
        for(String group : ref.getGroupList().split(",")){
            list += "\""+group+"\",";
        }
        list = list.substring(0, list.length() - 1);
        rpcService.request(domainId,"{\"method\":\"sudorule_add_deny_command\",\"params\":[[\""+ref.getRuleCN()+"\"],{\"sudocmdgroup\":["+list+"],\"version\":\"2.237\"}]}");
    }

    @Override
    public List listUserGroup(Long domainId,String cn) {

        LDAPResponse response = rpcService.request(domainId,"{\"method\":\"sudorule_show\",\"params\":[[\""+cn+"\"],{\"all\":true,\"rights\":true,\"version\":\"2.237\"}]}");
        List<String> userGroupCNList = (List)((Map)response.getResult().get("result")).get("memberuser_group");
        if(userGroupCNList != null && userGroupCNList.size() > 0) {
            StringBuilder in = new StringBuilder();
            for (String groupCN : userGroupCNList) {

                in.append("{" +
                        "                \"method\": \"group_show\"," +
                        "                \"params\": [" +
                        "                    [" +
                        "                        \"").append(groupCN).append("\"" +
                        "                    ]," +
                        "                    {" +
                        "                        \"no_members\": true," +
                        "                        \"all\": true" +
                        "                    }" +
                        "                ]" +
                        "            },");
            }
            if (in.length() > 1) {
                in = in.deleteCharAt(in.length() - 1);
            }

            String cmd2 = "";
            LDAPResponse resp2 = rpcService.request(domainId,"{" +
                    "    \"method\": \"batch\"," +
                    "    \"params\": [" +
                    "        [" +
                    "            " + in.toString() +
                    "        ]," +
                    "        {" +
                    "            \"version\": \"2.237\"" +
                    "        }" +
                    "    ]" +
                    "}");
            List list2 = (List) resp2.getResult().get("results");

            return list2;
        }else{
            return new ArrayList();
        }
    }

    @Override
    public List listAllUserGroup(Long domainId) {

        LDAPResponse resp = rpcService.request(domainId,"{" +
                "    \"method\": \"group_find\"," +
                "    \"params\": [" +
                "        [" +
                "            \"\"" +
                "        ]," +
                "        {" +
                "            \"pkey_only\": true," +
                "            \"sizelimit\": 0," +
                "            \"version\": \"2.237\"" +
                "        }" +
                "    ]" +
                "}");
        List list = (List) resp.getResult().get("result");
        StringBuilder in = new StringBuilder();
        for (Object item : list) {
            Map it = (Map) item;
            String cn = ((List) it.get("cn")).get(0) + "";

            in.append("{" +
                    "                \"method\": \"group_show\"," +
                    "                \"params\": [" +
                    "                    [" +
                    "                        \"").append(cn).append("\"" +
                    "                    ]," +
                    "                    {" +
                    "                        \"no_members\": true," +
                    "                        \"all\": true" +
                    "                    }" +
                    "                ]" +
                    "            },");
        }
        if(in.length() > 1) {
            in = in.deleteCharAt(in.length() - 1);
        }

        String cmd2 = "";
        LDAPResponse resp2 = rpcService.request(domainId,"{" +
                "    \"method\": \"batch\"," +
                "    \"params\": [" +
                "        [" +
                "            " + in.toString() +
                "        ]," +
                "        {" +
                "            \"version\": \"2.237\"" +
                "        }" +
                "    ]" +
                "}");

        List list2 = (List) resp2.getResult().get("results");
        List result = new ArrayList();
        for(Object item : list2){
            Map i = (Map)item;
            // 不展示系统内置用户组，防止被删除
            if(!(((List)(((Map)i.get("result")).get("cn"))).get(0) + "").matches("admins|editors|ipausers|trust admins")){
                result.add(i.get("result"));
            }
        }

        return result;
    }

    @Override
    public List listHost(Long domainId,String cn) {

        LDAPResponse response = rpcService.request(domainId,"{\"method\":\"sudorule_show\",\"params\":[[\""+cn+"\"],{\"all\":true,\"rights\":true,\"version\":\"2.237\"}]}");
        List<String> userGroupCNList = (List)((Map)response.getResult().get("result")).get("memberhost_host");
        if(userGroupCNList != null && userGroupCNList.size() > 0) {
            StringBuilder in = new StringBuilder();
            for (String groupCN : userGroupCNList) {

                in.append("{" +
                        "                \"method\": \"host_show\"," +
                        "                \"params\": [" +
                        "                    [" +
                        "                        \"").append(groupCN).append("\"" +
                        "                    ]," +
                        "                    {" +
                        "                        \"no_members\": true," +
                        "                        \"all\": true" +
                        "                    }" +
                        "                ]" +
                        "            },");
            }
            if (in.length() > 1) {
                in = in.deleteCharAt(in.length() - 1);
            }

            String cmd2 = "";
            LDAPResponse resp2 = rpcService.request(domainId,"{" +
                    "    \"method\": \"batch\"," +
                    "    \"params\": [" +
                    "        [" +
                    "            " + in.toString() +
                    "        ]," +
                    "        {" +
                    "            \"version\": \"2.237\"" +
                    "        }" +
                    "    ]" +
                    "}");
            List list2 = (List) resp2.getResult().get("results");

            return list2;
        }else{
            return new ArrayList();
        }
    }

    @Override
    public List listAllowCmdGroup(Long domainId,String cn) {

        LDAPResponse response = rpcService.request(domainId,"{\"method\":\"sudorule_show\",\"params\":[[\""+cn+"\"],{\"all\":true,\"rights\":true,\"version\":\"2.237\"}]}");
        List<String> userGroupCNList = (List)((Map)response.getResult().get("result")).get("memberallowcmd_sudocmdgroup");
        if(userGroupCNList != null && userGroupCNList.size() > 0) {
            StringBuilder in = new StringBuilder();
            for (String groupCN : userGroupCNList) {

                in.append("{" +
                        "                \"method\": \"sudocmdgroup_show\"," +
                        "                \"params\": [" +
                        "                    [" +
                        "                        \"").append(groupCN).append("\"" +
                        "                    ]," +
                        "                    {" +
                        "                        \"no_members\": true," +
                        "                        \"all\": true" +
                        "                    }" +
                        "                ]" +
                        "            },");
            }
            if (in.length() > 1) {
                in = in.deleteCharAt(in.length() - 1);
            }

            String cmd2 = "";
            LDAPResponse resp2 = rpcService.request(domainId,"{" +
                    "    \"method\": \"batch\"," +
                    "    \"params\": [" +
                    "        [" +
                    "            " + in.toString() +
                    "        ]," +
                    "        {" +
                    "            \"version\": \"2.237\"" +
                    "        }" +
                    "    ]" +
                    "}");
            List list2 = (List) resp2.getResult().get("results");

            return list2;
        }else{
            return new ArrayList();
        }
    }

    @Override
    public List listDenyCmdGroup(Long domainId,String cn) {

        LDAPResponse response = rpcService.request(domainId,"{\"method\":\"sudorule_show\",\"params\":[[\""+cn+"\"],{\"all\":true,\"rights\":true,\"version\":\"2.237\"}]}");
        List<String> userGroupCNList = (List)((Map)response.getResult().get("result")).get("memberdenycmd_sudocmdgroup");
        if(userGroupCNList != null && userGroupCNList.size() > 0) {
            StringBuilder in = new StringBuilder();
            for (String groupCN : userGroupCNList) {

                in.append("{" +
                        "                \"method\": \"sudocmdgroup_show\"," +
                        "                \"params\": [" +
                        "                    [" +
                        "                        \"").append(groupCN).append("\"" +
                        "                    ]," +
                        "                    {" +
                        "                        \"no_members\": true," +
                        "                        \"all\": true" +
                        "                    }" +
                        "                ]" +
                        "            },");
            }
            if (in.length() > 1) {
                in = in.deleteCharAt(in.length() - 1);
            }

            String cmd2 = "";
            LDAPResponse resp2 = rpcService.request(domainId,"{" +
                    "    \"method\": \"batch\"," +
                    "    \"params\": [" +
                    "        [" +
                    "            " + in.toString() +
                    "        ]," +
                    "        {" +
                    "            \"version\": \"2.237\"" +
                    "        }" +
                    "    ]" +
                    "}");
            List list2 = (List) resp2.getResult().get("results");

            return list2;
        }else{
            return new ArrayList();
        }
    }

    @Override
    public void removeUserGroup(Long domainId,SudoRefDTO ref) {

        String list = "[";
        for (String i : ref.getGroupList().split(",")){
            list += "\"" + i + "\",";
        }
        if(list.length() > 1){
            list = list.substring(0, list.length() - 1);
        }
        list += "]";

        rpcService.request(domainId,"{\"method\":\"sudorule_remove_user\",\"params\":[[\""+ref.getRuleCN()+"\"],{\"group\":"+list+",\"version\":\"2.237\"}]}");
    }

    @Override
    public void removeHost(Long domainId,SudoRefDTO ref) {

        String list = "[";
        for (String i : ref.getGroupList().split(",")){
            list += "\"" + i + "\",";
        }
        if(list.length() > 1){
            list = list.substring(0, list.length() - 1);
        }
        list += "]";

        rpcService.request(domainId,"{\"method\":\"sudorule_remove_host\",\"params\":[[\""+ref.getRuleCN()+"\"],{\"host\":"+list+",\"version\":\"2.237\"}]}");
    }

    @Override
    public void removeAllowCommand(Long domainId,SudoRefDTO ref) {

        String list = "[";
        for (String i : ref.getGroupList().split(",")){
            list += "\"" + i + "\",";
        }
        if(list.length() > 1){
            list = list.substring(0, list.length() - 1);
        }
        list += "]";

        rpcService.request(domainId,"{\"method\":\"sudorule_remove_allow_command\",\"params\":[[\""+ref.getRuleCN()+"\"],{\"sudocmdgroup\":"+list+",\"version\":\"2.237\"}]}");
    }

    @Override
    public void removeDenyCommand(Long domainId,SudoRefDTO ref) {

        String list = "[";
        for (String i : ref.getGroupList().split(",")){
            list += "\"" + i + "\",";
        }
        if(list.length() > 1){
            list = list.substring(0, list.length() - 1);
        }
        list += "]";

        rpcService.request(domainId,"{\"method\":\"sudorule_remove_deny_command\",\"params\":[[\""+ref.getRuleCN()+"\"],{\"sudocmdgroup\":"+list+",\"version\":\"2.237\"}]}");
    }
}
