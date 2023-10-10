package com.plzy.ldap.modules.role.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 角色资源关联表
 * @TableName t_role_res_ref
 */
@TableName(value ="t_role_res_ref")
@Data
public class TRoleResRef implements Serializable {
    /**
     * 编号
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 角色编号
     */
    private Long roleId;

    /**
     * 资源编号
     */
    private Long resId;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
