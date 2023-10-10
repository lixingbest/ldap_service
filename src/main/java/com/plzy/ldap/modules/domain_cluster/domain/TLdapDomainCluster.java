package com.plzy.ldap.modules.domain_cluster.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * Ldap域控部署定义
 * @TableName t_ldap_domain_cluster
 */
@TableName(value ="t_ldap_domain_cluster")
@Data
public class TLdapDomainCluster implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属的域id
     */
    @TableField(value = "domain_id")
    private Long domainId;

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
     * 排序号
     */
    @TableField(value = "ord_idx")
    private Integer ordIdx;

    /**
     * ip
     */
    @TableField(value = "ip")
    private String ip;

    /**
     * service地址
     */
    @TableField(value = "service_url")
    private String serviceUrl;

    /**
     * 账号名称
     */
    @TableField(value = "account")
    private String account;

    /**
     * 密码
     */
    @TableField(value = "passwd")
    private String passwd;

    /**
     * 是否启用，0启用，1禁用
     */
    @TableField(value = "enable")
    private Integer enable;

    /**
     * 描述
     */
    @TableField(value = "desc")
    private String desc;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
