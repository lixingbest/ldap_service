package com.plzy.ldap.modules.token.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.plzy.ldap.modules.admin.domain.TSysAdmin;
import com.plzy.ldap.modules.token.domain.TSysToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Entity com.ijc.modules.token.domain.TToken
 */
@Mapper
public interface TSysTokenMapper extends BaseMapper<TSysToken> {

    TSysAdmin getCurrUser(@Param("token") String token);

    int validate(@Param("token") String token);
}




