package com.plzy.ldap.modules.dict.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 数据字典
 * @TableName t_sys_dict
 */
@TableName(value ="t_sys_dict")
@Data
public class TSysDict implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 父id
     */
    @TableField(value = "pid")
    private Long pid;

    /**
     * 
     */
    @TableField(value = "name")
    private String name;

    /**
     * 编号
     */
    @TableField(value = "code")
    private String code;

    /**
     * 备注
     */
    @TableField(value = "comments")
    private String comments;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}