package com.plzy.ldap.modules.cross_trust.dto;

import com.plzy.ldap.modules.cross_trust.domain.TLdapCrossTrust;
import lombok.Data;

import java.util.Map;

@Data
public class TLdapCrossTrustDTO extends TLdapCrossTrust {

    private String targetDomainName;

    private String ouName;

    private Map userInfo;
}
