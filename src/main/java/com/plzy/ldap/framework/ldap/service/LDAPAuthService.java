package com.plzy.ldap.framework.ldap.service;

public interface LDAPAuthService {

    String authWithAdmin(Long domainId);



    boolean auth(Long domainId, String user, String password);

    boolean authWithUPN(String user, String password);
}
