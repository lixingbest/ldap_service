package com.plzy.ldap.modules.strategy.settings.mapper;

import com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsValues;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.plzy.ldap.modules.strategy.settings.dto.TLdapStrategySettingsValuesDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author root
* @description 针对表【t_ldap_strategy_settings_values】的数据库操作Mapper
* @createDate 2022-01-03 13:05:02
* @Entity com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsValues
*/
@Mapper
public interface TLdapStrategySettingsValuesMapper extends BaseMapper<TLdapStrategySettingsValues> {

    List<TLdapStrategySettingsValues> getConfig(@Param("ouId") Long ouId);

    List<TLdapStrategySettingsValuesDTO> getValues(@Param("strategyId") Long strategyId);
}




