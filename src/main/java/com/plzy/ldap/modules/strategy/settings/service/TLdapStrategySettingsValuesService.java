package com.plzy.ldap.modules.strategy.settings.service;

import com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsValues;
import com.baomidou.mybatisplus.extension.service.IService;
import com.plzy.ldap.modules.strategy.settings.dto.TLdapStrategySettingsValuesDTO;

import java.util.List;

/**
* @author root
* @description 针对表【t_ldap_strategy_settings_values】的数据库操作Service
* @createDate 2022-01-03 13:05:02
*/
public interface TLdapStrategySettingsValuesService extends IService<TLdapStrategySettingsValues> {

    List<TLdapStrategySettingsValues> getConfig(Long ouId);

    List<TLdapStrategySettingsValuesDTO> getValues(Long strategyId);
}
