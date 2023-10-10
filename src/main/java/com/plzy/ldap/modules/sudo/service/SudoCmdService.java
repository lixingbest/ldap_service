package com.plzy.ldap.modules.sudo.service;

import com.plzy.ldap.modules.sudo.dto.SudoCmdDTO;

import java.util.List;

public interface SudoCmdService {

    List list(Long domainId,String cmdGroupCN);

    void save(Long domainId,SudoCmdDTO sudoCmd);

    void delete(Long domainId,String sudocmd);
}
