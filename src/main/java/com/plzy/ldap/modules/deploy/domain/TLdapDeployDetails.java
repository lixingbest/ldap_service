package com.plzy.ldap.modules.deploy.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 部署计划执行明细
 * @TableName t_ldap_deploy_details
 */
@TableName(value ="t_ldap_deploy_details")
@Data
public class TLdapDeployDetails implements Serializable {
    /**
     * 编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 部署计划id
     */
    @TableField(value = "main_id")
    private Long mainId;

    /**
     * 终端id
     */
    @TableField(value = "terminal_id")
    private Long terminalId;

    /**
     * 开始执行时间
     */
    @TableField(value = "begin_time")
    private Date beginTime;

    /**
     * 结束执行时间
     */
    @TableField(value = "end_time")
    private Date endTime;

    /**
     * 执行日志
     */
    @TableField(value = "exec_log")
    private String execLog;

    /**
     * 执行状态：成功0，失败1
     */
    @TableField(value = "status")
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}