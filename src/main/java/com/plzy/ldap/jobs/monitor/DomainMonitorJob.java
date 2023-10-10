package com.plzy.ldap.jobs.monitor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.plzy.ldap.framework.ldap.service.LDAPRemoteService;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.domain_cluster.domain.TLdapDomainCluster;
import com.plzy.ldap.modules.domain_cluster.service.TLdapDomainClusterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Slf4j
public class DomainMonitorJob {

    @Autowired
    private TLdapDomainService domainService;

    @Autowired
    private TLdapDomainClusterService clusterService;

    @Autowired
    private LDAPRemoteService ldapRemoteService;

//    @Scheduled(cron = "0/3 * * * * ?")
    public void start(){

        try{

            List<TLdapDomain> domainList = domainService.listSubdomain();
            for(TLdapDomain domain: domainList){

                List<TLdapDomainCluster> clusters = clusterService.list(new LambdaQueryWrapper<TLdapDomainCluster>().eq(TLdapDomainCluster::getDomainId,domain.getId()).eq(TLdapDomainCluster::getEnable,0));
                for(TLdapDomainCluster cluster : clusters){
                    ldapRemoteService.requestAny(cluster.getId(), "");
                }
            }
        }catch (Exception e){
            log.error("执行domain监控时遇到错误：",e);
        }
    }
}
