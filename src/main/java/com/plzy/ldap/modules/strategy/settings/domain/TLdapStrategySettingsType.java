package com.plzy.ldap.modules.strategy.settings.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 策略分类
 * @TableName t_ldap_strategy_settings_type
 */
@TableName(value ="t_ldap_strategy_settings_type")
@Data
public class TLdapStrategySettingsType implements Serializable {
    /**
     * 主键
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
     * 备注
     */
    @TableField(value = "comments")
    private String comments;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}