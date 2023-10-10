package com.plzy.ldap.modules.domain.mapper;

import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.plzy.ldap.modules.domain.dto.DomainTree;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.plzy.ldap.modules.dc.domain.TLdapDc
 */
@Mapper
public interface TLdapDomainMapper extends BaseMapper<TLdapDomain> {

    List<DomainTree> tree(@Param("domainId") Long domainId);

    List<DomainTree> treeWithoutOu();

    List<TLdapDomain> listSubdomain();
}




