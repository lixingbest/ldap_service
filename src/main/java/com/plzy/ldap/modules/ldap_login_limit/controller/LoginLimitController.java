package com.plzy.ldap.modules.ldap_login_limit.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.modules.ldap_login_limit.domain.TLdapLoginLimit;
import com.plzy.ldap.modules.ldap_login_limit.dto.LoginLimitDto;
import com.plzy.ldap.modules.ldap_login_limit.service.TLdapLoginLimitService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("loginLimit")
public class LoginLimitController {

    @Resource
    private TLdapLoginLimitService ldapLoginLimitService;

    @PostMapping
    public ResponseEntity<ResponseData> saveOrUpdate(@RequestBody LoginLimitDto loginLimit) {

        if (loginLimit.getType().equals(0)) {
            ldapLoginLimitService.remove(new LambdaQueryWrapper<TLdapLoginLimit>()
                    .eq(TLdapLoginLimit::getDomainId, loginLimit.getDomainId())
                    .eq(TLdapLoginLimit::getUid, loginLimit.getUid()));

        } else {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                loginLimit.setLoginDateBegin(formatter.parse(loginLimit.getLoginDateBeginStr()));
                loginLimit.setLoginDateEnd(formatter.parse(loginLimit.getLoginDateEndStr()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            ldapLoginLimitService.saveOrUpdate(loginLimit);
        }

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping
    public ResponseEntity<ResponseData> get(TLdapLoginLimit loginLimit) {
        return ResponseEntity.ok(ResponseData.success(
                ldapLoginLimitService.getOne(new LambdaQueryWrapper<TLdapLoginLimit>()
                        .eq(TLdapLoginLimit::getDomainId, loginLimit.getDomainId())
                        .eq(TLdapLoginLimit::getUid, loginLimit.getUid()))
        ));
    }
}
