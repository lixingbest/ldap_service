package com.plzy.ldap.modules.strategy.settings.dto;

import com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsList;
import lombok.Data;

@Data
public class TLdapStrategySettingsListWithRefIdDTO extends TLdapStrategySettingsList {

    private Long refId;

    private Integer enable;
}
