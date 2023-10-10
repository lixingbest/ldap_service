package com.plzy.ldap.modules.active_directory.service;

import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;

import java.util.Collection;
import java.util.List;

public interface IncrementSyncActiveDirectoryService {

    void onChange(LdapEntry entry, TLdapDomain domain);
}
