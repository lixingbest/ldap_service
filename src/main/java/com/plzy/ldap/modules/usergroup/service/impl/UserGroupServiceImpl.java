package com.plzy.ldap.modules.usergroup.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzy.ldap.framework.ldap.protocol.LDAPResponse;
import com.plzy.ldap.framework.ldap.service.LDAPRemoteService;
import com.plzy.ldap.modules.usergroup.dto.UserGroupDTO;
import com.plzy.ldap.modules.usergroup.dto.UserGroupRefDTO;
import com.plzy.ldap.modules.usergroup.dto.UserGroupWithOUDTO;
import com.plzy.ldap.modules.usergroup.service.UserGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserGroupServiceImpl implements UserGroupService {

    @Autowired
    private LDAPRemoteService rpcService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List list(Long domainId, String ouCN, String groupCN, String uid) {

        LDAPResponse resp = rpcService.request(domainId, "{" +
                "    \"method\": \"group_find\"," +
                "    \"params\": [" +
                "        [" +
                "            \"" + ouCN + "\"" +
                "        ]," +
                "        {" +
                "            \"cn\": \"" + (StringUtils.hasText(groupCN) ? groupCN : "") + "\"," +
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
        if (in.length() > 1) {
            in = in.deleteCharAt(in.length() - 1);
        }

        String cmd2 = "";
        LDAPResponse resp2 = rpcService.request(domainId, "{" +
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
        for (Object item : list2) {
            Map i = (Map) item;
            // 不展示系统内置用户组，防止被删除
            if (!(((List) (((Map) i.get("result")).get("cn"))).get(0) + "").matches("admins|editors|ipausers|trust admins")) {
                result.add(i.get("result"));
            }
        }

        if (uid != null && !uid.equals("")) {
            ArrayList<Object> list1 = new ArrayList<>();
            for (Object item : result) {
                Map map = (Map) item;
                String uid1 = String.valueOf(map.get("uid"));
                if (uid1 != null && uid1.contains(uid)) {
                    list1.add(item);
                }
            }
            return list1;
        } else {
            return result;
        }
    }

    @Override
    public void save(Long domainId, UserGroupWithOUDTO group) throws Exception {

        Map<String, String> options = new HashMap<>();
        options.put("description", group.getDescription());
        options.put("ouCN", group.getOuCN());

        try {

            UserGroupDTO saveObj = new UserGroupDTO();
            saveObj.setCn(group.getCn());
            saveObj.setDescription(objectMapper.writeValueAsString(options));

            rpcService.request(domainId, "{" +
                    "    \"method\": \"group_add\"," +
                    "    \"params\": [" +
                    "        []," +
                    objectMapper.writeValueAsString(saveObj) +
                    "    ]" +
                    "}");
        } catch (Exception e) {
            log.error("保存域用户时出现错误：", e);
            throw e;
        }
    }

    @Override
    public void delete(Long domainId, String cn) {
        rpcService.request(domainId, "{\"method\":\"batch\",\"params\":[[{\"method\":\"group_del\",\"params\":[[\"" + cn + "\"],{}]}],{\"version\":\"2.237\"}]}");
    }

    @Override
    public List listUser(Long domainId, String groupCN) {
        LDAPResponse response = rpcService.request(domainId, "{\"method\":\"group_show\",\"params\":[[\"" + groupCN + "\"],{\"version\":\"2.237\"}]}");
        List<String> userCNList = (List<String>) ((Map) response.getResult().get("result")).get("member_user");

        if (userCNList != null && userCNList.size() > 0) {

            StringBuilder in = new StringBuilder();
            for (String userCN : userCNList) {

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

            LDAPResponse response1 = rpcService.request(domainId, "{\"method\":\"batch\",\"params\":[[" + in.toString() + "],{\"version\":\"2.237\"}]}");

            List list2 = (List) response1.getResult().get("results");
            List result = new ArrayList();
            for (Object item : list2) {
                Map i = (Map) item;
                result.add(i.get("result"));
            }

            return result;
        } else {
            return new ArrayList();
        }
    }

    @Override
    public void addUser(Long domainId, UserGroupRefDTO ref) {
        rpcService.request(domainId, "{\"method\":\"group_add_member\",\"params\":[[\"" + ref.getGroupCN() + "\"],{\"all\":true,\"user\":[" + ref.getUserCNList() + "],\"version\":\"2.237\"}]}");
    }

    @Override
    public void removeUser(Long domainId, UserGroupRefDTO ref) {
        rpcService.request(domainId, "{\"method\":\"group_remove_member\",\"params\":[[\"" + ref.getGroupCN() + "\"],{\"all\":true,\"user\":[" + ref.getUserCNList() + "],\"version\":\"2.237\"}]}");
    }

    @Override
    public List listAllNames(Long domainId) {
        LDAPResponse response = rpcService.request(domainId, "{\"method\":\"group_find\",\"params\":[[null],{\"no_members\":true,\"version\":\"2.237\"}]}");
        return (List) response.getResult().get("result");
    }
}
