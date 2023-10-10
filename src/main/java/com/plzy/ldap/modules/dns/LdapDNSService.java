package com.plzy.ldap.modules.dns;

public interface LdapDNSService {

    void addDNSRec(Long domainId, String hostname, String ip);
}
