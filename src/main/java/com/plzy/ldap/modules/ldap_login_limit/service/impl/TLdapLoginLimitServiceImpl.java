package com.plzy.ldap.modules.ldap_login_limit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.modules.ldap_login_limit.domain.TLdapLoginLimit;
import com.plzy.ldap.modules.ldap_login_limit.service.TLdapLoginLimitService;
import com.plzy.ldap.modules.ldap_login_limit.mapper.TLdapLoginLimitMapper;
import org.springframework.stereotype.Service;

/**
* @author lichao
* @description 针对表【t_ldap_login_limit(域用户登录限制)】的数据库操作Service实现
* @createDate 2023-07-23 13:02:04
*/
@Service
public class TLdapLoginLimitServiceImpl extends ServiceImpl<TLdapLoginLimitMapper, TLdapLoginLimit>
    implements TLdapLoginLimitService{

}




