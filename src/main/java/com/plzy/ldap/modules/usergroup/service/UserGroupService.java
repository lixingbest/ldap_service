package com.plzy.ldap.modules.usergroup.service;

import com.plzy.ldap.modules.usergroup.dto.UserGroupRefDTO;
import com.plzy.ldap.modules.usergroup.dto.UserGroupWithOUDTO;

import java.util.List;

public interface UserGroupService {

    List list(Long domainId,String ouCN, String groupCN,String uid);

    void save(Long domainId,UserGroupWithOUDTO user) throws Exception;

    void delete(Long domainId,String cn);

    List listUser(Long domainId,String groupCN);

    void addUser(Long domainId,UserGroupRefDTO ref);

    void removeUser(Long domainId,UserGroupRefDTO ref);

    List listAllNames(Long domainId);
}
