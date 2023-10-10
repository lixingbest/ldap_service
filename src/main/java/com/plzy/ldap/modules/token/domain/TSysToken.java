package com.plzy.ldap.modules.token.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName t_token
 */
@TableName(value ="t_sys_token")
@Data
public class TSysToken implements Serializable {
    /**
     * 编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * token值
     */
    @TableField(value = "token")
    private String token;

    /**
     * 用户编号
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 失效时间
     */
    @TableField(value = "expir_time")
    private Date expirTime;

    /**
     * 状态，0正常，1失效
     */
    @TableField(value = "status")
    private Byte status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
