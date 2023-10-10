package com.plzy.ldap.modules.admin.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.modules.admin.domain.TSysAdmin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.plzy.ldap.modules.admin.dto.SysAdminWithNameDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * @Entity com.plzy.ldap.modules.admin.domain.TSysAdmin
 */
@Mapper
public interface TSysAdminMapper extends BaseMapper<TSysAdmin> {

    IPage<SysAdminWithNameDTO> list(Page<TSysAdmin> page, TSysAdmin condition,@Param("domainIdSet") Set<Long> domainIdSet);
}




