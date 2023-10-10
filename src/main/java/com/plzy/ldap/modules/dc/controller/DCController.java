package com.plzy.ldap.modules.dc.controller;

import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.modules.dc.dto.DCDTO;
import com.plzy.ldap.modules.dc.service.DCService;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.host.dto.HostDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/dc")
@Slf4j
public class DCController {

    @Autowired
    private DCService dcService;

    @Autowired
    private TLdapDomainService domainService;

    @GetMapping("/list")
    public ResponseEntity<ResponseData> list(Long domainId, String fqdn){

        // 如果请求的是根域，则查询所有域下的主机列表
        if(domainId == 1L){
            List result = new ArrayList();
            for(TLdapDomain domain : domainService.listSubdomain()){
                List list = dcService.list(domain.getId(),fqdn);
                result.addAll(list);
            }
            return ResponseEntity.ok(ResponseData.success(result));
        }else{
            return ResponseEntity.ok(ResponseData.success(dcService.list(domainId,fqdn)));
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
    public ResponseEntity<ResponseData> update(Long domainId, DCDTO dto){

        dcService.update(domainId, dto);
        return ResponseEntity.ok(ResponseData.success());
    }
}
