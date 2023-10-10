package com.plzy.ldap.modules.domainuser.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzy.ldap.framework.utils.DateUtil;
import com.plzy.ldap.framework.utils.ProcessUtil;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.domainuser.dto.ActiveDomainUser;
import com.plzy.ldap.modules.domainuser.dto.ActiveDomainUserWithExtraCommentsDTO;
import com.plzy.ldap.modules.domainuser.service.DomainUserService;
import com.plzy.ldap.framework.ldap.protocol.LDAPResponse;
import com.plzy.ldap.framework.ldap.service.LDAPRemoteService;
import com.plzy.ldap.modules.ou.domain.TLdapOu;
import com.plzy.ldap.modules.ou.service.TLdapOuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Slf4j
public class DomainUserServiceImpl implements DomainUserService {

    @Autowired
    private LDAPRemoteService rpcService;

    @Autowired
    private TLdapDomainService domainService;

    @Autowired
    private LDAPRemoteService ldapRemoteService;

    @Autowired
    private TLdapOuService ouService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public boolean isExist(Long domainId, String uid) {

        LDAPResponse resp = rpcService.request(domainId, "{\"method\":\"user_find\",\"params\":[[],{\"uid\":\"" + uid + "\",\"no_members\":true,\"version\":\"2.240\",\"all\":false}]}");
        int count = Integer.valueOf(resp.getResult().get("count") + "");
        return count > 0;
    }

    @Override
    public Map getByUid(Long domainId, String uid) {

        LDAPResponse resp = rpcService.request(domainId, "{\"method\":\"user_show\",\"params\":[[\"" + uid + "\"],{\"no_members\":true,\"version\":\"2.240\",\"all\":true}]}");
        if (resp.getResult() != null) {
            return (Map) resp.getResult().get("result");
        }
        return null;
    }

    @Override
    public List getFullInfoByUidNoCache(Long domainId, String uid) {

        LDAPResponse resp = rpcService.request(domainId, false, "{\"method\":\"batch\",\"params\":[[{\"method\":\"user_show\",\"params\":[[\"" + uid + "\"],{\"all\":true,\"rights\":true}]},{\"method\":\"pwpolicy_show\",\"params\":[[],{\"user\":\"" + uid + "\",\"all\":true,\"rights\":true}]},{\"method\":\"krbtpolicy_show\",\"params\":[[\"" + uid + "\"],{\"all\":true,\"rights\":true}]},{\"method\":\"cert_find\",\"params\":[[],{\"user\":[\"" + uid + "\"],\"sizelimit\":0,\"all\":true}]}],{\"version\":\"2.237\"}]}");
        if (resp.getResult() != null) {
            return (List) resp.getResult().get("results");
        }
        return null;
    }

    @Override
    public List getFullInfoByUid(Long domainId, String uid) {

        LDAPResponse resp = rpcService.request(domainId, "{\"method\":\"batch\",\"params\":[[{\"method\":\"user_show\",\"params\":[[\"" + uid + "\"],{\"all\":true,\"rights\":true}]},{\"method\":\"pwpolicy_show\",\"params\":[[],{\"user\":\"" + uid + "\",\"all\":true,\"rights\":true}]},{\"method\":\"krbtpolicy_show\",\"params\":[[\"" + uid + "\"],{\"all\":true,\"rights\":true}]},{\"method\":\"cert_find\",\"params\":[[],{\"user\":[\"" + uid + "\"],\"sizelimit\":0,\"all\":true}]}],{\"version\":\"2.237\"}]}");
        if (resp.getResult() != null) {
            return (List) resp.getResult().get("results");
        }
        return null;
    }

