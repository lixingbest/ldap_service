package com.plzy.ldap.modules.active_directory.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

/**
 * ad同步过滤器
 * @TableName t_ldap_ad_sync_filter
 */
@TableName(value ="t_ldap_ad_sync_filter")
@Data
@ToString
public class TLdapAdSyncFilter implements Serializable {
    /**
     * 编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 表达式
     */
    @TableField(value = "expr")
    private String expr;

    /**
     * 0：OU，1：用户
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 配置项id
     */
    @TableField(value = "sync_config_id")
    private Long syncConfigId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}