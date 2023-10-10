package com.plzy.ldap.modules.dns.impl;

import com.plzy.ldap.framework.ldap.service.LDAPRemoteService;
import com.plzy.ldap.modules.dns.LdapDNSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LdapDNSServiceImpl implements LdapDNSService {

    @Autowired
    private LDAPRemoteService remoteService;

    @Override
    public void addDNSRec(Long domainId, String hostname, String ip) {

        int posi = hostname.indexOf(".");
        String prefix = hostname.substring(0,posi);
        String domain = hostname.substring(posi+1) + ".";

        remoteService.request(domainId,"{\"method\":\"dnsrecord_add\",\"params\":[[\""+domain+"\",\""+prefix+"\"],{\"a_part_ip_address\":\""+ip+"\",\"a_extra_create_reverse\":false,\"version\":\"2.237\"}]}");
    }
}
