package com.plzy.ldap.modules.sysconfig.dto;

import com.plzy.ldap.modules.sysconfig.domain.TSysConf;
import lombok.Data;

@Data
public class SysConfigDto extends TSysConf {

    private String viewName;
}
