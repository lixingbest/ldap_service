package com.plzy.ldap.modules.active_directory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzy.ldap.modules.active_directory.domain.TLdapAdSyncConfig;
import com.plzy.ldap.modules.active_directory.domain.TLdapAdSyncFilter;
import com.plzy.ldap.modules.active_directory.domain.TLdapAdSyncJob;
import com.plzy.ldap.modules.active_directory.domain.TLdapAdSyncJobDetails;
import com.plzy.ldap.modules.active_directory.service.*;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.domainuser.dto.ActiveDomainUserWithExtraCommentsDTO;
import com.plzy.ldap.modules.domainuser.service.DomainUserService;
import com.plzy.ldap.modules.ou.domain.TLdapOu;
import com.plzy.ldap.modules.ou.service.TLdapOuService;
import lombok.extern.slf4j.Slf4j;
import org.ldaptive.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@Slf4j
public class SyncActiveDirectoryServiceImpl implements SyncActiveDirectoryService {

    @Autowired
    private TLdapDomainService domainService;

    @Autowired
    private TLdapAdSyncConfigService adSyncConfigService;

    @Autowired
    private TLdapOuService ouService;

    @Autowired
    private DomainUserService domainUserService;

    @Autowired
    private TLdapAdSyncFilterService adSyncFilterService;

    @Autowired
    private TLdapAdSyncJobService jobService;

    @Autowired
    private TLdapAdSyncJobDetailsService jobDetailsService;

    private ObjectMapper objectMapper = new ObjectMapper();

    // 记录域用户的同步明细
    private List<TLdapAdSyncJobDetails> userJobDetails = new ArrayList<>();
    private Integer userTotal = 0;
    private Integer userSuccess = 0;
    private Integer userError = 0;

    @Override
    public String syncAll(Long domainId) {

        return syncOU(domainId, true);
    }

