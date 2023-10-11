package com.plzy.ldap.modules.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.plzy.ldap.framework.ldap.protocol.LDAPResponse;
import com.plzy.ldap.framework.ldap.service.LDAPRemoteService;
import com.plzy.ldap.framework.utils.DateUtil;
import com.plzy.ldap.jobs.HostPingJob;
import com.plzy.ldap.modules.client.domain.TLdapClientInstLog;
import com.plzy.ldap.modules.client.domain.TLdapClientLeaveLog;
import com.plzy.ldap.modules.client.service.TLdapClientInstLogService;
import com.plzy.ldap.modules.client.service.TLdapClientLeaveLogService;
import com.plzy.ldap.modules.client_access_log.domain.TLdapClientAccessLog;
import com.plzy.ldap.modules.client_access_log.service.TLdapClientAccessLogService;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.domainuser.service.DomainUserService;
import com.plzy.ldap.modules.ou.domain.TLdapOu;
import com.plzy.ldap.modules.ou.service.TLdapOuService;
import com.plzy.ldap.modules.report.service.ReportService;
import com.plzy.ldap.modules.strategy.settings.service.TLdapStrategySettingsListService;
import com.plzy.ldap.modules.sys_log.service.TSysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private LDAPRemoteService rpcService;

    @Autowired
    private TLdapOuService ldapOuService;

    @Autowired
    private TLdapDomainService domainService;

    @Autowired
    private TLdapClientAccessLogService accessLogService;

    @Autowired
    private TLdapClientInstLogService instLogService;

    @Autowired
    private DomainUserService domainUserService;

    @Autowired
    private TLdapStrategySettingsListService strategySettingsListService;


    @Autowired
    private TLdapClientLeaveLogService clientLeaveLogService;

    @Autowired
    private TLdapClientInstLogService clientInstLogService;

    @Autowired
    private TSysLogService sysLogService;

    @Value("${ldap.dc-prefix}")
    private String dcPrefix;

    @Override
    public Map<String, Object> charts(Long domainId) {

        TLdapDomain domainObj = domainService.getById(domainId);

        // 统计主机架构分类
        Map<String, Integer> sysArch = accessLogService.statSysArch(domainId);

        // 统计主机系统版本
        Map<String, Integer> sysVersion = accessLogService.statSysVersion(domainId);

        // 密码过期统计
        Map<String, Integer> passwdExpirMap = new HashMap<>();
        passwdExpirMap.put("expir", 0);
        passwdExpirMap.put("seven", 0);
        passwdExpirMap.put("fourteen", 0);
        passwdExpirMap.put("other", 0);


        List users = new ArrayList<>();

        if (!domainId.equals(1L)) {
            users = domainUserService.listAll(domainId);
        }
        for (Object user : users) {
            Map userMap = (Map) user;
            String uid = ((List) userMap.get("uid")).get(0) + "";
            // 如果有过期时间，则判断
            if (userMap.get("krbpasswordexpiration") != null) {
                String passwdExpir = (((Map) ((List) userMap.get("krbpasswordexpiration")).get(0)).get("__datetime__") + "").replaceAll("Z", "");
                Date passwdExpirDate = DateUtil.parseDate(passwdExpir, DateUtil.DATE_PATTERN.yyyyMMddHHmmss);

                if (System.currentTimeMillis() > passwdExpirDate.getTime()) {
                    passwdExpirMap.put("expir", passwdExpirMap.get("expir") + 1);
                } else if (System.currentTimeMillis() > passwdExpirDate.getTime() + 7 * 24 * 60 * 60 * 1000L) {
                    passwdExpirMap.put("seven", passwdExpirMap.get("seven") + 1);
                } else if (System.currentTimeMillis() > passwdExpirDate.getTime() + 14 * 24 * 60 * 60 * 1000L) {
                    passwdExpirMap.put("fourteen", passwdExpirMap.get("fourteen") + 1);
                } else {
                    passwdExpirMap.put("other", passwdExpirMap.get("other") + 1);
                }
            }
        }


        // 用户统计
        Map<String, Integer> userStat = new HashMap<>();
        Integer login_in_7d = accessLogService.statLoginIn7d(domainId);
        userStat.put("total", users.size());
        userStat.put("login_in_seven", login_in_7d);
        userStat.put("never_login_in_seven", users.size() - login_in_7d);

        // 组策略统计
        Map<String, Integer> stringIntegerMap = strategySettingsListService.statStrategy(domainId);

        // 客户端版本统计
        Map<String, Integer> clientVersion = accessLogService.statClientVersion(domainId);

        // 在线终端数量
        Map<Long, Integer> onelineNum = HostPingJob.getChartData(domainId);

        // 统计客户端安装情况
        List<Map<String, Integer>> installStat = instLogService.getInstallStat(domainObj.getDomainName());

        // 统计二级机构的终端安装量
        // 查询所有二级机构
        List<TLdapOu> ouList = ldapOuService.list(new LambdaQueryWrapper<TLdapOu>().eq(TLdapOu::getPid, 3130));
        // 统计访问日志，并按ou进行分组
        List<TLdapClientAccessLog> logs = accessLogService.groupByOu(domainId);
        Map<String, Integer> ouStat = new HashMap<>();
        for (TLdapOu ou : ouList) {
            int sum = 0;
            for (TLdapClientAccessLog acc : logs) {
                if (acc.getOuDn().endsWith(ou.getDn())) {
                    sum++;
                }
            }
            if (sum > 0) {
                ouStat.put(ou.getName(), sum);
            }
        }


        int clientinstcount = clientInstLogService.count(new LambdaQueryWrapper<TLdapClientInstLog>()
                .eq(TLdapClientInstLog::getDomain, domainObj.getName()));
        int clientleavecount = clientLeaveLogService.count(new LambdaQueryWrapper<TLdapClientLeaveLog>()
                .eq(TLdapClientLeaveLog::getDomain, domainObj.getName()));

        HashMap<String, Object> clientmap = new HashMap<>();
        clientmap.put("clientinstcount",clientinstcount);
        clientmap.put("clientleavecount",clientleavecount);

        List<Map<String, Object>> countByMessage = sysLogService.countByMessage(domainId);


        // 结果整合
        Map<String, Object> result = new HashMap<>();
        result.put("passwd_expir", passwdExpirMap);
        result.put("sys_arch", sysArch);
        result.put("user", userStat);
        result.put("strategy", stringIntegerMap);
        result.put("client_version", clientVersion);
        result.put("sys_version", sysVersion);
        result.put("online_num", onelineNum);
        result.put("install_stat", installStat);
        result.put("install_groupby_ou", ouStat);
        result.put("clientmap",clientmap);
        result.put("countByMessage",countByMessage);


        return result;
    }

    @Override
    public Map<String, Integer> countRootDomain() {

        Map<String, Integer> result = new HashMap<>();

        List<TLdapDomain> domainList = domainService.listSubdomain();
        for (TLdapDomain domain : domainList) {

            if (domain.getId() == 1L) {
                continue;
            }

            Map<String, Integer> subResult = countByDomain(domain.getId());

            result.put("user", result.getOrDefault("user", 0) + subResult.get("user"));
            result.put("host", result.getOrDefault("host", 0) + subResult.get("host"));
            result.put("host4dc", result.getOrDefault("host4dc", 0) + subResult.get("host4dc"));
            result.put("host4normal", result.getOrDefault("host4normal", 0) + subResult.get("host4normal"));
            result.put("sudurule", result.getOrDefault("sudurule", 0) + subResult.get("sudurule"));
            result.put("user_group", result.getOrDefault("user_group", 0) + subResult.get("user_group"));
            result.put("ou", result.getOrDefault("ou", 0) + subResult.get("ou"));
        }

        return result;
    }

    @Override
    public Map<String, Integer> countByDomain(Long domainId) {

        TLdapDomain domain = domainService.getById(domainId);

        Map<String, Integer> result = new HashMap<>();

        // 统计活跃域用户个数
        LDAPResponse userOut = rpcService.request(domainId, "{\"method\":\"user_find\",\"params\":[[\"\"],{\"pkey_only\":true,\"sizelimit\":0,\"version\":\"2.237\"}]}");
        int domainUserCount = Integer.valueOf(userOut.getResult().getOrDefault("count", 0) + "");
        result.put("user", domainUserCount - 1); //减去内置的admin账号

        // 统计域计算机总数
        LDAPResponse hostOut = rpcService.request(domainId, "{\"method\":\"host_find\",\"params\":[[\"\"],{\"pkey_only\":true,\"sizelimit\":0,\"version\":\"2.237\"}]}");
        int hostCount = Integer.valueOf(hostOut.getResult().getOrDefault("count", 0) + "");
        result.put("host", hostCount);

        // 分别统计域控制器和域计算机个数
        LDAPResponse resp = rpcService.request(domainId, "{" +
                "    \"method\": \"host_find\"," +
                "    \"params\": [" +
                "        [" +
                "            \"\"" +
                "        ]," +
                "        {" +
                "            \"pkey_only\": true," +
                "            \"sizelimit\": 0," +
                "            \"version\": \"2.237\"" +
                "        }" +
                "    ]" +
                "}");
        List list = (List) resp.getResult().get("result");
        StringBuilder in = new StringBuilder();
        int dcCount = 0;
        for (Object item : list) {
            Map it = (Map) item;
            String fqdn = ((List) it.get("fqdn")).get(0) + "";

            // 排除域控服务器，fqdn中包含server的字段
            if (fqdn.startsWith(dcPrefix)) {
                dcCount++;
            }
        }
        result.put("host4dc", dcCount);
        result.put("host4normal", list.size() - dcCount);

        // 统计组策略对象
        LDAPResponse sudoruleOut = rpcService.request(domainId, "{\"method\":\"sudorule_find\",\"params\":[[\"\"],{\"pkey_only\":true,\"sizelimit\":0,\"version\":\"2.237\"}]}");
        int suduruleCount = Integer.valueOf(sudoruleOut.getResult().getOrDefault("count", 0) + "");
        result.put("sudurule", suduruleCount);

        // 统计用户组（不包含系统组，对用户不可见）个数
        LDAPResponse userGroupOut = rpcService.request(domainId, "{\"method\":\"group_find\",\"params\":[[\"\"],{\"pkey_only\":true,\"sizelimit\":0,\"version\":\"2.237\"}]}");
        int userGroupCount = Integer.valueOf(userGroupOut.getResult().getOrDefault("count", 0) + "");
        result.put("user_group", userGroupCount - 4); // 4个系统组，分别是admins，editors，ipausers，trust admins

        // 统计组织单位个数
        int ouCount = ldapOuService.count(new LambdaQueryWrapper<TLdapOu>().eq(TLdapOu::getDomainId, domainId));
        result.put("ou", ouCount);

        return result;
    }

    @Override
    public Map<String, Integer> countByOU(Long domainId, Long ouId) {

        Map<String, Integer> result = new HashMap<>();

        // 统计活跃域用户个数
        LDAPResponse userOut = rpcService.request(domainId, "{\"method\":\"user_find\",\"params\":[[\"\\\"ouCN\\\":" + ouId + "\"],{\"pkey_only\":true,\"sizelimit\":0,\"version\":\"2.240\"}]}");
        int domainUserCount = Integer.valueOf(userOut.getResult().getOrDefault("count", 0) + "");
        result.put("user", domainUserCount);

        // 统计用户组个数
        LDAPResponse userGroupOut = rpcService.request(domainId, "{\"method\":\"group_find\",\"params\":[[\"{\\\"ouCN\\\":\\\"" + ouId + "\\\"\"],{\"pkey_only\":true,\"sizelimit\":0,\"version\":\"2.240\"}]}");
        int userGroupCount = Integer.valueOf(userGroupOut.getResult().getOrDefault("count", 0) + "");
        result.put("user_group", userGroupCount);

        // 统计组织单位个数
        int ouCount = ldapOuService.count();
        result.put("ou", ouCount);

        return result;
    }
}
