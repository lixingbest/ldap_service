package com.plzy.ldap.modules.message.dto;

import com.plzy.ldap.modules.message.domain.TLdapClientMsg;
import com.plzy.ldap.modules.message.domain.TLdapClientMsgReceiver;
import lombok.Data;

import java.util.List;
@Data
public class ClientMessageDto extends TLdapClientMsg {

    List<TLdapClientMsgReceiver> receiverList;

    private String userName;

}
