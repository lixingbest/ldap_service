package com.plzy.ldap.modules.dc.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzy.ldap.framework.ldap.protocol.LDAPResponse;
import com.plzy.ldap.framework.ldap.service.LDAPRemoteService;
import com.plzy.ldap.modules.dc.dto.DCDTO;
import com.plzy.ldap.modules.dc.service.DCService;
import com.plzy.ldap.modules.host.dto.HostDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DCServiceImpl implements DCService {

    @Autowired
    private LDAPRemoteService rpcService;

    @Value("${ldap.dc-prefix}")
    private String dcPrefix;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List list(Long domainId, String fqdnKeywords) {

        LDAPResponse resp = rpcService.request(domainId,"{" +
                "    \"method\": \"host_find\"," +
                "    \"params\": [" +
                "        [" +
                "            \""+(StringUtils.hasText(fqdnKeywords)?fqdnKeywords:"")+"\"" +
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
            String fqdn = ((List) it.get("fqdn")).get(0) + "";

            // 只包含域控服务器，即fqdn中包含server的字段
            if(!fqdn.startsWith(dcPrefix)){
                continue;
            }

            in.append("{" +
                    "                \"method\": \"host_show\"," +
                    "                \"params\": [" +
                    "                    [" +
                    "                        \"").append(fqdn).append("\"" +
                    "                    ]," +
                    "                    {" +
                    "                        \"no_members\": true"+
                    "                    }" +
                    "                ]" +
                    "            },");
        }
        if(in.length() > 1) {
            in = in.deleteCharAt(in.length() - 1);
        }

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

        return result;
    }

    @Override
    public void update(Long domainId, DCDTO dto) {

        Map<String, Object> map = BeanMap.create(dto);

        // 只包含更新内容的字段
        String modifyFilesStr = "";
        for (Map.Entry<String, Object> entry : map.entrySet()){
            // 跳过uid、空值，以及备注
            if(entry.getValue() != null && !entry.getKey().matches("fqdn|version")){
                modifyFilesStr += "\""+entry.getKey()+"\":\""+entry.getValue()+"\",";
            }
        }
        if(modifyFilesStr.length() > 0){
            modifyFilesStr = modifyFilesStr.substring(0, modifyFilesStr.length() - 1);
        }

        rpcService.request(domainId, "{\"method\":\"host_mod\",\"params\":[[\""+dto.getFqdn()+"\"],{\"all\":true,\"rights\":true,"+modifyFilesStr+",\"version\":\"2.237\"}]}");
    }
}
