package com.plzy.ldap.open_service.client_service.bean;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class StrategySettingsPublicServiceParams {

    private String domainName;

    private String hostname;

    private String hostIP;

    private String uid;

    private String sysName;

    private String sysVersion;

    private String sysArch;
}
