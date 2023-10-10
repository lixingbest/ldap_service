package com.plzy.ldap.modules.client_access_log.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.modules.client.domain.TLdapClientInstLog;
import com.plzy.ldap.modules.client_access_log.domain.TLdapClientAccessLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.plzy.ldap.modules.client_access_log.dto.LdapClientAccessLogDTO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author lixingbest
* @description 针对表【t_ldap_client_access_log(客户端访问日志表)】的数据库操作Mapper
* @createDate 2023-03-03 11:20:06
* @Entity com.plzy.ldap.modules.client_access_log.domain.TLdapClientAccessLog
*/
public interface TLdapClientAccessLogMapper extends BaseMapper<TLdapClientAccessLog> {

    TLdapClientAccessLog getHostInfoByFqdn(@Param("fqdn") String fqdn);

    IPage<LdapClientAccessLogDTO> list(Page<LdapClientAccessLogDTO> page, @Param("domainId") Long domainId, @Param("uid") String uid,@Param("userName") String userName, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("level") String level,
                                       @Param("action") String action,@Param("ip") String ip,@Param("hostname") String hostname,@Param("sysName") String sysName);

    @MapKey("key")
    Map<String, Integer> statSysArch(@Param("domainId") Long domainId);

    Integer statLoginIn7d(@Param("domainId") Long domainId);

    @MapKey("key")
    Map<String, Integer> statClientVersion(@Param("domainId") Long domainId);

    @MapKey("key")
    Map<String, Integer> statSysVersion(@Param("domainId") Long domainId);

    List<String> getIpList(@Param("domainId") Long domainId);

    List<TLdapClientAccessLog> groupByOu(@Param("domainId") Long domainId);
}




