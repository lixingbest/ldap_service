package com.plzy.ldap.jobs;

import com.plzy.ldap.framework.ldap.service.LDAPRemoteService;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.domainuser.service.DomainUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ModifyPasswdJob {

    @Autowired
    private TLdapDomainService domainService;

    @Autowired
    private DomainUserService domainUserService;

    @Autowired
    private LDAPRemoteService ldapRemoteService;

    public void run(){

        domainUserService.modifyPasswd("Jnhg2023");
    }
}
