package com.plzy.ldap.modules.strategy.settings.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.modules.ou.domain.TLdapOu;
import com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsList;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.plzy.ldap.modules.strategy.settings.dto.LdapStrategySettingsDetails;
import com.plzy.ldap.modules.strategy.settings.dto.LdapStrategySettingsListDTO;
import com.plzy.ldap.modules.strategy.settings.dto.StrategySettingOuDto;
import com.plzy.ldap.modules.strategy.settings.dto.StrategySettingsCommandDto;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Entity com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsList
 */
@Mapper
public interface TLdapStrategySettingsListMapper extends BaseMapper<TLdapStrategySettingsList> {

    List<LdapStrategySettingsListDTO> list();

    List<LdapStrategySettingsListDTO> treeByDomain(@Param("domainId") Long domainId);

    IPage<StrategySettingOuDto> getAppliedOU(Page<StrategySettingOuDto> page, @Param("id") Long id);

    List<LdapStrategySettingsDetails> listByOUId(@Param("ouId") Long ouId);

    List<StrategySettingsCommandDto> listCommands(@Param("strategyId") Long strategyId);

    @MapKey("type")
    Map<String, Integer> statStrategy(@Param("domainId") Long domainId);
}




