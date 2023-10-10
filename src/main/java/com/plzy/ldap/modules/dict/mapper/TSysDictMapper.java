package com.plzy.ldap.modules.dict.mapper;

import com.plzy.ldap.modules.dict.domain.TSysDict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.plzy.ldap.modules.dict.domain.TSysDictRecords;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.plzy.ldap.modules.dict.domain.TSysDict
 */
@Mapper
public interface TSysDictMapper extends BaseMapper<TSysDict> {

    List<TSysDictRecords> query(@Param("dictCode") String dictCode, @Param("itemCode") String itemCode);
}




