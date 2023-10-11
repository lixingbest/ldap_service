package com.plzy.ldap.modules.active_directory.controller;

import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.modules.active_directory.service.ActiveDirectoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/active_directory")
@Slf4j
public class ActiveDirectoryController {

    @Autowired
    private ActiveDirectoryService activeDirectoryService;

    @GetMapping("/list")
    public ResponseEntity<ResponseData> list(Long domainId, String domainName){

        // 根域名不能点击
        if(domainId == 1L){
            return ResponseEntity.ok(ResponseData.success());
        }

        List<Map> result = activeDirectoryService.list(domainId,domainName);
        return ResponseEntity.ok(ResponseData.success(result));
    }

    @GetMapping("/settings")
    public ResponseEntity<ResponseData> settings(Long domainId){

        // 根域名不能点击
        if(domainId == 1L){
            return ResponseEntity.ok(ResponseData.success());
        }

        Map result = activeDirectoryService.settings(domainId);
        return ResponseEntity.ok(ResponseData.success(result));
    }
}
