package com.plzy.ldap.modules.domain.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

/**
 * 域表
 * @TableName t_ldap_domain
 */
@TableName(value ="t_ldap_domain")
@Data
@ToString
public class TLdapDomain implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 父域id
     */
    @TableField(value = "pid")
    private Long pid;

    /**
     * 域控名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 英文编号
     */
    @TableField(value = "code")
    private String code;

    /**
     * ip地址
     */
    @TableField(value = "ip")
    private String ip;

    /**
     * 可分辨名称
     */
    @TableField(value = "dn")
    private String dn;

    /**
     * 域名称
     */
    @TableField(value = "domain_name")
    private String domainName;

    /**
     * 服务地址
     */
    @TableField(value = "service_url")
    private String serviceUrl;

    /**
     * 服务名称
     */
    @TableField(value = "service_name")
    private String serviceName;

    /**
     * 服务密码
     */
    @TableField(value = "service_passwd")
    private String servicePasswd;

    /**
     * 描述
     */
    @TableField(value = "comments")
    private String comments;

    /**
     * 上线状态：0上线，1下线
     */
    @TableField(value = "up_status")
    private Byte upStatus;

    /**
     * 此域登录后的cookie值
     */
    @TableField(exist = false)
    private String cookie;

    /**
     * 此域cookie的更新时间
     */
    @TableField(exist = false)
    private long cookieUpdateTimestamp;

    /**
     * 关联ad配置表的外键
     */
    @TableField(value = "ad_config_ref_id")
    private Long adConfigRefId;

    /**
     * 排序号
     */
    @TableField(value = "ord_idx")
    private Integer ordIdx;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
