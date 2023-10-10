package com.plzy.ldap.modules.report.dto;

import lombok.Data;

@Data
public class DomainUser {

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

    // 组织单位
    // 注意：此字段的存储结构为：ou的id-ou的name，例如"1-开发事业部"
    private String ou;

    // 员工编号
    private String employeenumber;

    // 支持的用户认证类型
    private String[] ipauserauthtype = new String[]{"password"};

    private String loginshell = "/bin/bash";

    private String version = "2.237";
}
