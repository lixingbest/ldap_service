package com.plzy.ldap.modules.active_directory.service;

import com.plzy.ldap.modules.active_directory.domain.TLdapAdSyncJobDetails;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.models.auth.In;

import java.util.List;
import java.util.Map;

/**
* @author lixingbest
* @description 针对表【t_ldap_ad_sync_job_details(Ad同步任务明细表)】的数据库操作Service
* @createDate 2023-04-11 10:24:43
*/
public interface TLdapAdSyncJobDetailsService extends IService<TLdapAdSyncJobDetails> {

    Map getStat(Long asSyncJobId);
}
