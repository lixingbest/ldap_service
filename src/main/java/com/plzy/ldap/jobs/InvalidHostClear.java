package com.plzy.ldap.jobs;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.host.service.HostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class InvalidHostClear {

    @Autowired
    private TLdapDomainService domainService;

    @Autowired
    private HostService hostService;

    @Scheduled(cron = "0 0/30 * * * ?")
//    @PostConstruct
    public void clear(){

        try{
            log.info("即将清理已退域的主机记录");

            // 查询所有的域
            List<TLdapDomain> domains = domainService.list(new LambdaQueryWrapper<TLdapDomain>().eq(TLdapDomain::getUpStatus,0));

            int count = 0;
            for(TLdapDomain domain : domains){
                count += hostService.clearInvalidHost(domain.getId());
            }

            log.info("已退域的主机记录已清理完成，清理了"+count+"个");
        }catch (Exception e){
            log.error("执行任务时遇到错误:",e);
        }


    }
}
