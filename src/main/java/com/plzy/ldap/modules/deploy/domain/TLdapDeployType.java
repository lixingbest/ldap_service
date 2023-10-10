package com.plzy.ldap.modules.deploy.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 部署计划分类
 * @TableName t_ldap_deploy_type
 */
@TableName(value ="t_ldap_deploy_type")
@Data
public class TLdapDeployType implements Serializable {
    /**
     * 编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 编号
     */
    @TableField(value = "code")
    private String code;

    /**
     * 父级id
     */
    @TableField(value = "pid")
    private Long pid;

    /**
     * 
     */
    @TableField(value = "comments")
    private String comments;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}