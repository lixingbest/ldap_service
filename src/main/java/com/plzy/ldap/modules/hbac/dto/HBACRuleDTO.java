package com.plzy.ldap.modules.hbac.dto;

import lombok.Data;

@Data
public class HBACRuleDTO {

    // 规则名
    private String cn;

    // 描述
    private String description;

    private String version = "2.237";
}
