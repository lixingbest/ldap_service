package com.plzy.ldap.modules.passwd_policy.service;

import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.passwd_policy.dto.PasswdPolicyDTO;

import java.util.List;

public interface PasswdPolicyService {

    List list(TLdapDomain domain, String group);

    void add(PasswdPolicyDTO passwdPolicy);

    void update(PasswdPolicyDTO passwdPolicy);

    void remove(Long domainId, String group);
}
