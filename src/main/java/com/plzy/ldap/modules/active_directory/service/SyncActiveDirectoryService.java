package com.plzy.ldap.modules.active_directory.service;

public interface SyncActiveDirectoryService {

    String syncOU(Long domainId,boolean isSyncDomainUser);

    String syncDomainUser(Long ouId, Long jobId);

    String syncAll(Long domainId);
}
