package com.plzy.ldap.modules.strategy.settings.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 
 * @TableName t_ldap_strategy_settings_values
 */
@TableName(value ="t_ldap_strategy_settings_values")
@Data
public class TLdapStrategySettingsValues implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属策略id
     */
    @TableField(value = "strategy_id")
    private Long strategyId;

    /**
     * 命令所属id
     */
    @TableField(value = "command_id")
    private Long commandId;

    /**
     * 命令类型
     */
    @TableField(value = "command_type")
    private String commandType;

    /**
     * 值内容
     */
    @TableField(value = "value")
    private String value;

    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}