package com.plzy.ldap.modules.role.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色表
 * @TableName t_role
 */
@TableName(value ="t_role")
@Data
public class TRole implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 角色名称

     */
    private String name;

    /**
     * 编号
     */
    private String code;

    /**
     * 是否启用，0启用，1禁用
     */
    private Byte enable;

    /**
     * 备注
     */
    private String comments;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
