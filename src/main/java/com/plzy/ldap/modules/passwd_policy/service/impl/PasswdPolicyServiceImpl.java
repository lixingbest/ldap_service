package com.plzy.ldap.modules.passwd_policy.service.impl;

import com.plzy.ldap.framework.ldap.protocol.LDAPResponse;
import com.plzy.ldap.framework.ldap.service.LDAPRemoteService;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.passwd_policy.dto.PasswdPolicyDTO;
import com.plzy.ldap.modules.passwd_policy.service.PasswdPolicyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.StringBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PutMapping;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PasswdPolicyServiceImpl implements PasswdPolicyService {

    @Autowired
    private LDAPRemoteService remoteService;

    @Autowired
    private TLdapDomainService domainService;

    @Override
    public List list(TLdapDomain domain, String group) {

        LDAPResponse response = remoteService.request(domain.getId(), "{\"method\":\"pwpolicy_find\",\"params\":[[\""+ (StringUtils.hasText(group)?group:"") +"\"],{\"pkey_only\":true,\"sizelimit\":0,\"version\":\"2.237\"}]}");
        List list = (List) response.getResult().get("result");
        if(list != null && list.size() > 0){

            StringBuilder args = new StringBuilder();
            for(Object rec : list){
                Map map = (Map)rec;
                String policyName = ((List)map.get("cn")).get(0) + "";
                args.append("{\"method\":\"pwpolicy_show\",\"params\":[[\""+policyName+"\"],{}]},");
            }
            if(args.length() > 0){
                args.deleteCharAt(args.length()-1);
            }
            String batchCmd = "{\"method\":\"batch\",\"params\":[["+args.toString()+"],{\"version\":\"2.237\"}]}";
            LDAPResponse result = remoteService.request(domain.getId(),batchCmd);
            List finalResult = (List) result.getResult().get("results");
            if(finalResult != null && finalResult.size() > 0){
                for(Object item : finalResult){
                    ((Map)item).put("domain", domain.getDomainName());
                    ((Map)item).put("domainName", domain.getName());
                }
            }

            return finalResult;
        }

        return  null;
    }

    @Override
    public void add(PasswdPolicyDTO passwdPolicy) {
        remoteService.request(passwdPolicy.getDomainId(), "{\"method\":\"pwpolicy_add\",\"params\":[[\""+passwdPolicy.getCn()+"\"],{\"cospriority\":\""+passwdPolicy.getCospriority()+"\",\"version\":\"2.237\"}]}");
        update(passwdPolicy);
    }

    @Override
    public void update(PasswdPolicyDTO passwdPolicy) {
        remoteService.request(passwdPolicy.getDomainId(), "{\"method\":\"pwpolicy_mod\",\"params\":[[\""+passwdPolicy.getCn()+"\"],{\"all\":true,\"rights\":true,\"krbmaxpwdlife\":\""+passwdPolicy.getKrbmaxpwdlife()+"\",\"krbminpwdlife\":\""+passwdPolicy.getKrbminpwdlife()+"\",\"krbpwdhistorylength\":\""+passwdPolicy.getKrbpwdhistorylength()+"\",\"krbpwdmindiffchars\":\""+passwdPolicy.getKrbpwdmindiffchars()+"\",\"krbpwdminlength\":\""+passwdPolicy.getKrbpwdminlength()+"\",\"krbpwdmaxfailure\":\""+passwdPolicy.getKrbpwdmaxfailure()+"\",\"krbpwdfailurecountinterval\":\""+passwdPolicy.getKrbpwdfailurecountinterval()+"\",\"krbpwdlockoutduration\":\""+passwdPolicy.getKrbpwdlockoutduration()+"\",\"version\":\"2.237\"}]}");
    }

    @Override
    public void remove(Long domainId, String group) {
        remoteService.request(domainId,"{\"method\":\"batch\",\"params\":[[{\"method\":\"pwpolicy_del\",\"params\":[[\""+group+"\"],{}]}],{\"version\":\"2.237\"}]}");
    }
}