    @Override
    public List listAll(Long domainId) {

        LDAPResponse resp = rpcService.request(domainId, "{" +
                "    \"method\": \"user_find\"," +
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
        for (Object item : list) {
            Map it = (Map) item;
            String cn = ((List) it.get("uid")).get(0) + "";

            in.append("{" +
                    "                \"method\": \"user_show\"," +
                    "                \"params\": [" +
                    "                    [" +
                    "                        \"").append(cn).append("\"" +
                    "                    ]," +
                    "                    {" +
                    "                        \"no_members\": true," +
                    "                        \"all\": true" +
                    "                    }" +
                    "                ]" +
                    "            },");
        }
        if (in.length() > 1) {
            in = in.deleteCharAt(in.length() - 1);
        }

        LDAPResponse resp2 = rpcService.request(domainId, "{" +
                "    \"method\": \"batch\"," +
                "    \"params\": [" +
                "        [" +
                "            " + in +
                "        ]," +
                "        {" +
                "            \"version\": \"2.237\"" +
                "        }" +
                "    ]" +
                "}");

        List list2 = (List) resp2.getResult().get("results");
        List result = new ArrayList();
        for (Object item : list2) {
            Map i = (Map) item;
            result.add(i.get("result"));
        }

        return result;
    }

    @Override
    public String save(Long domainId, ActiveDomainUserWithExtraCommentsDTO user) {

        String error = null;
        // 立即设置用户密码，避免H4A验证此用户时报错
        TLdapDomain domainObj = domainService.getById(domainId);

        Map<String, Object> ouJSONMap = new HashMap<>();
        ouJSONMap.put("ouCN", StringUtils.hasText(user.getOu()) ? Long.valueOf(user.getOu()) : null);
        if (user.getOu() != null) {
            TLdapOu ouObj = ouService.getById(Long.valueOf(user.getOu()));
            if (ouObj == null) {
                error = "组织单位id（" + user.getOu() + "）不存在";
                return error;
            }
            ouJSONMap.put("ouDN", ouObj.getDn());
        } else {
            ouJSONMap.put("ouDN", null);
        }
        ouJSONMap.put("comments1", user.getComments1());
        ouJSONMap.put("comments2", user.getComments2());
        ouJSONMap.put("comments3", user.getComments3());

        Map<String, Object> titleJSONMap = new HashMap<>();
        titleJSONMap.put("title", user.getTitle());
        titleJSONMap.put("job", user.getJob());

        // 设置用户家目录的位置在域名下
        user.setHomedirectory("/home/" + domainObj.getDomainName() + "/" + user.getUid());


        try {

            ActiveDomainUser target = new ActiveDomainUser();
            BeanUtils.copyProperties(user, target);
            target.setOu(objectMapper.writeValueAsString(ouJSONMap));
            target.setTitle(objectMapper.writeValueAsString(titleJSONMap));

            // 通过全名（cn）自动生成姓（givenname）和名（sn）
            if (StringUtils.hasText(target.getCn()) && target.getCn().length() >= 2) {
                target.setGivenname(target.getCn().substring(0, 1));
                target.setSn(target.getCn().substring(1));
            } else {
                error = "全名（cn）必填，且长度必须>=2！";

                return error;
                //throw new RuntimeException("全名（cn）必填，且长度必须>=2！");
            }

            rpcService.request(domainId, "{" +
                    "    \"method\": \"user_add\"," +
                    "    \"params\": [" +
                    "        []," +
                    objectMapper.writeValueAsString(target) +
                    "    ]" +
                    "}");

            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("user", user.getUid());
            params.add("old_password", user.getUserpassword());
            params.add("new_password", user.getUserpassword());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Referer", domainObj.getServiceUrl() + "/ipa/ui/");

            HttpEntity httpEntity = new HttpEntity<>(params, headers);

            log.info("即将发送认证请求");

            ResponseEntity resp = restTemplate.exchange(domainObj.getServiceUrl() + "/ipa/session/change_password", HttpMethod.POST, httpEntity, String.class);

            HttpHeaders respHeaders = resp.getHeaders();

            log.info("收到响应：" + respHeaders);

            HttpStatus status = resp.getStatusCode();
            log.info("响应状态码：" + status);
            if (status == HttpStatus.OK) {
                log.info("密码更新成功！");
            } else {
                log.error("验证账号中出现未知错误");
            }

            // 如果没有勾选在下次必须登录，则模拟用户手动登录改一次密码就好了
            if (user.getModifyPaswdNextLogin() != null && !user.getModifyPaswdNextLogin()) {
                String out = ProcessUtil.exec(new String[]{"/usr/bin/expect", "/opt/passwd.sh", user.getUid() + "@" + domainObj.getDomainName()});
                if (out.indexOf("鉴定故障") == -1) {
                    log.info("模拟用户登录成功：" + out);
                } else {

                    log.error("模拟用户登录失败：" + out);
                }
            }


        } catch (Exception e) {
            log.error("保存域用户时出现错误：", e);
            error = "保存域用户时出现错误：" + e.getMessage();
        }
        return error;
    }

    @Override
    public void update(Long domainId, ActiveDomainUserWithExtraCommentsDTO user) {

        // ou必须要传，否则会丢失ou下的options信息
        if (user.getOu() == null) {
            log.error("更新域用户信息时ou必须要传，否则会丢失ou下的options信息");
            throw new RuntimeException("更新域用户信息时ou必须要传，否则会丢失ou下的options信息");
        }

        // title必须要传，否则会丢失title下的options信息
        if (user.getTitle() == null) {
            log.error("更新域用户信息时title必须要传，否则会丢失title下的options信息");
            throw new RuntimeException("更新域用户信息时title必须要传，否则会丢失title下的options信息");
        }

        TLdapOu ouObj = ouService.getById(Long.valueOf(user.getOu()));

        // 这里之所以不自动生成json，是因为后面代码再拼接的时候不会将此内容生成\"
        user.setOu("{" +
                "\\\"ouCN\\\":" + user.getOu() + "," +
                "\\\"ouDN\\\":\\\"" + ouObj.getDn() + "\\\"," +
                "\\\"comments1\\\":" + (StringUtils.hasText(user.getComments1()) ? "\\\"" + user.getComments1() + "\\\"" : "null") + "," +
                "\\\"comments2\\\":" + (StringUtils.hasText(user.getComments2()) ? "\\\"" + user.getComments2() + "\\\"" : "null") + "," +
                "\\\"comments3\\\":" + (StringUtils.hasText(user.getComments3()) ? "\\\"" + user.getComments3() + "\\\"" : "null") +
                "}");
        user.setTitle("{" +
                "\\\"title\\\":\\\"" + user.getTitle() + "\\\"," +
                "\\\"job\\\":" + (StringUtils.hasText(user.getJob()) ? "\\\"" + user.getJob() + "\\\"" : "null") +
                "}");

        Map<String, Object> map = BeanMap.create(user);

        // 只包含更新内容的字段
        String modifyFilesStr = "";
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            // 跳过uid、空值，以及备注
            if (!"uid".equals(entry.getKey())
                    && entry.getValue() != null
                    && !entry.getKey().startsWith("comments") && !"ouDn".equals(entry.getKey())
                    && !entry.getKey().matches("loginshell|ipauserauthtype|version|job")
            ) {
                modifyFilesStr += "\"" + entry.getKey() + "\":\"" + entry.getValue() + "\",";
            }
        }
        if (modifyFilesStr.length() > 0) {
            modifyFilesStr = modifyFilesStr.substring(0, modifyFilesStr.length() - 1);
        }

        rpcService.request(domainId, "{\"method\":\"user_mod\",\"params\":[[\"" + map.get("uid") + "\"],{\"all\":true,\"rights\":true," + modifyFilesStr + ",\"version\":\"2.240\"}]}");
    }

