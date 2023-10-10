package com.plzy.ldap.modules.client_access_log.dto;

import com.plzy.ldap.modules.client_access_log.domain.TLdapClientAccessLog;
import lombok.Data;

@Data
public class LdapClientAccessLogDTO extends TLdapClientAccessLog {

    private String domain;

    private String domainDn;

    private String domainName;

    private String ouName;

    private String ouDN;
}
