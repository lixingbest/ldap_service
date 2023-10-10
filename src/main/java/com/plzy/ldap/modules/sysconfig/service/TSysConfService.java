package com.plzy.ldap.modules.sysconfig.service;

import com.plzy.ldap.modules.sysconfig.domain.TSysConf;
import com.baomidou.mybatisplus.extension.service.IService;
import com.plzy.ldap.modules.sysconfig.dto.SysConfigDto;

import java.util.List;

/**
 *
 */
public interface TSysConfService extends IService<TSysConf> {

    List<SysConfigDto> getList();
}