    @Override
    public void disable(Long domainId, String uid) {
        rpcService.request(domainId, "{" +
                "    \"method\": \"batch\"," +
                "    \"params\": [" +
                "        [" +
                "            {" +
                "                \"method\": \"user_disable\"," +
                "                \"params\": [[\"" +
                uid + "\"]," +
                "                    {}" +
                "                ]" +
                "            }" +
                "        ]," +
                "        {" +
                "            \"version\": \"2.237\"" +
                "        }" +
                "    ]" +
                "}");
    }

    @Override
    public void enable(Long domainId, String uid) {
        rpcService.request(domainId, "{" +
                "    \"method\": \"batch\"," +
                "    \"params\": [" +
                "        [" +
                "            {" +
                "                \"method\": \"user_enable\"," +
                "                \"params\": [[\"" +
                uid + "\"]," +
                "                    {}" +
                "                ]" +
                "            }" +
                "        ]," +
                "        {" +
                "            \"version\": \"2.237\"" +
                "        }" +
                "    ]" +
                "}");
    }

    @Override
    public void delete(Long domainId, String uid, boolean preserve) {

        rpcService.request(domainId, "{\"method\":\"batch\",\"params\":[[{\"method\":\"user_del\",\"params\":[[\"" + uid + "\"],{\"preserve\":\"" + preserve + "\"}]}],{\"version\":\"2.240\"}]}");
    }

    @Override
    public void deleteAllUser(Long domainId) {

        List<Map<String, Object>> allusers = listAll(domainId);
        log.info("即将删除" + (allusers.size() - 1) + "个用户");

        for (Map<String, Object> user : allusers) {
            String uid = ((List) user.get("uid")).get(0) + "";
            if (!"admin".equals(uid)) {
                delete(domainId, uid, false);
            }
        }
    }

