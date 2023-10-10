package com.plzy.ldap.modules.ldap_login_limit.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 域用户登录限制
 * @TableName t_ldap_login_limit
 */
@TableName(value ="t_ldap_login_limit")
@Data
public class TLdapLoginLimit implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 域id
     */
    @TableField(value = "domain_id")
    private Long domainId;

    /**
     * 用户 uid
     */
    @TableField(value = "uid")
    private String uid;

    /**
     * 允许登录的起始日期
     */
    @TableField(value = "login_date_begin")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date loginDateBegin;

    /**
     * 允许登录的结束日期
     */
    @TableField(value = "login_date_end")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date loginDateEnd;

    /**
     * 允许登录的起始时间 ，格式：hh:mm
     */
    @TableField(value = "login_time_begin")
    private String loginTimeBegin;

    /**
     * 允许登录的结束时间，格式：hh:mm
     */
    @TableField(value = "login_time_end")
    private String loginTimeEnd;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
