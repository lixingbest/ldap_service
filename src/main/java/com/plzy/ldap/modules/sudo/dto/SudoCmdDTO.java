package com.plzy.ldap.modules.sudo.dto;

import lombok.Data;

@Data
public class SudoCmdDTO {

    // sudo命令
    private String sudocmd;

    // 描述
    private String description;

    private String version = "2.237";
}
