package com.plzy.ldap.modules.active_directory.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.modules.active_directory.domain.TLdapAdSyncJob;
import com.plzy.ldap.modules.active_directory.mapper.TLdapAdSyncJobDetailsMapper;
import com.plzy.ldap.modules.active_directory.service.TLdapAdSyncJobDetailsService;
import com.plzy.ldap.modules.active_directory.service.TLdapAdSyncJobService;
import com.plzy.ldap.modules.active_directory.mapper.TLdapAdSyncJobMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author lixingbest
* @description 针对表【t_ldap_ad_sync_job(Ad同步任务表)】的数据库操作Service实现
* @createDate 2023-04-11 10:24:43
*/
@Service
public class TLdapAdSyncJobServiceImpl extends ServiceImpl<TLdapAdSyncJobMapper, TLdapAdSyncJob>
    implements TLdapAdSyncJobService{

    @Autowired
    private TLdapAdSyncJobMapper adSyncJobMapper;

    @Autowired
    private TLdapAdSyncJobDetailsService jobDetailsService;

    @Override
    public IPage<TLdapAdSyncJob> list(IPage<TLdapAdSyncJob> page, Long domainId, Date beginTime, Date endTime, Integer result) {
        IPage<TLdapAdSyncJob> data = adSyncJobMapper.list(page,domainId, beginTime,  endTime,  result);
        // 统计新增、更新的纪录数量
        for(TLdapAdSyncJob job : data.getRecords()){
            Map rec = jobDetailsService.getStat(job.getId());
            if(rec.containsKey("0-0")) {
                job.setAddNum4OU(Integer.parseInt(((Map) rec.get("0-0")).get("value")+""));
            }else {
                job.setAddNum4OU(0);
            }
            if(rec.containsKey("0-1")) {
                job.setUpdateNum4OU(Integer.parseInt(((Map) rec.get("0-1")).get("value")+""));
            }else {
                job.setUpdateNum4OU(0);
            }

            if(rec.containsKey("1-0")) {
                job.setAddNum4User(Integer.parseInt(((Map) rec.get("1-0")).get("value")+""));
            }else {
                job.setAddNum4User(0);
            }
            if(rec.containsKey("1-1")) {
                job.setUpdateNum4User(Integer.parseInt(((Map) rec.get("1-1")).get("value")+""));
            }else {
                job.setUpdateNum4User(0);
            }
        }
        return data;
    }
}




