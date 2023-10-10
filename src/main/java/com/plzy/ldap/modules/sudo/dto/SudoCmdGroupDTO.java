package com.plzy.ldap.modules.sudo.dto;

import lombok.Data;

@Data
public class SudoCmdGroupDTO {

    // 名称
    private String cn;

    // 描述
    private String description;

    private String version = "2.237";
}
