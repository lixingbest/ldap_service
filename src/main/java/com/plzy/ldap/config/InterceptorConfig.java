package com.plzy.ldap.config;

import com.plzy.ldap.interceptor.SysLogInterceptor;
import com.plzy.ldap.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter {
    @Autowired
    private TokenInterceptor tokenInterceptor;
    @Autowired
    private SysLogInterceptor sysLogInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor);
        registry.addInterceptor(sysLogInterceptor);
    }
}
