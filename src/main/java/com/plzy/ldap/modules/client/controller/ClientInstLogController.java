package com.plzy.ldap.modules.client.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.framework.utils.TextUtil;
import com.plzy.ldap.modules.client.domain.TLdapClientInstLog;
import com.plzy.ldap.modules.client.service.TLdapClientInstLogService;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/client_inst_log")
@Slf4j
public class ClientInstLogController {

    @Autowired
    private TLdapClientInstLogService clientInstLogService;

    @Autowired
    private TLdapDomainService domainService;

    /**
     * 加载客户端日志
     *
     * @param page
     * @param domainId
     * @param type     加载的数据类型，0=成功，1=失败，2=安装中
     * @param params
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<ResponseData> list(Page<TLdapClientInstLog> page, Long domainId, Integer type, TLdapClientInstLog params) {

        // 根据domainId查找domain对象
        TLdapDomain domain = domainService.getById(domainId);

        LambdaQueryWrapper<TLdapClientInstLog> condition = new LambdaQueryWrapper<TLdapClientInstLog>()
                .select(TLdapClientInstLog::getId, TLdapClientInstLog::getHostname, TLdapClientInstLog::getDomain, TLdapClientInstLog::getClientVersion, TLdapClientInstLog::getArch, TLdapClientInstLog::getSysVersion, TLdapClientInstLog::getUser, TLdapClientInstLog::getId, TLdapClientInstLog::getBeginTime, TLdapClientInstLog::getEndTime, TLdapClientInstLog::getStep, TLdapClientInstLog::getResult,TLdapClientInstLog::getIp,TLdapClientInstLog::getSysName)
                .eq(TLdapClientInstLog::getDomain, domain.getDomainName())
                .orderByDesc(TLdapClientInstLog::getBeginTime);

        if (type != null) {
            if (type == 0 || type == 1) {
                condition.eq(TLdapClientInstLog::getResult, type);
            } else if (type == 2) {
                condition.isNull(TLdapClientInstLog::getResult);
            }
        } else {
            condition.eq(TLdapClientInstLog::getResult, 0);
        }

        if (params.getHostname() != null) {
            condition.like(TLdapClientInstLog::getHostname, params.getHostname());
        }
        if (params.getUser() != null) {
            condition.like(TLdapClientInstLog::getUser, params.getUser());
        }
        if (params.getDomain() != null) {
            condition.like(TLdapClientInstLog::getDomain, params.getDomain());
        }
        if (params.getClientVersion() != null) {
            condition.like(TLdapClientInstLog::getClientVersion, params.getClientVersion());
        }

        Page<TLdapClientInstLog> result = clientInstLogService.page(page, condition);

        return ResponseEntity.ok(ResponseData.success(result));
    }

    @GetMapping("/getLog")
    public ResponseEntity<String> getLog(Long logId) {

        TLdapClientInstLog result = clientInstLogService.getById(logId);

        return ResponseEntity.ok(result.getLog());
    }

    @GetMapping("/remove")
    public ResponseEntity<ResponseData> remove(Long logId) {

        clientInstLogService.removeById(logId);
        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/clear")
    public ResponseEntity<ResponseData> clear(Long domainId, String type) {

        // 根据domainId查找domain对象
        TLdapDomain domain = domainService.getById(domainId);

        if("success".equals(type)){
            clientInstLogService.remove(new QueryWrapper<TLdapClientInstLog>().eq("domain", domain.getDomainName()).eq("result",0));
        }else if("fail".equals(type)){
            clientInstLogService.remove(new QueryWrapper<TLdapClientInstLog>().eq("domain", domain.getDomainName()).eq("result",1));
        }else if("ing".equals(type)){
            clientInstLogService.remove(new QueryWrapper<TLdapClientInstLog>().eq("domain", domain.getDomainName()).isNull("result"));
        }

        return ResponseEntity.ok(ResponseData.success());
    }
}
