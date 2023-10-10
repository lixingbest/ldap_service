package com.plzy.ldap.framework.ldap.service;

import com.plzy.ldap.framework.ldap.protocol.LDAPResponse;

public interface LDAPRemoteService {

    LDAPResponse request(Long domainId, String command);

    LDAPResponse request(Long domainId, boolean fromCache, String command);

    LDAPResponse requestAny(Long clusterId, String command);
}
