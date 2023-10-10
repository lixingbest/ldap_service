package com.plzy.ldap.modules.sys_log.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.modules.sys_log.domain.TSysLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.lettuce.core.dynamic.annotation.Key;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author lixingbest
* @description 针对表【t_sys_log(系统日志表)】的数据库操作Mapper
* @createDate 2023-06-29 17:29:35
* @Entity com.plzy.ldap.modules.sys_log.domain.TSysLog
*/
public interface TSysLogMapper extends BaseMapper<TSysLog> {

    Page<TSysLog> list(Page<TSysLog> page, @Param("domainId") Long domainId,@Param("jobNo") String jobNo, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate,
                       @Param("menu")String menu,@Param("message") String message);

    @MapKey("message")
    List<Map<String, Object>> countByMessage( @Param("domainId") Long domainId);
}




