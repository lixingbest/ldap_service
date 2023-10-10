package com.plzy.ldap.modules.client.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.modules.client.domain.TLdapClientInstLog;
import com.plzy.ldap.modules.client.mapper.TLdapClientInstLogMapper;
import com.plzy.ldap.modules.client.service.TLdapClientInstLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
public class TLdapClientInstLogServiceImpl extends ServiceImpl<TLdapClientInstLogMapper, TLdapClientInstLog>
    implements TLdapClientInstLogService {

    @Autowired
    private TLdapClientInstLogMapper clientInstLogMapper;

    @Override
    public List<Map<String, Integer>> getInstallStat(String domainDn) {
        return clientInstLogMapper.getInstallStat(domainDn);
    }
}




