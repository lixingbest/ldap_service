package com.plzy.ldap.modules.client.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.modules.client.domain.TLdapClientInstLog;
import com.plzy.ldap.modules.client.domain.TLdapClientLeaveLog;
import com.plzy.ldap.modules.client.dto.ClientLeaveLogDto;
import com.plzy.ldap.modules.client.service.TLdapClientLeaveLogService;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("clientLeaveLog")
@Controller
public class ClientLeaveLogController {

    @Autowired
    private TLdapClientLeaveLogService clientLeaveLogService;

    @Autowired
    private TLdapDomainService domainService;

    @GetMapping
    public ResponseEntity<ResponseData> page(Page<TLdapClientLeaveLog> page, ClientLeaveLogDto params) {

        IPage<ClientLeaveLogDto> res = clientLeaveLogService.page(page, params);

        return ResponseEntity.ok(ResponseData.success(res));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData> get(@PathVariable Long id) {
        TLdapClientLeaveLog res = clientLeaveLogService.getById(id);
        return ResponseEntity.ok(ResponseData.success(res));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData> delete(@PathVariable Long id) {
        boolean res = clientLeaveLogService.removeById(id);
        return ResponseEntity.ok(res ? ResponseData.success() : ResponseData.error("1234", "删除失败"));
    }

    @GetMapping("/clear")
    public ResponseEntity<ResponseData> clear(Long domainId, String type) {

        // 根据domainId查找domain对象
        TLdapDomain domain = domainService.getById(domainId);

        if("success".equals(type)){
            clientLeaveLogService.remove(new QueryWrapper<TLdapClientLeaveLog>().eq("domain", domain.getDomainName()).eq("result",0));
        }else if("fail".equals(type)){
            clientLeaveLogService.remove(new QueryWrapper<TLdapClientLeaveLog>().eq("domain", domain.getDomainName()).eq("result",1));
        }else if("ing".equals(type)){
            clientLeaveLogService.remove(new QueryWrapper<TLdapClientLeaveLog>().eq("domain", domain.getDomainName()).isNull("result"));
        }

        return ResponseEntity.ok(ResponseData.success());
    }
}
