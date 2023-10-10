package com.plzy.ldap.modules.trust.service;

import com.plzy.ldap.modules.trust.domain.TLdapTrust;
import com.baomidou.mybatisplus.extension.service.IService;
import com.plzy.ldap.modules.trust.dto.TrustTree;

import java.util.List;

/**
 *
 */
public interface TLdapTrustService extends IService<TLdapTrust> {

    List<TrustTree> tree(String uid);
}
