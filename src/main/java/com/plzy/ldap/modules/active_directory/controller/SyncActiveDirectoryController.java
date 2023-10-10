package com.plzy.ldap.modules.active_directory.controller;

import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.modules.active_directory.service.SyncActiveDirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sync_active_directory")
public class SyncActiveDirectoryController {

    @Autowired
    private SyncActiveDirectoryService syncActiveDirectoryService;

    @GetMapping("/syncOU")
    public ResponseEntity<ResponseData> syncOU(Long domainId){

        if(domainId == null){
            return ResponseEntity.ok(ResponseData.error("999999","domainId不能为空！"));
        }

        String log = syncActiveDirectoryService.syncOU(domainId, false);
        return ResponseEntity.ok(ResponseData.success(log));
    }

    @GetMapping("/syncDomainUser")
    public ResponseEntity<ResponseData> syncDomainUser(Long ouId){

        if(ouId == null){
            return ResponseEntity.ok(ResponseData.error("999999","ouId不能为空！"));
        }

        String log = syncActiveDirectoryService.syncDomainUser(ouId,null);
        return ResponseEntity.ok(ResponseData.success(log));
    }

    @GetMapping("/syncAll")
    public ResponseEntity<ResponseData> syncAll(Long domainId){

        if(domainId == null){
            return ResponseEntity.ok(ResponseData.error("999999","domainId不能为空！"));
        }

        String log = syncActiveDirectoryService.syncAll(domainId);
        return ResponseEntity.ok(ResponseData.success(log));
    }
}
