package com.plzy.ldap.modules.strategy.settings.dto;

import com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsCommand;
import com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsList;
import lombok.Data;

import java.util.List;

@Data
public class LdapStrategySettingsDetails extends TLdapStrategySettingsList {

    private List<StrategySettingsCommandDto> commands;

    /**
     * 策略是否启用，0启用，1禁用
     */
    private Integer enable;

    /**
     * 保存组策略ref表的主键id
     */
    private Long refid;

    private String extendOU;

    private String domainDn;
}
