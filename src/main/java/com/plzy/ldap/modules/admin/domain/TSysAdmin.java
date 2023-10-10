package com.plzy.ldap.modules.admin.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 管理员表
 * @TableName t_sys_admin
 */
@TableName(value ="t_sys_admin")
@Data
public class TSysAdmin implements Serializable {
    /**
     * 主键编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 姓名
     */
    @TableField(value = "name")
    private String name;

    /**
     * 密码（MD5）
     */
    @TableField(value = "password")
    private String password;

    /**
     * 手机号码
     */
    @TableField(value = "telephone")
    private String telephone;

    /**
     * 手机号码的md5值
     */
    @TableField(value = "telephone_md5")
    private String telephoneMd5;

    /**
     * 工号
     */
    @TableField(value = "jobno")
    private String jobno;

    /**
     * 性别，0男，1女
     */
    @TableField(value = "sex")
    private Byte sex;

    /**
     * 身份证件号码
     */
    @TableField(value = "idcard")
    private String idcard;

    /**
     * 所属部门
     */
    @TableField(value = "dept")
    private String dept;

    /**
     * 是否启用，0启用，1禁用
     */
    @TableField(value = "status")
    private Byte status;

    /**
     * 出生日期
     */
    @TableField(value = "birthday")
    private Date birthday;

    /**
     * 用户所属范围：0系统，1用户
     */
    @TableField(value = "scope")
    private Byte scope;

    /**
     * 登录时的所选域
     */
    @TableField(exist = false)
    private Long domainId;

    /**
     * 所在的OU
     */
    private Long ouId;

    /**
     * 有权管理的域
     */
    private Long mgtDomainId;

    /**
     * 登录图形验证码
     */
    @TableField(exist = false)
    private String imgcode;

    /**
     * 用户所属的角色id
     */
    @TableField(exist = false)
    private Long roleId;

    /**
     * 用户所属角色的名称
     */
    @TableField(exist = false)
    private String roleName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
