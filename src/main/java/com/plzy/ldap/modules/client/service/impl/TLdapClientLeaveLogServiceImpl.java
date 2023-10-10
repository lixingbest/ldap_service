package com.plzy.ldap.modules.client.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.modules.client.domain.TLdapClientLeaveLog;
import com.plzy.ldap.modules.client.dto.ClientLeaveLogDto;
import com.plzy.ldap.modules.client.service.TLdapClientLeaveLogService;
import com.plzy.ldap.modules.client.mapper.TLdapClientLeaveLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author lichao
* @description 针对表【t_ldap_client_leave_log(客户端退域日志)】的数据库操作Service实现
* @createDate 2023-07-25 09:18:47
*/
@Service
public class TLdapClientLeaveLogServiceImpl extends ServiceImpl<TLdapClientLeaveLogMapper, TLdapClientLeaveLog>
    implements TLdapClientLeaveLogService{

    @Autowired
    private TLdapClientLeaveLogMapper clientLeaveLogMapper;

    @Override
    public IPage<ClientLeaveLogDto> page(Page<TLdapClientLeaveLog> page, ClientLeaveLogDto params) {
        return clientLeaveLogMapper.page(page,params);
    }
}




