package com.plzy.ldap.modules.dict.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 数据字典条目表
 * @TableName t_sys_dict_records
 */
@TableName(value ="t_sys_dict_records")
@Data
public class TSysDictRecords implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 数据字典条目表id
     */
    @TableField(value = "dict_item_id")
    private Long dictItemId;

    /**
     * 编号
     */
    @TableField(value = "code")
    private String code;

    /**
     * 值
     */
    @TableField(value = "value")
    private String value;

    /**
     * 备注
     */
    @TableField(value = "comments")
    private String comments;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}