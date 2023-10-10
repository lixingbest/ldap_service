package com.plzy.ldap.framework.ldap.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzy.ldap.framework.ldap.bean.LDAPRequestCache;
import com.plzy.ldap.framework.ldap.protocol.LDAPResponse;
import com.plzy.ldap.framework.ldap.service.LDAPAuthService;
import com.plzy.ldap.framework.ldap.service.LDAPDomainCacheService;
import com.plzy.ldap.framework.ldap.service.LDAPRemoteService;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.domain_cluster.domain.TLdapDomainCluster;
import com.plzy.ldap.modules.domain_cluster.service.TLdapDomainClusterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@EnableAsync
public class LDAPRemoteServiceImpl implements LDAPRemoteService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LDAPDomainCacheService domainCacheService;

    @Autowired
    private TLdapDomainClusterService clusterService;

    @Autowired
    private LDAPAuthService ldapAuthService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, LDAPRequestCache> cache = new HashMap<>();

    @Value("${ldap.ldap-cache-timeout}")
    private Long ldapCacheTimeout;

    /**
     * 更新cookie
     *
     * @param domainId
     * @param currDomain
     */
    private void updateCookie(Long domainId,TLdapDomain currDomain){

        String cookie = ldapAuthService.authWithAdmin(domainId);
        log.info("cookie获取成功，内容为：" + cookie);
        if(cookie != null){
            currDomain.setCookie(cookie);
            currDomain.setCookieUpdateTimestamp(System.currentTimeMillis());
        }else {
            log.error("严重错误：根据域配置但admin账号错误，此错误会导致循环请求，请立即修复！");
            throw new RuntimeException("严重错误：根据域配置但admin账号错误，此错误会导致循环请求，请立即修复！");
        }
    }

    @Override
    public LDAPResponse request(Long domainId, String command) {
        return request(domainId, true, command);
    }

    @Override
    public LDAPResponse request(Long domainId, boolean fromCache, String command){

        // 从缓存中获取结果
        String key = domainId + "-" + command;

        if(command.contains("_add") || command.contains("_del") || command.contains("_undel") || command.contains("_mod") || command.contains("_remove") || command.contains("_rename")
                || command.contains("_disable") || command.contains("_enable") || command.contains("_unlock")
                || command.contains("_rebuild") || command.contains("_revoke") || command.contains("_unapply") || command.contains("_reset")
        ){
            log.info("发现更新类请求，强制清空缓存，请求体：domainId="+domainId+",command="+command);
            cache.clear();
        }
        if(fromCache && cache.containsKey(key)){
            LDAPRequestCache currCache = cache.get(key);
            // 在缓存有效期内
            if(System.currentTimeMillis() - currCache.getTimestamp() < ldapCacheTimeout){
                return currCache.getValue();
            }else {
                cache.remove(key);
            }
        }

        // 从缓存中获取当前域的信息
        TLdapDomain currDomain = domainCacheService.get(domainId);

        if(currDomain == null){
            log.error("没有找到domainId="+domainId+"的域信息，无法执行后续请求");
            throw new RuntimeException("没有找到domainId="+domainId+"的域信息，无法执行后续请求");
        }

        if(currDomain.getId() == 1L){
            log.error("不能请求根域（domainId=1），无法执行后续请求");
            throw new RuntimeException("不能请求根域（domainId=1），无法执行后续请求");
        }

        // 如果从没有获取过cookie，则获取
        if(currDomain.getCookie() == null){
            log.info("准备发送请求，但没有找到cookie，即将重新申请cookie");
            updateCookie(domainId,currDomain);
        }else{
            long timestamp = currDomain.getCookieUpdateTimestamp();
            // 判断cookie是否过期
            if(System.currentTimeMillis() - timestamp > 30 * 60 * 1000L){
                log.info("cookie已过期，即将重新请求cookie");
                updateCookie(domainId,currDomain);
            }
        }

        try{
            Map<String, Object> params = objectMapper.readValue(command, Map.class);

            String referer = currDomain.getServiceUrl() + "/ipa/ui/";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json");
            headers.set("Referer", referer);
            headers.set("Cookie", currDomain.getCookie());
            HttpEntity httpEntity = new HttpEntity<>(params, headers);

            log.info("即将向域（id="+domainId+",url="+currDomain.getServiceUrl() + "/ipa/session/json"+",Referer="+referer+",Cookie="+currDomain.getCookie()+"）发送请求：" + command);
            String respText = restTemplate.postForObject(currDomain.getServiceUrl() + "/ipa/session/json", httpEntity, String.class);
            log.info("请求响应原始结果：" + respText);

            if(!respText.startsWith("{") && !respText.startsWith("[")) {
                log.error("不是有效的JSON响应，无法执行后续操作！响应内容：" + respText);
                throw new RuntimeException("不是有效的JSON响应，无法执行后续操作！响应内容：" + respText);
            }

            log.info("即将转换原始结果到LDAPResponse");
            LDAPResponse resp = objectMapper.readValue(respText, LDAPResponse.class);
            log.info("LDAPResponse响应结果：" + resp);

            // 不能添加这个逻辑，因为如果用户不存在，不能抛出异常
            if(resp.getError() != null){
                log.error("调用server时出现错误：",resp.getError());
            }

            // 添加缓存
            cache.put(key, new LDAPRequestCache(resp,System.currentTimeMillis()));

            return resp;
        }catch (HttpClientErrorException.Unauthorized e){
            log.error("向域（domainId="+domainId+"）发送请求时出现401错误，可能token已过期",e);
            throw new RuntimeException("向域发送请求时出现401错误，可能token已过期，请重新登录！");
        }catch (Exception e){
            log.error("调用server时出现错误：",e);
            throw new RuntimeException("调用server时出现错误非授权错误，请重新登录或联系厂商排查！");
        }
    }

    @Override
    public LDAPResponse requestAny(Long clusterId, String command) {

        return null;

//        TLdapDomainCluster cluster = clusterService.getById(clusterId);
//
//        String cookie = ldapAuthService.authWithAdmin(domainId);
//        log.info("cookie获取成功，内容为：" + cookie);
//            currDomain.setCookie(cookie);
//            currDomain.setCookieUpdateTimestamp(System.currentTimeMillis());
//
//        // 如果从没有获取过cookie，则获取
//        if(currDomain.getCookie() == null){
//            log.info("准备发送请求，但没有找到cookie，即将重新申请cookie");
//            updateCookie(domainId,currDomain);
//        }else{
//            long timestamp = currDomain.getCookieUpdateTimestamp();
//            // 判断cookie是否过期
//            if(System.currentTimeMillis() - timestamp > 30 * 60 * 1000L){
//                log.info("cookie已过期，即将重新请求cookie");
//                updateCookie(domainId,currDomain);
//            }
//        }
//
//        try{
//            Map<String, Object> params = objectMapper.readValue(command, Map.class);
//
//            String referer = cluster.getServiceUrl() + "/ipa/ui/";
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.set("Accept", "application/json");
//            headers.set("Referer", referer);
//            headers.set("Cookie", currDomain.getCookie());
//            HttpEntity httpEntity = new HttpEntity<>(params, headers);
//
//            log.info("即将向域（id="+domainId+",url="+currDomain.getServiceUrl() + "/ipa/session/json"+",Referer="+referer+",Cookie="+currDomain.getCookie()+"）发送请求：" + command);
//            String respText = restTemplate.postForObject(currDomain.getServiceUrl() + "/ipa/session/json", httpEntity, String.class);
//            log.info("请求响应原始结果：" + respText);
//
//            if(!respText.startsWith("{") && !respText.startsWith("[")) {
//                log.error("不是有效的JSON响应，无法执行后续操作！响应内容：" + respText);
//                throw new RuntimeException("不是有效的JSON响应，无法执行后续操作！响应内容：" + respText);
//            }
//
//            log.info("即将转换原始结果到LDAPResponse");
//            LDAPResponse resp = objectMapper.readValue(respText, LDAPResponse.class);
//            log.info("LDAPResponse响应结果：" + resp);
//
//            // 不能添加这个逻辑，因为如果用户不存在，不能抛出异常
//            if(resp.getError() != null){
//                log.error("调用server时出现错误：",resp.getError());
//            }
//
//            // 添加缓存
//            cache.put(key, new LDAPRequestCache(resp,System.currentTimeMillis()));
//
//            return resp;
//        }catch (HttpClientErrorException.Unauthorized e){
//            log.error("向域（domainId="+domainId+"）发送请求时出现401错误，可能token已过期",e);
//            throw new RuntimeException("向域发送请求时出现401错误，可能token已过期，请重新登录！");
//        }catch (Exception e){
//            log.error("调用server时出现错误：",e);
//            throw new RuntimeException("调用server时出现错误非授权错误，请重新登录或联系厂商排查！");
//        }
    }
}
