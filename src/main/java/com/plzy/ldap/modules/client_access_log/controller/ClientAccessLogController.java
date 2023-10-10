package com.plzy.ldap.modules.client_access_log.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.modules.client.service.TLdapClientInstLogService;
import com.plzy.ldap.modules.client_access_log.domain.TLdapClientAccessLog;
import com.plzy.ldap.modules.client_access_log.dto.LdapClientAccessLogDTO;
import com.plzy.ldap.modules.client_access_log.service.TLdapClientAccessLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/client_access_log")
public class ClientAccessLogController {

    @Autowired
    private TLdapClientAccessLogService clientInstLogService;

    @GetMapping("/list")
    public ResponseEntity<ResponseData> list(Page<LdapClientAccessLogDTO> page, Long domainId, String uid, String userName, Long startTime, Long endTime, String level,
                                             String action,String ip,String hostname,String sysName){

        Date start = null;
        if(startTime != null){
            start = new Date(startTime);
        }
        Date end = null;
        if(endTime != null){
            end = new Date(endTime);
        }
        IPage<LdapClientAccessLogDTO> list = clientInstLogService.list(page,domainId,uid,userName,start,end,level,action,ip,hostname,sysName);
        return ResponseEntity.ok(ResponseData.success(list));
    }
}
