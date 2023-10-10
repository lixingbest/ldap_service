package com.plzy.ldap.modules.client_access_log.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.modules.client.domain.TLdapClientInstLog;
import com.plzy.ldap.modules.client_access_log.domain.TLdapClientAccessLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.plzy.ldap.modules.client_access_log.dto.LdapClientAccessLogDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author lixingbest
* @description 针对表【t_ldap_client_access_log(客户端访问日志表)】的数据库操作Service
* @createDate 2023-03-03 11:20:06
*/
public interface TLdapClientAccessLogService extends IService<TLdapClientAccessLog> {

    TLdapClientAccessLog getHostInfoByFqdn(String fqdn);

    IPage<LdapClientAccessLogDTO> list(Page<LdapClientAccessLogDTO> page, Long domainId, String uid, String userName, Date startTime, Date endTime, String level, String action,String ip,String hostname,String sysName);

    Map<String,Integer> statSysArch(Long domainId);

    Map<String,Integer> statSysVersion(Long domainId);

    Integer statLoginIn7d(Long domainId);

    Map<String, Integer> statClientVersion(Long domainId);

    List<String> getIpList(Long domainId);

    List<TLdapClientAccessLog> groupByOu(Long domainId);
}
