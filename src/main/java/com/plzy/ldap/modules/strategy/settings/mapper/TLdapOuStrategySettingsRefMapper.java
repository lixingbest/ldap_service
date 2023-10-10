package com.plzy.ldap.modules.strategy.settings.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.modules.strategy.settings.domain.TLdapOuStrategySettingsRef;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsList;
import com.plzy.ldap.modules.strategy.settings.dto.LdapStrategySettingsDetails;
import com.plzy.ldap.modules.strategy.settings.dto.TLdapStrategySettingsListWithRefIdDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author root
* @description 针对表【t_ldap_ou_strategy_settings_ref(组织单位-策略设置关联表)】的数据库操作Mapper
* @createDate 2022-01-04 10:40:23
* @Entity com.plzy.ldap.modules.strategy.settings.domain.TLdapOuStrategySettingsRef
*/
@Mapper
public interface TLdapOuStrategySettingsRefMapper extends BaseMapper<TLdapOuStrategySettingsRef> {

    List<LdapStrategySettingsDetails> getByOuId(@Param("ouId") Long ouId);
}




