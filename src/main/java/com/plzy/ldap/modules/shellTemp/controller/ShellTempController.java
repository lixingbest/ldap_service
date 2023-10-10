package com.plzy.ldap.modules.shellTemp.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.framework.utils.TreeDataUtil;
import com.plzy.ldap.modules.shellTemp.domain.TLdapShellTemp;
import com.plzy.ldap.modules.shellTemp.domain.TLdapShellTempType;
import com.plzy.ldap.modules.shellTemp.service.TLdapShellTempService;
import com.plzy.ldap.modules.shellTemp.service.TLdapShellTempTypeService;
import com.plzy.ldap.modules.strategy.settings.dto.StrategySettingsCommandDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("shellTemp")
public class ShellTempController {

    @Autowired
    private TLdapShellTempService shellTempService;
    @Autowired
    private TLdapShellTempTypeService shellTempTypeService;

    @PostMapping("/addOrUpdateType")
    public ResponseEntity<ResponseData> addOrUpdateType(@RequestBody TLdapShellTempType data) {
        if (null != data.getId()) {
            List<TLdapShellTempType> list = shellTempTypeService.list(new LambdaQueryWrapper<TLdapShellTempType>().eq(TLdapShellTempType::getCode, data.getCode()));
            if (list.size() > 0) {
                return ResponseEntity.ok(ResponseData.error("123456", "code已存在"));
            }
        }
        return ResponseEntity.ok(ResponseData.success(shellTempTypeService.saveOrUpdate(data)));
    }

    @GetMapping("/getTree")
    public ResponseEntity<ResponseData> getTree() {

        List<TreeDataUtil> nodes = getTypeNodes();

        return ResponseEntity.ok(ResponseData.success(TreeDataUtil.getTree(nodes)));
    }

    @DeleteMapping("/deleteType/{id}")
    public ResponseEntity<ResponseData> deleteType(@PathVariable Long id) {

        List<TLdapShellTemp> list = shellTempService.list(new LambdaQueryWrapper<TLdapShellTemp>().eq(TLdapShellTemp::getPid, id));
        if (list.size() == 0) {
            shellTempTypeService.removeById(id);
            return ResponseEntity.ok(ResponseData.success());
        } else {
            return ResponseEntity.ok(ResponseData.error("123456", "存在子项不能删除"));
        }
    }

    @GetMapping("hasTypeCode/{code}/{id}")
    public ResponseEntity<ResponseData> hasTypeCode(@PathVariable(value = "code", required = false) String code,
                                                    @PathVariable(value = "id", required = false) String id) {
        LambdaQueryWrapper<TLdapShellTempType> lambdaQueryWrapper;
        if (null == id) {
            lambdaQueryWrapper = new LambdaQueryWrapper<TLdapShellTempType>().eq(TLdapShellTempType::getCode, code);
        } else {
            lambdaQueryWrapper = new LambdaQueryWrapper<TLdapShellTempType>().eq(TLdapShellTempType::getCode, code).ne(TLdapShellTempType::getId, id);
        }
        List<TLdapShellTempType> list = shellTempTypeService
                .list(lambdaQueryWrapper);
        return ResponseEntity.ok(ResponseData.success(list.size() > 0));
    }

    @GetMapping("getTempPage")
    public ResponseEntity<ResponseData> getTempPage(Page<TLdapShellTemp> page, @RequestParam("typeId") Long typeId, @RequestParam(value = "name", required = false) String name) {

        List<TreeDataUtil> nodes = getTypeNodes();

        Set<String> childrenIdSet = TreeDataUtil.getChildrenIdSet(nodes, String.valueOf(typeId));

        Set<Long> set = TreeDataUtil.toLongSet(childrenIdSet);

        IPage<TLdapShellTemp> pageByTypeSet = shellTempService.getPageByTypeSet(page, set, name);

        return ResponseEntity.ok(ResponseData.success(pageByTypeSet));
    }

    @DeleteMapping("deleteById/{id}")
    @Transactional
    public ResponseEntity<ResponseData> deleteById(@PathVariable Long id) {

        shellTempService.removeById(id);

        return ResponseEntity.ok(ResponseData.success());
    }

    @PostMapping("addOrUpdate")
    public ResponseEntity<ResponseData> addOrUpdate(@RequestHeader("token") String token, @RequestBody TLdapShellTemp data) {

        shellTempService.saveOrUpdate(data);

        return ResponseEntity.ok(ResponseData.success());
    }


    public List<TreeDataUtil> getTypeNodes() {
        List<TLdapShellTempType> list = shellTempTypeService.list();

        List<TreeDataUtil> nodes = new ArrayList<>();
        for (TLdapShellTempType item : list) {
            nodes.add(new TreeDataUtil(String.valueOf(item.getId()), String.valueOf(item.getPid()), item.getName(),item.getName(), item));
        }
        return nodes;
    }
}
