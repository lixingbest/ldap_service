package com.plzy.ldap.modules.trust.mapper;

import com.plzy.ldap.modules.trust.domain.TLdapTrust;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.plzy.ldap.modules.trust.dto.TrustTree;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Entity com.plzy.ldap.modules.trust.domain.TLdapTrust
 */
@Mapper
public interface TLdapTrustMapper extends BaseMapper<TLdapTrust> {

    List<TrustTree> tree(String uid);
}




