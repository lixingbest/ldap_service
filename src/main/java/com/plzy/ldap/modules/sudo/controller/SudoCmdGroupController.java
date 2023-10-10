package com.plzy.ldap.modules.sudo.controller;

import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.sudo.dto.SudoCmdGroupDTO;
import com.plzy.ldap.modules.sudo.service.SudoCmdGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/sudo_cmd_group")
@Slf4j
public class SudoCmdGroupController {

    @Autowired
    private SudoCmdGroupService sudoCmdGroupService;

    @Autowired
    private TLdapDomainService domainService;

    @GetMapping("/list")
    public ResponseEntity<ResponseData> list(Long domainId){

        // 如果请求的是根域，则需要合并结果
        if(domainId == 1L){
            List result = new ArrayList();
            for(TLdapDomain domain : domainService.listSubdomain()){
                result.addAll(sudoCmdGroupService.list(domain.getId()));
            }
            return ResponseEntity.ok(ResponseData.success(result));
        }else {
            return ResponseEntity.ok(ResponseData.success(sudoCmdGroupService.list(domainId)));
        }
    }

    @GetMapping("/save")
    public ResponseEntity<ResponseData> save(Long domainId, SudoCmdGroupDTO sudo){

        sudoCmdGroupService.save(domainId,sudo);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/delete")
    public ResponseEntity<ResponseData> delete(Long domainId, String cn){

        sudoCmdGroupService.delete(domainId,cn);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/addSudoCmd")
    public ResponseEntity<ResponseData> addSudoCmd(Long domainId,String groupCN, String cmdCN){

        sudoCmdGroupService.addSudoCmd(domainId,groupCN, cmdCN);

        return ResponseEntity.ok(ResponseData.success());
    }
}
