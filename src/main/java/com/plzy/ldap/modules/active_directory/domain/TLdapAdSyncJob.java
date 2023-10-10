package com.plzy.ldap.modules.active_directory.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * Ad同步任务表
 * @TableName t_ldap_ad_sync_job
 */
@TableName(value ="t_ldap_ad_sync_job")
@Data
public class TLdapAdSyncJob implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 域id
     */
    @TableField(value = "domain_id")
    private Long domainId;

    /**
     * 开始同步时间
     */
    @TableField(value = "begin_time")
    private Date beginTime;

    /**
     * 结束同步时间
     */
    @TableField(value = "end_time")
    private Date endTime;

    /**
     * ou待同步总数
     */
    @TableField(value = "ou_total")
    private Integer ouTotal;

    /**
     * ou成功数
     */
    @TableField(value = "ou_success")
    private Integer ouSuccess;

    /**
     * ou错误数
     */
    @TableField(value = "ou_error")
    private Integer ouError;

    /**
     * 用户待同步总数
     */
    @TableField(value = "user_total")
    private Integer userTotal;

    /**
     * 用户成功数
     */
    @TableField(value = "user_success")
    private Integer userSuccess;

    /**
     * 用户错误数
     */
    @TableField(value = "user_error")
    private Integer userError;

    /**
     * 同步结果
     * 成功0，失败1，进行中2
     */
    @TableField(value = "result")
    private Integer result;

    /**
     * 同步日志
     */
    @TableField(value = "log")
    private String log;

    /**
     * domain地址
     */
    @TableField(exist = false)
    private String domain;

    /**
     * domain中文名称
     */
    @TableField(exist = false)
    private String domainName;

    /**
     * 同步地址
     */
    @TableField(exist = false)
    private String syncUrl;

    /**
     * 目标域名
     */
    @TableField(exist = false)
    private String syncDomainName;

    /**
     * 新增的条目
     */
    @TableField(exist = false)
    private Integer addNum4OU;

    /**
     * 更新的条目
     */
    @TableField(exist = false)
    private Integer updateNum4OU;

    @TableField(exist = false)
    private Integer addNum4User;

    @TableField(exist = false)
    private Integer updateNum4User;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
