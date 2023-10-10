package com.plzy.ldap.jobs.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HostPingResult {

    private Boolean result;

    private Long time;
}
