package com.plzy.ldap.modules.role.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.modules.resource.domain.TResource;
import com.plzy.ldap.modules.resource.service.TResourceService;
import com.plzy.ldap.modules.role.domain.TRole;
import com.plzy.ldap.modules.role.domain.TRoleResRef;
import com.plzy.ldap.modules.role.domain.TRoleUserRef;
import com.plzy.ldap.modules.role.dto.AddMenuToRoleDto;
import com.plzy.ldap.modules.role.service.TRoleResRefService;
import com.plzy.ldap.modules.role.service.TRoleService;
import com.plzy.ldap.modules.role.service.TRoleUserRefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;


@Controller
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private TRoleService roleService;
    @Autowired
    private TRoleUserRefService roleUserRefService;
    @Autowired
    private TRoleResRefService roleResRefService;
    @Autowired
    private TResourceService resourceService;

    @GetMapping("/getList")
    public ResponseEntity<ResponseData> getList(Page<TRole> page,
                                                @RequestParam(required = false, value = "name") String name,
                                                @RequestParam(required = false, value = "enable") Byte enable) {
        return ResponseEntity.ok(ResponseData.success(roleService.getList(page, name, enable)));
    }

    @DeleteMapping("/del/{id}")
    public ResponseEntity<ResponseData> delete(@PathVariable Long id) {
        roleResRefService.remove(new LambdaQueryWrapper<TRoleResRef>().eq(TRoleResRef::getRoleId, id));
        roleUserRefService.remove(new LambdaQueryWrapper<TRoleUserRef>().eq(TRoleUserRef::getRoleId, id));
        roleService.removeById(id);
        return ResponseEntity.ok(ResponseData.success());
    }

    @PostMapping("/addOrUpdate")
    public ResponseEntity<ResponseData> addOrUpdate(@RequestBody TRole role) {
        roleService.saveOrUpdate(role);
        return ResponseEntity.ok(ResponseData.success());
    }

    @PostMapping("/addOrUpdateMenuToRole")
    public ResponseEntity<ResponseData> addMenuToRole(@RequestBody AddMenuToRoleDto addMenuToRoleDto) {
        Long roleId = addMenuToRoleDto.getRoleId();
        roleResRefService.remove(new LambdaQueryWrapper<TRoleResRef>().eq(TRoleResRef::getRoleId, addMenuToRoleDto.getRoleId()));
        addMenuToRoleDto.getMenuIds().forEach(menuId -> {
            TRoleResRef tRoleResRef = new TRoleResRef();
            tRoleResRef.setRoleId(roleId);
            tRoleResRef.setResId(menuId);
            roleResRefService.save(tRoleResRef);
        });
        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseData> getAll() {
        List<TRole> list = roleService.list();
        return ResponseEntity.ok(ResponseData.success(list));
    }

    @GetMapping("/getMenuListByRoleId/{id}")
    public ResponseEntity<ResponseData> getMenuListByRoleId(@PathVariable Long id) {
        List<TRoleResRef> selectedList = roleResRefService.list(new LambdaQueryWrapper<TRoleResRef>().eq(TRoleResRef::getRoleId, id));
        List<TResource> all = resourceService.getAll();
        HashMap<String, Object> res = new HashMap<>();
        res.put("allList", all);
        res.put("selectedList", selectedList);
        return ResponseEntity.ok(ResponseData.success(res));
    }

    @GetMapping("/hasRoleCode/{code}")
    public ResponseEntity<ResponseData> hasRoleCode(@PathVariable String code) {
        TRole one = roleService.getOne(new LambdaQueryWrapper<TRole>().eq(TRole::getCode, code));

        return ResponseEntity.ok(ResponseData.success(one != null));

    }

}
