package com.plzy.ldap.modules.role.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class AddMenuToRoleDto {

    private Long roleId;
    private ArrayList<Long> menuIds;
}
