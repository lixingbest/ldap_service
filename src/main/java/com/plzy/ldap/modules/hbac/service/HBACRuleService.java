package com.plzy.ldap.modules.hbac.service;

import com.plzy.ldap.modules.hbac.dto.HBACRuleDTO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface HBACRuleService {

    List list(Long domainId);

    List listUser(Long domainId, String hbacCN);

    List listHost(Long domainId, String hbacCN);

    List listAllUsers(Long domainId,String uid);

    List listAllHost(Long domainId,String hostname);

    void save(Long domainId,HBACRuleDTO hbacRuleDTO);

    void update(Long domainId,HBACRuleDTO hbacRuleDTO);

    void remove(Long domainId,String hbacCN);

    void disable(Long domainId,String rule);

    void enable(Long domainId,String rule);

    void addUser(Long domainId,String hbacruleCN, List<String> userList);

    void addUserGroup(Long domainId,String hbacruleCN, List<String> userGroupList);

    void addHost(Long domainId,String hbacruleCN, List<String> hostList);

    void removeUser(Long domainId, String hbacCN, String uid);

    void removeHost(Long domainId, String hbacCN, String host);
}
