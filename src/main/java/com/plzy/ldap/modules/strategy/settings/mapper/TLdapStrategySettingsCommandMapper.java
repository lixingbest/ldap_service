package com.plzy.ldap.modules.strategy.settings.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsCommand;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.plzy.ldap.modules.strategy.settings.dto.StrategySettingsCommandDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
* @author root
* @description 针对表【t_ldap_strategy_settings_command】的数据库操作Mapper
* @createDate 2022-01-07 10:41:58
* @Entity com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsCommand
*/
@Mapper
public interface TLdapStrategySettingsCommandMapper extends BaseMapper<TLdapStrategySettingsCommand> {

    IPage<StrategySettingsCommandDto> getPageByTypeSet(Page page, @Param("typeSet") Set<Long> typeSet,@Param("name") String name);

    List<StrategySettingsCommandDto> getCommandWithValues(@Param("strategyId") Long strategyId);

    List<StrategySettingsCommandDto> listPubStrategay(@Param("strategyId") Long strategyId);
}




