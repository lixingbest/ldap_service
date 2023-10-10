package com.plzy.ldap.modules.resource.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.plzy.ldap.modules.resource.domain.TResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.ijc.modules.resource.domain.TResource
 */
@Mapper
public interface TResourceMapper extends BaseMapper<TResource> {

    IPage<TResource> getList(Page<TResource> page ,@Param("name") String name);

    List<TResource> getListByRoleId(@Param("roleId") Long roleId);

    List<TResource> getListByPid(@Param("pid")Long pid);

    IPage<TResource> getPageByPid(Page<TResource> page,@Param("pid")Long pid,@Param("name")String name);

    List<TResource> getParentList();
}




