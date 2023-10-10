package com.plzy.ldap.modules.passwd_policy.dto;

import io.swagger.models.auth.In;
import lombok.Data;

@Data
public class PasswdPolicyDTO {

    private Long domainId;

    private String cn;

    private Integer cospriority;

    private Integer krbmaxpwdlife;

    private Integer krbminpwdlife;

    private Integer krbpwdhistorylength;

    private Integer krbpwdmindiffchars;

    private Integer krbpwdminlength;

    private Integer krbpwdmaxfailure;

    private Integer krbpwdfailurecountinterval;

    private Integer krbpwdlockoutduration;
}
