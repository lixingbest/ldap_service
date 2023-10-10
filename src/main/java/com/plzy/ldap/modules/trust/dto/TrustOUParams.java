package com.plzy.ldap.modules.trust.dto;

import lombok.Data;

@Data
public class TrustOUParams {

    private Long domainId;

    private Long trustDomainId;

    private String ouIds;
}
