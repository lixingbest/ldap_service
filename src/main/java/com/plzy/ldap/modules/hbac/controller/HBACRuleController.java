package com.plzy.ldap.modules.hbac.controller;

import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.framework.utils.TextUtil;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.hbac.dto.HBACRuleDTO;
import com.plzy.ldap.modules.hbac.service.HBACRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/hbac")
@Slf4j
public class HBACRuleController {

    @Autowired
    private HBACRuleService hbacRuleService;

    @Autowired
    private TLdapDomainService domainService;

    @GetMapping("/list")
    public ResponseEntity<ResponseData> list(Long domainId){

        if(domainId == 1L){

            List all = new ArrayList();
            for(TLdapDomain domain: domainService.listSubdomain()){
                List list = hbacRuleService.list(domain.getId());
                all.addAll(list);
            }
            return ResponseEntity.ok(ResponseData.success(all));
        }

        List single = hbacRuleService.list(domainId);
        return ResponseEntity.ok(ResponseData.success(single));
    }

    @GetMapping("/listUser")
    public ResponseEntity<ResponseData> listUser(Long domainId,String hbacCN){

        List list = hbacRuleService.listUser(domainId,hbacCN);

        return ResponseEntity.ok(ResponseData.success(list));
    }

    @GetMapping("/listHost")
    public ResponseEntity<ResponseData> listHost(Long domainId,String hbacCN){

        List list = hbacRuleService.listHost(domainId,hbacCN);

        return ResponseEntity.ok(ResponseData.success(list));
    }

    @GetMapping("/save")
    public ResponseEntity<ResponseData> save(HBACRuleDTO hbacRuleDTO,Long domainId){

        hbacRuleService.save(domainId,hbacRuleDTO);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/update")
    public ResponseEntity<ResponseData> update(HBACRuleDTO hbacRuleDTO,Long domainId){

        hbacRuleService.update(domainId,hbacRuleDTO);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/remove")
    public ResponseEntity<ResponseData> remove(Long domainId,String hbacCN){

        hbacRuleService.remove(domainId,hbacCN);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/disable")
    public ResponseEntity<ResponseData> disable(Long domainId,String hbacCN){

        hbacRuleService.disable(domainId,hbacCN);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/enable")
    public ResponseEntity<ResponseData> enable(Long domainId, String hbacCN){

        hbacRuleService.enable(domainId,hbacCN);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/addUser")
    public ResponseEntity<ResponseData> addUser(Long domainId, String hbacCN, String uidList){

        List<String> list = new ArrayList();
        if(uidList != null){
            String[] items = uidList.split(",");
            list.addAll(Arrays.asList(items));
        }

        hbacRuleService.addUser(domainId,hbacCN,list);
        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/removeUser")
    public ResponseEntity<ResponseData> removeUser(Long domainId, String hbacCN, String uid){

        hbacRuleService.removeUser(domainId,hbacCN,uid);
        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/addHost")
    public ResponseEntity<ResponseData> addHost(Long domainId, String hbacCN, String hostList){

        List<String> list = new ArrayList();
        if(hostList != null){
            String[] items = hostList.split(",");
            list.addAll(Arrays.asList(items));
        }

        hbacRuleService.addHost(domainId,hbacCN,list);
        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/removeHost")
    public ResponseEntity<ResponseData> removeHost(Long domainId, String hbacCN, String host){

        hbacRuleService.removeHost(domainId,hbacCN,host);
        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/listAllUser")
    public ResponseEntity<ResponseData> listAllUser(Long domainId, String uid){


        return ResponseEntity.ok(ResponseData.success( hbacRuleService.listAllUsers(domainId, uid)));
    }

    @GetMapping("/listAllHost")
    public ResponseEntity<ResponseData> listAllHost(Long domainId, String hostname){


        return ResponseEntity.ok(ResponseData.success(hbacRuleService.listAllHost(domainId, hostname)));
    }
}
