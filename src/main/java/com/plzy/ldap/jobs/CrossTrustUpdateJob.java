package com.plzy.ldap.jobs;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.plzy.ldap.modules.cross_trust.domain.TLdapCrossTrust;
import com.plzy.ldap.modules.cross_trust.service.TLdapCrossTrustService;
import com.plzy.ldap.modules.domainuser.service.DomainUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class CrossTrustUpdateJob {

    @Autowired
    private TLdapCrossTrustService crossTrustService;

    @Autowired
    private DomainUserService domainUserService;

    @Scheduled(cron = "0 0/30 * * * ?")
    @PostConstruct
    public void run(){

        try{
            log.info("跨域信任任务即将执行");

            // 执行未同步的信任
            crossTrustService.execTrust();

            // 禁止已过期的信任
            List<TLdapCrossTrust> invalidList = crossTrustService.list(new LambdaQueryWrapper<TLdapCrossTrust>().lt(TLdapCrossTrust::getEndTime, new Date()).eq(TLdapCrossTrust::getSyncStatus,1));
            log.info("发现已过期的信任，即将禁用："+invalidList);

            // 执行取消信任
            for(TLdapCrossTrust item : invalidList){
                domainUserService.disable(item.getSrcDomainId(),item.getUid());

                // 更新数据库为未同步状态
                item.setEnable(1);
                crossTrustService.updateById(item);
            }

            log.info("跨域信任执行完成");
        }catch (Exception e){
            log.error("执行任务时遇到错误:",e);
        }
    }
}
