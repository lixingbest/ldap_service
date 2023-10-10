package com.plzy.ldap.modules.host.controller;

import com.alibaba.fastjson.JSONObject;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.framework.utils.DateUtil;
import com.plzy.ldap.framework.utils.ExcelUtil;
import com.plzy.ldap.modules.client.domain.TLdapClientInstLog;
import com.plzy.ldap.modules.client.service.TLdapClientInstLogService;
import com.plzy.ldap.modules.client_access_log.domain.TLdapClientAccessLog;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.host.dto.HostDTO;
import com.plzy.ldap.modules.host.service.HostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@RestController
@RequestMapping("/host")
@Slf4j
public class HostController {

    @Autowired
    private HostService hostService;

    @Autowired
    private TLdapDomainService domainService;

    @Autowired
    private TLdapClientInstLogService clientInstLogService;

    @GetMapping("/export")
    public void export(Long domainId, HttpServletResponse response) {

        List<Map<String, Object>> result = new ArrayList();
        // 如果请求的是根域，则查询所有域下的主机列表
        if (domainId == 1L) {
            for (TLdapDomain domain : domainService.listSubdomain()) {
                result.addAll(hostService.list(domain.getId(), null));
            }
        } else {
            result.addAll(hostService.list(domainId, null));
        }

        List<Map<String, Object>> data = new ArrayList<>();
        for (Object map : result) {
            Map<String, Object> item = (Map<String, Object>) map;

            Map<String, Object> rec = new HashMap<>();
            rec.put("fqdn", ((List) item.get("fqdn")).get(0));
            rec.put("is_join_domain", ((Boolean) item.get("has_keytab")) ? "已入域" : "已退域");

            if (item.get("details") != null) {
                TLdapClientAccessLog details = ((TLdapClientAccessLog) item.get("details"));

                rec.put("uid", details.getUid() != null ? details.getUid() : "");
                rec.put("sys_name", details.getSysName() != null ? details.getSysName() : "");
                rec.put("sys_version", details.getSysVersion() != null ? details.getSysVersion() : "");
                rec.put("arch", details.getSysArch() != null ? details.getSysArch() : "");
                rec.put("ip", details.getIp() != null ? details.getIp() : "");
                rec.put("mac", details.getMac() != null ? details.getMac() : "");
                rec.put("ou_name", details.getOuName() != null ? details.getOuName() : "");
                rec.put("username", details.getUserName() != null ? details.getUserName() : "");
                if (details.getIsOnline() != null) {
                    if (details.getIsOnline() == 0) {
                        rec.put("is_online", "在线");
                    } else if (details.getIsOnline() == 1) {
                        rec.put("is_online", "离线");
                    } else {
                        rec.put("is_online", "未知");
                    }
                } else {
                    rec.put("is_online", "未知");
                }
                rec.put("client_version", details.getClientVersion() != null ? details.getClientVersion() : "");
                rec.put("last_login_time", details.getAccessTime() != null ? DateUtil.formatDate(details.getAccessTime(), DateUtil.DATE_PATTERN.yyyy_MM_dd_HH_mm_ss) : "");
            }
            data.add(rec);
        }

        ExcelUtil.export("域主机列表", new String[]{"主机名:fqdn", "所在组织单位:ou_name", "最后登录账号:uid", "姓名:username", "是否入域:is_join_domain", "系统名称:sys_name", "系统版本:sys_version", "架构:arch", "IP:ip", "MAC:mac", "客户端版本:client_version", "最后登录时间:last_login_time", "是否在线:is_online"}, data, "host_" + DateUtil.formatDate(new Date(), DateUtil.DATE_PATTERN.yyyy_MM_dd_HH_mm_ss), response);
    }

    @PostMapping("importComputer")
    public ResponseEntity<ResponseData> importComputer(@RequestParam("file") MultipartFile multipartFile, @RequestParam("domainId") Long domainId) {

        File file = ExcelUtil.multipartFileToFile(multipartFile);

        List<LinkedHashMap<String, Object>> sheet1 = ExcelUtil.read(file, "域计算机", 2);

        file.delete();

        List<String> log = new ArrayList<>();

        for (int i = 0; i < sheet1.size(); i++) {

            LinkedHashMap<String, Object> map = sheet1.get(i);

            if (map.containsKey("主机名")) {
                String hostname = String.valueOf(map.get("主机名"));


                Map<String, Object> add = hostService.add(domainId, hostname);

                if (add != null) {
                   // log.add("【第" + i + "行】 " + hostname + " " + JSONObject.toJSONString(add));
                } else {
                    log.add("【第" + i + "行】 " + hostname + "  result is null");
                }
            } else {
                log.add("【第" + i + "行异常】 主机名不存在！");
            }

        }

        return ResponseEntity.ok(ResponseData.success(log));
    }

    @GetMapping("/list")
    public ResponseEntity<ResponseData> list(Long domainId, String fqdn) {

        // 如果请求的是根域，则查询所有域下的主机列表
        if (domainId == 1L) {
            List result = new ArrayList();
            for (TLdapDomain domain : domainService.listSubdomain()) {
                List list = hostService.list(domain.getId(), fqdn);
                result.addAll(list);
            }
            return ResponseEntity.ok(ResponseData.success(result));
        } else {
            return ResponseEntity.ok(ResponseData.success(hostService.list(domainId, fqdn)));
        }
    }

    /**
     * 编辑主机信息
     *
     * @param domainId
     * @param dto，其中fqdn必须传，因为是主机的唯一标识符，其他的字段只传修改的字段
     * @return
     */
    @GetMapping("/update")
    public ResponseEntity<ResponseData> update(Long domainId, HostDTO dto) {

        hostService.update(domainId, dto);
        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/remove")
    public ResponseEntity<ResponseData> remove(Long domainId, String fqdn) {

        hostService.removeWithDNS(domainId, fqdn);
        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/add")
    public ResponseEntity<ResponseData> add(Long domainId, String fqdn) {

        hostService.add(domainId, fqdn);
        return ResponseEntity.ok(ResponseData.success());
    }
}
