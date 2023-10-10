package com.plzy.ldap.modules.sys_log.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.sys_log.domain.TSysLog;
import com.plzy.ldap.modules.sys_log.dto.SysLogDto;
import com.plzy.ldap.modules.sys_log.service.TSysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/sys_log")
public class SysLogController {

    @Autowired
    private TSysLogService logService;

    @Autowired
    private TLdapDomainService domainService;

    @GetMapping("/list")
    public ResponseEntity<ResponseData> list(Page<TSysLog> page, SysLogDto sysLogDto){

        if(sysLogDto.getDomainId() == null  || sysLogDto.getDomainId() == 1L){

            List<TSysLog> total = new ArrayList<>();
            for(TLdapDomain domain : domainService.listSubdomain()){
                Page<TSysLog> result = logService.list(page,domain.getId(),sysLogDto.getJobNo(),  sysLogDto.getBeginDate(),  sysLogDto.getEndDate(),sysLogDto.getMenu(),sysLogDto.getMessage());
                total.addAll(result.getRecords());
            }
            return ResponseEntity.ok(ResponseData.success(total));
        }else {
            Page<TSysLog> result = logService.list(page,sysLogDto.getDomainId(), sysLogDto.getJobNo(),  sysLogDto.getBeginDate(),  sysLogDto.getEndDate(),sysLogDto.getMenu(),sysLogDto.getMessage());
            return ResponseEntity.ok(ResponseData.success(result));
        }
    }
}
