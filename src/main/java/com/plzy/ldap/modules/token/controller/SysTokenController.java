package com.plzy.ldap.modules.token.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.modules.admin.domain.TSysAdmin;
import com.plzy.ldap.modules.admin.service.TSysAdminService;
import com.plzy.ldap.modules.token.service.TSysTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/token")
public class SysTokenController {

    @Autowired
    private TSysTokenService tTokenService;

    @Autowired
    private TSysAdminService tUserService;

    @GetMapping("/get")
    public ResponseEntity<ResponseData> get(TSysAdmin user){

        // 判断用户是否存在
        TSysAdmin result = tUserService.getOne(new LambdaQueryWrapper<TSysAdmin>().eq(TSysAdmin::getTelephone, user.getTelephone()).eq(TSysAdmin::getPassword, user.getPassword()));
        if(result == null){
            return ResponseEntity.ok(ResponseData.error("999999","账号信息错误！"));
        }
        return ResponseEntity.ok(ResponseData.success(tTokenService.getToken(user)));
    }
}
