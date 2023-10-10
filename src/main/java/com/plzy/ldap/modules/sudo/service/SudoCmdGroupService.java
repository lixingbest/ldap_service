package com.plzy.ldap.modules.sudo.service;

import com.plzy.ldap.modules.sudo.dto.SudoCmdDTO;
import com.plzy.ldap.modules.sudo.dto.SudoCmdGroupDTO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface SudoCmdGroupService {

    List list(Long domainId);

    void save(Long domainId,SudoCmdGroupDTO sudoCmdGroup);

    void delete(Long domainId,String cn);

    void addSudoCmd(Long domainId,String groupCN, String cmdCN);
}
