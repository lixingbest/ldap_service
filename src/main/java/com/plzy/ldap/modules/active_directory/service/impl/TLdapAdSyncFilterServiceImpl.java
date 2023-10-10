package com.plzy.ldap.modules.active_directory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.modules.active_directory.domain.TLdapAdSyncFilter;
import com.plzy.ldap.modules.active_directory.service.TLdapAdSyncFilterService;
import com.plzy.ldap.modules.active_directory.mapper.TLdapAdSyncFilterMapper;
import org.springframework.stereotype.Service;

/**
* @author lixingbest
* @description 针对表【t_ldap_ad_sync_filter(ad同步过滤器)】的数据库操作Service实现
* @createDate 2022-02-21 16:02:46
*/
@Service
public class TLdapAdSyncFilterServiceImpl extends ServiceImpl<TLdapAdSyncFilterMapper, TLdapAdSyncFilter>
    implements TLdapAdSyncFilterService{

}




