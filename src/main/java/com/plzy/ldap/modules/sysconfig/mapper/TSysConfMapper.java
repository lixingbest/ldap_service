package com.plzy.ldap.modules.sysconfig.mapper;

import com.plzy.ldap.modules.sysconfig.domain.TSysConf;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.plzy.ldap.modules.sysconfig.dto.SysConfigDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Entity com.plzy.ldap.modules.sysConfig.domain.TSysConf
 */
@Mapper
public interface TSysConfMapper extends BaseMapper<TSysConf> {

    List<SysConfigDto> getList();
}




