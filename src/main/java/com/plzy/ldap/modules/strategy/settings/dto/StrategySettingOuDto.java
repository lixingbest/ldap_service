package com.plzy.ldap.modules.strategy.settings.dto;

import com.plzy.ldap.modules.ou.domain.TLdapOu;
import lombok.Data;

@Data
public class StrategySettingOuDto extends TLdapOu {

    private Integer enable;

    private Long refId;
}
