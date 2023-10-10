package com.plzy.ldap.modules.active_directory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.modules.active_directory.domain.TLdapAdSyncJobDetails;
import com.plzy.ldap.modules.active_directory.service.TLdapAdSyncJobDetailsService;
import com.plzy.ldap.modules.active_directory.mapper.TLdapAdSyncJobDetailsMapper;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* @author lixingbest
* @description 针对表【t_ldap_ad_sync_job_details(Ad同步任务明细表)】的数据库操作Service实现
* @createDate 2023-04-11 10:24:43
*/
@Service
public class TLdapAdSyncJobDetailsServiceImpl extends ServiceImpl<TLdapAdSyncJobDetailsMapper, TLdapAdSyncJobDetails>
    implements TLdapAdSyncJobDetailsService{

    @Autowired
    private TLdapAdSyncJobDetailsMapper jobDetailsMapper;

    @Override
    public Map getStat(Long asSyncJobId) {

        return jobDetailsMapper.getStat(asSyncJobId);
    }
}




