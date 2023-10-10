package com.plzy.ldap.modules.strategy.settings.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsValues;
import com.plzy.ldap.modules.strategy.settings.dto.TLdapStrategySettingsValuesDTO;
import com.plzy.ldap.modules.strategy.settings.service.TLdapStrategySettingsValuesService;
import com.plzy.ldap.modules.strategy.settings.mapper.TLdapStrategySettingsValuesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author root
* @description 针对表【t_ldap_strategy_settings_values】的数据库操作Service实现
* @createDate 2022-01-03 13:05:02
*/
@Service
public class TLdapStrategySettingsValuesServiceImpl extends ServiceImpl<TLdapStrategySettingsValuesMapper, TLdapStrategySettingsValues>
    implements TLdapStrategySettingsValuesService{

    @Autowired
    private TLdapStrategySettingsValuesMapper strategySettingsValuesMapper;

    @Override
    public List<TLdapStrategySettingsValues> getConfig(Long ouId) {
        return strategySettingsValuesMapper.getConfig(ouId);
    }

    @Override
    public List<TLdapStrategySettingsValuesDTO> getValues(Long strategyId) {
        return strategySettingsValuesMapper.getValues(strategyId);
    }
}




