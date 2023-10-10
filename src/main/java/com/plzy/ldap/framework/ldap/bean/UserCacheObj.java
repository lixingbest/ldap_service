package com.plzy.ldap.framework.ldap.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@AllArgsConstructor
@ToString
public class UserCacheObj {

    private String upn;

    private String password;

    private Long updateTimestamp;
}
