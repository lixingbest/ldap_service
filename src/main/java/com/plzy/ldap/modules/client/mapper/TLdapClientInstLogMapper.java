package com.plzy.ldap.modules.client.mapper;

import com.plzy.ldap.modules.client.domain.TLdapClientInstLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Entity com.plzy.ldap.open_service.client_service.domain.TLdapClientInstLog
 */
@Mapper
public interface TLdapClientInstLogMapper extends BaseMapper<TLdapClientInstLog> {

    List<Map<String, Integer>> getInstallStat(@Param("domainDn") String domainDn);
}




