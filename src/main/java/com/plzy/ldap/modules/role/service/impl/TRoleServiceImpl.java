package com.plzy.ldap.modules.role.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.modules.role.domain.TRole;
import com.plzy.ldap.modules.role.mapper.TRoleMapper;
import com.plzy.ldap.modules.role.service.TRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class TRoleServiceImpl extends ServiceImpl<TRoleMapper, TRole>
implements TRoleService {

    @Autowired
    private TRoleMapper roleMapper;

    @Override
    public IPage<TRole> getList(Page<TRole> page, String name, Byte enable) {
        return roleMapper.getList(page, name, enable);
    }
}




