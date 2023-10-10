package com.plzy.ldap.modules.trust.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.modules.domain.dto.DomainTree;
import com.plzy.ldap.modules.trust.domain.TLdapTrust;
import com.plzy.ldap.modules.trust.dto.TrustTree;
import com.plzy.ldap.modules.trust.service.TLdapTrustService;
import com.plzy.ldap.modules.trust.mapper.TLdapTrustMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class TLdapTrustServiceImpl extends ServiceImpl<TLdapTrustMapper, TLdapTrust>
    implements TLdapTrustService{

    @Autowired
    private TLdapTrustMapper trustMapper;

    @Override
    public List<TrustTree> tree(String uid) {
        return trustMapper.tree(uid);
    }
}




