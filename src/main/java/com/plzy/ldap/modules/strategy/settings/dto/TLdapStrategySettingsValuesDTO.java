package com.plzy.ldap.modules.strategy.settings.dto;

import com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsValues;
import lombok.Data;

@Data
public class TLdapStrategySettingsValuesDTO extends TLdapStrategySettingsValues {

    private String name;
}
