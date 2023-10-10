package com.plzy.ldap.modules.domainuser.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzy.ldap.framework.ldap.protocol.LDAPResponse;
import com.plzy.ldap.framework.ldap.service.LDAPRemoteService;
import com.plzy.ldap.modules.domainuser.dto.ActiveDomainUserWithExtraCommentsDTO;
import com.plzy.ldap.modules.domainuser.service.PreservedDomainUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PreservedDomainUserServiceImpl implements PreservedDomainUserService {

    @Autowired
    private LDAPRemoteService rpcService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List listAll(Long domainId) {

        LDAPResponse resp = rpcService.request(domainId,"{" +
                "    \"method\": \"user_find\"," +
                "    \"params\": [" +
                "        [" +
                "            \"\"" +
                "        ]," +
                "        {" +
                "            \"preserved\": true," +
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
            String cn = ((List) it.get("uid")).get(0) + "";

            in.append("{" +
                    "                \"method\": \"user_show\"," +
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

        LDAPResponse resp2 = rpcService.request(domainId,"{" +
                "    \"method\": \"batch\"," +
                "    \"params\": [" +
                "        [" +
                "            " + in +
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
    public void update(Long domainId, ActiveDomainUserWithExtraCommentsDTO user) {

        // ou必须要传，否则会丢失ou下的options信息
        if(user.getOu() == null){
            log.error("更新域用户信息时ou必须要传，否则会丢失ou下的options信息");
            throw new RuntimeException("更新域用户信息时ou必须要传，否则会丢失ou下的options信息");
        }

        // title必须要传，否则会丢失title下的options信息
        if(user.getTitle() == null){
            log.error("更新域用户信息时title必须要传，否则会丢失title下的options信息");
            throw new RuntimeException("更新域用户信息时title必须要传，否则会丢失title下的options信息");
        }

        // 这里之所以不自动生成json，是因为后面代码再拼接的时候不会将此内容生成\"
        user.setOu("{" +
            "\\\"ouCN\\\":"+user.getOu()+"," +
            "\\\"comments1\\\":"+ (StringUtils.hasText(user.getComments1()) ? "\\\""+user.getComments1()+"\\\"" : "null") +"," +
            "\\\"comments2\\\":"+ (StringUtils.hasText(user.getComments2()) ? "\\\""+user.getComments2()+"\\\"" : "null") +"," +
            "\\\"comments3\\\":"+ (StringUtils.hasText(user.getComments3()) ? "\\\""+user.getComments3()+"\\\"" : "null") +
            "}");
        user.setTitle("{" +
                "\\\"title\\\":\\\""+user.getTitle()+"\\\"," +
                "\\\"job\\\":"+ (StringUtils.hasText(user.getJob()) ? "\\\""+user.getJob()+"\\\"" : "null") +
                "}");

        Map<String, Object> map = BeanMap.create(user);

        // 只包含更新内容的字段
        String modifyFilesStr = "";
        for (Map.Entry<String, Object> entry : map.entrySet()){
            // 跳过uid、空值，以及备注
            if(!"uid".equals(entry.getKey())
                    && entry.getValue() != null
                    && !entry.getKey().startsWith("comments")
                    && !entry.getKey().matches("loginshell|ipauserauthtype|version|job")
            ){
                modifyFilesStr += "\""+entry.getKey()+"\":\""+entry.getValue()+"\",";
            }
        }
        if(modifyFilesStr.length() > 0){
            modifyFilesStr = modifyFilesStr.substring(0, modifyFilesStr.length() - 1);
        }

        rpcService.request(domainId, "{\"method\":\"user_mod\",\"params\":[[\""+map.get("uid")+"\"],{\"all\":true,\"rights\":true,"+modifyFilesStr+",\"version\":\"2.240\"}]}");
    }

    @Override
    public void delete(Long domainId, String uid) {

        rpcService.request(domainId,"{\"method\":\"batch\",\"params\":[[{\"method\":\"user_del\",\"params\":[[\""+uid+"\"],{\"preserve\":\"false\"}]}],{\"version\":\"2.240\"}]}");
    }

    @Override
    public void recover(Long domainId, String uid) {

        rpcService.request(domainId,"{\"method\":\"batch\",\"params\":[[{\"method\":\"user_undel\",\"params\":[[\""+uid+"\"],{}]}],{\"version\":\"2.237\"}]}");
    }
}
