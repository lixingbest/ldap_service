package com.plzy.ldap.modules.resource.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.List;


import lombok.Data;

/**
 * 系统资源表
 * @TableName t_resource
 */
@TableName(value ="t_resource")
@Data
public class TResource implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 编号
     */
    private String code;

    /**
     * url地址，请不要包含绝对路径
     */
    private String url;

    /**
     * 字体图标名称
     */
    private String icon;

    /**
     * 排序索引
     */
    private Integer idx;

    /**
     * 是否启用，0启用，1禁用
     */
    private Integer enable;

    private Long pid;

    private Integer type;

    private String tooltip;

    @TableField(exist = false)
    private Integer count;

    @TableField(exist = false)
    private List<TResource> children;
    @TableField(exist = false)
    private List<TResource> buttons;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