    @Override
    public void resetPasswd(Long domainId, String uid, String password) {

        rpcService.request(domainId, "{\"method\":\"passwd\",\"params\":[[\"" + uid + "\"],{\"password\":\"" + password + "\",\"version\":\"2.240\"}]}");
    }

    @Override
    public void modifyPasswd(String defaultPasswd) {

        // 加载所有的域名
        List<TLdapDomain> domainList = domainService.listSubdomain();
        for (TLdapDomain domain : domainList) {
            // 加载所有域用户
            List userList = listAll(domain.getId());
            for (Object user : userList) {
                // 不能因为出错而停止循环
                try {
                    Map userMap = (Map) user;

                    String uid = ((List) userMap.get("uid")).get(0) + "";

                    // 排除管理员
                    if ("admin".equals(uid)) {
                        log.info("admin管理员无需修改家目录");
                        continue;
                    }

                    // 如果有过期时间，则判断
                    if (userMap.get("krbpasswordexpiration") != null) {
                        String passwdExpir = (((Map) ((List) userMap.get("krbpasswordexpiration")).get(0)).get("__datetime__") + "").replaceAll("Z", "");
                        Date passwdExpirDate = DateUtil.parseDate(passwdExpir, DateUtil.DATE_PATTERN.yyyyMMddHHmmss);
                        if (passwdExpirDate.getTime() >= DateUtil.parseDate("2023-04-03 00:00:00", DateUtil.DATE_PATTERN.yyyy_MM_dd_HH_mm_ss2).getTime() && passwdExpirDate.getTime() <= DateUtil.parseDate("2023-04-03 23:59:59", DateUtil.DATE_PATTERN.yyyy_MM_dd_HH_mm_ss2).getTime()) {
                            // 更新域用户的密码
                            ldapRemoteService.request(domain.getId(), "{\"method\":\"passwd\",\"params\":[[\"" + uid + "\"],{\"password\":\"" + defaultPasswd + "\",\"version\":\"2.237\"}]}");
                            log.info("已完成域用户 " + uid + " 的密码更新");
                        } else {
                            log.info("域用户 " + uid + " 的密码过期时间(" + passwdExpir + ")不符合条件，无需更新");
                        }
                    } else {
                        // 如果没有过期时间，则强制修改
                        log.info("没有找到域用户 " + uid + " 的密码过期字段，强制修改");
                        ldapRemoteService.request(domain.getId(), "{\"method\":\"passwd\",\"params\":[[\"" + uid + "\"],{\"password\":\"" + defaultPasswd + "\",\"version\":\"2.237\"}]}");
                        log.info("已完成域用户 " + uid + " 的密码更新");
                    }
                } catch (Exception e) {
                    log.error("更新密码时出错：", e);
                }
            }
        }
    }

    @Override
    public void modifyHomedir() {

        // 加载所有的域名
        List<TLdapDomain> domainList = domainService.listSubdomain();
        for (TLdapDomain domain : domainList) {
            // 不能因为错误而终止
            try {
                // 加载所有域用户
                List userList = listAll(domain.getId());
                for (Object user : userList) {
                    Map userMap = (Map) user;

                    String uid = ((List) userMap.get("uid")).get(0) + "";
                    String homedir = ((List) userMap.get("homedirectory")).get(0) + "";

                    // 排除管理员
                    if ("admin".equals(uid)) {
                        log.info("admin管理员无需修改家目录");
                        continue;
                    }

                    // 检查家目录是否已经修改过
                    if (homedir.contains(domain.getDomainName())) {
                        log.info("用户 " + uid + " 已经修改过家目录，当前为" + homedir + "，跳过");
                        continue;
                    }

                    // 更新域用户的家目录位置
                    ldapRemoteService.request(domain.getId(), "{\"method\":\"user_mod\",\"params\":[[\"" + uid + "\"],{\"all\":true,\"rights\":true,\"homedirectory\":\"/home/" + domain.getDomainName() + "/" + uid + "\",\"version\":\"2.237\"}]}");
                    log.info("已完成域用户 " + uid + " 的家目录更新");
                }
            } catch (Exception e) {
                log.info("修改家目录时遇到错误：", e);
            }
        }
    }
}
