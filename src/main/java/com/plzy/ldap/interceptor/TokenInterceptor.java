package com.plzy.ldap.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.plzy.ldap.modules.token.domain.TSysToken;
import com.plzy.ldap.modules.token.service.TSysTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * token 校验器
 */
@Component
@Order(1)
@Slf4j
public class TokenInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private TSysTokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String uri = request.getRequestURI().toLowerCase();
        // 注意：uri在上文中已经全部转为小写，因此在地址判断时需要匹配
        if (uri.endsWith(".js") || uri.endsWith(".css") || uri.endsWith(".html") || uri.indexOf("/token/get") != -1
                || uri.indexOf("/token/validate") != -1
                || uri.indexOf("/admin/login") != -1
                || uri.indexOf("/public_service/") != -1
                || uri.indexOf("/domain/treewithoutou") != -1 //登录界面的域列表
                || uri.indexOf("/strategy/settings/getimage") != -1
                || uri.indexOf("/admin/kaptcha-image") != -1
                || uri.indexOf("/public_service/client") != -1
                || uri.indexOf("verification_code_enable") != -1
                || uri.indexOf("/host/export") != -1
                || uri.indexOf("/ou/exportactiveuserlist") != -1
        ) {
            return true;
        }

        String token = request.getHeader("token");

        if (!StringUtils.hasText(token)) {
            response.setContentType("text/json; charset=UTF-8");
            response.getWriter().println("{\"success\":false,\"code\":\"387678\",\"message\":\"token不能为空！\",\"time\":\"" + new Date(System.currentTimeMillis()) + "\",\"data\":null}");
            return false;
        }

        if (!tokenService.validate(token)) {
            response.setContentType("text/json; charset=UTF-8");
            response.getWriter().println("{\"success\":false,\"code\":\"387679\",\"message\":\"token不合法或已过期！\",\"time\":\"" + new Date(System.currentTimeMillis()) + "\",\"data\":null}");
            return false;
        } else {
            return true;
        }
    }
}
