package com.plzy.ldap.modules.client.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.modules.client.domain.TLdapClientLeaveLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.plzy.ldap.modules.client.dto.ClientLeaveLogDto;
import org.apache.ibatis.annotations.Param;

/**
* @author lichao
* @description 针对表【t_ldap_client_leave_log(客户端退域日志)】的数据库操作Mapper
* @createDate 2023-07-25 09:18:47
* @Entity com.plzy.ldap.modules.client.domain.TLdapClientLeaveLog
*/
public interface TLdapClientLeaveLogMapper extends BaseMapper<TLdapClientLeaveLog> {

    IPage<ClientLeaveLogDto> page(Page<TLdapClientLeaveLog> page,@Param("params") ClientLeaveLogDto params);
}




