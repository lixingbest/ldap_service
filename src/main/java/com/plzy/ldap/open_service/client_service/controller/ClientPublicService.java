package com.plzy.ldap.open_service.client_service.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.modules.admin.domain.TSysAdmin;
import com.plzy.ldap.modules.admin.service.TSysAdminService;
import com.plzy.ldap.modules.client.domain.TLdapClientInstLog;
import com.plzy.ldap.modules.client.service.TLdapClientInstLogService;
import com.plzy.ldap.modules.client_access_log.domain.TLdapClientAccessLog;
import com.plzy.ldap.modules.client_access_log.service.TLdapClientAccessLogService;
import com.plzy.ldap.modules.dns.LdapDNSService;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.domainuser.service.DomainUserService;
import com.plzy.ldap.modules.host.service.HostService;
import com.plzy.ldap.modules.ou.domain.TLdapOu;
import com.plzy.ldap.modules.ou.service.TLdapOuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/public_service/client/")
@Slf4j
public class ClientPublicService {

    @Autowired
    private TLdapClientInstLogService clientInstLogService;

    @Autowired
    private TSysAdminService adminService;

    @Autowired
    private HostService hostService;

    @Autowired
    private TLdapDomainService domainService;

    @Autowired
    private DomainUserService domainUserService;

    @Autowired
    private TLdapClientAccessLogService accessLogService;

    @Autowired
    private LdapDNSService dnsService;

    @Autowired
    private TLdapOuService ouService;

    @Value("#{${ldap.sudoUserPasswd}}")
    private Map<String,String> sudoUserPasswd;

    private ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/getSudoUserPasswd")
    public ResponseEntity<ResponseData> getSudoUserPasswd(String domain){

        if(sudoUserPasswd.containsKey(domain)){
            return ResponseEntity.ok(ResponseData.success(sudoUserPasswd.get(domain)));
        }
        return ResponseEntity.ok(ResponseData.error("962091","没有找到此"+domain+"的密码配置！"));
    }

    @PostMapping("/echo")
    public ResponseEntity<ResponseData> echo(@RequestBody TLdapClientAccessLog accessLog){

        // 根据domain查询domain对象
        TLdapDomain obj = domainService.getOne(new LambdaQueryWrapper<TLdapDomain>().eq(TLdapDomain::getDomainName, accessLog.getDomain().toLowerCase()).eq(TLdapDomain::getUpStatus,0));

        accessLog.setAccessTime(new Date());
        accessLog.setDomainId(obj.getId());
        accessLog.setLevel("info");
        accessLog.setAction("域用户登录终端");
        accessLog.setDetails("域用户登录终端");

        // 根据用户uid查询ouid
        // 如果带有ldap域后缀，则去掉
        String shortname = accessLog.getUid();
        if(shortname.indexOf("@") != -1){
            shortname = accessLog.getUid().split("@")[0];
        }
        Map userinfo =  domainUserService.getByUid(obj.getId(),shortname);
        if(userinfo != null) {

            // 获取当前用户所属的ou
            TLdapOu currOU = null;
            try{
                String ouCN = ((List)userinfo.get("ou")).get(0) + "";
                Map<String,Object> ouObj = objectMapper.readValue(ouCN,Map.class);
                Long ouId = Long.valueOf(ouObj.get("ouCN")+"");
                currOU = ouService.getById(ouId);
            }catch (Exception e){
                log.error("解析用户ou json时遇到错误：",e);
            }

            if(currOU != null) {
                accessLog.setOuId(currOU.getId());
                accessLog.setOuName(currOU.getName());
            }
            accessLog.setUserName(((List) userinfo.get("cn")).get(0) + "");
            accessLog.setDomainId(obj.getId());
        }

        accessLogService.save(accessLog);

        return ResponseEntity.ok(ResponseData.success(accessLog.getId()));
    }

    @GetMapping("/clearInvalidHost")
    public ResponseEntity<ResponseData> clearInvalidHost(String domain){

        // 根据domain查询domain对象
        TLdapDomain obj = domainService.getOne(new LambdaQueryWrapper<TLdapDomain>().eq(TLdapDomain::getDomainName, domain.toLowerCase()).eq(TLdapDomain::getUpStatus,0));

        int effect = hostService.clearInvalidHost(obj.getId());

        return ResponseEntity.ok(ResponseData.success(effect));
    }


