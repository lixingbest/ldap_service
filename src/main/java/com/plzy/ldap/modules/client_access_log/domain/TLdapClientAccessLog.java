package com.plzy.ldap.modules.client_access_log.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 客户端访问日志表
 * @TableName t_ldap_client_access_log
 */
@TableName(value ="t_ldap_client_access_log")
@Data
public class TLdapClientAccessLog implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 域id
     */
    @TableField(value = "domain_id")
    private Long domainId;

    /**
     * ou的id
     */
    @TableField(value = "ou_id")
    private Long ouId;

    /**
     * 用户uid
     */
    @TableField(value = "uid")
    private String uid;

    /**
     * 用户姓名
     */
    @TableField(value = "user_name")
    private String userName;

    /**
     * 主机名
     */
    @TableField(value = "hostname")
    private String hostname;

    /**
     * ip
     */
    @TableField(value = "ip")
    private String ip;

    /**
     * 访问时间
     */
    @TableField(value = "access_time")
    private Date accessTime;

    /**
     * 系统名称
     */
    @TableField(value = "sys_name")
    private String sysName;

    /**
     * 系统架构
     */
    @TableField(value = "sys_arch")
    private String sysArch;

    /**
     * 系统版本
     */
    @TableField(value = "sys_version")
    private String sysVersion;

    /**
     * 日志级别：info，warn，error
     */
    @TableField(value = "level")
    private String level;

    /**
     * 类型：例如登录等
     */
    @TableField(value = "action")
    private String action;

    /**
     * 日志明细
     */
    @TableField(value = "details")
    private String details;

    /**
     * mac地址
     */
    @TableField(value = "mac")
    private String mac;

    /**
     * 客户端版本
     */
    @TableField(value = "client_version")
    private String clientVersion;

    /**
     * 域名称
     */
    @TableField(exist = false)
    private String domain;

    /**
     * 所在ou的名称
     */
    @TableField(exist = false)
    private String ouName;

    /**
     * 组织机构的dn
     */
    @TableField(exist = false)
    private String ouDn;

    /**
     * 是否在线，0在线，1不在线，2未知
     */
    @TableField(exist = false)
    private Integer isOnline;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
