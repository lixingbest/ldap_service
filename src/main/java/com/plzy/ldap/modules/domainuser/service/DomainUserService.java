package com.plzy.ldap.modules.domainuser.service;

import com.plzy.ldap.modules.domainuser.dto.ActiveDomainUserWithExtraCommentsDTO;

import java.util.List;
import java.util.Map;

public interface DomainUserService {

    boolean isExist(Long domainId, String uid);

    Map getByUid(Long domainId, String uid);

    List getFullInfoByUid(Long domainId, String uid);

    List getFullInfoByUidNoCache(Long domainId, String uid);

    List listAll(Long domainId);

    String save(Long domainId, ActiveDomainUserWithExtraCommentsDTO user);

    void update(Long domainId, ActiveDomainUserWithExtraCommentsDTO user);

    void disable(Long domainId,String uid);

    void enable(Long domainId,String uid);

    void delete(Long domainId,String uid, boolean preserve);

    void deleteAllUser(Long domainId);

    void resetPasswd(Long domainId,String uid, String password);

    void modifyHomedir();

    void modifyPasswd(String defaultPasswd);
}
