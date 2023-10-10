package com.plzy.ldap.framework.ldap.service;

public interface LDAPUserCachePoolService {

    boolean authWithUPN(String user, String password);
}
