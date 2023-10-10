package com.plzy.ldap.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.modules.admin.domain.TSysAdmin;
import com.plzy.ldap.modules.admin.dto.SysAdminWithNameDTO;
import com.plzy.ldap.modules.admin.service.TSysAdminService;
import com.plzy.ldap.modules.admin.mapper.TSysAdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 *
 */
@Service
public class TSysAdminServiceImpl extends ServiceImpl<TSysAdminMapper, TSysAdmin>
    implements TSysAdminService{

    @Autowired
    private TSysAdminMapper sysAdminMapper;

    @Override
    public IPage<SysAdminWithNameDTO> list(Page<TSysAdmin> page, TSysAdmin condition, Set<Long> domainIdSet) {
        return sysAdminMapper.list(page, condition,domainIdSet);
    }
}




