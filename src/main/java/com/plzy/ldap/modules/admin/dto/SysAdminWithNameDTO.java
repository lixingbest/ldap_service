package com.plzy.ldap.modules.admin.dto;

import com.plzy.ldap.modules.admin.domain.TSysAdmin;
import lombok.Data;

@Data
public class SysAdminWithNameDTO extends TSysAdmin {

    private String ou;

    private String mgtDomain;
}
