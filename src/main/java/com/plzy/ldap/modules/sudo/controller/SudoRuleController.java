package com.plzy.ldap.modules.sudo.controller;

import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.sudo.dto.SudoRefDTO;
import com.plzy.ldap.modules.sudo.dto.SudoRuleDTO;
import com.plzy.ldap.modules.sudo.service.SudoRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sudo_rule")
@Slf4j
public class SudoRuleController {

    @Autowired
    private TLdapDomainService domainService;

    @Autowired
    private SudoRuleService sudoRuleService;

    @GetMapping("/list")
    public ResponseEntity<ResponseData> list(Long domainId){

        // 如果请求的是根域，则需要合并结果
        if(domainId == 1L){
            List result = new ArrayList();
            for(TLdapDomain domain : domainService.listSubdomain()){
                result.addAll(sudoRuleService.list(domain.getId()));
            }
            return ResponseEntity.ok(ResponseData.success(result));
        }else {
            return ResponseEntity.ok(ResponseData.success(sudoRuleService.list(domainId)));
        }
    }

    @GetMapping("/save")
    public ResponseEntity<ResponseData> save(Long domainId, SudoRuleDTO rule){

        sudoRuleService.save(domainId,rule);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/delete")
    public ResponseEntity<ResponseData> delete(Long domainId, String cn){

        sudoRuleService.delete(domainId,cn);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/listUserGroup")
    public ResponseEntity<ResponseData> listUserGroup(Long domainId,String cn){

        return ResponseEntity.ok(ResponseData.success(sudoRuleService.listUserGroup(domainId,cn)));
    }

    @GetMapping("/addUserGroup")
    public ResponseEntity<ResponseData> addUserGroup(Long domainId, SudoRefDTO ref){

        sudoRuleService.addUserGroup(domainId,ref);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/removeUserGroup")
    public ResponseEntity<ResponseData> removeUserGroup(Long domainId, SudoRefDTO ref){

        sudoRuleService.removeUserGroup(domainId,ref);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/listHost")
    public ResponseEntity<ResponseData> listHost(Long domainId,String cn){

        return ResponseEntity.ok(ResponseData.success(sudoRuleService.listHost(domainId,cn)));
    }

    @GetMapping("/addHost")
    public ResponseEntity<ResponseData> addHost(Long domainId, SudoRefDTO ref){

        sudoRuleService.addHost(domainId,ref);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/removeHost")
    public ResponseEntity<ResponseData> removeHost(Long domainId, SudoRefDTO ref){

        sudoRuleService.removeHost(domainId,ref);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/listCmdGroup")
    public ResponseEntity<ResponseData> listCmdGroup(Long domainId,String cn){

        List<Map<String, Object>> allow = sudoRuleService.listAllowCmdGroup(domainId,cn);
        for(Map<String, Object> rec : allow){
            ((Map)rec.get("result")).put("type", "allow");
        }

        List<Map<String, Object>> deny = sudoRuleService.listDenyCmdGroup(domainId,cn);
        for(Map<String, Object> rec : deny){
            ((Map)rec.get("result")).put("type", "deny");
        }
        allow.addAll(deny);

        return ResponseEntity.ok(ResponseData.success(allow));
    }

    @GetMapping("/listAllowCmdGroup")
    public ResponseEntity<ResponseData> listAllowCmdGroup(Long domainId,String cn){

        return ResponseEntity.ok(ResponseData.success(sudoRuleService.listAllowCmdGroup(domainId,cn)));
    }

    @GetMapping("/addAllowCmdGroup")
    public ResponseEntity<ResponseData> addAllowCmdGroup(Long domainId, SudoRefDTO ref){

        sudoRuleService.addAllowCmdGroup(domainId,ref);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/listDenyCmdGroup")
    public ResponseEntity<ResponseData> listDenyCmdGroup(Long domainId,String cn){

        return ResponseEntity.ok(ResponseData.success(sudoRuleService.listDenyCmdGroup(domainId,cn)));
    }

    @GetMapping("/addDenyCommand")
    public ResponseEntity<ResponseData> addDenyCommand(Long domainId, SudoRefDTO ref){

        sudoRuleService.addDenyCommand(domainId,ref);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/removeAllowCommand")
    public ResponseEntity<ResponseData> removeAllowCommand(Long domainId, SudoRefDTO ref){

        sudoRuleService.removeAllowCommand(domainId,ref);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/removeDenyCommand")
    public ResponseEntity<ResponseData> removeDenyCommand(Long domainId, SudoRefDTO ref){

        sudoRuleService.removeDenyCommand(domainId,ref);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/listAllUserGroup")
    public ResponseEntity<ResponseData> listAllUserGroup(Long domainId){

        List list = sudoRuleService.listAllUserGroup(domainId);

        return ResponseEntity.ok(ResponseData.success(list));
    }
}
