package com.plzy.ldap.modules.active_directory.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * Ad同步任务明细表
 * @TableName t_ldap_ad_sync_job_details
 */
@TableName(value ="t_ldap_ad_sync_job_details")
@Data
public class TLdapAdSyncJobDetails implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * ad同步任务id
     */
    @TableField(value = "ad_sync_job_id")
    private Long adSyncJobId;

    /**
     * 同步类型，ou：0，域用户：1
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 同步对象名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 更新类型：新增：0，更新：1
     */
    @TableField(value = "update_type")
    private Integer updateType;

    /**
     * 结果：成功0，失败1
     */
    @TableField(value = "result")
    private Integer result;

    /**
     * 更新时间
     */
    @TableField(value = "time")
    private Date time;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
