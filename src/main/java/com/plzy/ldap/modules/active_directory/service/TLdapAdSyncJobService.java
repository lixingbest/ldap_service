package com.plzy.ldap.modules.active_directory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.plzy.ldap.modules.active_directory.domain.TLdapAdSyncJob;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;

/**
* @author lixingbest
* @description 针对表【t_ldap_ad_sync_job(Ad同步任务表)】的数据库操作Service
* @createDate 2023-04-11 10:24:43
*/
public interface TLdapAdSyncJobService extends IService<TLdapAdSyncJob> {

    IPage<TLdapAdSyncJob> list(IPage<TLdapAdSyncJob> page, Long domainId, Date beginTime, Date endTime, Integer result);
}
