package com.plzy.ldap.modules.token.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.modules.admin.domain.TSysAdmin;
import com.plzy.ldap.modules.token.domain.TSysToken;
import com.plzy.ldap.modules.token.mapper.TSysTokenMapper;
import com.plzy.ldap.modules.token.service.TSysTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 *
 */
@Service
public class TSysTokenServiceImpl extends ServiceImpl<TSysTokenMapper, TSysToken>
implements TSysTokenService {

    @Autowired
    private TSysTokenMapper tTokenMapper;

    @Value("${ldap.session-timeout}")
    private Integer sessionTimeout;

    @Override
    public TSysAdmin getCurrUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return tTokenMapper.getCurrUser(request.getHeader("token"));
    }

    @Override
    public TSysToken getToken(TSysAdmin user) {

        // 判断此用户是否已存在token，如有则令其失效
        List<TSysToken> oldToken = list(new LambdaQueryWrapper<TSysToken>().eq(TSysToken::getUserId, user.getId()).eq(TSysToken::getStatus, 0));
        for(TSysToken t : oldToken){
            t.setStatus((byte)1);
            updateById(t);
        }

        // 创建token
        TSysToken token = new TSysToken();
        token.setToken(UUID.randomUUID().toString());
        token.setStatus((byte)0);
        token.setCreateTime(new Date());
        token.setExpirTime(new Date(System.currentTimeMillis() + sessionTimeout*60*1000L));
        token.setUserId(user.getId());
        save(token);

        return token;
    }

    @Override
    public Boolean validate(String token) {
        return tTokenMapper.validate(token) > 0;
    }
}




