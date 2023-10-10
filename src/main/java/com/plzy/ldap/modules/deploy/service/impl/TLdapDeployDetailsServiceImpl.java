package com.plzy.ldap.modules.deploy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.modules.deploy.domain.TLdapDeployDetails;
import com.plzy.ldap.modules.deploy.service.TLdapDeployDetailsService;
import com.plzy.ldap.modules.deploy.mapper.TLdapDeployDetailsMapper;
import org.springframework.stereotype.Service;

/**
* @author lichao
* @description 针对表【t_ldap_deploy_details(部署计划执行明细)】的数据库操作Service实现
* @createDate 2022-12-27 13:15:45
*/
@Service
public class TLdapDeployDetailsServiceImpl extends ServiceImpl<TLdapDeployDetailsMapper, TLdapDeployDetails>
    implements TLdapDeployDetailsService{

}




