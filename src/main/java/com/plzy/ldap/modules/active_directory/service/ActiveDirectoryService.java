package com.plzy.ldap.modules.active_directory.service;

import java.util.List;
import java.util.Map;

public interface ActiveDirectoryService {

    List<Map> list(Long domainId, String domainName);

    Map settings(Long domainId);
}
