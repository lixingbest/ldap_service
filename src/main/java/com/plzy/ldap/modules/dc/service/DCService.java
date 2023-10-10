package com.plzy.ldap.modules.dc.service;

import com.plzy.ldap.modules.dc.dto.DCDTO;

import java.util.List;

public interface DCService {

    List list(Long domainId, String fqdn);

    void update(Long domainId, DCDTO dto);
}
