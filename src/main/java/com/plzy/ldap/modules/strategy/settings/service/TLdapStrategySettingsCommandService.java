package com.plzy.ldap.modules.strategy.settings.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsCommand;
import com.baomidou.mybatisplus.extension.service.IService;
import com.plzy.ldap.modules.strategy.settings.dto.StrategySettingsCommandDto;

import java.util.List;
import java.util.Set;

/**
* @author root
* @description 针对表【t_ldap_strategy_settings_command】的数据库操作Service
* @createDate 2022-01-07 10:41:58
*/
public interface TLdapStrategySettingsCommandService extends IService<TLdapStrategySettingsCommand> {

    IPage<StrategySettingsCommandDto> getPageByTypeSet(Page page, Set<Long> typeSet,String name);

    List<StrategySettingsCommandDto> getCommandWithValues(Long strategyId);

    List<StrategySettingsCommandDto> listPubStrategay(Long strategyId);
}
