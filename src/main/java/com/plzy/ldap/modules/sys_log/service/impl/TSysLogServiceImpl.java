package com.plzy.ldap.modules.sys_log.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.modules.sys_log.domain.TSysLog;
import com.plzy.ldap.modules.sys_log.service.TSysLogService;
import com.plzy.ldap.modules.sys_log.mapper.TSysLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author lixingbest
* @description 针对表【t_sys_log(系统日志表)】的数据库操作Service实现
* @createDate 2023-06-29 17:29:35
*/
@Service
public class TSysLogServiceImpl extends ServiceImpl<TSysLogMapper, TSysLog>
    implements TSysLogService{

    @Autowired
    private TSysLogMapper logMapper;

    @Override
    public Page<TSysLog> list(Page<TSysLog> page, Long domainId, String jobNo, Date beginDate, Date endDate,String menu,String message) {
        return logMapper.list(page,domainId,jobNo,beginDate,endDate,menu,message);
    }

    @Override
    public List<Map<String, Object>> countByMessage(Long domainId) {
        return logMapper.countByMessage(domainId);
    }
}




