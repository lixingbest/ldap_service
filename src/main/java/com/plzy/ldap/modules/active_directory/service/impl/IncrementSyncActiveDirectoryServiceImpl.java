package com.plzy.ldap.modules.active_directory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzy.ldap.modules.active_directory.domain.TLdapAdSyncConfig;
import com.plzy.ldap.modules.active_directory.service.IncrementSyncActiveDirectoryService;
import com.plzy.ldap.modules.active_directory.service.TLdapAdSyncConfigService;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.domainuser.dto.ActiveDomainUserWithExtraCommentsDTO;
import com.plzy.ldap.modules.domainuser.service.DomainUserService;
import com.plzy.ldap.modules.ou.domain.TLdapOu;
import com.plzy.ldap.modules.ou.service.TLdapOuService;
import lombok.extern.slf4j.Slf4j;
import org.ldaptive.*;
import org.ldaptive.ad.control.util.NotificationClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

@Slf4j
@Service
@EnableAsync
public class IncrementSyncActiveDirectoryServiceImpl implements IncrementSyncActiveDirectoryService {

    @Autowired
    private TLdapDomainService domainService;

    @Autowired
    private TLdapOuService ouService;

    @Autowired
    private TLdapAdSyncConfigService adSyncConfigService;

    @Autowired
    private DomainUserService domainUserService;

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 在所有域的所有OU上启动增量更新监听器
     */
//    @PostConstruct
//    @Async
    public void startListen() {
        // 注意使用线程，否则会阻塞启动
        Thread t = new Thread(() -> {
            listen();
        });
        t.setDaemon(true);
        t.start();
    }

