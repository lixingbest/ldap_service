package com.plzy.ldap.modules.strategy.settings.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsCommand;
import com.plzy.ldap.modules.strategy.settings.dto.StrategySettingsCommandDto;
import com.plzy.ldap.modules.strategy.settings.service.TLdapStrategySettingsCommandService;
import com.plzy.ldap.modules.strategy.settings.mapper.TLdapStrategySettingsCommandMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
* @author root
* @description 针对表【t_ldap_strategy_settings_command】的数据库操作Service实现
* @createDate 2022-01-07 10:41:58
*/
@Service
public class TLdapStrategySettingsCommandServiceImpl extends ServiceImpl<TLdapStrategySettingsCommandMapper, TLdapStrategySettingsCommand>
    implements TLdapStrategySettingsCommandService{

    @Resource
    private TLdapStrategySettingsCommandMapper commandMapper;

    @Override
    public IPage<StrategySettingsCommandDto> getPageByTypeSet(Page page, Set<Long> typeSet,String name) {
        return commandMapper.getPageByTypeSet(page, typeSet,name);
    }

    @Override
    public List<StrategySettingsCommandDto> getCommandWithValues(Long strategyId) {
        return commandMapper.getCommandWithValues(strategyId);
    }

    @Override
    public List<StrategySettingsCommandDto> listPubStrategay(Long strategyId) {
        return commandMapper.listPubStrategay(strategyId);
    }
}

