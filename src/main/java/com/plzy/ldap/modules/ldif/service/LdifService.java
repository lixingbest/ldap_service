package com.plzy.ldap.modules.ldif.service;

public interface LdifService {

    void domainUserImport(Long domainId,String domainDN, String defaultPasswd, String fileName);

    void ouImport(Long domainId,Long parentOUId,String domainDN, String fileName);

    String getLogs();
}
