package com.plzy.ldap.modules.domain.service;

import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.baomidou.mybatisplus.extension.service.IService;
import com.plzy.ldap.modules.domain.dto.DomainTree;

import java.util.List;

/**
 *
 */
public interface TLdapDomainService extends IService<TLdapDomain> {

    List<DomainTree> tree(Long domainId);

    List<DomainTree> treeWithoutOu();

    List<TLdapDomain> listSubdomain();

    boolean changeUpStatus(Long domainId, Byte upStatus);
}
