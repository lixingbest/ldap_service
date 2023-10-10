package com.plzy.ldap.modules.trust.dto;

import lombok.Data;

@Data
public class TrustUserParams {

    // 信任此用户的域的id的集合，以,分割
    private String domainIds;

    // 当前用户的域
    private Long currDomainId;

    // 当前用户的组织单位
    private Long currOUId;

    // 当前用户的uid
    private String currUId;
}
