package com.plzy.ldap.modules.usergroup.dto;

import lombok.Data;

@Data
public class UserGroupRefDTO {

    private String groupCN;

    private String userCNList;
}
