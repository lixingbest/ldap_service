package com.plzy.ldap.modules.dict.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 数据字典条目表
 * @TableName t_sys_dict_item
 */
@TableName(value ="t_sys_dict_item")
@Data
public class TSysDictItem implements Serializable {
    /**
     * 编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 数据字典类别外键id
     */
    @TableField(value = "dict_id")
    private Long dictId;

    /**
     * 名称
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