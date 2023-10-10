package com.plzy.ldap.modules.usergroup.dto;

import lombok.Data;

@Data
public class UserGroupDTO {

    // 组名
    private String cn;

    // 组描述
    private String description;

    private String version = "2.237";
}
