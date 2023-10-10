package com.plzy.ldap.modules.usergroup.dto;

import lombok.Data;

@Data
public class UserGroupWithOUDTO {

    // 组名
    private String cn;

    // 组描述
    private String description;

    // 所属的组织单位名称
    private String ouCN;

    private String version = "2.237";
}
