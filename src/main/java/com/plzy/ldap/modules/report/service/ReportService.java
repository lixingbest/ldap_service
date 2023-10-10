package com.plzy.ldap.modules.report.service;

import com.plzy.ldap.modules.report.dto.DomainUser;

import java.util.List;
import java.util.Map;

public interface ReportService {

    Map<String, Integer> countRootDomain();

    Map<String, Integer> countByDomain(Long domainId);

    Map<String, Integer> countByOU(Long domainId, Long ouId);

    Map<String,Object >charts(Long domainId);
}
