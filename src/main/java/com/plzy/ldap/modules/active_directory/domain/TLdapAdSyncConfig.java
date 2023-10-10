package com.plzy.ldap.modules.active_directory.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

/**
 * AD域同步配置
 * @TableName t_ldap_ad_sync_config
 */
@TableName(value ="t_ldap_ad_sync_config")
@Data
@ToString
public class TLdapAdSyncConfig implements Serializable {
    /**
     * 主键
     */
    @TableField(value = "id")
    private Long id;

    /**
     * 域中文描述
     */
    @TableField(value = "name")
    private String name;

    /**
     * 域名称
     */
    @TableField(value = "domain_name")
    private String domainName;

    /**
     * 同步地址，形如：ldap://jn.intra.customs.gov.cn
     */
    @TableField(value = "sync_url")
    private String syncUrl;

    /**
     * 管理员名称DN
     */
    @TableField(value = "admin_name")
    private String adminName;

    /**
     * 管理员密码
     */
    @TableField(value = "admin_passwd")
    private String adminPasswd;

    /**
     * 同步根DN
     */
    @TableField(value = "base_dn")
    private String baseDn;

    /**
     * 域用户表达式
     */
    @TableField(value = "user_filter_expr")
    private String userFilterExpr;

    /**
     * 域组织机构表达式
     */
    @TableField(value = "ou_filter_expr")
    private String ouFilterExpr;

    /**
     * 注释
     */
    @TableField(value = "comments")
    private String comments;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}