package com.plzy.ldap.modules.shellTemp.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.plzy.ldap.modules.shellTemp.domain.TLdapShellTemp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Set;

/**
 * @Entity com.plzy.ldap.modules.shellTemp.domain.TLdapShellTemp
 */
public interface TLdapShellTempMapper extends BaseMapper<TLdapShellTemp> {

    IPage<TLdapShellTemp> getPageByTypeSet(Page<TLdapShellTemp> page, @Param("typeIdSet") Set<Long> set, @Param("name") String name);
}




