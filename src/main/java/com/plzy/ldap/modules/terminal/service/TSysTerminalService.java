package com.plzy.ldap.modules.terminal.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.modules.terminal.domain.TSysTerminal;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
* @author lichao
* @description 针对表【t_sys_terminal(终端列表)】的数据库操作Service
* @createDate 2022-12-26 15:52:55
*/
public interface TSysTerminalService extends IService<TSysTerminal> {

    IPage<TSysTerminal> getPage(Page<TSysTerminal> page,Set<Long> typeIds, String param);

    List<TSysTerminal> getList(Set<Long> typeIds, String param);
}
