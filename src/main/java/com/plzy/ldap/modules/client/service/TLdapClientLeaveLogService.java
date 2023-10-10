package com.plzy.ldap.modules.client.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.modules.client.domain.TLdapClientLeaveLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.plzy.ldap.modules.client.dto.ClientLeaveLogDto;

/**
* @author lichao
* @description 针对表【t_ldap_client_leave_log(客户端退域日志)】的数据库操作Service
* @createDate 2023-07-25 09:18:47
*/
public interface TLdapClientLeaveLogService extends IService<TLdapClientLeaveLog> {

    IPage<ClientLeaveLogDto> page(Page<TLdapClientLeaveLog> page, ClientLeaveLogDto params);
}
