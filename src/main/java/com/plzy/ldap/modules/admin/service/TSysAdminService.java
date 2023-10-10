package com.plzy.ldap.modules.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.modules.admin.domain.TSysAdmin;
import com.baomidou.mybatisplus.extension.service.IService;
import com.plzy.ldap.modules.admin.dto.SysAdminWithNameDTO;

import java.util.Set;

/**
 *
 */
public interface TSysAdminService extends IService<TSysAdmin> {

    IPage<SysAdminWithNameDTO> list(Page<TSysAdmin> page, TSysAdmin condition, Set<Long> domainIdSet);
}
