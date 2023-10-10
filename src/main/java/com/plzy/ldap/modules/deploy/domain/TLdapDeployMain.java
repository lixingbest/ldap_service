package com.plzy.ldap.modules.deploy.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 部署计划主表
 * @TableName t_ldap_deploy_main
 */
@TableName(value ="t_ldap_deploy_main")
@Data
public class TLdapDeployMain implements Serializable {
    /**
     * 编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 计划名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 计划编号
     */
    @TableField(value = "code")
    private String code;


    /**
     * 所属分类
     */
    @TableField(value = "type_id")
    private Long typeId;

    /**
     * 执行的脚本
     */
    @TableField(value = "shell")
    private String shell;

    /**
     * 用户id

     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 执行类型 0 立即执行 2 周期性执行 1 定时执行
     */
    private Integer execType;

    /**
     * 执行表达式
     * 1 2020-10-09
     * 2 10/8  10分钟1次 执行8次
     */
    private String  execExpr;

    /**
     * 计划执行时间
     */
    @TableField(value = "time")
    private Date time;

    /**
     * 说明
     */
    @TableField(value = "comments")
    private String comments;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
