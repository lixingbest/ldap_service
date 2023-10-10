package com.plzy.ldap.modules.terminal.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.modules.terminal.domain.TSysTerminal;
import com.plzy.ldap.modules.terminal.service.TSysTerminalService;
import com.plzy.ldap.modules.terminal.mapper.TSysTerminalMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @author lichao
 * @description 针对表【t_sys_terminal(终端列表)】的数据库操作Service实现
 * @createDate 2022-12-26 15:52:55
 */
@Service
public class TSysTerminalServiceImpl extends ServiceImpl<TSysTerminalMapper, TSysTerminal>
        implements TSysTerminalService {

    @Resource
    private TSysTerminalMapper tSysTerminalMapper;

    @Override
    public IPage<TSysTerminal> getPage(Page<TSysTerminal> page, Set<Long> typeIds, String param) {

        return tSysTerminalMapper.getPage(page,typeIds, param);
    }

    @Override
    public List<TSysTerminal> getList(Set<Long> typeIds, String param) {

        return tSysTerminalMapper.getList(typeIds,param);
    }
}




