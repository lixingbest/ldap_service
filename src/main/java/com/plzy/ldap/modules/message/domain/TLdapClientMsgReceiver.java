package com.plzy.ldap.modules.message.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 客户端消息接收者
 * @TableName t_ldap_client_msg_receiver
 */
@TableName(value ="t_ldap_client_msg_receiver")
@Data
public class TLdapClientMsgReceiver implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 客户端消息 id
     */
    @TableField(value = "client_msg_id")
    private Long clientMsgId;

    /**
     * 类型，0 组织单位，1 终端
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 记录关联的 id，取决于type
     */
    @TableField(value = "ref_id")
    private String refId;

    /**
     * 客户端是否已接收
     */
    @TableField(value = "is_recv")
    private Integer isRecv;

    /**
     * 客户端接收时间
     */
    @TableField(value = "recv_time")
    private LocalDateTime recvTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}