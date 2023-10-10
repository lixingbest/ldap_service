package com.plzy.ldap.modules.cross_trust.mapper;

import com.plzy.ldap.modules.cross_trust.domain.TLdapCrossTrust;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.plzy.ldap.modules.cross_trust.dto.TLdapCrossTrustDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author lixingbest
* @description 针对表【t_ldap_cross_trust(跨域信任设置)】的数据库操作Mapper
* @createDate 2023-02-25 14:01:58
* @Entity com.plzy.ldap.modules.cross_trust.domain.TLdapCrossTrust
*/
public interface TLdapCrossTrustMapper extends BaseMapper<TLdapCrossTrust> {

    List<TLdapCrossTrust> getNewRecords();

    List<TLdapCrossTrustDTO> getTrustUsers(@Param("srcDomainId") Long srcDomainId, @Param("targetDomainId") Long targetDomainId,  @Param("ouId") Long ouId, @Param("uid") String uid);
}




