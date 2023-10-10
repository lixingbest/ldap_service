package com.plzy.ldap.modules.deploy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.modules.deploy.domain.TLdapDeployType;
import com.plzy.ldap.modules.deploy.service.TLdapDeployTypeService;
import com.plzy.ldap.modules.deploy.mapper.TLdapDeployTypeMapper;
import org.springframework.stereotype.Service;

/**
* @author lichao
* @description 针对表【t_ldap_deploy_type(部署计划分类)】的数据库操作Service实现
* @createDate 2022-12-27 13:15:28
*/
@Service
public class TLdapDeployTypeServiceImpl extends ServiceImpl<TLdapDeployTypeMapper, TLdapDeployType>
    implements TLdapDeployTypeService{

}




