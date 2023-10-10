package com.plzy.ldap.modules.active_directory.mapper;

import com.plzy.ldap.modules.active_directory.domain.TLdapAdSyncJobDetails;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author lixingbest
* @description 针对表【t_ldap_ad_sync_job_details(Ad同步任务明细表)】的数据库操作Mapper
* @createDate 2023-04-11 10:24:43
* @Entity com.plzy.ldap.modules.active_directory.domain.TLdapAdSyncJobDetails
*/
public interface TLdapAdSyncJobDetailsMapper extends BaseMapper<TLdapAdSyncJobDetails> {

    @MapKey("key")
    Map getStat(@Param("adSyncJobId") Long adSyncJobId);
}




