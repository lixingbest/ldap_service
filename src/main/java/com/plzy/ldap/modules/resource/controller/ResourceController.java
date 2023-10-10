package com.plzy.ldap.modules.resource.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.framework.utils.TreeDataUtil;
import com.plzy.ldap.modules.resource.domain.TResource;
import com.plzy.ldap.modules.resource.service.TResourceService;
import com.plzy.ldap.modules.role.domain.TRoleResRef;
import com.plzy.ldap.modules.role.service.TRoleResRefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/resource")
public class ResourceController {

    @Autowired
    private TResourceService resourceService;

    @Autowired
    private TRoleResRefService roleResRefService;

    @GetMapping("/getList")
    public ResponseEntity<ResponseData> getList(Page<TResource> page,
                                                @RequestParam(required = false, value = "name") String name) {
        IPage<TResource> tResourceIPage = resourceService.treeTable(page, name);
        ArrayList<Object> list = new ArrayList<>();
        for (TResource res : tResourceIPage.getRecords()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("key", res.getId());
            map.put("label", res.getName());
            map.put("data", res);

            ArrayList<Object> ch = new ArrayList<>();
            if (!CollectionUtils.isEmpty(res.getChildren())) {
                res.getChildren().forEach(item -> {
                    HashMap<String, Object> cmap = new HashMap<>();
                    cmap.put("key", item.getId());
                    cmap.put("label", item.getName());
                    cmap.put("data", item);
                    ch.add(cmap);
                    ArrayList<Object> ch3 = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(item.getChildren())) {
                        item.getChildren().forEach(item3 -> {
                            HashMap<String, Object> cmap3 = new HashMap<>();
                            cmap3.put("key", item3.getId());
                            cmap3.put("label", item3.getName());
                            cmap3.put("data", item3);
                            ch3.add(cmap3);
                            ArrayList<Object> ch4 = new ArrayList<>();
                            if (!CollectionUtils.isEmpty(item3.getChildren())) {
                                item3.getChildren().forEach(item4 -> {
                                    HashMap<String, Object> cmap4 = new HashMap<>();
                                    cmap4.put("key", item4.getId());
                                    cmap4.put("label", item4.getName());
                                    cmap4.put("data", item4);
                                    ch4.add(cmap4);
                                });
                            }
                            cmap3.put("children", ch4);
                        });

                    }
                    cmap.put("children", ch3);
                });

            }
            map.put("children", ch);
            list.add(map);
        }
        HashMap<String, Object> result = new HashMap<>();
        result.put("records", list);
        result.put("total", tResourceIPage.getTotal());
        return ResponseEntity.ok(ResponseData.success(result));
    }

    @GetMapping("all")
    public ResponseEntity<ResponseData> all() {
        List<TResource> list = resourceService.list();

        List<TreeDataUtil> nodes = new ArrayList<>();
        for (TResource resource : list) {
            nodes.add(new TreeDataUtil(String.valueOf(resource.getId()), String.valueOf(resource.getPid()), resource.getName(), null, resource));
        }
        List<TreeDataUtil> tree = TreeDataUtil.getTree(nodes);
        return ResponseEntity.ok(ResponseData.success(tree));
    }

    @GetMapping("/getListByAddOrUpdate")
    public ResponseEntity<ResponseData> getListByAddOrUpdate() {
        List<TResource> all = resourceService.getAll();
        ArrayList<Object> list = new ArrayList<>();
        for (TResource res : all) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("key", res.getId());
            map.put("label", res.getName());
            map.put("data", res);

            ArrayList<Object> ch = new ArrayList<>();
            if (!CollectionUtils.isEmpty(res.getChildren())) {

                res.getChildren().stream().filter(f -> !f.getType().equals(2)).forEach(item -> {
                    HashMap<String, Object> cmap = new HashMap<>();
                    cmap.put("key", item.getId());
                    cmap.put("label", item.getName());
                    cmap.put("data", item);
                    ch.add(cmap);
                    ArrayList<Object> ch3 = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(item.getChildren())) {
                        item.getChildren().stream().filter(f -> !f.getType().equals(2)).forEach(item3 -> {
                            HashMap<String, Object> cmap3 = new HashMap<>();
                            cmap3.put("key", item3.getId());
                            cmap3.put("label", item3.getName());
                            cmap3.put("data", item3);
                            ch3.add(cmap3);
                            ArrayList<Object> ch4 = new ArrayList<>();
                            if (!CollectionUtils.isEmpty(item3.getChildren())) {
                                item3.getChildren().stream().filter(f -> !f.getType().equals(2)).forEach(item4 -> {
                                    HashMap<String, Object> cmap4 = new HashMap<>();
                                    cmap4.put("key", item4.getId());
                                    cmap4.put("label", item4.getName());
                                    cmap4.put("data", item4);
                                    ch4.add(cmap4);
                                });
                            }
                            cmap3.put("children", ch4);
                        });

                    }
                    cmap.put("children", ch3);
                });

            }
            map.put("children", ch);
            list.add(map);
        }
        HashMap<String, Object> result = new HashMap<>();
        result.put("records", list);
        return ResponseEntity.ok(ResponseData.success(result));
    }

    @DeleteMapping("/del/{id}")
    public ResponseEntity<ResponseData> delete(@PathVariable String id) {
        roleResRefService.remove(new LambdaQueryWrapper<TRoleResRef>().eq(TRoleResRef::getResId, id));
        resourceService.removeById(id);
        return ResponseEntity.ok(ResponseData.success());
    }

    @PostMapping("/addOrUpdate")
    public ResponseEntity<ResponseData> addOrUpdate(@RequestBody TResource tResource) {
        boolean b = resourceService.addOrUpdate(tResource);
        if (b) {
            return ResponseEntity.ok(ResponseData.success());
        } else {
            return ResponseEntity.ok(ResponseData.error("999", "编号重复"));
        }

    }

    @GetMapping("/getAllOfType")
    public ResponseEntity<ResponseData> getAllOfType(@RequestParam(value = "type") Integer type) {
        return ResponseEntity.ok(ResponseData.success(resourceService.list(new LambdaQueryWrapper<TResource>().eq(TResource::getType, type))));
    }

    @GetMapping("/getAll")
    public ResponseEntity<ResponseData> getAllOfType() {

        List<TResource> list = resourceService.getAll();
        return ResponseEntity.ok(ResponseData.success(list));
    }

}
