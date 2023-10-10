package com.plzy.ldap.modules.role.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.plzy.ldap.modules.role.domain.TRole;

/**
 *
 */
public interface TRoleService extends IService<TRole> {

    IPage<TRole> getList(Page<TRole> page, String name, Byte enable);
}
