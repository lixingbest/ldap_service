package com.plzy.ldap.modules.admin.dto;

import com.plzy.ldap.modules.admin.domain.TSysAdmin;
import com.plzy.ldap.modules.resource.domain.TResource;
import com.plzy.ldap.modules.token.domain.TSysToken;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserWithToken {

    private TSysAdmin admin;

    private TSysToken token;

    private List<TResource> resources;
}
