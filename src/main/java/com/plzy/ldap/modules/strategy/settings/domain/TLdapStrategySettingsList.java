package com.plzy.ldap.modules.strategy.settings.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 策略控制面板设置列表
 * @TableName t_ldap_strategy_settings_list
 */
@TableName(value ="t_ldap_strategy_settings_list")
@Data
public class TLdapStrategySettingsList implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
   * pid
     */
    @TableField(value = "pid")
    private Long pid;

    /**
     * 所属domain的id
     */
    @TableField(value = "domain_id")
    private Long domainId;

    /**
     * 类型：0目录，1设置条目
     */
    @TableField(value = "type")
    private Integer type;

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
     * 备注
     */
    @TableField(value = "comments")
    private String comments;

    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(value = "update_user")
    private Long updateUser;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
