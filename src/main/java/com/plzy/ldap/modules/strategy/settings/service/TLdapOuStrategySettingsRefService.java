package com.plzy.ldap.modules.strategy.settings.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.modules.strategy.settings.domain.TLdapOuStrategySettingsRef;
import com.baomidou.mybatisplus.extension.service.IService;
import com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsList;
import com.plzy.ldap.modules.strategy.settings.dto.LdapStrategySettingsDetails;
import com.plzy.ldap.modules.strategy.settings.dto.TLdapStrategySettingsListWithRefIdDTO;

import java.util.List;

/**
* @author root
* @description 针对表【t_ldap_ou_strategy_settings_ref(组织单位-策略设置关联表)】的数据库操作Service
* @createDate 2022-01-04 10:40:23
*/
public interface TLdapOuStrategySettingsRefService extends IService<TLdapOuStrategySettingsRef> {

    List<LdapStrategySettingsDetails> getByOuId(Long ouId);
}
