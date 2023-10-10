package com.plzy.ldap.modules.client.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 客户端安装日志
 * @TableName t_ldap_client_inst_log
 */
@TableName(value ="t_ldap_client_inst_log")
@Data
public class TLdapClientInstLog implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 主机名
     */
    @TableField(value = "hostname")
    private String hostname;

    /**
     * 所属域
     */
    @TableField(value = "domain")
    private String domain;

    /**
     * 客户端版本
     */
    @TableField(value = "client_version")
    private String clientVersion;

    /**
     * 架构
     */
    @TableField(value = "arch")
    private String arch;

    /**
     * 系统名称
     */
    @TableField(value = "sys_name")
    private String sysName;

    /**
     * 系统版本
     */
    @TableField(value = "sys_version")
    private String sysVersion;

    /**
     * 用户名
     */
    @TableField(value = "user")
    private String user;

    /**
     * ip地址
     */
    @TableField(value = "ip")
    private String ip;

    /**
     * 开始安装时间

     */
    @TableField(value = "begin_time")
    private Date beginTime;

    /**
     * 结束安装时间
     */
    @TableField(value = "end_time")
    private Date endTime;

    /**
     * 当前步骤：0开始安装，1结束安装
     */
    @TableField(value = "step")
    private Byte step;

    /**
     * 安装结果：0成功，1失败
     */
    @TableField(value = "result")
    private Byte result;

    /**
     * 日志内容
     */
    @TableField(value = "log")
    private String log;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
