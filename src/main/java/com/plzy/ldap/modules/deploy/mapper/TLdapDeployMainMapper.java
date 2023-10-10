package com.plzy.ldap.modules.deploy.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.modules.deploy.dto.DeployMainDto;
import com.plzy.ldap.modules.deploy.domain.TLdapDeployMain;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.plzy.ldap.modules.deploy.dto.DeployDetailDto;
import java.util.Set;

/**
 * @author lichao
 * @description 针对表【t_ldap_deploy_main(部署计划主表)】的数据库操作Mapper
 * @createDate 2022-12-27 13:15:40
 * @Entity com.plzy.ldap.modules.deploy.domain.TLdapDeployMain
 */
public interface TLdapDeployMainMapper extends BaseMapper<TLdapDeployMain> {

    Page<DeployMainDto> getPage(Page<TLdapDeployMain> page,
                                @Param("typeIdList") Set<Long> typeIdList,
                                @Param("name") String name);
    DeployMainDto getMain(@Param("id") Long id);

    DeployDetailDto getDetail(@Param("id") Long id);

}




