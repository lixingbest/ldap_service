package com.plzy.ldap.modules.sys_log.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.plzy.ldap.modules.sys_log.domain.TSysLog;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
@Data
public class SysLogDto extends TSysLog {

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date beginDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date endDate;

    private String jobNo;

    private Long domainId;

}
