package com.plzy.ldap.modules.message.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.modules.message.domain.TLdapClientMsg;
import com.baomidou.mybatisplus.extension.service.IService;
import com.plzy.ldap.modules.message.dto.ClientMessageDto;

/**
* @author lichao
* @description 针对表【t_ldap_client_msg(客户端消息)】的数据库操作Service
* @createDate 2023-07-13 17:09:19
*/
public interface TLdapClientMsgService extends IService<TLdapClientMsg> {

    Page<ClientMessageDto> getpage(Page<ClientMessageDto> page ,TLdapClientMsg message);

    ClientMessageDto getInfoById(Long id);
}
