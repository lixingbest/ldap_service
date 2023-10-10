package com.plzy.ldap.modules.cross_trust.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.framework.utils.TextUtil;
import com.plzy.ldap.modules.cross_trust.domain.TLdapCrossTrust;
import com.plzy.ldap.modules.cross_trust.dto.TLdapCrossTrustDTO;
import com.plzy.ldap.modules.cross_trust.service.TLdapCrossTrustService;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.domainuser.service.DomainUserService;
import com.plzy.ldap.modules.sysconfig.domain.TSysConf;
import com.plzy.ldap.modules.token.service.TSysTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/cross_trust")
public class CrossTrustController {

    @Autowired
    private TLdapCrossTrustService crossTrustService;

    @Autowired
    private TSysTokenService tokenService;

    @Autowired
    private DomainUserService domainUserService;

    @GetMapping("/enable")
    public ResponseEntity<ResponseData> enable(Long id, Integer enable){

        // 更新数据库
        crossTrustService.update(new LambdaUpdateWrapper<TLdapCrossTrust>().eq(TLdapCrossTrust::getId, id).set(TLdapCrossTrust::getEnable, enable));

        // 更新域用户状态
        TLdapCrossTrust trust = crossTrustService.getById(id);
        if(enable == 0){
            domainUserService.enable(trust.getSrcDomainId(),trust.getUid());
        }else {
            domainUserService.disable(trust.getSrcDomainId(),trust.getUid());
        }

        return ResponseEntity.ok(ResponseData.success());
    }

    @PostMapping("/saveOrUpdate")
    public ResponseEntity<ResponseData> saveOrUpdate(@RequestBody List<TLdapCrossTrust> trustList){

        // 更新数据库
        if(trustList != null && trustList.size() > 0) {
            for(TLdapCrossTrust trust : trustList) {
                trust.setCreateUserId(tokenService.getCurrUser().getId());
                trust.setCreateTime(new Date());
                trust.setSyncStatus(0);
                crossTrustService.saveOrUpdate(trust);
            }
        }

        // 通过信任
        crossTrustService.execTrust();

        // 启用用户，因此上次删除后会禁用用户
        if(trustList != null && trustList.size() > 0) {
            for (TLdapCrossTrust trust : trustList) {
                domainUserService.enable(trust.getSrcDomainId(),trust.getUid());
            }
        }

        return ResponseEntity.ok(ResponseData.success());
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ResponseData> remove(String ids){

        List<Long> idList = TextUtil.ids2LongList(ids);

        // 先禁用用户，再删除记录，否则会导致禁用失败
        crossTrustService.cancelTrust(idList);
        crossTrustService.removeByIds(idList);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/getTrustUsers")
    public ResponseEntity<ResponseData> getTrustUsers(Long srcDomainId, Long targetDomainId, Long ouId, String uid){

        if(srcDomainId == null){
            return ResponseEntity.ok(ResponseData.error("973914","domainId不能为空！"));
        }

        List<TLdapCrossTrustDTO> list = crossTrustService.getTrustUsers(srcDomainId,targetDomainId,ouId,uid);
        return ResponseEntity.ok(ResponseData.success(list));
    }
}
