package com.plzy.ldap.modules.domainuser.service;

import com.plzy.ldap.modules.domainuser.dto.ActiveDomainUserWithExtraCommentsDTO;
import com.plzy.ldap.modules.domainuser.dto.StageDomainUserWithExtraCommentsDTO;

import java.util.List;

public interface StageDomainUserService {

    boolean isExist(Long domainId, String uid);

    List listAll(Long domainId);

    void save(Long domainId, StageDomainUserWithExtraCommentsDTO user);

    void update(Long domainId, ActiveDomainUserWithExtraCommentsDTO user);

    void delete(Long domainId,String uid);

    void active(Long domainId,String uid);
}
