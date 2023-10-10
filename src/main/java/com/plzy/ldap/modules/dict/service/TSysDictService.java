package com.plzy.ldap.modules.dict.service;

import com.plzy.ldap.modules.dict.domain.TSysDict;
import com.baomidou.mybatisplus.extension.service.IService;
import com.plzy.ldap.modules.dict.domain.TSysDictRecords;

import java.util.List;

/**
 *
 */
public interface TSysDictService extends IService<TSysDict> {

    List<TSysDictRecords> query(String dictCode, String itemCode);
}
