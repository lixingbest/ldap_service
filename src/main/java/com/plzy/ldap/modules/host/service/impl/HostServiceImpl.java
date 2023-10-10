package com.plzy.ldap.modules.host.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzy.ldap.framework.ldap.protocol.LDAPResponse;
import com.plzy.ldap.framework.ldap.service.LDAPRemoteService;
import com.plzy.ldap.framework.utils.TerminalUtil;
import com.plzy.ldap.jobs.HostPingJob;
import com.plzy.ldap.jobs.bean.HostPingResult;
import com.plzy.ldap.modules.client.domain.TLdapClientInstLog;
import com.plzy.ldap.modules.client.service.TLdapClientInstLogService;
import com.plzy.ldap.modules.client_access_log.domain.TLdapClientAccessLog;
import com.plzy.ldap.modules.client_access_log.service.TLdapClientAccessLogService;
import com.plzy.ldap.modules.host.dto.HostDTO;
import com.plzy.ldap.modules.host.service.HostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class HostServiceImpl implements HostService {

    @Autowired
    private LDAPRemoteService rpcService;

    @Autowired
    private TLdapClientAccessLogService accessLogService;

    @Value("${ldap.dc-prefix}")
    private String dcPrefix;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public int clearInvalidHost(Long domainId) {

        // 跳过根域
        if(domainId == 1){
            return 0;
        }

        List<Map<String,Object>> hosts = list(domainId,null);

        int count = 0;
        for(Map<String, Object> host : hosts){
            boolean hasKeytab = (Boolean)host.get("has_keytab");
            if(!hasKeytab) {
                log.info("发现已退域的主机记录，即将清理："+host);
                String fqdn = ((List)host.get("fqdn")).get(0) + "";
                removeWithDNS(domainId,fqdn);
                count++;
            }
        }

        return count;
    }

    @Override
    public List find(Long domainId, String fqdn) {

        LDAPResponse resp = rpcService.request(domainId,"{\"method\":\"host_find\",\"params\":[[\""+fqdn+"\"],{\"pkey_only\":true,\"sizelimit\":0,\"version\":\"2.237\"}]}");
        List list = (List) resp.getResult().get("result");
        StringBuilder in = new StringBuilder();
        for (Object item : list) {
            Map it = (Map) item;
            String _fqdn = ((List) it.get("fqdn")).get(0) + "";

            in.append("{" +
                    "                \"method\": \"host_show\"," +
                    "                \"params\": [" +
                    "                    [" +
                    "                        \"").append(_fqdn).append("\"" +
                    "                    ]," +
                    "                    {" +
                    "                        \"no_members\": true"+
                    "                    }" +
                    "                ]" +
                    "            },");
        }
        if(in.length() > 1) {
            in = in.deleteCharAt(in.length() - 1);
        }

        LDAPResponse resp2 = rpcService.request(domainId,"{" +
                "    \"method\": \"batch\"," +
                "    \"params\": [" +
                "        [" +
                "            " + in.toString() +
                "        ]," +
                "        {" +
                "            \"version\": \"2.237\"" +
                "        }" +
                "    ]" +
                "}");

        List list2 = (List) resp2.getResult().get("results");
        List result = new ArrayList();
        for(Object item : list2){
            Map i = (Map)item;
            result.add(i.get("result"));
        }

        return result;
    }

    @Override
    public List list(Long domainId, String fqdnKeywords) {

        LDAPResponse resp = rpcService.request(domainId,"{" +
                "    \"method\": \"host_find\"," +
                "    \"params\": [" +
                "        [" +
                "            \""+(StringUtils.hasText(fqdnKeywords)?fqdnKeywords:"")+"\"" +
                "        ]," +
                "        {" +
                "            \"pkey_only\": true," +
                "            \"sizelimit\": 0," +
                "            \"version\": \"2.237\"" +
                "        }" +
                "    ]" +
                "}");
        List list = (List) resp.getResult().get("result");
        StringBuilder in = new StringBuilder();
        for (Object item : list) {
            Map it = (Map) item;
            String fqdn = ((List) it.get("fqdn")).get(0) + "";

            // 排除域控服务器，fqdn中包含server的字段
            if(fqdn.startsWith(dcPrefix)){
                continue;
            }

            in.append("{" +
                    "                \"method\": \"host_show\"," +
                    "                \"params\": [" +
                    "                    [" +
                    "                        \"").append(fqdn).append("\"" +
                    "                    ]," +
                    "                    {" +
                    "                        \"no_members\": true"+
                    "                    }" +
                    "                ]" +
                    "            },");
        }
        if(in.length() > 1) {
            in = in.deleteCharAt(in.length() - 1);
        }

        LDAPResponse resp2 = rpcService.request(domainId,"{" +
                "    \"method\": \"batch\"," +
                "    \"params\": [" +
                "        [" +
                "            " + in.toString() +
                "        ]," +
                "        {" +
                "            \"version\": \"2.237\"" +
                "        }" +
                "    ]" +
                "}");

        List list2 = (List) resp2.getResult().get("results");
        List result = new ArrayList();
        for(Object item : list2){
            Map i = (Map)item;
            result.add(i.get("result"));
        }

        // 从终端访问日志表中查询主机的相关信息
        if(result != null && result.size() > 0) {
            for(Object item : result){
                String targetFqdn = ((List)((Map)item).get("fqdn")).get(0) + "";
                TLdapClientAccessLog info = accessLogService.getHostInfoByFqdn(targetFqdn);
                if(info != null) {
                    // 探测主机状态
                    if(StringUtils.hasText(info.getIp())){
                        Boolean pingResult = HostPingJob.getLatestPingResult(info.getIp());
                        if(pingResult != null) {
                            info.setIsOnline(pingResult ? 0 : 1); // 0在线，1不在线
                        }else{
                            info.setIsOnline(2); // 未知状态
                        }
                    }else {
                        info.setIsOnline(2); // 未知状态
                    }
                    // 更改ou name，因为仅需要显示处级，所以需要将dn转换为处级的名称
                    if(StringUtils.hasText(info.getOuName())){
                        String[] items = info.getOuName().replaceAll(",DC=.*","").replaceAll("OU=","").split(",");
                        if(items != null && items.length >= 2){
                            info.setOuName(items[items.length - 2]);
                        }else {
                            info.setOuName(Arrays.toString(items));
                        }
                    }
                }
                ((Map)item).put("details",info);
            }
        }

        return result;
    }

    @Override
    public void update(Long domainId, HostDTO dto) {

        Map<String, Object> map = BeanMap.create(dto);

        // 只包含更新内容的字段
        String modifyFilesStr = "";
        for (Map.Entry<String, Object> entry : map.entrySet()){
            // 跳过uid、空值，以及备注
            if(entry.getValue() != null && !entry.getKey().matches("fqdn|version")){
                modifyFilesStr += "\""+entry.getKey()+"\":\""+entry.getValue()+"\",";
            }
        }
        if(modifyFilesStr.length() > 0){
            modifyFilesStr = modifyFilesStr.substring(0, modifyFilesStr.length() - 1);
        }

        rpcService.request(domainId, "{\"method\":\"host_mod\",\"params\":[[\""+dto.getFqdn()+"\"],{\"all\":true,\"rights\":true,"+modifyFilesStr+",\"version\":\"2.237\"}]}");
    }

    @Override
    public void removeWithDNS(Long domainId, String fqdn) {

        rpcService.request(domainId, "{\"method\":\"batch\",\"params\":[[{\"method\":\"host_del\",\"params\":[[\""+fqdn+"\"],{\"updatedns\":true}]}],{\"version\":\"2.237\"}]}");
    }

    @Override
    public Map<String, Object> add(Long domainId, String fqdn) {

        LDAPResponse response = rpcService.request(domainId, "{\"method\":\"host_add\",\"params\":[[\"" + fqdn + "\"],{\"force\":true,\"version\":\"2.237\"}]}");

        Map<String, Object> result = response.getResult();

        return result;
    }
}
