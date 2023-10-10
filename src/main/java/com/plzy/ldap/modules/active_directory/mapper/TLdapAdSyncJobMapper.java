package com.plzy.ldap.modules.active_directory.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.plzy.ldap.modules.active_directory.domain.TLdapAdSyncJob;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
* @author lixingbest
* @description 针对表【t_ldap_ad_sync_job(Ad同步任务表)】的数据库操作Mapper
* @createDate 2023-04-11 10:24:43
* @Entity com.plzy.ldap.modules.active_directory.domain.TLdapAdSyncJob
*/
public interface TLdapAdSyncJobMapper extends BaseMapper<TLdapAdSyncJob> {

    IPage<TLdapAdSyncJob> list(IPage<TLdapAdSyncJob> page, @Param("domainId") Long domainId, @Param("beginTime") Date beginTime, @Param("endTime") Date endTime, @Param("result") Integer result);
}




