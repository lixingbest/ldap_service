package com.plzy.ldap.modules.deploy.dto;

import com.plzy.ldap.modules.deploy.domain.TLdapDeployDetails;
import lombok.Data;

@Data
public class DeployDetailDto extends TLdapDeployDetails {

    private Long terId;

    /**
     * 主机名
     */
    private String hostname;

    /**
     * Ipv4地址
     */
    private String ipv4;

    /**
     * Ssh的端口号
     */
    private Integer sshPort;

    /**
     * 超级管理员的用户名
     */
    private String account;


    /**
     * 所属的终端类型

     */
    private Long terTypeId;

    /**
     * 说明
     */
    private String terComments;
}
