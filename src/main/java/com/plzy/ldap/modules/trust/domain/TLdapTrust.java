package com.plzy.ldap.modules.trust.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName t_ldap_trust
 */
@TableName(value ="t_ldap_trust")
@Data
public class TLdapTrust implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 类型：1域，2OU，3用户
     */
    @TableField(value = "type")
    private Byte type;

    /**
     * 当前域id

     */
    @TableField(value = "domain_id")
    private Long domainId;

    /**
     * 信任的域id
     */
    @TableField(value = "trust_domain_id")
    private Long trustDomainId;

    /**
     * 信任的组织单位id
     */
    @TableField(value = "trust_ou_id")
    private Long trustOuId;

    /**
     * 信任的用户id
     */
    @TableField(value = "trust_uid")
    private String trustUid;

    /**
     * 是否启用：0启用，1禁用
     */
    @TableField(value = "enable")
    private Byte enable;

    /**
     * 是否同步，0未同步，1同步
     */
    @TableField(value = "is_sync")
    private Byte isSync;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}