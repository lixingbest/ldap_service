package com.plzy.ldap.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.plzy.ldap.modules.dict.domain.TSysDictItem;
import com.plzy.ldap.modules.dict.domain.TSysDictRecords;
import com.plzy.ldap.modules.dict.service.TSysDictItemService;
import com.plzy.ldap.modules.dict.service.TSysDictRecordsService;
import com.plzy.ldap.modules.resource.domain.TResource;
import com.plzy.ldap.modules.resource.service.TResourceService;
import com.plzy.ldap.modules.sys_log.domain.TSysLog;
import com.plzy.ldap.modules.sys_log.service.TSysLogService;
import com.plzy.ldap.modules.token.domain.TSysToken;
import com.plzy.ldap.modules.token.service.TSysTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Order(2)
public class SysLogInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private TSysTokenService tokenService;
    @Autowired
    private TSysLogService logService;
    @Autowired
    private TSysDictItemService dictItemService;
    @Autowired
    private TSysDictRecordsService dictRecordsService;
    @Autowired
    private TResourceService resourceService;


    private boolean isWhiteList(Set<String> whiteList, String url) {
        boolean flag = false;
        for (String s : whiteList) {
            String mu = s;
            if ('^' == mu.charAt(0)) {
                mu = mu.substring(1);
                String releaseurl = "^" + mu;//放行的url
                releaseurl = releaseurl.replace("**", "[\\s\\S]*");
                if (url.matches(releaseurl)) {
                    break;
                }
            } else if ("**".equals(mu)) {//所有都放行
                flag = true;
                break;
            } else {
                String releaseurl = "^" + mu;//放行的url
                releaseurl = releaseurl.replace("**", "[\\s\\S]*");
                if (url.matches(releaseurl)) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI().replace("/service", "");

        TSysDictItem log = dictItemService.getOne(new LambdaQueryWrapper<TSysDictItem>().eq(TSysDictItem::getCode, "log"));

        List<TSysDictRecords> list = dictRecordsService.list(new LambdaQueryWrapper<TSysDictRecords>().eq(TSysDictRecords::getDictItemId, log.getId()));

        HashMap<String, String> uriMap = new HashMap<>();


        for (int i = 0; i < list.size(); i++) {

            uriMap.put(list.get(i).getCode(), list.get(i).getValue());
        }

        if (!isWhiteList(uriMap.keySet(), requestURI)) {
            return true;
        }

        String token = request.getHeader("token");
        String body;

        Long domainId = null;

        if (request.getMethod().equals("POST")) {
            RequestWrapper requestWrapper = new RequestWrapper(request);

            body = requestWrapper.getBody();

            try {
                Map map = JSONObject.parseObject(body, Map.class);
                domainId = Long.parseLong(String.valueOf(map.get("domainId")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Enumeration<String> parameterNames = request.getParameterNames();

            HashMap<String, String> map = new HashMap<>();
            while (parameterNames.hasMoreElements()) {
                String name = parameterNames.nextElement();
                String value = request.getParameter(name);
                map.put(name, value);
            }
            domainId = Long.parseLong(map.get("domainId"));

            body = JSONObject.toJSONString(map);
        }

        TSysToken tokenInst = tokenService.getOne(new LambdaQueryWrapper<TSysToken>().eq(TSysToken::getToken, token).eq(TSysToken::getStatus, 0));


        if (domainId != null) {
            String path = request.getHeader("path");

            List<TResource> resources = resourceService.list();
            List<TResource> resourceList = resources.stream().filter(item -> null != item.getUrl() && path.contains(item.getUrl())).collect(Collectors.toList());

            String resource = "";
            if (resourceList.size() > 0) {
                for (TResource res : resourceList) {
                    if (res.getUrl().length() > resource.length()) {
                        resource = res.getName();
                    }
                }
                //resource = resourceList.get(0).getName();
            } else {
                resource = path;
            }
            String[] split = uriMap.get(requestURI).split(":");
            TSysLog tSysLog = new TSysLog(domainId, tokenInst.getUserId(), new Date(), resource, split[0], split[1], body);

            logService.save(tSysLog);
        }

        return true;
    }
}
