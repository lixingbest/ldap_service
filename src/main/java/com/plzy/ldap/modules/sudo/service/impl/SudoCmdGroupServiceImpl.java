package com.plzy.ldap.modules.sudo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzy.ldap.framework.ldap.protocol.LDAPResponse;
import com.plzy.ldap.framework.ldap.service.LDAPRemoteService;
import com.plzy.ldap.modules.sudo.dto.SudoCmdGroupDTO;
import com.plzy.ldap.modules.sudo.service.SudoCmdGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SudoCmdGroupServiceImpl implements SudoCmdGroupService {

    @Autowired
    private LDAPRemoteService rpcService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List list(Long domainId) {

        LDAPResponse resp = rpcService.request(domainId,"{" +
                "    \"method\": \"sudocmdgroup_find\"," +
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
                    "                \"method\": \"sudocmdgroup_show\"," +
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
            // 在返回结果中单独添加domainId，因为前台需要知道这条数据属于哪个域
            record.put("domainId", domainId);
            result.add(record);
        }

        return result;
    }

    @Override
    public void save(Long domainId,SudoCmdGroupDTO sudoCmdGroup) {

        try{
            rpcService.request(domainId,"{" +
                    "    \"method\": \"sudocmdgroup_add\"," +
                    "    \"params\": [" +
                    "        []," +
                    objectMapper.writeValueAsString(sudoCmdGroup) +
                    "    ]" +
                    "}");
        }catch (Exception e){
            log.error("保存sudocmdgroup时出现错误：", e);
        }
    }

    @Override
    public void delete(Long domainId,String cn) {
        rpcService.request(domainId,"{" +
                "    \"method\": \"batch\"," +
                "    \"params\": [" +
                "        [" +
                "            {" +
                "                \"method\": \"sudocmdgroup_del\"," +
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
    public void addSudoCmd(Long domainId,String groupCN, String cmdCN) {
        rpcService.request(domainId,"{\"method\":\"sudocmdgroup_add_member\",\"params\":[[\""+groupCN+"\"],{\"all\":true,\"sudocmd\":[\""+cmdCN+"\"],\"version\":\"2.237\"}]}");
    }
}
