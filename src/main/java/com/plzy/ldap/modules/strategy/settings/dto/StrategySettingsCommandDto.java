package com.plzy.ldap.modules.strategy.settings.dto;

import com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsCommand;
import lombok.Data;

@Data
public class StrategySettingsCommandDto extends TLdapStrategySettingsCommand {

    //发布人名称
    private String userName;

    // 值，以json表达
    private String value;

    // value表的主键
    private Long valueId;

    // 所属策略的id
    private Long strategyId;
}
