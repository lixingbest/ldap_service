package com.plzy.ldap.framework.ldap.service.impl;

import com.plzy.ldap.framework.ldap.bean.UserCacheObj;
import com.plzy.ldap.framework.ldap.service.LDAPAuthService;
import com.plzy.ldap.framework.ldap.service.LDAPUserCachePoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@EnableAsync
public class LDAPUserCachePoolServiceImpl implements LDAPUserCachePoolService {

    @Autowired
    private LDAPAuthService ldapAuthService;

    private Map<String, UserCacheObj> userCache = new ConcurrentHashMap<>();

    @Value("${ldap.cacheTTL}")
    private Long cacheTTL;

    public boolean authWithUPN(String user, String password){

        String key = user + "***" + password;

        log.info("正在准备使用cache验证用户登录信息，key=" + key);

        if(userCache.containsKey(key)){
            UserCacheObj obj = userCache.get(key);
            log.info("cache中包含用户信息，直接返回：" + obj);
            return true;
        }

        log.info("cache中没有包含用户信息，即将查询服务");

        boolean result = ldapAuthService.authWithUPN(user, password);
        if(result){
            log.info("用户凭证校验成功，并已将此用户添加到cache中");
            userCache.put(key, new UserCacheObj(user, password, System.currentTimeMillis()));
        }

        return result;
    }

    @Scheduled(cron = "0 0/20 * * * ?")
    private void updateCache(){

        log.info("开始执行cache更新任务");

        Iterator<Map.Entry<String, UserCacheObj>> it = userCache.entrySet().iterator();

        int total = 0;
        while (it.hasNext()){

            Map.Entry<String, UserCacheObj> entry = it.next();
            UserCacheObj currUserObj = entry.getValue();

            if(System.currentTimeMillis() - currUserObj.getUpdateTimestamp() > cacheTTL){

                log.info("发现一个过期的用户（"+entry.getKey()+"）缓存，即将验证此用户的账号有效性");

                boolean result = ldapAuthService.authWithUPN(currUserObj.getUpn(), currUserObj.getPassword());
                if(result){
                    log.info("该用户（"+entry.getKey()+"）账号有效，已延长缓存有效期");
                    currUserObj.setUpdateTimestamp(currUserObj.getUpdateTimestamp() + cacheTTL);
                }else{
                    log.info("该用户（"+entry.getKey()+"）账号验证不通过，已从缓存中删除");
                    userCache.remove(entry.getKey());
                    total ++;
                }
            }
        }

        log.info("cache更新任务执行完成，已清理了"+total+"个用户");
    }
}
