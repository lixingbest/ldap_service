package com.plzy.ldap.modules.token.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.plzy.ldap.modules.admin.domain.TSysAdmin;
import com.plzy.ldap.modules.token.domain.TSysToken;

/**
 *
 */
public interface TSysTokenService extends IService<TSysToken> {

    TSysToken getToken(TSysAdmin user);

    TSysAdmin getCurrUser();

    Boolean validate(String token);
}
