package com.plzy.ldap.framework.ldap.service.impl;

import com.plzy.ldap.framework.error_handler.CustomResponseErrorHandler;
import com.plzy.ldap.framework.ldap.service.LDAPAuthService;
import com.plzy.ldap.framework.ldap.service.LDAPDomainCacheService;
import com.plzy.ldap.framework.ldap.service.LDAPRemoteService;
import com.plzy.ldap.framework.utils.DESUtil4H4A;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.domainuser.service.DomainUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class LDAPAuthServiceImpl implements LDAPAuthService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LDAPDomainCacheService domainCacheService;

    @Autowired
    private LDAPRemoteService ldapRemoteService;

    @Override
    public String authWithAdmin(Long domainId) {

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

        try{
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("user", currDomain.getServiceName());
            params.add("password", currDomain.getServicePasswd());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Referer", currDomain.getServiceUrl() + "/ipa/ui/");

            HttpEntity httpEntity = new HttpEntity<>(params, headers);

            log.info("即将发送认证请求");

            ResponseEntity resp = restTemplate.exchange(currDomain.getServiceUrl() + "/ipa/session/login_password", HttpMethod.POST, httpEntity, String.class);

            HttpHeaders respHeaders = resp.getHeaders();

            log.info("收到响应：" + respHeaders);

            HttpStatus status = resp.getStatusCode();
            log.info("响应状态码：" + status);
            if(status == HttpStatus.OK){

                // 更新登录凭证
                return respHeaders.get("Set-Cookie").get(0) + "";

            }else if(status == HttpStatus.UNAUTHORIZED){
                log.error("账号验证错误");
            }else {
                log.error("验证账号中出现未知错误");
            }

        }catch (Exception e){
            log.error("调用server时出现错误：",e);
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public boolean authWithUPN(String user, String password) {

        log.info("接受到UPN验证请求，user="+user+"，password=" + password);

        TLdapDomain targetDomainId = null;
        String userName = null;

        // 获取用户名中的域名
        if(user.indexOf("/") != -1){
            // 使用的短域名
            String[] items = user.split("/");
            String shortDomainName = items[0];
            userName = items[1];
            log.info("当前用户使用短域名，shortDomainName=" + shortDomainName + ",userName=" + userName);
            // 获取对应的domainId
            for(Map.Entry entry : domainCacheService.getCache().entrySet()){
                TLdapDomain val = (TLdapDomain)entry.getValue();
                if(shortDomainName.equals(val.getCode())){
                    targetDomainId = val;
                    break;
                }
            }
        }else if(user.indexOf("@") != -1){
            // 使用的长域名
            String[] items = user.split("@");
            String domainName = items[1];
            userName = items[0];
            log.info("当前用户使用长域名，shortDomainName=" + domainName + ",userName=" + userName);
            // 获取对应的domainId
            for(Map.Entry entry : domainCacheService.getCache().entrySet()){
                TLdapDomain val = (TLdapDomain)entry.getValue();
                if(domainName.equals(val.getDomainName())){
                    targetDomainId = val;
                    break;
                }
            }
        }

        // 没有匹配到对应的域
        if(targetDomainId == null){
            log.warn("没有登录凭证（user="+user+",password="+password+"）没有匹配到对应的域名");
            return false;
        }

        log.info("定位到用户所在域：" + targetDomainId.getId());

        log.info("开始验证用户有效性");
        boolean result = false;
        try {
            // 密码解密
            DESUtil4H4A des = new DESUtil4H4A();
            des.getKey("A3F2569DESJEIWBCJOTY45DYQWF68H1Y");
            password = des.createDecryptor(des.decodeBase64(password));

            result = auth(targetDomainId.getId(), userName, password);
            log.info("验证结果：" + result);
        }catch (Exception e){
            log.info("验证账号时出现错误：", e);
        }

        return result;
    }

    @Override
    public boolean auth(Long domainId, String user, String password) {

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

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("user", user);
        params.add("password", password);

        String referer = currDomain.getServiceUrl() + "/ipa/ui/";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Referer", referer);

        HttpEntity httpEntity = new HttpEntity<>(params, headers);

        log.info("即将发送认证请求");

        ResponseEntity resp = null;

        try{
            restTemplate.setErrorHandler(new CustomResponseErrorHandler());
            resp = restTemplate.exchange(currDomain.getServiceUrl() + "/ipa/session/login_password", HttpMethod.POST, httpEntity, String.class);
        }catch (Exception e){
            log.error("请求时出现异常：",e);
        }

        HttpHeaders respHeaders = resp.getHeaders();

        log.info("收到响应：" + respHeaders);

        HttpStatus status = resp.getStatusCode();
        log.info("响应状态码：" + status);
        if(status == HttpStatus.OK){

            // 如果是admin登录，则更新登录凭证
            // 这里一定要更新，因为重新调用了登录方法，会导致之前的失效了
            if("admin".equals(user)) {
                domainCacheService.updateCookie(domainId, respHeaders.get("Set-Cookie").get(0) + "");
            }

            return true;

        }else if(status == HttpStatus.UNAUTHORIZED){
            String rejectionReason = respHeaders.get("X-IPA-Rejection-Reason").get(0);
            if("password-expired".equals(rejectionReason)){
                log.error("账号验证通过，X-IPA-Rejection-Reason=password-expired，用户必须在下次登录时更改密码");
                return true;
            }
        }else {
            log.error("验证账号中出现未知错误");
        }

        return false;
    }
}
