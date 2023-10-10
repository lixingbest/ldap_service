package com.plzy.ldap.modules.ou.dto;

import lombok.Data;

import java.util.List;

@Data
public class OUUserRefDTO {

    private String ouId;

    private String userCNList;
}
