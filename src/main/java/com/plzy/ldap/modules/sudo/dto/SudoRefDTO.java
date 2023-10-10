package com.plzy.ldap.modules.sudo.dto;

import lombok.Data;

import java.util.List;

@Data
public class SudoRefDTO {

    private String ruleCN;

    private String groupList;

    private String userList;
}
