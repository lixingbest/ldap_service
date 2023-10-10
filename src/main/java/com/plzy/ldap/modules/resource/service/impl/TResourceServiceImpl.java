package com.plzy.ldap.modules.resource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.plzy.ldap.modules.resource.domain.TResource;
import com.plzy.ldap.modules.resource.mapper.TResourceMapper;
import com.plzy.ldap.modules.resource.service.TResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class TResourceServiceImpl extends ServiceImpl<TResourceMapper, TResource>
        implements TResourceService {

    @Autowired
    private TResourceMapper resourceMapper;


    @Override
    public IPage<TResource> getList(Page<TResource> page, String name) {
        return resourceMapper.getList(page, name);
    }

    @Override
    public List<TResource> getListByRoleId(Long roleId) {
        return resourceMapper.getListByRoleId(roleId);
    }

    @Override
    public IPage<TResource> treeTable(Page<TResource> page, String name) {

        IPage<TResource> first = resourceMapper.getPageByPid(page, null,name);
        for (TResource res : first.getRecords()) {
            res.setChildren(getTree(res.getId()));
//            List<TResource> sc = resourceMapper.getListByPid(res.getId());
//            if (sc != null) {
//                res.setChildren(sc);
//
//                for(TResource second: sc){
//                    List<TResource> third = resourceMapper.getListByPid(second.getId());
//                    if(third != null){
//                        second.setChildren(third);
//                    }
//                }
//            }
        }
        return first;
    }

    @Override
    public List<TResource> getParentList() {
        return resourceMapper.getParentList();
    }

    @Override
    public List<TResource> getAll() {
        List<TResource> tree = getTree(null);
        return tree;
    }

    @Override
    public List<TResource> reloadResource(List<TResource> resources) {

        return  getTreeofList(null,resources);
    }

    @Override
    public List<TResource> getMenu(List<TResource> resources) {
        return getMenuTree(null,resources);
    }


    @Override
    public boolean addOrUpdate(TResource tResource) {
        if(tResource.getId() !=null){
            long count = baseMapper.selectList(new LambdaQueryWrapper<TResource>()
                    .ne(TResource::getId, tResource.getId())
                    .eq(TResource::getCode, tResource.getCode())
            ).stream().count();
            if(count>0){
                return false;
            }
            baseMapper.updateById(tResource);
        }else{
            long count = baseMapper.selectList(new LambdaQueryWrapper<TResource>()
                    .eq(TResource::getCode, tResource.getCode())
            ).stream().count();
            if(count>0){
                return false;
            }
            baseMapper.insert(tResource);
        }

        return true;
    }

    public List<TResource> getTree(Long pid){
        List<TResource> listByPid = resourceMapper.getListByPid(pid);
        if(!CollectionUtils.isEmpty(listByPid)){
            listByPid.sort(Comparator.comparingInt(TResource::getIdx));
            for (TResource tResource : listByPid) {
                List<TResource> tree = getTree(tResource.getId());
                if (!CollectionUtils.isEmpty(tree)){
                    tree.sort(Comparator.comparingInt(TResource::getIdx));
                    tResource.setChildren(tree);
                }
            }

            return listByPid;
        }
        return null;

    }
    public List<TResource> getTreeofList(Long pid,List<TResource> list){
        List<TResource> collect = null;
        if(pid ==null){
        collect = list.stream().filter(f -> f.getPid() == null).collect(Collectors.toList());
        }else{
          collect = list.stream().filter(f -> f.getPid() != null).filter(f -> f.getPid().equals(pid)).collect(Collectors.toList());
        }
        if(!CollectionUtils.isEmpty(collect)){
            for (TResource tResource : collect) {
                List<TResource> treeofList = getTreeofList(tResource.getId(), list);
                if(!CollectionUtils.isEmpty(treeofList)){
                    tResource.setChildren(treeofList);
                }
            }
            return collect;
        }
        return null;
    }
    public List<TResource> getMenuTree(Long pid,List<TResource> list){
        List<TResource> collect = null;
        if(pid ==null){
            collect = list.stream().filter(f -> f.getPid() == null).collect(Collectors.toList());
        }else{
            collect = list.stream().filter(f -> f.getPid() != null).filter(f -> f.getPid().equals(pid)).collect(Collectors.toList());
        }
        if(!CollectionUtils.isEmpty(collect)){
            for (TResource tResource : collect) {
                List<TResource> treeofList = getMenuTree(tResource.getId(), list);
                if(!CollectionUtils.isEmpty(treeofList)){
                    Optional<TResource> first = treeofList.stream().findFirst();
                    if(first.get().getType().equals(2)){
                        tResource.setButtons(treeofList);
                    }else{
                        tResource.setChildren(treeofList);
                    }

                }

            }
            return collect;
        }
        return null;
    }


}




