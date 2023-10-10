package com.plzy.ldap.modules.message.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 客户端消息
 * @TableName t_ldap_client_msg
 */
@TableName(value ="t_ldap_client_msg")
@Data
public class TLdapClientMsg implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @TableField(value = "time")
    private Date time;

    /**
     * 发布用户
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 发送类型，0立即发送，1 定时发送
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 定时发送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @TableField(value = "send_time")
    private Date sendTime;

    /**
     * 接收者类型：0组织单位,1 终端
     */
    @TableField(value = "receiver_type")
    private Integer receiverType;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
