package com.plzy.ldap.modules.strategy.settings.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 组织单位-策略设置关联表
 * @TableName t_ldap_ou_strategy_settings_ref
 */
@TableName(value ="t_ldap_ou_strategy_settings_ref")
@Data
public class TLdapOuStrategySettingsRef implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 组织单位编号
     */
    @TableField(value = "ou_id")
    private Long ouId;

    /**
     * 策略编号
     */
    @TableField(value = "strategy_id")
    private Long strategyId;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 是否启用，0启用，1禁用
     */
    @TableField(value = "enable")
    private Integer enable;

    @TableField(value = "is_force")
    private Integer isForce;

    @TableField(value = "level")
    private Integer level;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
