package com.plzy.ldap.modules.role.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 角色用户关联表
 * @TableName t_role_user_ref
 */
@TableName(value ="t_role_user_ref")
@Data
public class TRoleUserRef implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 角色编号
     */
    private Long roleId;


    private Integer isTemporary;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
