package com.plzy.ldap.jobs;

import com.plzy.ldap.modules.ou.domain.TLdapOu;
import com.plzy.ldap.modules.ou.service.TLdapOuService;
import com.plzy.ldap.modules.strategy.settings.dto.LdapStrategySettingsDetails;
import com.plzy.ldap.modules.strategy.settings.dto.StrategySettingsCommandDto;
import com.plzy.ldap.modules.strategy.settings.mapper.TLdapStrategySettingsListMapper;
import com.plzy.ldap.modules.strategy.settings.service.TLdapStrategySettingsListService;
import com.plzy.ldap.modules.strategy.settings.service.impl.TLdapStrategySettingsListServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Slf4j
public class StrategyUpdateJob {

    @Autowired
    private TLdapStrategySettingsListService listService;

    @PostConstruct
    @Scheduled(cron = "0 0/30 * * * ?")
    public void update(){

        try{
            listService.makeCache();
        }catch (Exception e){
            log.error("执行任务时遇到错误:",e);
        }
    }
}
