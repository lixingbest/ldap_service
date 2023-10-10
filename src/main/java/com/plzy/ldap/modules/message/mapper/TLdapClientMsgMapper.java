package com.plzy.ldap.modules.message.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.modules.message.domain.TLdapClientMsg;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.plzy.ldap.modules.message.dto.ClientMessageDto;
import org.apache.ibatis.annotations.Param;

/**
* @author lichao
* @description 针对表【t_ldap_client_msg(客户端消息)】的数据库操作Mapper
* @createDate 2023-07-13 17:09:19
* @Entity com.plzy.ldap.modules.message.domain.TLdapClientMsg
*/
public interface TLdapClientMsgMapper extends BaseMapper<TLdapClientMsg> {

    Page<ClientMessageDto> getpage(Page<ClientMessageDto> page,@Param("message") TLdapClientMsg message);

    ClientMessageDto getInfoById(@Param("id") Long id);
}




