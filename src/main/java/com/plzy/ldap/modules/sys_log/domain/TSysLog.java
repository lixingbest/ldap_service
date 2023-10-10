package com.plzy.ldap.modules.sys_log.domain;

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
 * 系统日志表
 * @TableName t_sys_log
 */
@TableName(value ="t_sys_log")
@Data
public class TSysLog implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所在域
     */
    @TableField(value = "domain_id")
    private Long domainId;

    /**
     * 用户 id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 时间
     */
    @TableField(value = "time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date time;

    /**
     * menu name
     */
    @TableField(value = "menu")
    private String menu;

    /**
     * 操作类型
     */
    @TableField(value = "type")
    private String type;

    /**
     * 操作内容
     */
    @TableField(value = "message")
    private String message;

    /**
     * 操作数据
     */
    @TableField(value = "data")
    private String data;

    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String domainName;

    @TableField(exist = false)
    private String jobNo;

    public TSysLog() {
    }

    public TSysLog(Long domainId, Long userId, Date time, String menu, String type, String message, String data) {
        this.domainId = domainId;
        this.userId = userId;
        this.time = time;
        this.menu = menu;
        this.type = type;
        this.message = message;
        this.data = data;
    }

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
