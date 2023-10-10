package com.plzy.ldap.modules.deploy.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.modules.deploy.dto.DeployMainDto;
import com.plzy.ldap.modules.deploy.domain.TLdapDeployMain;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Set;

/**
* @author lichao
* @description 针对表【t_ldap_deploy_main(部署计划主表)】的数据库操作Service
* @createDate 2022-12-27 13:15:40
*/
public interface TLdapDeployMainService extends IService<TLdapDeployMain> {

    Page<DeployMainDto> getPage(Page<TLdapDeployMain> page, Set<Long> typeIdList, String name);

    DeployMainDto getMain(Long id);
}
