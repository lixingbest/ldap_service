package com.plzy.ldap.modules.domainuser.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.modules.domainuser.dto.ActiveDomainUserWithExtraCommentsDTO;
import com.plzy.ldap.modules.domainuser.service.DomainUserService;
import com.plzy.ldap.modules.sys_log.domain.TSysLog;
import com.plzy.ldap.modules.sys_log.service.TSysLogService;
import com.plzy.ldap.modules.token.domain.TSysToken;
import com.plzy.ldap.modules.token.service.TSysTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/domainuser")
@Slf4j
public class ActiveDomainUserController {

    @Autowired
    private DomainUserService domainUserService;


    @GetMapping("/list")
    public ResponseEntity<ResponseData> list(Long domainId) {

        List list = domainUserService.listAll(domainId);

        return ResponseEntity.ok(ResponseData.success(list));
    }

    @GetMapping("/save")
    public ResponseEntity<ResponseData> save(ActiveDomainUserWithExtraCommentsDTO user, Long domainId) {


        // 判断用户是否存在
        boolean result = domainUserService.isExist(domainId, user.getUid());
        if (!result) {
            domainUserService.save(domainId, user);
            return ResponseEntity.ok(ResponseData.success());
        } else {
            return ResponseEntity.ok(ResponseData.error("999999", "登录名已存在！"));
        }
    }

    /**
     * 修改域用户信息
     *
     * @param user     仅包含需要更新的字段，必须包含uid字段
     * @param domainId
     * @return
     */
    @GetMapping("/update")
    public ResponseEntity<ResponseData> update(ActiveDomainUserWithExtraCommentsDTO user, Long domainId) {

        domainUserService.update(domainId, user);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/disable")
    public ResponseEntity<ResponseData> disable(Long domainId, String uid) {

        domainUserService.disable(domainId, uid);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/enable")
    public ResponseEntity<ResponseData> enable(Long domainId, String uid) {

        domainUserService.enable(domainId, uid);

        return ResponseEntity.ok(ResponseData.success());
    }

    /**
     * 删除域用户
     *
     * @param domainId
     * @param uid
     * @param preserve 删除模式：0保留到域用户，1彻底删除
     * @return
     */
    @GetMapping("/delete")
    public ResponseEntity<ResponseData> delete(Long domainId, String uid, Integer preserve) {

        domainUserService.delete(domainId, uid, preserve == 0);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/deleteAllUser")
    public ResponseEntity<ResponseData> deleteAllUser(Long domainId) {

        if (domainId == null) {
            return ResponseEntity.ok(ResponseData.error("999999", "domainId不能为空"));
        }

        domainUserService.deleteAllUser(domainId);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/resetPasswd")
    public ResponseEntity<ResponseData> resetPasswd(Long domainId, String uid, String password) {

        domainUserService.resetPasswd(domainId, uid, password);

        return ResponseEntity.ok(ResponseData.success());
    }
}
