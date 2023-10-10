package com.plzy.ldap.modules.ou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.plzy.ldap.framework.ldap.protocol.LDAPResponse;
import com.plzy.ldap.framework.ldap.service.LDAPRemoteService;
import com.plzy.ldap.modules.domainuser.service.DomainUserService;
import com.plzy.ldap.modules.ou.domain.TLdapOu;
import com.plzy.ldap.modules.ou.dto.OUUserRefDTO;
import com.plzy.ldap.modules.ou.service.TLdapOuService;
import com.plzy.ldap.modules.ou.mapper.TLdapOuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
public class TLdapOuServiceImpl extends ServiceImpl<TLdapOuMapper, TLdapOu>
    implements TLdapOuService{

    @Autowired
    private LDAPRemoteService rpcService;

    @Autowired
    private TLdapOuMapper tLdapOuMapper;

    @Autowired
    private DomainUserService domainUserService;

    /**
     * 递归查找子树
     *
     * @param data
     * @param id
     */
    private void searchTree(List<Map<String, Object>> data, String id, List<Map<String, Object>> result){
        for(Map<String, Object> x : data){
            if(id.equals(x.get("pid") + "")){
                result.add(x);
                searchTree(data, x.get("key") + "", result);
            }
        }
    }

    /**
     * 查找指定域下的所有ou子树的记录（注意域下的直接叶子可能包含多个）
     * @param domainId
     * @return
     */
    @Override
    public List<Map<String, Object>> treeByDomain(Long domainId) {

        List<Map<String, Object>> list = tLdapOuMapper.tree();
        // 分别遍历每个节点的子树，并将结果合并
        List<Map<String, Object>> result = new ArrayList<>();
        searchTree(list, domainId + "-0", result);

        return result;
    }

    @Override
    public List getActiveUserList(Long domainId, String ouCN, String keywords) {

        LDAPResponse resp = null;
        if(!StringUtils.hasText(keywords)){
            // 按照组织机构筛选用户
            TLdapOu ouObj = getById(Long.valueOf(ouCN));
            resp = rpcService.request(domainId,"{\"method\":\"user_find\",\"params\":[[\""+ouObj.getDn()+"\"],{\"uid\":\""+ (StringUtils.hasText(keywords)?keywords:"") +"\",\"pkey_only\":true,\"sizelimit\":0,\"version\":\"2.240\"}]}");
        }else {
            // 用户全局搜索
           resp = rpcService.request(domainId, "{\"method\":\"user_find\",\"params\":[[\"" + keywords + "\"],{\"pkey_only\":true,\"sizelimit\":0,\"version\":\"2.237\"}]}");
        }

        List list = (List) resp.getResult().get("result");
        StringBuilder in = new StringBuilder();
        for (Object item : list) {
            Map it = (Map) item;
            String currCN = ((List) it.get("uid")).get(0) + "";

            in.append("{" +
                    "                \"method\": \"user_show\"," +
                    "                \"params\": [" +
                    "                    [" +
                    "                        \"").append(currCN).append("\"" +
                    "                    ]," +
                    "                    {" +
                    "                        \"no_members\": true," +
                    "                        \"all\": true" +
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
    public List getStageUserList(Long domainId, String ouCN, String uid, String mobile, String cn) {

        LDAPResponse resp = rpcService.request(domainId,"{\"method\":\"stageuser_find\",\"params\":[[\"\\\"ouCN\\\":"+ouCN+"\"],{\"uid\":\""+ (StringUtils.hasText(uid)?uid:"") +"\",\"mobile\":\""+(StringUtils.hasText(mobile)?mobile:"")+"\",\"cn\":\""+(StringUtils.hasText(cn)?cn:"")+"\",\"pkey_only\":true,\"sizelimit\":0,\"version\":\"2.240\"}]}");

        List list = (List) resp.getResult().get("result");
        StringBuilder in = new StringBuilder();
        for (Object item : list) {
            Map it = (Map) item;
            String currCN = ((List) it.get("uid")).get(0) + "";

            in.append("{" +
                    "                \"method\": \"stageuser_show\"," +
                    "                \"params\": [" +
                    "                    [" +
                    "                        \"").append(currCN).append("\"" +
                    "                    ]," +
                    "                    {" +
                    "                        \"no_members\": true," +
                    "                        \"all\": true" +
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
    public List getPreservedUserList(Long domainId, String ouCN, String uid, String mobile, String cn) {

        LDAPResponse resp = rpcService.request(domainId,"{\"method\":\"user_find\",\"params\":[[\"\\\"ouCN\\\":"+ouCN+"\"],{\"uid\":\""+ (StringUtils.hasText(uid)?uid:"") +"\",\"mobile\":\""+(StringUtils.hasText(mobile)?mobile:"")+"\",\"cn\":\""+(StringUtils.hasText(cn)?cn:"")+"\",\"preserved\":true,\"pkey_only\":true,\"sizelimit\":0,\"version\":\"2.240\"}]}");

        List list = (List) resp.getResult().get("result");
        StringBuilder in = new StringBuilder();
        for (Object item : list) {
            Map it = (Map) item;
            String currCN = ((List) it.get("uid")).get(0) + "";

            in.append("{" +
                    "                \"method\": \"user_show\"," +
                    "                \"params\": [" +
                    "                    [" +
                    "                        \"").append(currCN).append("\"" +
                    "                    ]," +
                    "                    {" +
                    "                        \"no_members\": true," +
                    "                        \"all\": true" +
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
    public void addUsers(Long domainId, OUUserRefDTO refDTO) {

        for(String uid : refDTO.getUserCNList().split(",")){
            rpcService.request(domainId,"{\"method\":\"user_mod\",\"params\":[[\""+uid+"\"],{\"all\":true,\"rights\":true,\"ou\":\"ou:"+refDTO.getOuId()+"\",\"version\":\"2.237\"}]}");
        }
    }

    @Override
    public void removeUsers(Long domainId,boolean preserve, OUUserRefDTO refDTO) {

        for(String uid : refDTO.getUserCNList().split(",")){
            domainUserService.delete(domainId,uid,preserve);
        }
    }

    @Override
    public List<TLdapOu> bulkExport(Long pid) {
        return tLdapOuMapper.bulkExport(pid);
    }
}




