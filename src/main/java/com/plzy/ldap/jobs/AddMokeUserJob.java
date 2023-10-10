package com.plzy.ldap.jobs;

import com.plzy.ldap.modules.domainuser.dto.ActiveDomainUserWithExtraCommentsDTO;
import com.plzy.ldap.modules.domainuser.service.DomainUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class AddMokeUserJob {

    @Autowired
    private DomainUserService domainUserService;

//    @PostConstruct
    public void run(){

        for (int i = 2010; i < 2030; i++) {

            ActiveDomainUserWithExtraCommentsDTO user = new ActiveDomainUserWithExtraCommentsDTO();
            user.setUid("user_"+i);
            user.setGivenname("user_"+i);
            user.setCn("user_"+i);
            domainUserService.save(16L,user);
        }
    }
}
