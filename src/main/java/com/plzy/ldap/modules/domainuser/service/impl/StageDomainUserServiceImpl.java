package com.plzy.ldap.modules.domainuser.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzy.ldap.framework.ldap.protocol.LDAPResponse;
import com.plzy.ldap.framework.ldap.service.LDAPRemoteService;
import com.plzy.ldap.modules.domainuser.dto.ActiveDomainUser;
import com.plzy.ldap.modules.domainuser.dto.ActiveDomainUserWithExtraCommentsDTO;
import com.plzy.ldap.modules.domainuser.dto.StageDomainUser;
import com.plzy.ldap.modules.domainuser.dto.StageDomainUserWithExtraCommentsDTO;
import com.plzy.ldap.modules.domainuser.service.StageDomainUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StageDomainUserServiceImpl implements StageDomainUserService {

    @Autowired
    private LDAPRemoteService rpcService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public boolean isExist(Long domainId, String uid) {

        LDAPResponse resp = rpcService.request(domainId, "{\"method\":\"stageuser_find\",\"params\":[[],{\"uid\":\""+uid+"\",\"no_members\":true,\"version\":\"2.240\",\"all\":false}]}");
        int count = Integer.valueOf(resp.getResult().get("count") + "");
        return count > 0;
    }

    @Override
    public List listAll(Long domainId) {

        LDAPResponse resp = rpcService.request(domainId,"{" +
                "    \"method\": \"stageuser_find\"," +
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
    public void save(Long domainId, StageDomainUserWithExtraCommentsDTO user) {

        Map<String, Object> ouJSONMap = new HashMap<>();
        ouJSONMap.put("ouCN", StringUtils.hasText(user.getOu()) ? Long.valueOf(user.getOu()) : null);
        ouJSONMap.put("comments1", user.getComments1());
        ouJSONMap.put("comments2", user.getComments2());
        ouJSONMap.put("comments3", user.getComments3());

        Map<String, Object> titleJSONMap = new HashMap<>();
        titleJSONMap.put("title", user.getTitle());
        titleJSONMap.put("job", user.getJob());

        try{

            StageDomainUser target = new StageDomainUser();
            BeanUtils.copyProperties(user, target);
            target.setOu(objectMapper.writeValueAsString(ouJSONMap));
            target.setTitle(objectMapper.writeValueAsString(titleJSONMap));

            // 通过全名（cn）自动生成姓（givenname）和名（sn）
            if(StringUtils.hasText(target.getCn()) && target.getCn().length() >= 2){
                target.setSn(target.getCn().substring(0,1));
                target.setGivenname(target.getCn().substring(1));
            }else {
                throw new RuntimeException("全名（cn）必填，且长度必须>=2！");
            }

            rpcService.request(domainId,"{" +
                    "    \"method\": \"stageuser_add\"," +
                    "    \"params\": [" +
                    "        []," +
                    objectMapper.writeValueAsString(target) +
                    "    ]" +
                    "}");

            // 立即设置用户密码，避免H4A验证此用户时报错
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("user", user.getUid());
            params.add("old_password", user.getUserpassword());
            params.add("new_password", user.getUserpassword());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Referer", "https://ipaserver.ipadomain.example.com/ipa/ui/");

            HttpEntity httpEntity = new HttpEntity<>(params, headers);

            log.info("即将发送认证请求");

            ResponseEntity resp = restTemplate.exchange("https://ipaserver.ipadomain.example.com/ipa/session/change_password", HttpMethod.POST, httpEntity, String.class);

            HttpHeaders respHeaders = resp.getHeaders();

            log.info("收到响应：" + respHeaders);

            HttpStatus status = resp.getStatusCode();
            log.info("响应状态码：" + status);
            if(status == HttpStatus.OK){
                log.info("密码更新成功！");
            }else {
                log.error("验证账号中出现未知错误");
            }


        }catch (Exception e){
            log.error("保存域用户时出现错误：", e);
        }
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

        rpcService.request(domainId, "{\"method\":\"stageuser_mod\",\"params\":[[\""+map.get("uid")+"\"],{\"all\":true,\"rights\":true,"+modifyFilesStr+",\"version\":\"2.240\"}]}");
    }

    @Override
    public void delete(Long domainId, String uid) {

        rpcService.request(domainId,"{\"method\":\"batch\",\"params\":[[{\"method\":\"stageuser_del\",\"params\":[[\""+uid+"\"],{\"preserve\":\"false\"}]}],{\"version\":\"2.240\"}]}");
    }

    @Override
    public void active(Long domainId, String uid) {

        rpcService.request(domainId,"{\"method\":\"batch\",\"params\":[[{\"method\":\"stageuser_activate\",\"params\":[[\""+uid+"\"],{}]}],{\"version\":\"2.237\"}]}");
    }
}
