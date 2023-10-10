package com.plzy.ldap.modules.sudo.controller;

import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.sudo.dto.SudoCmdDTO;
import com.plzy.ldap.modules.sudo.service.SudoCmdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/sudo_cmd")
@Slf4j
public class SudoCmdController {

    @Autowired
    private SudoCmdService sudoCmdService;

    @Autowired
    private TLdapDomainService domainService;

    @GetMapping("/list")
    public ResponseEntity<ResponseData> list(Long domainId, String cmdGroupCN){

        // 如果请求的是根域，则需要合并结果
        if(domainId == 1L){
            List result = new ArrayList();
            for(TLdapDomain domain : domainService.listSubdomain()){
                result.addAll(sudoCmdService.list(domain.getId(),cmdGroupCN));
            }
            return ResponseEntity.ok(ResponseData.success(result));
        }else {
            return ResponseEntity.ok(ResponseData.success(sudoCmdService.list(domainId,cmdGroupCN)));
        }
    }

    @GetMapping("/save")
    public ResponseEntity<ResponseData> save(Long domainId, SudoCmdDTO sudo){

        sudoCmdService.save(domainId,sudo);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/delete")
    public ResponseEntity<ResponseData> delete(Long domainId, String sudocmd){

        sudoCmdService.delete(domainId,sudocmd);

        return ResponseEntity.ok(ResponseData.success());
    }
}
