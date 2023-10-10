package com.plzy.ldap.modules.domainuser.controller;

import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.modules.domainuser.dto.ActiveDomainUserWithExtraCommentsDTO;
import com.plzy.ldap.modules.domainuser.service.PreservedDomainUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 备用用户控制器
 */
@RestController
@RequestMapping("/preserved_domainuser")
@Slf4j
public class PreservedDomainUserController {

    @Autowired
    private PreservedDomainUserService domainUserService;

    @GetMapping("/list")
    public ResponseEntity<ResponseData> list(Long domainId){

        List list = domainUserService.listAll(domainId);

        return ResponseEntity.ok(ResponseData.success(list));
    }

    /**
     * 修改域用户信息
     *
     * @param user 仅包含需要更新的字段，必须包含uid字段
     * @param domainId
     * @return
     */
    @GetMapping("/update")
    public ResponseEntity<ResponseData> update(ActiveDomainUserWithExtraCommentsDTO user, Long domainId){

        domainUserService.update(domainId,user);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/delete")
    public ResponseEntity<ResponseData> delete(Long domainId,String uid){

        domainUserService.delete(domainId,uid);

        return ResponseEntity.ok(ResponseData.success());
    }

    /**
     * 恢复用户
     *
     * @param domainId
     * @param uid
     * @return
     */
    @GetMapping("/recover")
    public ResponseEntity<ResponseData> recover(Long domainId,String uid){

        domainUserService.recover(domainId,uid);

        return ResponseEntity.ok(ResponseData.success());
    }
}
