package com.plzy.ldap.modules.dict.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.modules.dict.domain.TSysDict;
import com.plzy.ldap.modules.dict.domain.TSysDictRecords;
import com.plzy.ldap.modules.dict.service.TSysDictService;
import com.plzy.ldap.modules.dict.mapper.TSysDictMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class TSysDictServiceImpl extends ServiceImpl<TSysDictMapper, TSysDict>
    implements TSysDictService{

    @Autowired
    private TSysDictMapper sysDictMapper;

    @Override
    public List<TSysDictRecords> query(String dictCode, String itemCode) {
        return sysDictMapper.query(dictCode, itemCode);
    }
}




