package com.plzy.ldap.modules.cross_trust.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.framework.ldap.protocol.LDAPResponse;
import com.plzy.ldap.framework.ldap.service.LDAPRemoteService;
import com.plzy.ldap.modules.cross_trust.domain.TLdapCrossTrust;
import com.plzy.ldap.modules.cross_trust.dto.TLdapCrossTrustDTO;
import com.plzy.ldap.modules.cross_trust.service.TLdapCrossTrustService;
import com.plzy.ldap.modules.cross_trust.mapper.TLdapCrossTrustMapper;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.domainuser.service.DomainUserService;
import com.plzy.ldap.modules.ou.domain.TLdapOu;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;

/**
* @author lixingbest
* @description 针对表【t_ldap_cross_trust(跨域信任设置)】的数据库操作Service实现
* @createDate 2023-02-25 14:01:58
*/
@Service
@Slf4j
public class TLdapCrossTrustServiceImpl extends ServiceImpl<TLdapCrossTrustMapper, TLdapCrossTrust>
    implements TLdapCrossTrustService{

    @Autowired
    private TLdapCrossTrustMapper crossTrustMapper;

    @Autowired
    private DomainUserService domainUserService;

    @Autowired
    private TLdapDomainService domainService;

    @Autowired
    private LDAPRemoteService ldapRemoteService;

    @Override
    public List<TLdapCrossTrustDTO> getTrustUsers(Long srcDomainId, Long targetDomainId, Long ouId, String uid) {

        List<TLdapCrossTrustDTO> list = crossTrustMapper.getTrustUsers(srcDomainId,targetDomainId,ouId,uid);

        // 从ldap系统中加载用户数据
        for(TLdapCrossTrustDTO item : list){
            Map userinfo= domainUserService.getByUid(item.getTargetDomainId(),item.getUid());
            item.setUserInfo(userinfo);
        }

        return list;
    }

    @Override
    public void cancelTrust(List<Long> ids) {

        List<TLdapCrossTrust> list = listByIds(ids);

        // 执行取消信任
        for(TLdapCrossTrust item : list){
            domainUserService.disable(item.getSrcDomainId(),item.getUid());

            // 更新数据库为未同步状态
            item.setEnable(1);
            updateById(item);
        }
    }

    @Override
    @PostConstruct
    public void execTrust() {

        // 查询所有待同步的信任记录
        List<TLdapCrossTrust> crossTrustList = crossTrustMapper.getNewRecords();

        if(crossTrustList.size() ==0 ){
            log.info("没有待同步的信任记录！");
            return;
        }

        Set<String> domainIds = new HashSet<>();
        for(TLdapCrossTrust trust : crossTrustList){
            domainIds.add(trust.getSrcDomainId() + "-" + trust.getTargetDomainId());
        }
        log.info("待同步的域信息为："+domainIds);

        for(String id : domainIds) {

            try{
                String[] items = id.split("-");
                TLdapDomain srcDomain = domainService.getById(Long.valueOf(items[0]));
                TLdapDomain targetDomain = domainService.getById(Long.valueOf(items[1]));

                String cmdLine = "{" +
                        "\"method\":\"migrate_ds\"," +
                        "\"params\":[[]," +
                        "{\"ldapuri\":\"ldap://" + targetDomain.getIp() + ":389\"," +
                        "\"bindpw\":\"" + targetDomain.getServicePasswd() + "\"," +
                        "\"usercontainer\":\"cn=users,cn=accounts\"," +
                        "\"groupcontainer\":\"cn=groups,cn=accounts\"," +
                        "\"compat\":true," +
                        "\"exclude_groups\":[\"ipausers\",\"admins\",\"trust admins\",\"editors\"]," +
                        "\"userignoreattribute\":[\"krbPrincipalName\",\"krbextradata\",\"krblastfailedauth\",\"krblastpwdchange\",\"krblastsuccessfulauth\",\"krbloginfailedcount\",\"krbpasswordexpiration\",\"krbticketflags\",\"krbpwdpolicyreference\",\"mepManagedEntry\"]," +
                        "\"version\":\"2.237\"}]}";
                log.info("即将同步信任：" + cmdLine);
                LDAPResponse response = ldapRemoteService.request(srcDomain.getId(), cmdLine);
                log.info("信任执行完成：" + response.getResult());
            }catch (Exception e){
                log.error("同步信任时出错："+e);
            }
        }

        for(TLdapCrossTrust row : crossTrustList){

            // 不能因为出错而停止同步
            try{
                TLdapDomain targetDomain = domainService.getById(row.getTargetDomainId());
                update(new LambdaUpdateWrapper<TLdapCrossTrust>().set(TLdapCrossTrust::getSyncStatus, 1).set(TLdapCrossTrust::getSyncTime,new Date()).eq(TLdapCrossTrust::getId,row.getId()));
            }catch (Exception e){
                log.error("同步信任时出现错误：",e);
            }
        }
    }
}




