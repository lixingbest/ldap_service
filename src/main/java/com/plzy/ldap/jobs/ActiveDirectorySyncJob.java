package com.plzy.ldap.jobs;

import com.plzy.ldap.modules.active_directory.service.SyncActiveDirectoryService;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Slf4j
public class ActiveDirectorySyncJob {

    @Autowired
    private TLdapDomainService domainService;

    @Autowired
    private SyncActiveDirectoryService syncActiveDirectoryService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void sync(){

        long start = System.currentTimeMillis();
        log.info("即将开始同步ad");

        List<TLdapDomain> domainList = domainService.listSubdomain();
        for(TLdapDomain domain: domainList) {
            log.info("即将同步域："+domain.getDomainName());
            syncActiveDirectoryService.syncAll(domain.getId());
            log.info("同步域完成："+domain.getDomainName());
        }

        log.info("ad同步完成，同步耗时："+(System.currentTimeMillis()-start)/1000L+"s");
    }
}
