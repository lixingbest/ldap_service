package com.plzy.ldap.open_service.client_service.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.ldap_login_limit.domain.TLdapLoginLimit;
import com.plzy.ldap.modules.ldap_login_limit.service.TLdapLoginLimitService;
import com.plzy.ldap.modules.strategy.settings.service.TLdapStrategySettingsListService;
import com.plzy.ldap.modules.sysconfig.domain.TSysConf;
import com.plzy.ldap.modules.sysconfig.service.TSysConfService;
import com.plzy.ldap.open_service.client_service.bean.StrategySettingsPublicServiceParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/public_service/strategySettings")
@Slf4j
public class StrategySettingsPublicService {

    @Autowired
    private TLdapStrategySettingsListService strategySettingsListService;

    @Autowired
    private TLdapDomainService domainService;

    @Autowired
    private TSysConfService sysConfService;

    @Autowired
    private TLdapLoginLimitService loginLimitService;

    @GetMapping("/get")
    public ResponseEntity<ResponseData> get(String domain,String uid){

        if(!StringUtils.hasText(uid) || !StringUtils.hasText(domain)){
            return ResponseEntity.ok(ResponseData.error("999999","domainName、uid均不能为空！"));
        }

        // 根据domain找到对象
        TLdapDomain obj = domainService.getOne(new LambdaQueryWrapper<TLdapDomain>().eq(TLdapDomain::getDomainName, domain.toLowerCase()).eq(TLdapDomain::getUpStatus,0));

        return ResponseEntity.ok(ResponseData.success(strategySettingsListService.get(obj.getId(),uid)));
    }

    @GetMapping("/execute")
    public ResponseEntity<ResponseData> execute(StrategySettingsPublicServiceParams params){

        if(!StringUtils.hasText(params.getDomainName()) || !StringUtils.hasText(params.getUid())){
            return ResponseEntity.ok(ResponseData.error("999999","domainName、uid均不能为空！"));
        }

        Map<String,Object> resp = strategySettingsListService.execute(params);
        return ResponseEntity.ok(ResponseData.success(resp));
    }

    @GetMapping("/get_interval")
    public ResponseEntity<ResponseData> getInterval(){

        TSysConf config = sysConfService.getOne(new LambdaQueryWrapper<TSysConf>().eq(TSysConf::getName,"strategy_interval"));
        return ResponseEntity.ok(ResponseData.success(config));
    }

    @GetMapping("/get_login_time_limit")
    public ResponseEntity<ResponseData> getLoginTimeLimit(String domain, String uid){

        if(uid.contains("@")){
            uid = uid.split("@")[0];
        }

        // 根据domain找到对象
        TLdapDomain obj = domainService.getOne(new LambdaQueryWrapper<TLdapDomain>().eq(TLdapDomain::getDomainName, domain.toLowerCase()).eq(TLdapDomain::getUpStatus,0));

        TLdapLoginLimit limit = loginLimitService.getOne(new LambdaQueryWrapper<TLdapLoginLimit>()
                .eq(TLdapLoginLimit::getDomainId,obj.getId())
                .eq(TLdapLoginLimit::getUid,uid));
        return ResponseEntity.ok(ResponseData.success(limit));
    }
}
