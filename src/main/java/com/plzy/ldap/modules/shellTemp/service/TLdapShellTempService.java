package com.plzy.ldap.modules.shellTemp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.plzy.ldap.modules.shellTemp.domain.TLdapShellTemp;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Set;

/**
 *
 */
public interface TLdapShellTempService extends IService<TLdapShellTemp> {

    IPage<TLdapShellTemp> getPageByTypeSet(Page<TLdapShellTemp> page, Set<Long> set,String name);
}
