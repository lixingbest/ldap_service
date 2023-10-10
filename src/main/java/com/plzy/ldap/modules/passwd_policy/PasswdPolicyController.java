package com.plzy.ldap.modules.passwd_policy;

import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.passwd_policy.dto.PasswdPolicyDTO;
import com.plzy.ldap.modules.passwd_policy.service.PasswdPolicyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/passwd_policy")
@Slf4j
public class PasswdPolicyController {

    @Autowired
    private PasswdPolicyService passwdPolicyService;

    @Autowired
    private TLdapDomainService domainService;

    @GetMapping("/list")
    public ResponseEntity<ResponseData> list(Long domainId, String group){

        // 如果是总署，则遍历所有的域
        if(domainId == 1){
            List result = new ArrayList();
            List<TLdapDomain> domains = domainService.listSubdomain();
            for(TLdapDomain domain : domains){
                result.addAll(passwdPolicyService.list(domain,group));
            }
            return ResponseEntity.ok(ResponseData.success(result));
        }

        return ResponseEntity.ok(ResponseData.success(passwdPolicyService.list(domainService.getById(domainId),group)));
    }

    @PutMapping("/add")
    public ResponseEntity<ResponseData> add(@RequestBody PasswdPolicyDTO passwdPolicy){

        // 一个用户组只能加一个用户策略，需要判断是否重复了
        TLdapDomain domain= domainService.getById(passwdPolicy.getDomainId());
        List exists = passwdPolicyService.list(domain, passwdPolicy.getCn());
        if(exists!= null && exists.toString().indexOf(passwdPolicy.getCn()) != -1){
            return ResponseEntity.ok(ResponseData.error("892031",passwdPolicy.getCn()+" 用户组下已经关联了密码策略，不能重复添加！"));
        }

        passwdPolicyService.add(passwdPolicy);
        return ResponseEntity.ok(ResponseData.success());
    }

    @PostMapping("/update")
    public ResponseEntity<ResponseData> update(@RequestBody PasswdPolicyDTO passwdPolicy){

        passwdPolicyService.update(passwdPolicy);
        return ResponseEntity.ok(ResponseData.success());
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ResponseData> remove(Long domainId, String group){

        passwdPolicyService.remove(domainId, group);
        return ResponseEntity.ok(ResponseData.success());
    }
}
