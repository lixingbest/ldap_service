package com.plzy.ldap.modules.cross_trust.service;

import com.plzy.ldap.modules.cross_trust.domain.TLdapCrossTrust;
import com.baomidou.mybatisplus.extension.service.IService;
import com.plzy.ldap.modules.cross_trust.dto.TLdapCrossTrustDTO;
import com.plzy.ldap.modules.cross_trust.mapper.TLdapCrossTrustMapper;

import java.util.List;

/**
* @author lixingbest
* @description 针对表【t_ldap_cross_trust(跨域信任设置)】的数据库操作Service
* @createDate 2023-02-25 14:01:58
*/
public interface TLdapCrossTrustService extends IService<TLdapCrossTrust> {

    void execTrust();

    void cancelTrust(List<Long> ids);

    List<TLdapCrossTrustDTO> getTrustUsers(Long srcDomainId, Long targetDomainId, Long ouId, String uid);
}
