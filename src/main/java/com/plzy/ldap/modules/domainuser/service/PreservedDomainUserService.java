package com.plzy.ldap.modules.domainuser.service;

import com.plzy.ldap.modules.domainuser.dto.ActiveDomainUserWithExtraCommentsDTO;

import java.util.List;

public interface PreservedDomainUserService {

    List listAll(Long domainId);

    void update(Long domainId, ActiveDomainUserWithExtraCommentsDTO user);

    void delete(Long domainId,String uid);

    void recover(Long domainId,String uid);
}
