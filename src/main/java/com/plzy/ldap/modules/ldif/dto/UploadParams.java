package com.plzy.ldap.modules.ldif.dto;

import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import lombok.Data;

@Data
public class UploadParams extends TLdapDomain {

    private Long domainId;

    private String originalDomainDN;

    private String defaultPasswd;

    private String ouLdifFileName;

    private String userLdifFileName;

}
