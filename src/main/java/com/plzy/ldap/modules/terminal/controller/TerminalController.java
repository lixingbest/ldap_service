package com.plzy.ldap.modules.terminal.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.framework.utils.SecurityUtil;
import com.plzy.ldap.framework.utils.TreeDataUtil;
import com.plzy.ldap.modules.terminal.domain.TSysTerminal;
import com.plzy.ldap.modules.terminal.domain.TSysTerminalType;
import com.plzy.ldap.modules.terminal.service.TSysTerminalService;
import com.plzy.ldap.modules.terminal.service.TSysTerminalTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("terminal")
public class TerminalController {

    @Resource
    private TSysTerminalTypeService terminalTypeService;

    @Resource
    private TSysTerminalService terminalService;

    @GetMapping("getTypeTree")
    public ResponseEntity<ResponseData> getTerminalTypeTree() {

        List<TreeDataUtil> nodes = getTypeNodes();
        List<TreeDataUtil> tree = TreeDataUtil.getTree(nodes);
        return ResponseEntity.ok(ResponseData.success(tree));
    }

    @GetMapping("getTree")
    public ResponseEntity<ResponseData> getTree() {

        List<TSysTerminalType> types = terminalTypeService.list();

        ArrayList<TreeDataUtil> nodes = new ArrayList<>();

        for (TSysTerminalType item : types) {
            nodes.add(new TreeDataUtil(String.valueOf(item.getId()), String.valueOf(item.getPid()), item.getName(), null,item,"pi pi-inbox"));
        }

        List<TSysTerminal> list = terminalService.list();
        for (TSysTerminal item : list) {
            nodes.add(new TreeDataUtil("t" + item.getId(), String.valueOf(item.getTypeId()), item.getHostname() + "(" + item.getIpv4() + ")",null, item, "pi pi-desktop"));
        }
        return ResponseEntity.ok(ResponseData.success(TreeDataUtil.getTree(nodes)));
    }

    @PostMapping("addOrUpdateType")
    public ResponseEntity<ResponseData> addOrUpdateType(@RequestBody TSysTerminalType type) {
        terminalTypeService.saveOrUpdate(type);
        return ResponseEntity.ok(ResponseData.success());
    }

    @DeleteMapping("deleteType/{id}")
    public ResponseEntity<ResponseData> deleteType(@PathVariable Long id) {

        int size = terminalService.list(new LambdaQueryWrapper<TSysTerminal>().eq(TSysTerminal::getTypeId, id)).size();
        if (size == 0) {
            terminalTypeService.removeById(id);
            return ResponseEntity.ok(ResponseData.success());
        } else {
            return ResponseEntity.ok(ResponseData.error("123", "此类型包含内容，不能删除"));
        }
    }

    @GetMapping("hasTypeCode/{code}/{id}")
    public ResponseEntity<ResponseData> hasTypeCode(@PathVariable(value = "code", required = false) String code, @PathVariable(value = "id", required = false) String id) {
        LambdaQueryWrapper<TSysTerminalType> lambdaQueryWrapper;
        if (null == id) {
            lambdaQueryWrapper = new LambdaQueryWrapper<TSysTerminalType>().eq(TSysTerminalType::getCode, code);
        } else {
            lambdaQueryWrapper = new LambdaQueryWrapper<TSysTerminalType>().eq(TSysTerminalType::getCode, code).ne(TSysTerminalType::getId, id);
        }
        List<TSysTerminalType> list = terminalTypeService
                .list(lambdaQueryWrapper);
        return ResponseEntity.ok(ResponseData.success(list.size() > 0));
    }

    @PostMapping("addOrUpdate")
    public ResponseEntity<ResponseData> addOrUpdate(@RequestBody TSysTerminal terminal) {
        terminalService.saveOrUpdate(terminal);
        return ResponseEntity.ok(ResponseData.success());
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<ResponseData> delete(@PathVariable Long id) {
        terminalService.removeById(id);
        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("getPage")
    public ResponseEntity<ResponseData> getPage(Page<TSysTerminal> page, @RequestParam("typeId") Long typeId,
                                                @RequestParam(value = "param", required = false) String param) {

        List<TreeDataUtil> nodes = getTypeNodes();

        Set<String> set = TreeDataUtil.getChildrenIdSet(nodes, String.valueOf(typeId));

        HashSet<Long> ids = new HashSet<>();
        for (String str : set) {
            ids.add(Long.valueOf(str));
        }

        IPage<TSysTerminal> res = terminalService.getPage(page, ids, param);

        return ResponseEntity.ok(ResponseData.success(res));
    }
    @GetMapping("getList")
    public ResponseEntity<ResponseData> getList(@RequestParam("typeId") Long typeId,
                                                @RequestParam(value = "param", required = false) String param) {

        List<TreeDataUtil> nodes = getTypeNodes();

        Set<String> set = TreeDataUtil.getChildrenIdSet(nodes, String.valueOf(typeId));

        HashSet<Long> ids = new HashSet<>();
        for (String str : set) {
            ids.add(Long.valueOf(str));
        }

        List<TSysTerminal> res = terminalService.getList(ids, param);

        return ResponseEntity.ok(ResponseData.success(res));
    }

    @PostMapping("importTerminal")
    public ResponseEntity<ResponseData> importTerminal(MultipartFile file) {

        File tempFile = null;
        FileInputStream fileInputStream = null;
        try {
            tempFile = File.createTempFile(UUID.randomUUID().toString(), "xlsx");

            file.transferTo(tempFile);
            fileInputStream = new FileInputStream(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }
        List<TSysTerminal> terminalList = EasyExcel.read(fileInputStream).head(TSysTerminal.class).sheet().doReadSync();

        List<TSysTerminalType> list = terminalTypeService.list();
        HashMap<String, Long> map = new HashMap<>();
        for (TSysTerminalType type : list) {
            map.put(type.getName(), type.getId());
        }

        int total = terminalList.size();
        int remove = 0;
        Iterator<TSysTerminal> iterator = terminalList.iterator();
        while (iterator.hasNext()) {
            TSysTerminal terminal = iterator.next();
            //terminal.setPassword(SecurityUtil.MD5_16(terminal.getPassword()));

            String typeName = terminal.getTypeName();
            if (map.containsKey(typeName)) {
                terminal.setTypeId(map.get(typeName));
            } else {
                remove++;
                iterator.remove();
            }
        }
        terminalService.saveBatch(terminalList);
        HashMap<String, Object> res = new HashMap<>();
        res.put("total", total);
        res.put("remove", remove);

        return ResponseEntity.ok(ResponseData.success(res));
    }

    public List<TreeDataUtil> getTypeNodes() {

        List<TSysTerminalType> types = terminalTypeService.list();

        ArrayList<TreeDataUtil> nodes = new ArrayList<>();

        for (TSysTerminalType item : types) {
            nodes.add(new TreeDataUtil(String.valueOf(item.getId()), String.valueOf(item.getPid()), item.getName(), null,item));
        }
        return nodes;
    }
}
