package com.plzy.ldap.framework.ldap.service;

import com.plzy.ldap.modules.domain.domain.TLdapDomain;

import java.util.Map;

public interface LDAPDomainCacheService {

    void reload();

    TLdapDomain get(Long domainId);

    Map<Long, TLdapDomain> getCache();

    void updateCookie(Long domainId, String cookie);
}
