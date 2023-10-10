package com.plzy.ldap.modules.host.service;

import com.plzy.ldap.modules.host.dto.HostDTO;

import java.util.List;
import java.util.Map;

public interface HostService {

    List list(Long domainId, String fqdn);

    void update(Long domainId, HostDTO dto);

    void removeWithDNS(Long domainId, String fqdn);

    List find(Long domainId, String fqdn);

    int clearInvalidHost(Long domainId);

    Map<String, Object> add(Long domainId, String fqdn);
}