    @GetMapping("/checkHostnameAvli")
    public ResponseEntity<ResponseData> checkHostnameAvli(String domain, String fqdn){

        // 根据domain查询domain对象
        TLdapDomain obj = domainService.getOne(new LambdaQueryWrapper<TLdapDomain>().eq(TLdapDomain::getDomainName, domain.toLowerCase()).eq(TLdapDomain::getUpStatus,0));

        List rs = hostService.find(obj.getId(), fqdn);
        return ResponseEntity.ok(ResponseData.success(rs.size() == 0));
    }

    @GetMapping("/getUserPwdExpirDate")
    public ResponseEntity<ResponseData> getUserPwdExpirDate(String domain, String uid){

        // 根据domain查询domain对象
        TLdapDomain obj = domainService.getOne(new LambdaQueryWrapper<TLdapDomain>().eq(TLdapDomain::getDomainName, domain.toLowerCase()).eq(TLdapDomain::getUpStatus,0));

        List resp = domainUserService.getFullInfoByUidNoCache(obj.getId(),uid);
        return ResponseEntity.ok(ResponseData.success(resp));
    }

    @GetMapping("/removeHost")
    public ResponseEntity<ResponseData> removeByDomainAndFQDN(String domain, String fqdn){

        // 根据domain查询domain对象
        TLdapDomain obj = domainService.getOne(new LambdaQueryWrapper<TLdapDomain>().eq(TLdapDomain::getDomainName, domain.toLowerCase()).eq(TLdapDomain::getUpStatus,0));

        int count = hostService.clearInvalidHost(obj.getId());
        log.info("已退域的主机记录已清理完成，清理了"+count+"个");

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/validateAdmin")
    public ResponseEntity<ResponseData> validateAdmin(String username, String password){

        TSysAdmin obj = adminService.getOne(new LambdaQueryWrapper<TSysAdmin>().eq(TSysAdmin::getJobno,username).eq(TSysAdmin::getPassword,password).eq(TSysAdmin::getStatus,0).eq(TSysAdmin::getScope,0));
        if(obj == null){
            return ResponseEntity.ok(ResponseData.error("679845","管理员账号验证错误！"));
        }

        return ResponseEntity.ok(ResponseData.success());
    }

    @PostMapping("/beginInstall")
    public ResponseEntity<ResponseData> beginInstall(@RequestBody TLdapClientInstLog log){

        log.setBeginTime(new Date());
        log.setStep((byte)0);
        clientInstLogService.save(log);

        return ResponseEntity.ok(ResponseData.success(log.getId()));
    }

    @PostMapping("/endInstall")
    public ResponseEntity<ResponseData> endInstall(@RequestParam("file") MultipartFile[] multipartFile, @RequestHeader("id") Long id, @RequestHeader("result") Byte _result) throws Exception{

        String logContent = "";
        if(multipartFile != null){
            String newLine = System.getProperty("line.separator");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(multipartFile[0].getInputStream()));
            StringBuilder result = new StringBuilder();
            for (String line; (line = reader.readLine()) != null; ) {
                if (result.length() > 0) {
                    result.append(newLine);
                }
                result.append(line);
            }
            logContent = result.toString();
        }

        TLdapClientInstLog result = clientInstLogService.getById(id);
        result.setLog(logContent);
        result.setEndTime(new Date());
        result.setStep((byte)1);
        result.setResult(_result);

        clientInstLogService.updateById(result);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/addDNSRec")
    public ResponseEntity<ResponseData> addDNSRec(String domain, String hostname, String ip){

        TLdapDomain target = domainService.getOne(new LambdaQueryWrapper<TLdapDomain>().eq(TLdapDomain::getDomainName,domain).eq(TLdapDomain::getUpStatus,0));
        dnsService.addDNSRec(target.getId(), hostname, ip);

        return ResponseEntity.ok(ResponseData.success());
    }
}
