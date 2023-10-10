package com.plzy.ldap.modules.usergroup.controller;

import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.modules.usergroup.dto.UserGroupRefDTO;
import com.plzy.ldap.modules.usergroup.dto.UserGroupWithOUDTO;
import com.plzy.ldap.modules.usergroup.service.UserGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/usergroup")
@Slf4j
public class UserGroupController {

    @Autowired
    private UserGroupService userGroupService;

    @GetMapping("/list")
    public ResponseEntity<ResponseData> list(Long domainId, String ouCN, String groupCN,String uid) {

        if (domainId.equals(1L)) {
            return ResponseEntity.ok(ResponseData.success(new ArrayList<>()));
        }

        List list = userGroupService.list(domainId, ouCN, groupCN,uid);

        return ResponseEntity.ok(ResponseData.success(list));
    }

    @GetMapping("/listAllNames")
    public ResponseEntity<ResponseData> listAllNames(Long domainId) {
        return ResponseEntity.ok(ResponseData.success(userGroupService.listAllNames(domainId)));
    }

    @GetMapping("/save")
    public ResponseEntity<ResponseData> save(Long domainId, UserGroupWithOUDTO groupDTO) {

        try {
            userGroupService.save(domainId, groupDTO);
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseData.error("999999", "用户组保存错误！"));
        }
        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/delete")
    public ResponseEntity<ResponseData> delete(Long domainId, String cn) {

        userGroupService.delete(domainId, cn);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/listUser")
    public ResponseEntity<ResponseData> listUser(Long domainId, String groupCN) {

        return ResponseEntity.ok(ResponseData.success(userGroupService.listUser(domainId, groupCN)));
    }

    @GetMapping("/addUser")
    public ResponseEntity<ResponseData> addUser(Long domainId, UserGroupRefDTO ref) {

        userGroupService.addUser(domainId, ref);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/removeUser")
    public ResponseEntity<ResponseData> removeUser(Long domainId, UserGroupRefDTO ref) {

        userGroupService.removeUser(domainId, ref);

        return ResponseEntity.ok(ResponseData.success());
    }
}
