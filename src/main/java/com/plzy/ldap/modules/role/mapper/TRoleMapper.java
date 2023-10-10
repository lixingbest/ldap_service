package com.plzy.ldap.modules.role.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.modules.role.domain.TRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Entity com.ijc.modules.role.domain.TRole
 */
@Mapper
public interface TRoleMapper extends BaseMapper<TRole> {


    IPage<TRole> getList(Page<TRole> page, @Param("name") String name, @Param("enable") Byte enable);
}




