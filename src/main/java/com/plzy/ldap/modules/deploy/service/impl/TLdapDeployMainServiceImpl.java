package com.plzy.ldap.modules.deploy.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.modules.deploy.dto.DeployMainDto;
import com.plzy.ldap.modules.deploy.domain.TLdapDeployMain;
import com.plzy.ldap.modules.deploy.service.TLdapDeployMainService;
import com.plzy.ldap.modules.deploy.mapper.TLdapDeployMainMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

/**
* @author lichao
* @description 针对表【t_ldap_deploy_main(部署计划主表)】的数据库操作Service实现
* @createDate 2022-12-27 13:15:40
*/
@Service
public class TLdapDeployMainServiceImpl extends ServiceImpl<TLdapDeployMainMapper, TLdapDeployMain>
    implements TLdapDeployMainService{

    @Resource
    private TLdapDeployMainMapper deployMainMapper;

    @Override
    public Page<DeployMainDto> getPage(Page<TLdapDeployMain> page, Set<Long> typeIdList, String name) {
        return deployMainMapper.getPage(page, typeIdList, name);
    }

    @Override
    public DeployMainDto getMain(Long id){
        return deployMainMapper.getMain(id);
    }
}




