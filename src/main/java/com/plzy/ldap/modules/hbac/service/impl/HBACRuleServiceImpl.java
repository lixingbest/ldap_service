package com.plzy.ldap.modules.hbac.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzy.ldap.framework.ldap.protocol.LDAPResponse;
import com.plzy.ldap.framework.ldap.service.LDAPRemoteService;
import com.plzy.ldap.modules.domainuser.service.DomainUserService;
import com.plzy.ldap.modules.hbac.dto.HBACRuleDTO;
import com.plzy.ldap.modules.hbac.service.HBACRuleService;
import com.plzy.ldap.modules.host.service.HostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class HBACRuleServiceImpl implements HBACRuleService {

    @Autowired
    private LDAPRemoteService rpcService;

    @Autowired
    private HostService hostService;

    @Autowired
    private DomainUserService domainUserService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List list(Long domainId) {

        LDAPResponse resp = rpcService.request(domainId,"{" +
                "    \"method\": \"hbacrule_find\"," +
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
                    "                \"method\": \"hbacrule_show\"," +
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
            result.add(i.get("result"));
        }

        // add domain_id in result
        for(Object rec : result){
            Map map = (Map) rec;
            map.put("domain_id", domainId);
        }

        return result;
    }

    @Override
    public List listUser(Long domainId, String hbacCN) {

        LDAPResponse response = rpcService.request(domainId,"{\"method\":\"hbacrule_show\",\"params\":[[\""+hbacCN+"\"],{\"all\":true,\"rights\":true,\"version\":\"2.237\"}]}");
        List<String> userList = (List)((Map)response.getResult().get("result")).get("memberuser_user");
        if(userList != null && userList.size() > 0) {
            StringBuilder in = new StringBuilder();
            for (String userCN : userList) {

                in.append("{" +
                        "                \"method\": \"user_show\"," +
                        "                \"params\": [" +
                        "                    [" +
                        "                        \"").append(userCN).append("\"" +
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
    public List listHost(Long domainId, String hbacCN) {

        LDAPResponse response = rpcService.request(domainId,"{\"method\":\"hbacrule_show\",\"params\":[[\""+hbacCN+"\"],{\"all\":true,\"rights\":true,\"version\":\"2.237\"}]}");
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
    public void update(Long domainId,HBACRuleDTO hbacRuleDTO) {
        rpcService.request(domainId,"{\"method\":\"hbacrule_mod\",\"params\":[[\""+hbacRuleDTO.getCn()+"\"],{\"all\":true,\"rights\":true,\"description\":\""+hbacRuleDTO.getDescription()+"\",\"version\":\"2.237\"}]}");
    }

    @Override
    public void save(Long domainId,HBACRuleDTO hbacRuleDTO) {

        try{
            rpcService.request(domainId,"{" +
                    "    \"method\": \"hbacrule_add\"," +
                    "    \"params\": [" +
                    "        []," +
                    objectMapper.writeValueAsString(hbacRuleDTO) +
                    "    ]" +
                    "}");
            // settings allow all rights
            rpcService.request(domainId,"{\"method\":\"hbacrule_mod\",\"params\":[[\""+hbacRuleDTO.getCn()+"\"],{\"all\":true,\"rights\":true,\"servicecategory\":\"all\",\"version\":\"2.237\"}]}");
        }catch (Exception e){
            log.error("保存hbac rule时出现错误：", e);
        }
    }

    @Override
    public void remove(Long domainId, String hbacCN) {

        rpcService.request(domainId,"{\"method\":\"batch\",\"params\":[[{\"method\":\"hbacrule_del\",\"params\":[[\""+hbacCN+"\"],{}]}],{\"version\":\"2.237\"}]}");
    }

    @Override
    public void disable(Long domainId, String rule){
        rpcService.request(domainId,"{\"method\":\"batch\",\"params\":[[{\"method\":\"hbacrule_disable\",\"params\":[[\""+rule+"\"],{}]}],{\"version\":\"2.237\"}]}");
    }

    @Override
    public void enable(Long domainId,String rule) {
        rpcService.request(domainId,"{\"method\":\"batch\",\"params\":[[{\"method\":\"hbacrule_enable\",\"params\":[[\""+rule+"\"],{}]}],{\"version\":\"2.237\"}]}");
    }

    @Override
    public void addUser(Long domainId, String hbacruleCN, List<String> userList) {

        String list = "";
        for(String user : userList){
            list += "\""+user+"\",";
        }
        list = list.substring(0, list.length() - 1);

        rpcService.request(domainId,"{\"method\":\"hbacrule_add_user\",\"params\":[[\""+hbacruleCN+"\"],{\"user\":["+list+"],\"version\":\"2.237\"}]}");
    }

    @Override
    public void addUserGroup(Long domainId,String hbacruleCN, List<String> userGroupList) {

        String list = "";
        for(String group : userGroupList){
            list += "\""+group+"\",";
        }
        list = list.substring(0, list.length() - 1);

        rpcService.request(domainId,"{\"method\":\"hbacrule_add_user\",\"params\":[[\""+hbacruleCN+"\"],{\"group\":["+list+"],\"version\":\"2.237\"}]}");
    }

    @Override
    public void addHost(Long domainId,String hbacruleCN, List<String> hostList) {

        String list = "";
        for(String group : hostList){
            list += "\""+group+"\",";
        }
        list = list.substring(0, list.length() - 1);

        rpcService.request(domainId,"{\"method\":\"hbacrule_add_host\",\"params\":[[\""+hbacruleCN+"\"],{\"host\":["+list+"],\"version\":\"2.237\"}]}");
    }

    @Override
    public void removeUser(Long domainId, String hbacCN, String uid) {

        rpcService.request(domainId,"{\"method\":\"hbacrule_remove_user\",\"params\":[[\""+hbacCN+"\"],{\"user\":[\""+uid+"\"],\"version\":\"2.237\"}]}");
    }

    @Override
    public void removeHost(Long domainId, String hbacCN, String host) {

        rpcService.request(domainId,"{\"method\":\"hbacrule_remove_host\",\"params\":[[\""+hbacCN+"\"],{\"host\":[\""+host+"\"],\"version\":\"2.237\"}]}");
    }

    @Override
    public List listAllHost(Long domainId,String hostname) {

        return hostService.list(domainId,hostname);
    }

    @Override
    public List listAllUsers(Long domainId,String uid) {

        return domainUserService.listAll(domainId);
    }
}
