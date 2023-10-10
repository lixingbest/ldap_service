package com.plzy.ldap.modules.client.service;

import com.plzy.ldap.modules.client.domain.TLdapClientInstLog;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 */
public interface TLdapClientInstLogService extends IService<TLdapClientInstLog> {

    List<Map<String,Integer>> getInstallStat(@Param("domainDn") String domainDn);
}
