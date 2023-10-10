package com.plzy.ldap.modules.strategy.settings.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.modules.strategy.settings.domain.TLdapOuStrategySettingsRef;
import com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsList;
import com.plzy.ldap.modules.strategy.settings.dto.LdapStrategySettingsDetails;
import com.plzy.ldap.modules.strategy.settings.dto.TLdapStrategySettingsListWithRefIdDTO;
import com.plzy.ldap.modules.strategy.settings.service.TLdapOuStrategySettingsRefService;
import com.plzy.ldap.modules.strategy.settings.mapper.TLdapOuStrategySettingsRefMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author root
* @description 针对表【t_ldap_ou_strategy_settings_ref(组织单位-策略设置关联表)】的数据库操作Service实现
* @createDate 2022-01-04 10:40:23
*/
@Service
public class TLdapOuStrategySettingsRefServiceImpl extends ServiceImpl<TLdapOuStrategySettingsRefMapper, TLdapOuStrategySettingsRef>
    implements TLdapOuStrategySettingsRefService{

    @Autowired
    private TLdapOuStrategySettingsRefMapper ouStrategySettingsRefMapper;

    @Override
    public List<LdapStrategySettingsDetails> getByOuId(Long ouId) {
        return ouStrategySettingsRefMapper.getByOuId(ouId);
    }
}