    /**
     * 同步组织单位
     *
     * @param domainId
     * @return
     */
    @Override
    public String syncOU(Long domainId, boolean isSyncDomainUser) {

        // 存储日志的对象
        StringBuilder logObj = new StringBuilder();
        logInfo(logObj, "即将根据域domainId=" + domainId + "来查新同步配置OU信息");

        TLdapDomain domain = domainService.getById(domainId);
        logInfo(logObj, "查询到的域信息为=" + domain);

        TLdapAdSyncConfig adSyncConfig = adSyncConfigService.getById(domain.getAdConfigRefId());
        logInfo(logObj, "查询到的ad同步对象为=" + adSyncConfig);

        // 是否有ad配置项
        if (adSyncConfig == null) {
            logInfo(logObj, "没有找到域的ad同步对象，跳过");
            return logObj.toString();
        }

        // 插入同步job主记录
        TLdapAdSyncJob newJob = new TLdapAdSyncJob();
        newJob.setDomainId(domainId);
        newJob.setBeginTime(new Date());
        newJob.setOuTotal(0);
        newJob.setOuSuccess(0);
        newJob.setOuError(0);
        newJob.setUserTotal(0);
        newJob.setUserSuccess(0);
        newJob.setUserError(0);
        newJob.setResult(2);
        jobService.save(newJob);

        Long jobId = newJob.getId();

        // 查找当前域下的过滤器
        List<TLdapAdSyncFilter> filters = adSyncFilterService.list(new QueryWrapper<TLdapAdSyncFilter>().eq("sync_config_id", adSyncConfig.getId()).eq("type", 0));
        logInfo(logObj, "加载到过滤列表=" + filters);

        SearchOperation search = new SearchOperation(
                DefaultConnectionFactory.builder()
                        .config(ConnectionConfig.builder()
                                .url(adSyncConfig.getSyncUrl())
                                .connectionInitializers(BindConnectionInitializer.builder()
                                        .dn(adSyncConfig.getAdminName())
                                        .credential(adSyncConfig.getAdminPasswd())
                                        .build())
                                .build())
                        .build(),
                adSyncConfig.getBaseDn());

        // 待同步总数
        int total = 0;
        // 成功同步总数
        int success = 0;
        // 找不到父OU
        int notFoundParentOU = 0;

        // 记录任务明细
        List<TLdapAdSyncJobDetails> jobDetails = new ArrayList<>();

        try {
            logInfo(logObj, "即将向AD发起查询");
            SearchResponse response = search.execute(adSyncConfig.getOuFilterExpr());
            logInfo(logObj, "AD查询成功，条数=" + response.entrySize() + "，即将更新LDAP系统OU表");

            List<Map<String, Object>> ouList = new LinkedList<>();
            for (LdapEntry entry : response.getEntries()) {
                String dn = entry.getDn();

                // 判断当前ou是否在过滤器中，如果在，则过滤这个ou
                boolean isNext = true;
                for (TLdapAdSyncFilter filter : filters) {
                    if (dn.indexOf(filter.getExpr()) != -1) {
                        isNext = false;
                        break;
                    }
                }
                if (!isNext) {
                    logInfo(logObj, "检测到此OU（" + dn + "）在过滤列表中，跳过");
                    continue;
                }

                String shortDN = dn.replaceFirst(adSyncConfig.getBaseDn(), "").replaceAll("OU=", "");

                Map<String, Object> ouMap = new HashMap();
                ouMap.put("dn", dn);
                ouMap.put("ou", shortDN.split(","));
                ouList.add(ouMap);
            }
            ouList.sort((o1, o2) -> {
                String[] o1a = (String[]) o1.get("ou");
                String[] o2a = (String[]) o2.get("ou");

                if (o1a.length == o2a.length) {
                    return 0;
                }

                if (o1a.length > o2a.length) {
                    return 1;
                } else {
                    return -1;
                }
            });

            log.info("排序OU完成：");
            for (Map<String, Object> item : ouList) {
                log.info(Arrays.toString((String[]) item.get("ou")));
            }
            logInfo(logObj, "即将更新数据库");

            List<TLdapOu> list = ouService.list(new LambdaQueryWrapper<TLdapOu>().orderByDesc(TLdapOu::getOrdIdx));
            TLdapOu tLdapOu = list.get(0);
            int ordIdx = tLdapOu.getOrdIdx();


            for (Map<String, Object> record : ouList) {

                ordIdx = ordIdx+1;

                String dn = record.get("dn").toString();
                String[] ou = (String[]) record.get("ou");

                String currOuName = null;
                byte pidType = 0; //0:domain,1:ou
                long pid = domainId;

                if (ou.length > 0) {
                    currOuName = ou[0];

                    logInfo(logObj, "即将更新组织单位：" + Arrays.toString(ou));
                    total++;

                    if (ou.length > 1) {
                        pidType = 1;

                        // 查询此ou的父级ou是否存在，如果不存在则无法执行
                        String parentDN = dn.substring(dn.indexOf(",") + 1);
                        logInfo(logObj, "查询此ou（d=" + dn + "）的父级ou（dn=" + parentDN + "）是否存在");
                        List<TLdapOu> parentOuObj = ouService.list(new QueryWrapper<TLdapOu>().eq("dn", parentDN));
                        if (parentOuObj == null || parentOuObj.size() == 0) {
                            logError(logObj, "同步组织单位时出现严重问题，没有找到" + currOuName + "的父组织单位，无法继续");
                            notFoundParentOU++;
                            break;
                        }
                        if (parentOuObj.size() > 1) {
                            logError(logObj, "找到了多个" + currOuName + "的父组织单位，可能由于脏数据导致，无法继续");
                            notFoundParentOU++;
                            break;
                        }
                        pid = parentOuObj.get(0).getId();
                    }

                    // 不能因为异常而中断循环
                    try {
                        // 查询是否已存在这个名字的ou
                        TLdapOu targetOU = ouService.getOne(new QueryWrapper<TLdapOu>().eq("dn", dn));
                        if (targetOU == null) {

                            TLdapOu obj = new TLdapOu();
                            obj.setName(currOuName);
                            obj.setPid(pid);
                            obj.setPidType(pidType);
                            obj.setDelProtect(1);
                            obj.setDomainId(domainId);
                            obj.setDn(dn);
                            obj.setOrdIdx(ordIdx);
                            ouService.save(obj);
                            logInfo(logObj, "已新增ou：" + obj);
                        } else {

                            targetOU.setName(currOuName);
                            targetOU.setPid(pid);
                            targetOU.setPidType(pidType);
                            targetOU.setDelProtect(1);
                            targetOU.setDomainId(domainId);
                            targetOU.setDn(dn);
                            ouService.updateById(targetOU);
                            logInfo(logObj, "已存在此名称和pid的ou，即将更新：" + targetOU);
                        }

                        // 更新任务明细记录
                        TLdapAdSyncJobDetails details = new TLdapAdSyncJobDetails();
                        details.setAdSyncJobId(jobId);
                        details.setTime(new Date());
                        details.setType(0);
                        details.setUpdateType(targetOU == null ? 0 : 1);
                        details.setName(dn);
                        details.setResult(0);
                        jobDetails.add(details);
                    } catch (Exception e) {
                        logError(logObj, "保存OU时遇到错误：" + e);
                    }

                    log.info("----------------------");
                    log.info("OU同步进度：" + Math.round(((float) total / (float) response.entrySize() * 100)) + "%");
                    log.info("----------------------");

                    success++;
                }
            }

        } catch (Exception e) {
            logError(logObj, "从AD同步OU时出现错误：" + e.getMessage());
            log.error("从AD同步OU时出现错误:", e);
        }

        logInfo(logObj, "OU同步完成，待同步=" + total + "，成功=" + success + "，未找到父OU=" + notFoundParentOU);

        // 更新同步记录
        TLdapAdSyncJob job = jobService.getById(jobId);
        job.setOuTotal(total);
        job.setOuSuccess(success);
        job.setOuError(notFoundParentOU);
        job.setLog(logObj.toString());
        jobService.updateById(job);
        jobDetailsService.saveBatch(jobDetails);

        if (isSyncDomainUser) {

            // 重新统计
            userJobDetails.clear();
            userTotal = 0;
            userSuccess = 0;
            userError = 0;

            logInfo(logObj, "即将同步所有域用户");
            // 仅查询最顶层的ou，因为其也会包含所有子ou下的域用户
            List<TLdapOu> ouList = ouService.list(new QueryWrapper<TLdapOu>().eq("domain_id", domainId).eq("pid_type", 0));
            for (TLdapOu ou : ouList) {

                // 查询此OU是否在过滤名单中
                boolean isNext = true;
                for (TLdapAdSyncFilter filter : filters) {
                    if (ou.getDn().indexOf(filter.getExpr()) != -1) {
                        isNext = false;
                        break;
                    }
                }
                if (!isNext) {
                    logInfo(logObj, "检测到此OU（" + ou.getDn() + "）在过滤列表中，跳过");
                    continue;
                }

                logInfo(logObj, "即将同步ou=" + ou + "下的域用户");
                String log = syncDomainUser(ou.getId(), jobId);
                logObj.append(log);
            }

            // 更新同步记录
            TLdapAdSyncJob job2 = jobService.getById(jobId);
            job2.setEndTime(new Date());
            job2.setUserTotal(userTotal);
            job2.setUserSuccess(userSuccess);
            job2.setUserError(userError);
            job2.setLog(logObj.toString());
            job2.setResult(0);
            jobService.updateById(job2);
            jobDetailsService.saveBatch(userJobDetails);
        }

        return logObj.toString();
    }

