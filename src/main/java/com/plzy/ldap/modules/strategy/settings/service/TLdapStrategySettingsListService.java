package com.plzy.ldap.modules.strategy.settings.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.plzy.ldap.modules.ou.domain.TLdapOu;
import com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsList;
import com.plzy.ldap.modules.strategy.settings.dto.LdapStrategySettingsDetails;
import com.plzy.ldap.modules.strategy.settings.dto.LdapStrategySettingsListDTO;
import com.plzy.ldap.modules.strategy.settings.dto.StrategySettingOuDto;
import com.plzy.ldap.open_service.client_service.bean.StrategySettingsPublicServiceParams;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface TLdapStrategySettingsListService extends IService<TLdapStrategySettingsList> {

    void makeCache();

    List<LdapStrategySettingsListDTO> tree();

    List<LdapStrategySettingsListDTO> treeByDomain(Long domainId);

    Map<String, Object> execute(StrategySettingsPublicServiceParams params);

    void clearCache();

    List<LdapStrategySettingsDetails> get(Long domainId, String uid);

    IPage<StrategySettingOuDto> getAppliedOU(Page<StrategySettingOuDto> page,Long id);

    List<LdapStrategySettingsDetails> assignmentList(Long ouId);

    Map<String, Integer> statStrategy(Long domainId);
}
