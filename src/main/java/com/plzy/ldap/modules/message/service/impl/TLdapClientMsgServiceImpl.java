package com.plzy.ldap.modules.message.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.modules.message.domain.TLdapClientMsg;
import com.plzy.ldap.modules.message.dto.ClientMessageDto;
import com.plzy.ldap.modules.message.service.TLdapClientMsgService;
import com.plzy.ldap.modules.message.mapper.TLdapClientMsgMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author lichao
* @description 针对表【t_ldap_client_msg(客户端消息)】的数据库操作Service实现
* @createDate 2023-07-13 17:09:19
*/
@Service
public class TLdapClientMsgServiceImpl extends ServiceImpl<TLdapClientMsgMapper, TLdapClientMsg>
    implements TLdapClientMsgService{

    @Resource
    private TLdapClientMsgMapper clientMsgMapper;

    @Override
    public Page<ClientMessageDto> getpage(Page<ClientMessageDto> page, TLdapClientMsg message) {

        return clientMsgMapper.getpage(page,message);
    }

    @Override
    public ClientMessageDto getInfoById(Long id) {
        return clientMsgMapper.getInfoById(id);
    }
}




