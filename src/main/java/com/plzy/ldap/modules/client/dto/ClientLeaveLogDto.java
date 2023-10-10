package com.plzy.ldap.modules.client.dto;

import com.plzy.ldap.modules.client.domain.TLdapClientLeaveLog;
import lombok.Data;

@Data
public class ClientLeaveLogDto extends TLdapClientLeaveLog {

    private Long domainId;

    private Integer type;
}
