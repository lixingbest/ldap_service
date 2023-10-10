package com.plzy.ldap.modules.active_directory.service.impl;

import com.plzy.ldap.framework.ldap.protocol.LDAPResponse;
import com.plzy.ldap.framework.ldap.service.LDAPRemoteService;
import com.plzy.ldap.modules.active_directory.service.ActiveDirectoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ActiveDirectoryServiceImpl implements ActiveDirectoryService {

    @Autowired
    private LDAPRemoteService ldapRemoteService;

    @Override
    public List<Map> list(Long domainId,String domainName) {

        LDAPResponse response = ldapRemoteService.request(domainId, "{\"method\":\"trust_find\",\"params\":[[\"\"],{"+(StringUtils.hasText(domainName)?"\"cn\":\""+domainName+"\",":"")+"\"pkey_only\":true,\"sizelimit\":0,\"version\":\"2.237\"}]}");
        List<Map> trustList = (List<Map>)response.getResult().get("result");
        if(trustList != null && trustList.size() > 0){

            StringBuilder part = new StringBuilder();
            for(Map record : trustList){
                part.append("\"").append(((List)record.get("cn")).get(0)).append("\",");
            }
            String partStr = part.substring(0, part.length() - 1);
            LDAPResponse response1 = ldapRemoteService.request(domainId, "{\"method\":\"trust_show\",\"params\":[["+partStr+"],{\"all\":true,\"rights\":true,\"version\":\"2.237\"}]}");

            // 判断结果类型，如果是一条则result为map，否则是list<map>
            Object result = response1.getResult().get("result");
            if(result instanceof Map){
                return Arrays.asList((Map)result);
            }
            return (List<Map>) response1.getResult().get("result");
        }

        return new ArrayList<>();
    }

    @Override
    public Map settings(Long domainId) {

        LDAPResponse response = ldapRemoteService.request(domainId, "{\"method\":\"trust_find\",\"params\":[[\"\"],{\"pkey_only\":true,\"sizelimit\":0,\"version\":\"2.237\"}]}");
        List<Map> trustList = (List<Map>)response.getResult().get("result");
        if(trustList != null && trustList.size() > 0){

            LDAPResponse response1 = ldapRemoteService.request(domainId, "{\"method\":\"trustconfig_show\",\"params\":[[],{\"all\":true,\"rights\":true,\"trust_type\":\"ad\",\"version\":\"2.237\"}]}");
            if(response1 != null){
                return (Map) response1.getResult().get("result");
            }
        }

        return null;
    }
}
