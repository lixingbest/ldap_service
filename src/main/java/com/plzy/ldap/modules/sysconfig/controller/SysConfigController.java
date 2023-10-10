package com.plzy.ldap.modules.sysconfig.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.modules.sysconfig.domain.TSysConf;
import com.plzy.ldap.modules.sysconfig.dto.SysConfigDto;
import com.plzy.ldap.modules.sysconfig.service.TSysConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("sysConfig")
public class SysConfigController {

    @Autowired
    private TSysConfService sysConfService;

    @GetMapping("getList")
    public ResponseEntity<ResponseData> getList() {
        List<TSysConf> list = sysConfService.list();
        return ResponseEntity.ok(ResponseData.success(list));
    }

    @PostMapping("update")
    @Transactional
    public ResponseEntity<ResponseData> update(@RequestBody List<TSysConf> items) {
        for (TSysConf item : items) {
            if (null != item.getValue() && !item.getValue().equals("")) {
                sysConfService.update(new LambdaUpdateWrapper<TSysConf>().eq(TSysConf::getName, item.getName()).set(TSysConf::getValue, item.getValue()));
            }
        }
        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("get/{key}")
    public ResponseEntity<ResponseData> get(@PathVariable String key) {
        try {
            String value = sysConfService.getOne(new LambdaQueryWrapper<TSysConf>().eq(TSysConf::getName, key)).getValue();
            return ResponseEntity.ok(ResponseData.success(value));
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseData.error("12399", e.getMessage()));
        }

    }

}
