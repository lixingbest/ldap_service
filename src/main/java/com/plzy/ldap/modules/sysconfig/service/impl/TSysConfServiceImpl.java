package com.plzy.ldap.modules.sysconfig.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.modules.sysconfig.domain.TSysConf;
import com.plzy.ldap.modules.sysconfig.dto.SysConfigDto;
import com.plzy.ldap.modules.sysconfig.service.TSysConfService;
import com.plzy.ldap.modules.sysconfig.mapper.TSysConfMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class TSysConfServiceImpl extends ServiceImpl<TSysConfMapper, TSysConf>
    implements TSysConfService{

    @Autowired
    private TSysConfMapper sysConfMapper;

    @Override
    public List<SysConfigDto> getList() {
        return sysConfMapper.getList();
    }
}




