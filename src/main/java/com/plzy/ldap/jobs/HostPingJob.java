package com.plzy.ldap.jobs;

import com.plzy.ldap.framework.utils.TerminalUtil;
import com.plzy.ldap.modules.client_access_log.service.TLdapClientAccessLogService;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class HostPingJob {

    @Autowired
    private TLdapClientAccessLogService accessLogService;

    @Autowired
    private TLdapDomainService domainService;

    // 数据结构：<时间戳,<ip，ping结果>>
    public static Map<Long, Map<String, Boolean>> hostPingResult = new ConcurrentHashMap<>();

    /**
     * 生成图表的数据
     *
     * @return
     */
    public static Map<Long,Integer> getChartData(Long domainId){

        Map<Long,Integer> result = new TreeMap<>();

        for(Map.Entry<Long,Map<String,Boolean>> entry : hostPingResult.entrySet()){
            // 计算在线的个数
            int onlineSize = 0;
            for(Map.Entry<String,Boolean> child : entry.getValue().entrySet()){
                if(child.getValue()){
                    onlineSize += 1;
                }
            }
            result.put(entry.getKey(), onlineSize);
        }

        return result;
    }

    /**
     * 获取最新的ip在线状态
     *
     * @param ip
     * @return
     */
    public static Boolean getLatestPingResult(String ip){

        Long time = -1L;
        Map<String,Boolean> value = null;
        for(Map.Entry<Long,Map<String,Boolean>> entry : hostPingResult.entrySet()){
            // 找到最新的时间和值
            if(entry.getKey() > time){
                time = entry.getKey();
                value = entry.getValue();
            }
        }

        if(value != null && value.containsKey(ip)){
            return value.get(ip);
        }
        return null;
    }

    @Scheduled(cron = "0 0/30 * * * ?")
//    @Scheduled(cron = "0 0 0-23 * * ?")
    public void ping(){

        try{

            log.info("开始运行主机ping任务");

            // 删除一天前的记录数据
            Iterator<Map.Entry<Long, Map<String, Boolean>>> it = hostPingResult.entrySet().iterator();
            while (it.hasNext()){
                Map.Entry<Long, Map<String, Boolean>> obj = (Map.Entry)it.next();
                if(obj.getKey() <= System.currentTimeMillis() - 24 * 60 * 60 * 1000L){
                    it.remove();
                }
            }

            long currTime = System.currentTimeMillis();

            Map<String, Boolean> child = new HashMap<>();

            List<TLdapDomain> domainList = domainService.listSubdomain();
            for(TLdapDomain domain : domainList){
                List<String> ipList = accessLogService.getIpList(domain.getId());

                for(String ip : ipList){
                    boolean result = TerminalUtil.ping(ip);
                    child.put(ip, result);
                }
            }

            hostPingResult.put(currTime,child);

            log.info("主机ping任务执行完毕，map结果数量："+hostPingResult.size());
        }catch (Exception e){
            log.error("执行任务时遇到错误:",e);
        }
    }
}
