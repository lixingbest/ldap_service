package com.plzy.ldap.jobs;

import com.plzy.ldap.framework.utils.ProcessUtil;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.domainuser.service.DomainUserService;
import com.plzy.ldap.modules.ou.domain.TLdapOu;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PasswordChangeJob {

    @Autowired
    private TLdapDomainService domainService;

    @Autowired
    private DomainUserService domainUserService;

//    @PostConstruct
    public void change(){

        long start = System.currentTimeMillis();

        // 加载所有的域名
        List<TLdapDomain> domainList = domainService.listSubdomain();
        for(TLdapDomain domain : domainList){

            int index = 0;
            int success = 0;
            int error = 0;

            // 加载所有域用户
            List userList = domainUserService.listAll(domain.getId());
            for(Object user : userList){
                try{
                    Map userMap = (Map)user;
                    String uid = ((List)userMap.get("uid")).get(0) + "";

                    if("admin".equals(uid)){
                        log.info("跳过admin域用户!");
                        continue;
                    }

                    log.info("即将修改 "+uid+" 的密码");

                    String out = ProcessUtil.exec(new String[]{"/usr/bin/expect","/opt/passwd.sh",uid+"@jn.gacc.hg.cn"});
                    if(out.indexOf("鉴定故障") == -1){
                        success ++;
                    }else {
                        error ++;
                    }
                    log.info(uid+ " 的密码修改完成，结果："+out);
                    log.info("进度："+index+"/"+userList.size()+", 成功="+success+", 失败="+error);

                    index ++;

                    Thread.sleep(500L);

                }catch (Exception e2){
                    log.error("更新属性时出现错误：",e2);
                }
            }

            log.info("所有密码修改完成，统计：success="+success+" , error="+error + ", 耗时="+(System.currentTimeMillis()-start)/1000L+"s");
        }

    }
}
