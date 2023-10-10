package com.plzy.ldap.modules.active_directory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.modules.active_directory.domain.TLdapAdSyncConfig;
import com.plzy.ldap.modules.active_directory.service.TLdapAdSyncConfigService;
import com.plzy.ldap.modules.active_directory.mapper.TLdapAdSyncConfigMapper;
import org.springframework.stereotype.Service;

/**
* @author lixingbest
* @description 针对表【t_ldap_ad_sync_config(AD域同步配置)】的数据库操作Service实现
* @createDate 2022-02-11 18:49:49
*/
@Service
public class TLdapAdSyncConfigServiceImpl extends ServiceImpl<TLdapAdSyncConfigMapper, TLdapAdSyncConfig>
    implements TLdapAdSyncConfigService{

}




