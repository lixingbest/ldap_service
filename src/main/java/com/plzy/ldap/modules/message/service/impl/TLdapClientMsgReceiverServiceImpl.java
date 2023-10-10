package com.plzy.ldap.modules.message.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.modules.message.domain.TLdapClientMsgReceiver;
import com.plzy.ldap.modules.message.service.TLdapClientMsgReceiverService;
import com.plzy.ldap.modules.message.mapper.TLdapClientMsgReceiverMapper;
import org.springframework.stereotype.Service;

/**
* @author lichao
* @description 针对表【t_ldap_client_msg_receiver(客户端消息接收者)】的数据库操作Service实现
* @createDate 2023-07-13 17:09:50
*/
@Service
public class TLdapClientMsgReceiverServiceImpl extends ServiceImpl<TLdapClientMsgReceiverMapper, TLdapClientMsgReceiver>
    implements TLdapClientMsgReceiverService{

}




