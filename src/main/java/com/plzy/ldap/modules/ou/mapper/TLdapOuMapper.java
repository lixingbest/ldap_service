package com.plzy.ldap.modules.ou.mapper;

import com.plzy.ldap.modules.ou.domain.TLdapOu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Entity com.plzy.ldap.modules.ou.domain.TLdapOu
 */
@Mapper
public interface TLdapOuMapper extends BaseMapper<TLdapOu> {

    @MapKey("id")
    List<Map<String, Object>> tree();

    List<TLdapOu> bulkExport(@Param("pid") Long pid);
}




