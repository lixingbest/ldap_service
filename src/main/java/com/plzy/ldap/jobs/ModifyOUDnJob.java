package com.plzy.ldap.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzy.ldap.framework.ldap.service.LDAPRemoteService;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.domainuser.service.DomainUserService;
import com.plzy.ldap.modules.ou.domain.TLdapOu;
import com.plzy.ldap.modules.ou.service.TLdapOuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ModifyOUDnJob {

    @Autowired
    private TLdapDomainService domainService;

    @Autowired
    private DomainUserService domainUserService;

    @Autowired
    private TLdapOuService ouService;

    @Autowired
    private LDAPRemoteService ldapRemoteService;

    private ObjectMapper objectMapper = new ObjectMapper();

    public void modify(){

        // 加载所有的域名
        List<TLdapDomain> domainList = domainService.listSubdomain();
        for(TLdapDomain domain : domainList){
            // 加载所有域用户
            List userList = domainUserService.listAll(domain.getId());
            for(Object user : userList){
                try{
                    Map userMap = (Map)user;
                    String uid = ((List)userMap.get("uid")).get(0) + "";

                    if(userMap.get("ou") != null) {
                        String ouStr = ((List) userMap.get("ou")).get(0) + "";
                        Map ouMap = objectMapper.readValue(ouStr, Map.class);
                        Long ouId = Long.valueOf(ouMap.get("ouCN") + "");
                        TLdapOu ouRec = ouService.getById(ouId);

                        // 更新域用户的ou设置
                        String cmd = "{\"method\":\"user_mod\",\"params\":[[\""+uid+"\"],{\"all\":true,\"rights\":true,\"ou\":\"{\\\"ouCN\\\":"+ouId+",\\\"ouDN\\\":\\\""+ouRec.getDn()+"\\\",\\\"comments3\\\":null,\\\"comments2\\\":null,\\\"comments1\\\":null}\",\"version\":\"2.237\"}]}";
                        ldapRemoteService.request(domain.getId(),cmd);
                    }
                }catch (Exception e2){
                    log.error("更新属性时出现错误：",e2);
                }
            }
        }
    }
}
