package com.plzy.ldap.modules.sudo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzy.ldap.framework.ldap.protocol.LDAPResponse;
import com.plzy.ldap.framework.ldap.service.LDAPRemoteService;
import com.plzy.ldap.modules.sudo.dto.SudoCmdDTO;
import com.plzy.ldap.modules.sudo.service.SudoCmdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SudoCmdServiceImpl implements SudoCmdService {

    @Autowired
    private LDAPRemoteService rpcService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List list(Long domainId,String cmdGroupCN) {

        LDAPResponse resp = rpcService.request(domainId,"{" +
                "    \"method\": \"sudocmdgroup_show\"," +
                "    \"params\": [[\"" +
                        cmdGroupCN+
                "\"]        ,{" +
                "            \"version\": \"2.237\"" +
                "        }" +
                "    ]" +
                "}");
        List list = (List) ((Map)resp.getResult().get("result")).get("member_sudocmd");
        // 如果分组下没有命令，则list为null
        if(list != null && list.size() > 0) {
            StringBuilder in = new StringBuilder();
            for (Object item : list) {
                String it = (String) item;

                in.append("{" +
                        "                \"method\": \"sudocmd_show\"," +
                        "                \"params\": [" +
                        "                    [" +
                        "                        \"").append(it).append("\"" +
                        "                    ]," +
                        "                    {" +
                        "                        \"no_members\": true" +
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
            List result = new ArrayList();
            for (Object item : list2) {
                Map i = (Map) item;
                Map record = (Map)i.get("result");
                // 额外添加domainId，因为前端需要知道此记录所属哪个域
                record.put("domainId", domainId);
                result.add(record);
            }

            return result;
        }else{
            return new ArrayList();
        }
    }

    @Override
    public void save(Long domainId,SudoCmdDTO sudo) {

        try{
            rpcService.request(domainId,"{" +
                    "    \"method\": \"sudocmd_add\"," +
                    "    \"params\": [" +
                    "        []," +
                    objectMapper.writeValueAsString(sudo) +
                    "    ]" +
                    "}");
        }catch (Exception e){
            log.error("保存sudocmd时出现错误：", e);
        }
    }

    @Override
    public void delete(Long domainId,String sudocmd) {
        rpcService.request(domainId,"{" +
                "    \"method\": \"batch\"," +
                "    \"params\": [" +
                "        [" +
                "            {" +
                "                \"method\": \"sudocmd_del\"," +
                "                \"params\": [[\"" +
                sudocmd + "\"]," +
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
}
