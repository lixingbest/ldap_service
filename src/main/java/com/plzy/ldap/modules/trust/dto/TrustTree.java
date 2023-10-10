package com.plzy.ldap.modules.trust.dto;

import lombok.Data;

@Data
public class TrustTree {

    private String id;

    private String pid;

    private String name;

    private String domainName;

    private String checked;
}