    private void listen() {

        log.info("即将启动所有域下所有OU的增量更新监听器");

        try {

            // 查找所有域
            List<TLdapDomain> domains = domainService.listSubdomain();
            for (TLdapDomain domain : domains) {

                // 查找当前域的ad同步配置
                TLdapAdSyncConfig config = adSyncConfigService.getById(domain.getAdConfigRefId());

                // 从AD中加载所有OU列表
                SearchOperation search = new SearchOperation(
                        DefaultConnectionFactory.builder()
                                .config(ConnectionConfig.builder()
                                        .url(config.getSyncUrl())
                                        .connectionInitializers(BindConnectionInitializer.builder()
                                                .dn(config.getAdminName())
                                                .credential(config.getAdminPasswd())
                                                .build())
                                        .build())
                                .build(),
                        config.getBaseDn());

                SearchResponse response = search.execute(SearchRequest.builder()
                        .dn(config.getBaseDn())
                        .filter("(objectClass=organizationalUnit)")
                        .build());
                log.info("域下的OU拉取完成（" + domain + "），即将建立监听");

                int index = 1;
                for (LdapEntry entry : response.getEntries()) {
                    String currDN = entry.getDn();
                    log.info("即将在OU（" + currDN + "）上添加监听，进度：" + (index++) + "/" + response.getEntries().size());

                    Thread thread = new Thread(() -> {

                        SingleConnectionFactory factory = null;

                        try {

                            factory = new SingleConnectionFactory(ConnectionConfig.builder()
                                    .url(config.getSyncUrl())
                                    .connectionInitializers(new BindConnectionInitializer(config.getAdminName(), new Credential(config.getAdminPasswd())))
                                    .build());
                            factory.initialize();
                            NotificationClient client = new NotificationClient(factory);
                            SearchRequest request = SearchRequest.builder()
                                    .dn(currDN)
                                    .filter("(objectClass=*)")
                                    .scope(SearchScope.ONELEVEL)
                                    .build();
                            BlockingQueue<NotificationClient.NotificationItem> results = client.execute(request);
                            while (true) {
                                NotificationClient.NotificationItem item = results.take(); // blocks until result is received
                                if (item.isEntry()) {
                                    LdapEntry entry1 = item.getEntry();
                                    onChange(entry1, domain);
                                } else if (item.isException()) {
                                    log.error("监听时遇到错误：" + item.getException().getMessage());
                                    if (factory != null) {
                                        factory.close();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            log.error("启动对OU（" + entry.getDn() + "）的监听时遇到错误：", e);
                        } finally {
                            if (factory != null) {
                                factory.close();
                            }
                        }
                    });
                    thread.setDaemon(true);
                    thread.start();
                }
            }
        } catch (Exception e) {
            log.error("启动IncrementSyncActiveDirectory时遇到错误：", e);
        }
    }

    @Override
    public void onChange(LdapEntry entry, TLdapDomain domain) {

        String objectClass = entry.getAttribute("objectClass").getStringValues().toString();
        log.info("检测到" + entry.getDn() + "（type=" + objectClass + "）发生了变更：");

        log.info("-------------");
        for (LdapAttribute attribute : entry.getAttributes()) {
            log.info("  -> " + attribute.getName() + " = " + attribute.getStringValues());
        }
        log.info("-------------");

        if (objectClass.contains("organizationalUnit")) {

            // 判断此OU属于新增还是更新
//            String shortDN = entry.getDn().replaceFirst(adSyncConfig.getBaseDn(),"");
//            shortDN = shortDN.substring(0,shortDN.length()-1); // 去掉最后一个,
//            TLdapOu result = ouService.getOne(new QueryWrapper<TLdapOu>().eq("dn", shortDN));
//            if(result == null){
//                log.info("不存在此OU（"+shortDN+"），即将新增");
//
//                TLdapOu obj = new TLdapOu();
//                obj.setName(entry.getAttribute("name").getStringValue());
//                obj.setPid(pid);
//                obj.setPidType(pidType);
//                obj.setDelProtect(1);
//                obj.setDomainId(domain.getId());
//                obj.setDn(entry.getDn());
//                ouService.save(obj);
//
//            }else {
//                log.info("已存在已OU（"+shortDN+"），无需操作");
//            }
        } else if (objectClass.contains("person")) {

            String uid = entry.getAttribute("cn").getStringValue();
            // 如果displayName为null，则设置为uid，因此displayName必填
            String displayName = entry.getAttribute("displayName") != null ? entry.getAttribute("displayName").getStringValue() : uid;
            String mail = entry.getAttribute("mail") != null ? entry.getAttribute("mail").getStringValue() : null;
            String mobile = entry.getAttribute("mobile") != null ? entry.getAttribute("mobile").getStringValue() : null;
            String title = entry.getAttribute("title") != null ? entry.getAttribute("title").getStringValue() : null;
            String ipPhone = entry.getAttribute("ipPhone") != null ? entry.getAttribute("ipPhone").getStringValue() : null;
            String homePhone = entry.getAttribute("homePhone") != null ? entry.getAttribute("homePhone").getStringValue() : null;
            String samaccountname = entry.getAttribute("samaccountname") != null ? entry.getAttribute("samaccountname").getStringValue() : null;
            String description = entry.getAttribute("description") != null ? entry.getAttribute("description").getStringValue() : null;
            // 替换文字中包含的\，否则特殊转义符号会造成ldap无法保存
            if (StringUtils.hasText(description)) {
                description = description.replaceAll("\\\\", "/");
            }
            // 用户控制字段：514用户已禁用，512正常
            String uac = entry.getAttribute("useraccountcontrol").getStringValue();

            // 获取用户所属的OU DN
            String dn = entry.getDn();
            String ouDN = dn.substring(dn.indexOf(",") + 1);
            List<TLdapOu> ouList = ouService.list(new QueryWrapper<TLdapOu>().eq("dn", ouDN));
            if (ouList == null || ouList.size() == 0) {
                log.error("找不到用户" + dn + "的组织单位（dn=" + ouDN + "），此用户无法执行更新");
                return;
            }
            if (ouList.size() > 1) {
                log.error("找到了多个ou对象，无法确定唯一ou，无法继续:" + ouList);
                return;
            }
            Long currOuId = ouList.get(0).getId();

            ActiveDomainUserWithExtraCommentsDTO user = new ActiveDomainUserWithExtraCommentsDTO();
            user.setUid(uid);
            user.setGivenname(displayName);
            user.setCn(displayName);
            if (mail != null) {
                user.setMail(mail);
            }
            if (mobile != null) {
                user.setMobile(mobile);
            }
            if (title != null) {
                user.setTitle(title);
            } else {
                // 更新域用户信息时title必须要传，否则会丢失title下的options信息
                user.setTitle("无");
            }
            if (ipPhone != null) {
                user.setPostalcode(ipPhone);
            }
            if (homePhone != null) {
                user.setTelephonenumber(homePhone);
            }
            if (samaccountname != null) {
                user.setEmployeenumber(samaccountname);
            }
            user.setOu(currOuId + "");
            if (description != null) {
                user.setComments1(description);
            }

            try {

                // 判断是否已存在这个用户
                Map existUser = domainUserService.getByUid(domain.getId(), uid);

                // 处理备注内容
                if (existUser != null && existUser.get("ou") != null && ((List) existUser.get("ou")).size() > 0) {
                    Map value = objectMapper.readValue(((List) existUser.get("ou")).get(0) + "", Map.class);
                    if (value.get("comments2") != null) {
                        user.setComments2(value.get("comments2") + "");
                    }
                    if (value.get("comments3") != null) {
                        user.setComments3(value.get("comments3") + "");
                    }
                }

                // 处理职位内容
                if (existUser != null && existUser.get("title") != null && ((List) existUser.get("title")).size() > 0) {
                    Map value = objectMapper.readValue(((List) existUser.get("title")).get(0) + "", Map.class);
                    if (value.get("job") != null) {
                        user.setJob(value.get("job") + "");
                    }
                }

                if (existUser == null) {
                    log.info("即将新增域用户:" + user);
                    // 只有新增用户设置默认密码，更新用户时不能设置
                    user.setUserpassword("qwer1234");
                    domainUserService.save(domain.getId(), user);
                } else {
                    log.info("即将更新域用户:" + user);
                    domainUserService.update(domain.getId(), user);
                }

                // 如果用户为禁用状态，则设置
                if ("514".equals(uac)) {
                    domainUserService.disable(domain.getId(), uid);
                } else if ("512".equals(uac)) {
                    domainUserService.enable(domain.getId(), uid);
                } else {
                    log.warn("未能处理的uac编码：" + uac);
                }

            } catch (Exception e2) {
                log.error("为用户" + uid + "执行ldap操作时出现问题：" + e2);
            }
        }
    }
}
