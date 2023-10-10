package com.plzy.ldap.modules.domain.dto;

import lombok.Data;

@Data
public class DomainTree {

    private String id;

    private String pid;

    private String name;

    private String type;

    private String domainName;

    private Byte upStatus;

    private String dn;

    private Integer ordIdx;

    private String tooltip;

    private Integer enable;

    private Long refid;
}
