package com.plzy.ldap.modules.sys_log.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.modules.sys_log.domain.TSysLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author lixingbest
* @description 针对表【t_sys_log(系统日志表)】的数据库操作Service
* @createDate 2023-06-29 17:29:35
*/
public interface TSysLogService extends IService<TSysLog> {

    Page<TSysLog> list(Page<TSysLog> page, Long domainId, String jobNo, Date beginDate, Date endDate,String menu,String message);

    List<Map<String,Object>> countByMessage(Long domainId);
}
