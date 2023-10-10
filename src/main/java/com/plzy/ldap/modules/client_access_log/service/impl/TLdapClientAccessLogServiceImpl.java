package com.plzy.ldap.modules.client_access_log.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.modules.client_access_log.domain.TLdapClientAccessLog;
import com.plzy.ldap.modules.client_access_log.service.TLdapClientAccessLogService;
import com.plzy.ldap.modules.client_access_log.mapper.TLdapClientAccessLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.plzy.ldap.modules.client_access_log.dto.LdapClientAccessLogDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author lixingbest
* @description 针对表【t_ldap_client_access_log(客户端访问日志表)】的数据库操作Service实现
* @createDate 2023-03-03 11:20:06
*/
@Service
public class TLdapClientAccessLogServiceImpl extends ServiceImpl<TLdapClientAccessLogMapper, TLdapClientAccessLog>
    implements TLdapClientAccessLogService{

    @Autowired
    private TLdapClientAccessLogMapper clientAccessLogMapper;

    @Override
    public TLdapClientAccessLog getHostInfoByFqdn(String fqdn) {
        return clientAccessLogMapper.getHostInfoByFqdn(fqdn);
    }

    @Override
    public IPage<LdapClientAccessLogDTO> list(Page<LdapClientAccessLogDTO> page, Long domainId, String uid,String userName, Date startTime, Date endTime, String level, String action,String ip,String hostname,String sysName) {
        return clientAccessLogMapper.list(page,domainId,uid,userName,startTime,endTime,level,action,ip,hostname,sysName);
    }

    @Override
    public Map<String, Integer> statSysArch(Long domainId) {
        return clientAccessLogMapper.statSysArch(domainId);
    }

    @Override
    public Map<String, Integer> statSysVersion(Long domainId) {
        return clientAccessLogMapper.statSysVersion(domainId);
    }

    @Override
    public Integer statLoginIn7d(Long domainId) {
        return clientAccessLogMapper.statLoginIn7d(domainId);
    }

    @Override
    public Map<String, Integer> statClientVersion(Long domainId) {
        return clientAccessLogMapper.statClientVersion(domainId);
    }

    @Override
    public List<String> getIpList(Long domainId) {
        return clientAccessLogMapper.getIpList(domainId);
    }

    @Override
    public List<TLdapClientAccessLog> groupByOu(Long domainId) {
        return clientAccessLogMapper.groupByOu(domainId);
    }
}




