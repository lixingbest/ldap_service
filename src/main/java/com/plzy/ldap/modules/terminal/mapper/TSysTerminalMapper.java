package com.plzy.ldap.modules.terminal.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.modules.terminal.domain.TSysTerminal;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author lichao
 * @description 针对表【t_sys_terminal(终端列表)】的数据库操作Mapper
 * @createDate 2022-12-26 15:52:55
 * @Entity com.plzy.ldap.modules.terminal.domain.TSysTerminal
 */
public interface TSysTerminalMapper extends BaseMapper<TSysTerminal> {

    IPage<TSysTerminal> getPage(Page<TSysTerminal> page,
                                @Param("typeIds") Set<Long> typeId,
                                @Param("param") String param);

    List<TSysTerminal> getList(Set<Long> typeIds, String param);
}




