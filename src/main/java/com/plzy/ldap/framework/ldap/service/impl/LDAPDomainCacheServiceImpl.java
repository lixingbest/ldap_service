package com.plzy.ldap.framework.ldap.service.impl;

import com.plzy.ldap.framework.ldap.service.LDAPDomainCacheService;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class LDAPDomainCacheServiceImpl implements LDAPDomainCacheService {

    @Autowired
    private TLdapDomainService domainService;

    private Map<Long, TLdapDomain> domainCache;

    @PostConstruct
    public void init(){

        domainCache = new ConcurrentHashMap<>();

        List<TLdapDomain> domainList = domainService.listSubdomain();
        for (TLdapDomain domain : domainList){
            domainCache.put(domain.getId(), domain);
        }
    }

    public void reload(){

        log.info("开始刷新domain缓存");

        if(domainCache != null) {
            domainCache.clear();
        }

        init();

        log.info("domain缓存刷新成功！");
    }

    @Override
    public TLdapDomain get(Long domainId) {

        if(domainCache.containsKey(domainId)){
            TLdapDomain obj = domainCache.get(domainId);
            //检查cookie是否过期，如有过期，则删除，让业务代码自行获取
            if(System.currentTimeMillis() - obj.getCookieUpdateTimestamp() > 30 * 60 * 1000){
                log.info("发现查找到的domain中的cookie已过去，已清空cookie");
                obj.setCookie(null);
                obj.setCookieUpdateTimestamp(-1L); //一个必然过期的值
            }
            return obj;
        }

        throw new RuntimeException("没有找到domainId="+domainId+"的域");
    }

    @Override
    public Map<Long, TLdapDomain> getCache() {
        return domainCache;
    }

    @Override
    public void updateCookie(Long domainId, String cookie) {

        TLdapDomain obj = domainCache.get(domainId);

        obj.setCookieUpdateTimestamp(System.currentTimeMillis());
        obj.setCookie(cookie);
    }
}
