package com.plzy.ldap.modules.strategy.settings.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @TableName t_ldap_strategy_settings_command
 */
@TableName(value = "t_ldap_strategy_settings_command")
@Data
public class TLdapStrategySettingsCommand implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "type_id")
    private Long typeId;

    @TableField(value = "code")
    private String code;

    @TableField(value = "arch")
    private String arch;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 操作系统类型
     */
    @TableField(value = "os")
    private String os;

    /**
     * 命令类型
     */
    @TableField(value = "type")
    private String type;

    /**
     * 命令名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 命令内容
     */
    @TableField(value = "command")
    private String command;

    /**
     * 说明
     */
    @TableField(value = "comments")
    private String comments;

    /**
     * 参数说明
     */
    @TableField(value = "args")
    private String args;

    /**
     * 版本
     */
    @TableField(value = "version")
    private String version;

    /**
     * 作用域,0公共，1私有
     */
    @TableField(value = "scope")
    private Integer scope;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
