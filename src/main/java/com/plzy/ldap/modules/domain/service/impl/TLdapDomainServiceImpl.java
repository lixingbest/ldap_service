package com.plzy.ldap.modules.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.dto.DomainTree;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.domain.mapper.TLdapDomainMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

/**
 *
 */
@Service
public class TLdapDomainServiceImpl extends ServiceImpl<TLdapDomainMapper, TLdapDomain>
    implements TLdapDomainService {

    @Autowired
    private TLdapDomainMapper ldapDcMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<DomainTree> tree(Long domainId) {
        return ldapDcMapper.tree(domainId);
    }

    @Override
    public List<DomainTree> treeWithoutOu() {
        return ldapDcMapper.treeWithoutOu();
    }

    @Override
    public List<TLdapDomain> listSubdomain() {
        return ldapDcMapper.listSubdomain();
    }

    @Override
    public boolean changeUpStatus(Long domainId, Byte upStatus) {
        TLdapDomain target = getById(domainId);

        // 如果要修改为在线，则需要首先探测状态
        if(upStatus == 0){

            try{
                ResponseEntity resp = restTemplate.exchange(target.getServiceUrl(), HttpMethod.POST, null, String.class);
                if(resp.getStatusCode() != HttpStatus.MOVED_PERMANENTLY){
                    return false;
                }
            }catch (Exception e){
                return false;
            }
        }

        target.setUpStatus(upStatus);
        updateById(target);
        return true;
    }
}




