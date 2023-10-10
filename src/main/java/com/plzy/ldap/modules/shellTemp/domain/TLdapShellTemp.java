package com.plzy.ldap.modules.shellTemp.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 脚本模板表
 * @TableName t_ldap_shell_temp
 */
@TableName(value ="t_ldap_shell_temp")
@Data
public class TLdapShellTemp implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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
     * 父级id
     */
    @TableField(value = "pid")
    private Long pid;

    /**
     * 脚本内容
     */
    @TableField(value = "shell")
    private String shell;

    /**
     * 说明
     */
    @TableField(value = "comments")
    private String comments;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}