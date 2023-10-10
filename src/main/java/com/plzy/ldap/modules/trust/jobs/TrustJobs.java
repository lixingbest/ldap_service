package com.plzy.ldap.modules.trust.jobs;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzy.ldap.framework.ldap.protocol.LDAPResponse;
import com.plzy.ldap.framework.ldap.service.LDAPRemoteService;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.domainuser.dto.ActiveDomainUserWithExtraCommentsDTO;
import com.plzy.ldap.modules.domainuser.service.DomainUserService;
import com.plzy.ldap.modules.trust.domain.TLdapTrust;
import com.plzy.ldap.modules.trust.service.TLdapTrustService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@EnableAsync
@Slf4j
public class TrustJobs {

    @Autowired
    private TLdapTrustService trustService;

    @Autowired
    private TLdapDomainService domainService;

    @Autowired
    private DomainUserService domainUserService;

    @Autowired
    private LDAPRemoteService rpcService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<Long, TLdapDomain> domainCache;

//    @PostConstruct
//    @Scheduled(cron = "0 0/30 * * * ?")
    public void doJob(){

        // 预加载域信息
        if(domainCache == null){
            preloadDomain();
        }

        syncDomain();

//        syncUsers();
    }

    /**
     * 预加载域信息
     */
    public void preloadDomain(){

        domainCache = new ConcurrentHashMap<>();

        List<TLdapDomain> domainList = domainService.listSubdomain();
        for (TLdapDomain domain : domainList){
            domainCache.put(domain.getId(), domain);
        }
    }

    /**
     * 同步域
     */
    public void syncDomain(){

        // 获取所有待同步的域
        List<TLdapTrust> trustList = trustService.list(new LambdaQueryWrapper<TLdapTrust>()
                .eq(TLdapTrust::getType,(byte)1)
                .eq(TLdapTrust::getEnable,(byte)0)
                .eq(TLdapTrust::getIsSync, (byte)0));

        log.info("待同步的域数目为" + trustList.size() + "个：" + trustList);

        for (TLdapTrust item : trustList){

            log.info("准备查找当前域（"+item.getTrustDomainId()+"）下的所有用户");

            // 获取当前域下的用户
            List<Map> domainUsers = domainUserService.listAll(item.getTrustDomainId());

            log.info("检索到域（"+item.getTrustDomainId()+"）下的用户的数量为"+domainUsers.size()+"，开始同步");

            // 同步这些用户到域
            for(Map originalUser : domainUsers){

                // 构建目标用户的属性
                // 不能设置ou信息，因为此用户同步过去应不可见
                ActiveDomainUserWithExtraCommentsDTO targetUser = new ActiveDomainUserWithExtraCommentsDTO();

                String newUID = originalUser.get("uid")+"." + domainCache.get(item.getTrustDomainId()).getCode();
                targetUser.setUid(newUID);
                log.info("为避免重名，已为用户（"+item.getTrustUid()+"）生成新的uid：" + newUID);

                targetUser.setGivenname((originalUser.get("givenname") + "").replaceAll("\\[|\\]",""));
                targetUser.setSn((originalUser.get("sn") + "").replaceAll("\\[|\\]",""));
                targetUser.setCn((originalUser.get("cn") + "").replaceAll("\\[|\\]",""));
                targetUser.setUserpassword("1234567");
                log.info("即将推送用户（"+item.getTrustUid()+"）到域（"+item.getDomainId()+"），明细属性为：" + targetUser);

                log.info("开始同步用户（"+originalUser.get("uid")+"）信息");

                domainUserService.save(item.getDomainId(), targetUser);

                log.info("用户（"+targetUser.getUid()+"）信息同步完成");
            }
        }
    }

    /**
     * 同步用户
     */
    public void syncUsers(){

        // 获取所有待同步的用户
        List<TLdapTrust> trustList = trustService.list(new LambdaQueryWrapper<TLdapTrust>()
                .eq(TLdapTrust::getType,(byte)3)
                .eq(TLdapTrust::getEnable,(byte)0)
                .eq(TLdapTrust::getIsSync, (byte)0));

        log.info("待同步的用户数为" + trustList.size()+"个：" + trustList);

        for (TLdapTrust item : trustList){

            log.info("计划同步用户（"+item.getTrustUid()+"）到域（"+item.getDomainId()+"）");

            // 查找当前被信任用户的详细信息
            log.info("开始获取用户（"+item.getTrustUid()+"）的详细信息");
            LDAPResponse response = rpcService.request(item.getTrustDomainId(), "{\n" +
                    "    \"method\": \"batch\",\n" +
                    "    \"params\": [\n" +
                    "        [\n" +
                    "            {\n" +
                    "                \"method\": \"user_show\",\n" +
                    "                \"params\": [\n" +
                    "                    [\n" +
                    "                        \""+item.getTrustUid()+"\"\n" +
                    "                    ],\n" +
                    "                    {\n" +
                    "                        \"no_members\": true,\n" +
                    "                        \"all\": false\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            }\n" +
                    "        ],\n" +
                    "        {\n" +
                    "            \"version\": \"2.237\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}");
            Map originalUser = (Map)((Map)((List)response.getResult().get("results")).get(0)).get("result");
            log.info("用户（"+item.getTrustUid()+"）详细信息获取完成：" + originalUser);

            String newUID = ((List)originalUser.get("uid")).get(0)+"." + domainCache.get(item.getTrustDomainId()).getCode();
            originalUser.put("uid", Arrays.asList(newUID));
            log.info("为避免重名，已为用户（"+item.getTrustUid()+"）生成新的uid：" + newUID);

            // 构建目标用户的属性
            // 不能设置ou信息，因为此用户同步过去应不可见
            ActiveDomainUserWithExtraCommentsDTO targetUser = new ActiveDomainUserWithExtraCommentsDTO();
            targetUser.setUid(newUID);
            targetUser.setGivenname((originalUser.get("givenname") + "").replaceAll("\\[|\\]",""));
            targetUser.setSn((originalUser.get("sn") + "").replaceAll("\\[|\\]",""));
            targetUser.setCn((originalUser.get("cn") + "").replaceAll("\\[|\\]",""));
            targetUser.setUserpassword("1234567");
            log.info("即将推送用户（"+item.getTrustUid()+"）到域（"+item.getDomainId()+"），明细属性为：" + targetUser);

            domainUserService.save(item.getDomainId(), targetUser);
            log.info(item.getTrustUid()+"用户（"+item.getTrustUid()+"）已成功推送到域（" + item.getDomainId() + "）");

            // 更新表记录
            item.setIsSync((byte)1);
            trustService.updateById(item);
        }
    }
}
