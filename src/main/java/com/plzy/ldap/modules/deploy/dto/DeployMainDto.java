package com.plzy.ldap.modules.deploy.dto;

import com.plzy.ldap.modules.deploy.domain.TLdapDeployDetails;
import com.plzy.ldap.modules.deploy.domain.TLdapDeployMain;
import lombok.Data;

import java.util.List;

@Data
public class DeployMainDto extends TLdapDeployMain {

    List<TLdapDeployDetails> details;


}
