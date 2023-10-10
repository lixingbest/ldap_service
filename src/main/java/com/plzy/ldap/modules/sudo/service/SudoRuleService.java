package com.plzy.ldap.modules.sudo.service;

import com.plzy.ldap.modules.sudo.dto.SudoRefDTO;
import com.plzy.ldap.modules.sudo.dto.SudoRuleDTO;

import java.util.List;

public interface SudoRuleService {

    List list(Long domainId);

    void save(Long domainId,SudoRuleDTO rule);

    void delete(Long domainId,String uid);

    void addUserGroup(Long domainId,SudoRefDTO ref);

    void addHost(Long domainId,SudoRefDTO ref);

    void addAllowCmdGroup(Long domainId,SudoRefDTO ref);

    void addDenyCommand(Long domainId,SudoRefDTO ref);

    List listUserGroup(Long domainId,String cn);

    List listAllUserGroup(Long domainId);

    List listHost(Long domainId,String cn);

    List listDenyCmdGroup(Long domainId,String cn);

    List listAllowCmdGroup(Long domainId,String cn);

    void removeUserGroup(Long domainId,SudoRefDTO ref);

    void removeHost(Long domainId,SudoRefDTO ref);

    void removeDenyCommand(Long domainId,SudoRefDTO refDTO);

    void removeAllowCommand(Long domainId,SudoRefDTO refDTO);
}
