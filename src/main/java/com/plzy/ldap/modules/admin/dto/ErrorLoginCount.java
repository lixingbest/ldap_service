package com.plzy.ldap.modules.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorLoginCount {

    private Integer count;

    private Long lastLoginTime;
}
