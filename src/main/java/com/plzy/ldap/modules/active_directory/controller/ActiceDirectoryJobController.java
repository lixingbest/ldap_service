package com.plzy.ldap.modules.active_directory.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.modules.active_directory.domain.TLdapAdSyncJob;
import com.plzy.ldap.modules.active_directory.domain.TLdapAdSyncJobDetails;
import com.plzy.ldap.modules.active_directory.service.TLdapAdSyncJobDetailsService;
import com.plzy.ldap.modules.active_directory.service.TLdapAdSyncJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
@RequestMapping("/active_directory/job")
public class ActiceDirectoryJobController {

    @Autowired
    private TLdapAdSyncJobService jobService;

    @Autowired
    private TLdapAdSyncJobDetailsService jobDetailsService;

    @GetMapping("/list")
    public ResponseEntity<ResponseData> list(Page<TLdapAdSyncJob> page, Long domainId, Long beginTime, Long endTime, Integer result){

        Date begin = null;
        Date end = null;
        if(beginTime != null){
            begin = new Date(beginTime);
        }
        if(endTime != null){
            end = new Date(endTime);
        }
        IPage<TLdapAdSyncJob> list = jobService.list(page,domainId, begin,  end,  result);
        return ResponseEntity.ok(ResponseData.success(list));
    }

    @GetMapping("/getLog")
    public ResponseEntity<ResponseData> getLog(Long jobId){

        TLdapAdSyncJob job = jobService.getById(jobId);
        return ResponseEntity.ok(ResponseData.success(job.getLog()));
    }

    @GetMapping("/details")
    public ResponseEntity<ResponseData> details(Page<TLdapAdSyncJobDetails> page, Long jobId, Integer type,String name, Integer utype){

        LambdaQueryWrapper<TLdapAdSyncJobDetails> cond = new LambdaQueryWrapper<TLdapAdSyncJobDetails>().eq(TLdapAdSyncJobDetails::getAdSyncJobId,jobId).eq(TLdapAdSyncJobDetails::getType,type);
        if(StringUtils.hasText(name)){
            cond.like(TLdapAdSyncJobDetails::getName, name);
        }
        if(utype != null){
            cond.eq(TLdapAdSyncJobDetails::getUpdateType, utype);
        }

        IPage<TLdapAdSyncJobDetails> detailsIPage = jobDetailsService.page(page, cond);
        return ResponseEntity.ok(ResponseData.success(detailsIPage));
    }
}
