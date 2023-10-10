package com.plzy.ldap.modules.domainuser.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class StageDomainUserWithExtraCommentsDTO {

    // 用户登录名
    private String uid;

    // 名
    private String givenname;

    // 姓
    private String sn;

    // 全名
    private String cn;

    // 用户密码
    private String userpassword;

    // 邮箱
    private String mail;

    // 联系电话
    private String telephonenumber;

    // 手机号码
    private String mobile;

    // 省
    private String st;

    // 城
    private String l;

    // 区+街道地址
    private String street;

    // 邮政编码
    private String postalcode;

    // 职称
    private String title;

    // 职务
    private String job;

    // 组织单位
    private String ou;

    // 员工编号
    private String employeenumber;

    // 备注1
    private String comments1;

    // 备注2
    private String comments2;

    // 备注3
    private String comments3;

    private String loginshell = "/bin/bash";

    private String version = "2.237";
}
