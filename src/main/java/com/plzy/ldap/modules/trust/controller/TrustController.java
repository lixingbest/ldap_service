package com.plzy.ldap.modules.trust.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.framework.utils.TextUtil;
import com.plzy.ldap.modules.trust.domain.TLdapTrust;
import com.plzy.ldap.modules.trust.dto.TrustDomainParams;
import com.plzy.ldap.modules.trust.dto.TrustOUParams;
import com.plzy.ldap.modules.trust.dto.TrustUserParams;
import com.plzy.ldap.modules.trust.service.TLdapTrustService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/trust")
@Slf4j
public class TrustController {

    @Autowired
    private TLdapTrustService trustService;

    @GetMapping("/tree")
    public ResponseEntity<ResponseData> tree(String uid){

        return ResponseEntity.ok(ResponseData.success(trustService.tree(uid)));
    }

    /**
     * 设置信任某个用户的所有域
     *
     * @param params
     * @return
     */
    @GetMapping("/trustUser")
    public ResponseEntity<ResponseData> trustUser(TrustUserParams params){

        // 首先删除之前此用户的相关信任数据
        trustService.remove(new LambdaQueryWrapper<TLdapTrust>()
                .eq(TLdapTrust::getType,(byte)3)
                .eq(TLdapTrust::getTrustUid,params.getCurrUId())
                .eq(TLdapTrust::getEnable,(byte)0));

        List<Long> domainIds = TextUtil.ids2LongList(params.getDomainIds());
        for(long domainId : domainIds){

            TLdapTrust trust = new TLdapTrust();
            trust.setType((byte)3);
            trust.setDomainId(domainId);
            trust.setTrustDomainId(params.getCurrDomainId());
            trust.setTrustOuId(params.getCurrOUId());
            trust.setTrustUid(params.getCurrUId());
            trust.setEnable((byte)0);
            trust.setIsSync((byte)0);
            trustService.save(trust);
        }

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/trustOU")
    public ResponseEntity<ResponseData> trustOU(TrustOUParams params){

        // 首先删除之前此域的相关信任数据
        trustService.remove(new LambdaQueryWrapper<TLdapTrust>()
                .eq(TLdapTrust::getType,(byte)2)
                .eq(TLdapTrust::getDomainId, params.getDomainId())
                .eq(TLdapTrust::getEnable,(byte)0));

        List<Long> ouList = TextUtil.ids2LongList(params.getOuIds());
        for(long ouId : ouList){

            TLdapTrust trust = new TLdapTrust();
            trust.setType((byte)2);
            trust.setDomainId(params.getDomainId());
            trust.setTrustDomainId(params.getTrustDomainId());
            trust.setTrustOuId(ouId);
            trust.setEnable((byte)0);
            trustService.save(trust);
        }

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/trustDomain")
    public ResponseEntity<ResponseData> trustDomain(TrustDomainParams params){

        // 首先删除之前此域的相关信任数据
        trustService.remove(new LambdaQueryWrapper<TLdapTrust>()
                .eq(TLdapTrust::getType,(byte)1)
                .eq(TLdapTrust::getDomainId, params.getDomainId())
                .eq(TLdapTrust::getEnable,(byte)0));

        List<Long> domainList = TextUtil.ids2LongList(params.getTrustDomainIds());
        for(long domainId : domainList){

            TLdapTrust trust = new TLdapTrust();
            trust.setType((byte)1);
            trust.setDomainId(params.getDomainId());
            trust.setTrustDomainId(domainId);
            trust.setEnable((byte)0);
            trustService.save(trust);
        }

        return ResponseEntity.ok(ResponseData.success());
    }
}
