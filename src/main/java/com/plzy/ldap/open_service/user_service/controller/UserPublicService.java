package com.plzy.ldap.open_service.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzy.ldap.framework.ldap.service.LDAPDomainCacheService;
import com.plzy.ldap.framework.ldap.service.LDAPUserCachePoolService;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.framework.utils.ProcessUtil;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domainuser.dto.ActiveDomainUserWithExtraCommentsDTO;
import com.plzy.ldap.modules.domainuser.service.DomainUserService;
import lombok.extern.slf4j.Slf4j;
import org.ldaptive.LdapAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Map;

@Controller
@RequestMapping("/public_service/user/")
@Slf4j
public class UserPublicService {

    @Autowired
    private LDAPUserCachePoolService ldapUserCachePoolService;

    @Autowired
    private LDAPDomainCacheService ldapDomainCacheService;

    @Autowired
    private DomainUserService domainUserService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping(value = "/validate",method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<ResponseData> validate(String user, String password){

        if(!StringUtils.hasText(user) || !StringUtils.hasText(password)){
            return ResponseEntity.ok(ResponseData.error("999001","用户名或密码为空！"));
        }

        // 如果不是UPN格式，则自动处理
        if(user.indexOf("/") == -1 && user.indexOf("@") == -1){
            log.info("发现user不是UPN格式，即将自动处理");
            // 如果是济南海关，则拼接前缀
            if(user.startsWith("jn")){
                String newUser = "jn/" + user;
                log.info("已生成UPN格式，原名称：" + user + "，新名称：" + newUser);
                user = newUser;
            }
        }

        boolean result = ldapUserCachePoolService.authWithUPN(user, password);
        if(result){
            return ResponseEntity.ok(ResponseData.success("000000","用户凭证校验通过！"));
        }else {
            return ResponseEntity.ok(ResponseData.error("999002","用户凭证验证失败！"));
        }
    }

    @RequestMapping(value = "/whoami", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<ResponseData> whoami(String ipasession, HttpServletRequest request){

        log.info("接收到 whoami 验证请求，ipasession=" + ipasession);

        // todo: 临时处理，h4a的发送的后端请求直接放行
        if(!"get".equalsIgnoreCase(request.getMethod())){
            return ResponseEntity.ok(ResponseData.success());
        }

        if(!StringUtils.hasText(ipasession)){
            return ResponseEntity.ok(ResponseData.error("999999","ipasession不能为空！"));
        }else{
            ipasession = new String(Base64.getDecoder().decode(ipasession));
        }

        // 分别查找每个域，是否存在此ipasession
        Map<Long, TLdapDomain> cache = ldapDomainCacheService.getCache();
        for(Map.Entry<Long, TLdapDomain> entry : cache.entrySet()){
            try{

                String resp = ProcessUtil.exec(new String[]{"curl","--insecure","-X","POST", entry.getValue().getServiceUrl()+"/ipa/session/json",
                        "-H", "Accept:application/json",
                        "-H", "Content-type:application/json",
                        "-H","Referer:"+entry.getValue().getServiceUrl()+"/ipa/ui",
                        "-H","Cookie:ipa_session="+ipasession,
                        "-d","{\n" +
                        "    \"method\": \"whoami\",\n" +
                        "    \"params\": [\n" +
                        "        [],\n" +
                        "        {\n" +
                        "            \"version\": \"2.237\"\n" +
                        "        }\n" +
                        "    ]\n" +
                        "}"
                });
                log.info("收到域（domainId="+entry.getKey()+"）的 whoami 响应：" + resp);

                if(!StringUtils.hasText(resp) || resp.indexOf("html") != -1){
                    return ResponseEntity.ok(ResponseData.error("999999","找不到到ipasession的用户信息！"));
                }

                Map respMap = objectMapper.readValue(resp,Map.class);
                Map result = (Map)respMap.get("result");
                log.info("在域（domainId="+entry.getKey()+"）检索到此 ipasession，结果：" + result);

                return ResponseEntity.ok(ResponseData.success(result));

            }catch (Exception e){
                e.printStackTrace();
                log.error("向域（domainId="+entry.getKey()+"）发送 whoami 请求时出错，表示此域不存在该ipasession，继续尝试下一个域",e);
            }
        }

        log.warn("接收到 whoami 请求，但所有域中均验证失败，请检查ipasession的正确性！");

        return ResponseEntity.ok(ResponseData.error("999999","找不到到ipasession的用户信息！"));
    }

    @GetMapping("/batchAddUser4Test")
    public ResponseEntity<ResponseData> batchAddUser4Test(){

        for(int i = 500; i < 3000; i ++) {
            String uname = "ldap"+i;
            ActiveDomainUserWithExtraCommentsDTO domainUser = new ActiveDomainUserWithExtraCommentsDTO();
            domainUser.setUid(uname);
            domainUser.setGivenname(uname);
            domainUser.setSn(uname);
            domainUser.setCn(uname);
            domainUser.setUserpassword("tswcbyy5413LX"); // 默认密码
            domainUserService.save(16L, domainUser);
        }

        return ResponseEntity.ok(ResponseData.success());
    }
}
