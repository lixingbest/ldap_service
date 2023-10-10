package com.plzy.ldap.modules.ou.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

/**
 *
 * @TableName t_ldap_ou
 */
@TableName(value ="t_ldap_ou")
@Data
@ToString
public class TLdapOu implements Serializable {
    /**
     * 编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 父编号
     */
    @TableField(value = "pid")
    private Long pid;

    /**
     * 父类型
     * 0域，1组织单位
     */
    @TableField(value = "pid_type")
    private Byte pidType;

    /**
     * 名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 省
     */
    @TableField(value = "province")
    private String province;

    /**
     * 市
     */
    @TableField(value = "city")
    private String city;

    /**
     * 区
     */
    @TableField(value = "district")
    private String district;

    /**
     * 详细地址
     */
    @TableField(value = "address")
    private String address;

    /**
     * 邮政编码
     */
    @TableField(value = "postal_code")
    private String postalCode;

    /**
     * 说明
     */
    @TableField(value = "comments")
    private String comments;

    /**
     * 是否删除保护
     */
    @TableField(value = "del_protect")
    private Integer delProtect;

    /**
     * 所属的domain id
     */
    @TableField(value = "domain_id")
    private Long domainId;

    /**
     * 组织单位的ldap dn
     */
    @TableField(value = "dn")
    private String dn;

    @TableField(value = "ord_idx")
    public Integer ordIdx;

    public TLdapOu() {
    }

    public TLdapOu(Long id, Long pid, Byte pidType, String name, String province, String city, String district, String address, String postalCode, String comments, Integer delProtect, Long domainId, String dn, Integer ordIdx) {
        this.id = id;
        this.pid = pid;
        this.pidType = pidType;
        this.name = name;
        this.province = province;
        this.city = city;
        this.district = district;
        this.address = address;
        this.postalCode = postalCode;
        this.comments = comments;
        this.delProtect = delProtect;
        this.domainId = domainId;
        this.dn = dn;
        this.ordIdx = ordIdx;
    }

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
