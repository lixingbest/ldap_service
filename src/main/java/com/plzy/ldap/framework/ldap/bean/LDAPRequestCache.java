package com.plzy.ldap.framework.ldap.bean;

import com.plzy.ldap.framework.ldap.protocol.LDAPResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LDAPRequestCache {

    private LDAPResponse value;

    private Long timestamp;
}
