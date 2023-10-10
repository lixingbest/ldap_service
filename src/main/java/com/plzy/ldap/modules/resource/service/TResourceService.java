package com.plzy.ldap.modules.resource.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.plzy.ldap.modules.resource.domain.TResource;

import java.util.List;

/**
 *
 */
public interface TResourceService extends IService<TResource> {

    IPage<TResource> getList(Page<TResource> page ,String name);

    List<TResource> getListByRoleId(Long roleId);

    IPage<TResource> treeTable(Page<TResource> page ,String name);

    List<TResource> getParentList();

    List<TResource> getAll();
    List<TResource> reloadResource(List<TResource> resources);
    List<TResource> getMenu(List<TResource> resources);

    boolean addOrUpdate(TResource tResource);
}
