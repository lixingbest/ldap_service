package com.plzy.ldap.modules.shellTemp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.modules.shellTemp.domain.TLdapShellTemp;
import com.plzy.ldap.modules.shellTemp.service.TLdapShellTempService;
import com.plzy.ldap.modules.shellTemp.mapper.TLdapShellTempMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 *
 */
@Service
public class TLdapShellTempServiceImpl extends ServiceImpl<TLdapShellTempMapper, TLdapShellTemp>
    implements TLdapShellTempService{

    @Autowired
    private TLdapShellTempMapper shellTempMapper;

    @Override
    public IPage<TLdapShellTemp> getPageByTypeSet(Page<TLdapShellTemp> page, Set<Long> set, String name) {
        return shellTempMapper.getPageByTypeSet(page, set, name);
    }
}




