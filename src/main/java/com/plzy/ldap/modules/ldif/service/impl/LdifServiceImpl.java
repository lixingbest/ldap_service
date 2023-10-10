package com.plzy.ldap.modules.ldif.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.plzy.ldap.framework.ldap.protocol.LDAPResponse;
import com.plzy.ldap.framework.ldap.service.LDAPRemoteService;
import com.plzy.ldap.modules.domainuser.dto.ActiveDomainUserWithExtraCommentsDTO;
import com.plzy.ldap.modules.domainuser.service.DomainUserService;
import com.plzy.ldap.modules.ldif.service.LdifService;
import com.plzy.ldap.modules.ou.domain.TLdapOu;
import com.plzy.ldap.modules.ou.service.TLdapOuService;
import lombok.extern.slf4j.Slf4j;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.SearchResponse;
import org.ldaptive.io.LdifReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.util.*;

@Service
@Slf4j
public class LdifServiceImpl implements LdifService {

    @Autowired
    private LDAPRemoteService rpcService;

    @Autowired
    private TLdapOuService ouService;

    @Autowired
    private DomainUserService domainUserService;

    private Set<String> uidList = new HashSet<>();

    private Map<String, TLdapOu> ouList = new HashMap<>();

    private StringBuilder logs = new StringBuilder();

    /**
     * 预先加载域用户uid列表，便于在导入用户时判断是否存在
     */
    public void preLoadDomainUsers(Long domainId){

        logs.append("<span style='color:green;font-weight:bold;margin-right:6px;'>消息：</span>准备预加载域用户列表...<br/>");
        uidList.clear();

        LDAPResponse response = rpcService.request(domainId,"{\"method\":\"user_find\",\"params\":[[\"\"],{\"pkey_only\":true,\"sizelimit\":0,\"version\":\"2.240\"}]}");
        List<Map> userList = (List<Map>)response.getResult().get("result");
        for(Map item : userList){
            String uid = ((List)item.get("uid")).get(0) + "";
            uidList.add(uid);
        }
        logs.append("<span style='color:green;font-weight:bold;margin-right:6px;'>消息：</span>域用户预加载完成，共计" + uidList.size() + "个<br/>");
    }

    @Override
    public void ouImport(Long domainId,Long parentOUId, String domainDN, String fileName) {

        logs.append("<span style='color:green;font-weight:bold;margin-right:6px;'>消息：</span>----------------开始导入组织单位----------------<br/>");

        // 清空缓存
        ouList.clear();

        try {

            FileReader reader = new FileReader(fileName);
            LdifReader ldifReader = new LdifReader(reader);
            SearchResponse response = ldifReader.read();

            for(LdapEntry entry : response.getEntries()){
                // 跳过系统OU
                LdapAttribute name = entry.getAttribute("name");
                if(name != null && "Domain Controllers".equals(name.getStringValue())){
                    continue;
                }
                String[] dnItem = entry.getDn()
                        .replaceFirst(domainDN, "")
                        .replaceAll("OU=","")
                        .split(",");
                log.info(Arrays.toString(dnItem));

                // 倒序循环，从父节点开始新增
                for(int i = dnItem.length - 1; i > -1; i --){
                    logs.append("<span style='color:green;font-weight:bold;margin-right:6px;'>消息：</span>正在准备新增组织单位："+dnItem[i]+"<br/>");
                    TLdapOu result = ouService.getOne(new LambdaQueryWrapper<TLdapOu>().eq(TLdapOu::getName, dnItem[i]).eq(TLdapOu::getPid, parentOUId));
                    if(result == null){
                        logs.append("<span style='color:green;font-weight:bold;margin-right:6px;'>消息：</span>没有找到目标组织单位（"+dnItem[i]+"），准备新增<br/>");

                        TLdapOu newObj = new TLdapOu();
                        newObj.setName(dnItem[i]);
                        newObj.setPid(parentOUId);
                        // 如果是第一层则父类型是域0，否则即使ou1
                        newObj.setPidType((byte)(i == dnItem.length - 1 ? 0 : 1));
                        newObj.setDelProtect(1);
                        ouService.save(newObj);

                        // 存入缓存，方便导入人员信息时使用，减少数据库查询次数
                        ouList.put(newObj.getName(), newObj);

                        log.info("<span style='color:green;font-weight:bold;margin-right:6px;'>消息：</span>组织单位（"+dnItem[i]+"）新增完成！<br/>");
                    }else{
                        parentOUId = result.getId();
                        logs.append("<span style='color:green;font-weight:bold;margin-right:6px;'>消息：</span>组织单位（"+dnItem[i]+"）已存在，跳过<br/>");
                        log.info("组织单位（"+dnItem[i]+"）已存在，跳过");
                    }
                }
            }

        }catch (Exception e){
            logs.append("<span style='color:red;font-weight:bold;margin-right:6px;'>错误：</span>导入组织单位时遇到错误："+e.getMessage()+"<br/>");
            log.error("导入组织单位时遇到错误：", e);
        }

        logs.append("<span style='color:green;font-weight:bold;margin-right:6px;'>消息：</span>----------------组织单位导入完成----------------<br/>");
    }

