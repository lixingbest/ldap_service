package com.plzy.ldap.framework.ldap.protocol;

import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
public class LDAPResponse {

    private Map<String, Object> result;

    private Map<String, Object> error;

    private String version;

    private String id;

    private String principal;
}
