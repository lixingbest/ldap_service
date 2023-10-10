package com.plzy.ldap.modules.cross_trust.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 跨域信任设置
 * @TableName t_ldap_cross_trust
 */
@TableName(value ="t_ldap_cross_trust")
@Data
public class TLdapCrossTrust implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 源域id
     */
    @TableField(value = "src_domain_id")
    private Long srcDomainId;

    /**
     * 所信任的域id
     */
    @TableField(value = "target_domain_id")
    private Long targetDomainId;

    /**
     * 组织机构id
     */
    @TableField(value = "ou_id")
    private Long ouId;

    /**
     * 用户id
     */
    @TableField(value = "uid")
    private String uid;

    /**
     * 生效时间
     */
    @TableField(value = "begin_time")
    private Date beginTime;

    /**
     * 截止时间
     */
    @TableField(value = "end_time")
    private Date endTime;

    /**
     * 是否启用，0启用，1禁用
     */
    @TableField(value = "enable")
    private Integer enable;

    /**
     * 申请事由
     */
    @TableField(value = "reason")
    private String reason;

    /**
     * 创建用户id
     */
    @TableField(value = "create_user_id")
    private Long createUserId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 同步结果，0未同步，1同步成功，2同步失败
     */
    @TableField(value = "sync_status")
    private Integer syncStatus;

    /**
     * 信任同步时间
     */
    @TableField(value = "sync_time")
    private Date syncTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