    /**
     * 同步用户
     *
     * @param ouId
     * @return
     */
    @Override
    public String syncDomainUser(Long ouId, Long jobId) {

        // 存储日志的对象
        StringBuilder logObj = new StringBuilder();

        TLdapOu ouObj = ouService.getById(ouId);
        TLdapDomain domainObj = domainService.getById(ouObj.getDomainId());

        long adConfigId = domainService.getById(domainObj.getId()).getAdConfigRefId();
        TLdapAdSyncConfig config = adSyncConfigService.getById(adConfigId);
        logInfo(logObj, "查找到域的AD同步配置为=" + config);

        // 查找当前域下的过滤器
        List<TLdapAdSyncFilter> filters = adSyncFilterService.list(new QueryWrapper<TLdapAdSyncFilter>().eq("sync_config_id", config.getId()).eq("type", 0));
        logInfo(logObj, "加载到过滤列表=" + filters);

        try {
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
                    ouObj.getDn());

            SearchResponse response = search.execute(config.getUserFilterExpr());
            int size = response.entrySize();
            logInfo(logObj, "共计查找到" + size + "个用户");

            int currOUUserSuccess = 0;  // 统计当前组织机构下的人员同步进度
            for (LdapEntry entry : response.getEntries()) {
                String dn = entry.getDn();

                // 检测此用户是否在过滤列表中
                boolean isNext = true;
                for (TLdapAdSyncFilter filter : filters) {
                    if (dn.indexOf(filter.getExpr()) != -1) {
                        isNext = false;
                        break;
                    }
                }
                if (!isNext) {
                    logInfo(logObj, "检测到此用户（" + dn + "）在过滤列表中，跳过");
                    continue;
                }

                userTotal++;

//                String[] dnItems = dn.replaceFirst(config.getBaseDn(), "").split(",");
//                log.info("当前扫描的用户："+Arrays.toString(dnItems));
//                for(LdapAttribute attr : entry.getAttributes()){
//                    // 只打印非二进制的属性
//                    if(!attr.isBinary()) {
//                        log.info("  -> " + attr.getName() + "=" + attr.getStringValue());
//                    }
//                }

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
                String ouDN = dn.substring(dn.indexOf(",") + 1);
                List<TLdapOu> ouList = ouService.list(new QueryWrapper<TLdapOu>().eq("dn", ouDN));
                if (ouList == null || ouList.size() == 0) {
                    logError(logObj, "找不到用户" + dn + "的组织单位（dn=" + ouDN + "），此用户无法执行更新");
                    userError++;
                    continue;
                }
                if (ouList.size() > 1) {
                    logError(logObj, "找到了多个ou对象，无法确定唯一ou，无法继续:" + ouList);
                    userError++;
                    continue;
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

                // 不能因为一个用户执行ldap出错而中断后续操作
                try {

                    // 判断是否已存在这个用户
                    Map existUser = domainUserService.getByUid(domainObj.getId(), uid);
                    if (existUser != null && existUser.get("ou") != null && ((List) existUser.get("ou")).size() > 0) {
                        Map value = objectMapper.readValue(((List) existUser.get("ou")).get(0) + "", Map.class);
                        if (value.get("comments2") != null) {
                            user.setComments2(value.get("comments2") + "");
                        }
                        if (value.get("comments3") != null) {
                            user.setComments3(value.get("comments3") + "");
                        }
                    }

                    if (existUser != null && existUser.get("title") != null && ((List) existUser.get("title")).size() > 0) {
                        Map value = objectMapper.readValue(((List) existUser.get("title")).get(0) + "", Map.class);
                        if (value.get("job") != null) {
                            user.setJob(value.get("job") + "");
                        }
                    }

                    if (existUser == null) {
                        logInfo(logObj, "即将新增域用户:" + user);
                        // 只有新增用户设置默认密码，更新用户时不能设置
                        user.setUserpassword("qwer1234");
                        domainUserService.save(domainObj.getId(), user);
                    } else {
                        logInfo(logObj, "即将更新域用户:" + user);
                        domainUserService.update(domainObj.getId(), user);
                    }

                    // 如果用户为禁用状态，则设置
//                    if ("514".equals(uac)) {
//                        domainUserService.disable(domainObj.getId(), uid);
//                    }else if("512".equals(uac)){
//                        domainUserService.enable(domainObj.getId(),uid);
//                    }else {
//                        logWarn(logObj,"未能处理的uac编码："+uac);
//                    }

                    userSuccess++;
                    currOUUserSuccess++;

                    // 更新任务明细记录
                    TLdapAdSyncJobDetails details = new TLdapAdSyncJobDetails();
                    details.setAdSyncJobId(jobId);
                    details.setTime(new Date());
                    details.setType(1);
                    details.setUpdateType(existUser == null ? 0 : 1);
                    details.setName(dn);
                    details.setResult(0);
                    userJobDetails.add(details);

                } catch (Exception e2) {
                    userError++;
                    logError(logObj, "为用户" + uid + "执行ldap操作时出现问题：" + e2);
                    log.error("为用户" + uid + "执行ldap操作时出现问题：", e2);
                }

                log.info("----------------------");
                log.info("当前组织单位(ouid=" + ouId + ")下的域用户同步进度：" + Math.round(((float) currOUUserSuccess / (float) size * 100)) + "%");
                log.info("----------------------");

            }
        } catch (Exception e) {
            logError(logObj, "执行user同步时出现问题：" + e);
            log.error("执行user同步时出现问题：", e);
        }

        logInfo(logObj, "域用户同步完成，待同步总数=" + userTotal + "，成功=" + userSuccess + "，失败=" + userError);

        return logObj.toString();
    }

    /**
     * 打印消息
     *
     * @param logObj
     * @param content
     */
    private void logInfo(StringBuilder logObj, String content) {
        log.info(content);
        if (logObj != null) {
            logObj.append("<span style='color:green;'>【消息】：</span>").append(content).append("<br/>");
        }
    }

    /**
     * 打印错误
     *
     * @param logObj
     * @param content
     */
    private void logError(StringBuilder logObj, String content) {
        log.error(content);
        if (logObj != null) {
            logObj.append("<span style='color:red;'>【错误】：</span>").append(content).append("<br/>");
        }
    }

    /**
     * 打印警告
     *
     * @param logObj
     * @param content
     */
    private void logWarn(StringBuilder logObj, String content) {
        log.warn(content);
        if (logObj != null) {
            logObj.append("<span style='color:orange;'>【警告】：</span>").append(content).append("<br/>");
        }
    }
}
