package com.plzy.ldap.modules.ldap_login_limit.dto;

import com.plzy.ldap.modules.ldap_login_limit.domain.TLdapLoginLimit;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class LoginLimitDto extends TLdapLoginLimit {

    private Integer type;

    private String loginDateBeginStr;

    private String loginDateEndStr;
}
