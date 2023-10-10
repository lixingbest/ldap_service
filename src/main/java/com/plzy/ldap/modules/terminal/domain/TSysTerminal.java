package com.plzy.ldap.modules.terminal.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * 终端列表
 *
 * @TableName t_sys_terminal
 */
@TableName(value = "t_sys_terminal")
@Data
public class TSysTerminal implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 主机名
     */
    @TableField(value = "hostname")
    @ExcelProperty(value = "主机名")
    private String hostname;

    /**
     * Ipv4地址
     */
    @TableField(value = "ipv4")
    @ExcelProperty(value = "Ipv4地址")
    private String ipv4;

    /**
     * Ssh的端口号
     */
    @TableField(value = "ssh_port")
    @ExcelProperty(value = "ssh的端口号")
    private Integer sshPort;

    /**
     * 超级管理员的用户名
     */
    @TableField(value = "account")
    @ExcelProperty(value = "超级管理员的用户名")
    private String account;

    /**
     * 密码
     */
    @TableField(value = "password")
    @ExcelProperty(value = "密码")
    private String password;

    /**
     * 所属的终端类型
     */
    @TableField(value = "type_id")
    private Long typeId;


    @TableField(exist = false)
    @ExcelProperty(value = "所属的终端类型")
    private String typeName;

    /**
     * 说明
     */
    @TableField(value = "comments")
    @ExcelProperty(value = "说明")
    private String comments;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
