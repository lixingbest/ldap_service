package com.plzy.ldap.modules.host.dto;

import lombok.Data;

@Data
public class HostDTO {

    // 主机定位符
    private String fqdn;

    // 主机描述
    private String description;

    // 架构
    private String nshardwareplatform;

    // 操作系统
    private String nsosversion;

    private String version = "2.237";
}