    @Override
    public void domainUserImport(Long domainId,String domainDN, String defaultPasswd, String fileName) {

        logs.append("<span style='color:green;font-weight:bold;margin-right:6px;'>消息：</span>----------------开始导入域用户----------------<br/>");

        preLoadDomainUsers(domainId);

        try {

            FileReader reader = new FileReader(fileName);
            LdifReader ldifReader = new LdifReader(reader);
            SearchResponse response = ldifReader.read();

            for(LdapEntry entry : response.getEntries()) {
                String[] dnItem = entry.getDn()
                        .replaceFirst(domainDN, "")
                        .split(",");
                // 跳过系统内置的非业务账号，即父级不是在OU下的用户
                if(dnItem.length == 0 || dnItem[dnItem.length - 1].indexOf("OU=") == -1 || dnItem[dnItem.length - 1].equals("OU=Domain Controllers")){
                    continue;
                }

                String uid = entry.getAttribute("cn").getStringValue();
                logs.append("<span style='color:green;font-weight:bold;margin-right:6px;'>消息：</span>准备新增域用户（"+uid+"）<br/>");

                // 判断是否存在此用户
                if(!uidList.contains(uid)){

                    logs.append("<span style='color:green;font-weight:bold;margin-right:6px;'>消息：</span>域用户（"+uid+"）不存在，即将创建<br/>");

                    // 获取用户的所属ou层级
                    String ouItems[] = entry.getDn()
                            .replaceFirst(domainDN, "")
                            .replaceFirst("CN="+entry.getAttribute("cn").getStringValue()+",","")
                            .replaceAll("OU=","").split(",");
                    // 根据ou名称查找id
//                    TLdapOu targetOu = ouService.getOne(new LambdaQueryWrapper<TLdapOu>().eq(TLdapOu::getName, ouItems[0]));
                    TLdapOu targetOu = ouList.get(ouItems[0]);
                    if(targetOu != null){
                        ActiveDomainUserWithExtraCommentsDTO domainUser = new ActiveDomainUserWithExtraCommentsDTO();
                        domainUser.setUid(entry.getAttribute("cn").getStringValue());
                        domainUser.setGivenname(entry.getAttribute("cn").getStringValue());
                        domainUser.setSn(entry.getAttribute("cn").getStringValue());
                        domainUser.setCn(entry.getAttribute("cn").getStringValue());
                        domainUser.setUserpassword(defaultPasswd); // 默认密码
                        domainUser.setOu(targetOu.getId() + "");

                        LdapAttribute telephonenumber = entry.getAttribute("telephonenumber");
                        if(telephonenumber != null) {
                            domainUser.setTelephonenumber(telephonenumber.getStringValue());
                        }
                        domainUserService.save(domainId,domainUser);

                        logs.append("<span style='color:green;font-weight:bold;margin-right:6px;'>消息：</span>域用户（"+uid+"）创建完成！<br/>");
                    }else {
                        logs.append("<span style='color:orange;font-weight:bold;margin-right:6px;'>警告：</span>找不到域用户所在的ou，跳过创建此用户！<br/>");
                    }
                }else{
                    logs.append("<span style='color:green;font-weight:bold;margin-right:6px;'>消息：</span>已存在域用户（"+uid+"），无需新增！<br/>");
                }
            }


        }catch (Exception e){
            logs.append("<span style='color:green;font-weight:bold;margin-right:6px;'>错误：</span>导入域用户时出现错误："+e.getMessage()+"<br/>");
            log.error("导入域用户时出现错误：", e);
        }

        logs.append("<span style='color:green;font-weight:bold;margin-right:6px;'>消息：</span>----------------域用户导入完成----------------<br/>");
    }

    @Override
    public String getLogs() {
        String content =  logs.toString();
        logs.delete(0, logs.length());

        return content;
    }
}
