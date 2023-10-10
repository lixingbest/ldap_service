package com.plzy.ldap.modules.ou.service;

import com.plzy.ldap.modules.ou.domain.TLdapOu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.plzy.ldap.modules.ou.dto.OUUserRefDTO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface TLdapOuService extends IService<TLdapOu> {

    List<Map<String, Object>> treeByDomain(Long domainId);

    List getActiveUserList(Long domainId, String uid, String keywords);

    List getStageUserList(Long domainId, String uid, String keywords, String mobile, String cn);

    List getPreservedUserList(Long domainId, String uid, String keywords, String mobile, String cn);

    void addUsers(Long domainId, OUUserRefDTO refDTO);

    void removeUsers(Long domainId, boolean preserve, OUUserRefDTO refDTO);

    List<TLdapOu> bulkExport(Long pid);
}
