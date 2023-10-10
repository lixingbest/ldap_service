package com.plzy.ldap.modules.ou.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzy.ldap.framework.utils.DateUtil;
import com.plzy.ldap.framework.utils.ExcelUtil;
import com.plzy.ldap.framework.utils.TextUtil;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.modules.domainuser.dto.ActiveDomainUserWithExtraCommentsDTO;
import com.plzy.ldap.modules.domainuser.service.DomainUserService;
import com.plzy.ldap.modules.ou.domain.TLdapOu;
import com.plzy.ldap.modules.ou.dto.OUUserRefDTO;
import com.plzy.ldap.modules.ou.service.TLdapOuService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Controller
@RequestMapping("/ou")
@Slf4j
public class OUController {

    @Autowired
    private TLdapOuService ldapOuService;

    @Autowired
    private DomainUserService domainUserService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/list")
    public ResponseEntity<ResponseData> list(Page<TLdapOu> page, TLdapOu condition) {

        Page<TLdapOu> list = ldapOuService.page(page);
        return ResponseEntity.ok(ResponseData.success(list));
    }

    @GetMapping("/get")
    public ResponseEntity<ResponseData> get(Long id) {

        TLdapOu obj = ldapOuService.getById(id);
        return ResponseEntity.ok(ResponseData.success(obj));
    }

    @GetMapping("/treeByDomain")
    public ResponseEntity<ResponseData> treeByDomain(Long domainId) {

        List<Map<String, Object>> result = ldapOuService.treeByDomain(domainId);
        return ResponseEntity.ok(ResponseData.success(result));
    }

    @GetMapping("/saveOrUpdate")
    public ResponseEntity<ResponseData> saveOrUpdate(TLdapOu ou) {
        List<TLdapOu> list = ldapOuService.list(new LambdaQueryWrapper<TLdapOu>().orderByDesc(TLdapOu::getOrdIdx));
        ou.setOrdIdx(list.get(0).getOrdIdx() + 1);

        ldapOuService.saveOrUpdate(ou);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/remove")
    public ResponseEntity<ResponseData> remove(String ids) {

        TLdapOu ou = ldapOuService.getById(ids);
        // 删除当前ou以及子机构
        ldapOuService.remove(new QueryWrapper<TLdapOu>().like("dn", ou.getDn()));

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/getActiveUserList")
    public ResponseEntity<ResponseData> getActiveUserList(Long domainId, String ouCN, String keywords) {

        return ResponseEntity.ok(ResponseData.success(ldapOuService.getActiveUserList(domainId, ouCN, keywords)));
    }

    @GetMapping("moveUser")
    public ResponseEntity<ResponseData> moveuser(
            @RequestParam("domainId") Long domainId,
            @RequestParam("uid") String uid,
            @RequestParam("ou") String ou) {


        Map<String, Object> map = domainUserService.getByUid(domainId, uid);

        Map<String, Object> newmap = new HashMap<>();

        for (String key : map.keySet()) {
            if (key.equals("ipauserauthtype")) {
                newmap.put(key, map.get(key));
                continue;
            }
            String value = String.valueOf(map.get(key));

            if (value != null && value.contains("[") && value.contains("]")) {
                String content;
                try {
                    JSONArray array = JSONArray.parseArray(value);
                    content = String.valueOf(array.get(0));
                } catch (Exception e) {
                    content = value.replace("[", "").replace("]", "");
                }
                if (content.contains("{") && content.contains("}")) {

                    try {
                        Map<String, String> obj = JSONObject.parseObject(content, Map.class);

                        for (String k : obj.keySet()) {
                            newmap.put(k, obj.get(k));
                        }
                    } catch (Exception e) {
                        //newmap.put(key,content);
                    }
                } else {
                    newmap.put(key, content);
                }
            } else {
                newmap.put(key, map.get(key));
            }
        }


        ActiveDomainUserWithExtraCommentsDTO user = JSONObject.parseObject(JSONObject.toJSONString(newmap), ActiveDomainUserWithExtraCommentsDTO.class);


        TLdapOu ouInst = ldapOuService.getById(ou);

        user.setOu(ou);
        user.setOuDn(ouInst.getDn());


        domainUserService.update(domainId, user);

        return ResponseEntity.ok(ResponseData.success());
    }


    @PostMapping("importActiveUser")
    public ResponseEntity<ResponseData> importActiveUser(@RequestParam("file") MultipartFile multipartFile, @RequestParam("domainId") Long domainId) {

        File file = ExcelUtil.multipartFileToFile(multipartFile);

        List<LinkedHashMap<String, Object>> sheet1 = ExcelUtil.read(file, "域用户", 23);

        file.delete();

        List<String> log = new ArrayList<>();

        for (int i = 0; i < sheet1.size(); i++) {

            LinkedHashMap<String, Object> map = sheet1.get(i);

            String uid = map.get("登录名") != null && !Objects.equals(map.get("登录名"), "") ? String.valueOf(map.get("登录名")) : null;

            String title = map.get("职称") != null && !Objects.equals(map.get("职称"), "") ? String.valueOf(map.get("职称")) : null;

            String job = map.get("职务") != null && !Objects.equals(map.get("职务"), "") ? String.valueOf(map.get("职务")) : null;

            String ou = map.get("组织单位") != null && !Objects.equals(map.get("组织单位"), "") ? String.valueOf(map.get("组织单位")) : null;

            String ouDn = map.get("组织单位的dn") != null && !Objects.equals(map.get("组织单位的dn"), "") ? String.valueOf(map.get("组织单位的dn")) : null;

            if(uid==null || title==null || job==null ||ou==null ||ouDn==null){
                log.add("【第" + i + "行异常】 登录名、职称、职务、组织单位、组织单位的dn 都不能为空。" );
                continue;
            }

            boolean isExist = domainUserService.isExist(domainId, uid);

            if (!isExist) {

                try {
                    ActiveDomainUserWithExtraCommentsDTO user = new ActiveDomainUserWithExtraCommentsDTO();

                    user.setUid(uid);

                    user.setGivenname(map.get("名") != null && !Objects.equals(map.get("名"), "") ? String.valueOf(map.get("名")) : null);
                    user.setSn(map.get("姓") != null && !Objects.equals(map.get("姓"), "") ? String.valueOf(map.get("姓")) : null);
                    user.setCn(map.get("全名") != null && !Objects.equals(map.get("全名"), "") ? String.valueOf(map.get("全名")) : null);
                    user.setUserpassword(map.get("用户密码") != null && !Objects.equals(map.get("用户密码"), "") ? String.valueOf(map.get("用户密码")) : null);
                    user.setMail(map.get("邮箱") != null && !Objects.equals(map.get("邮箱"), "") ? String.valueOf(map.get("邮箱")) : null);
                    user.setTelephonenumber(map.get("联系电话") != null && !Objects.equals(map.get("联系电话"), "") ? String.valueOf(map.get("联系电话")) : null);
                    user.setMobile(map.get("手机号码") != null && !Objects.equals(map.get("手机号码"), "") ? String.valueOf(map.get("手机号码")) : null);
                    user.setSt(map.get("省") != null && !Objects.equals(map.get("省"), "") ? String.valueOf(map.get("省")) : null);
                    user.setL(map.get("市") != null && !Objects.equals(map.get("市"), "") ? String.valueOf(map.get("市")) : null);
                    if(!(String.valueOf(map.get("区")) + map.get("详细地址")).equals("")) {
                        user.setStreet(String.valueOf(map.get("区")) + map.get("详细地址"));
                    }
                    user.setPostalcode(map.get("邮政编码") != null && !Objects.equals(map.get("邮政编码"), "") ? String.valueOf(map.get("邮政编码")) : null);
                    user.setTitle(map.get("职称") != null && !Objects.equals(map.get("职称"), "") ? String.valueOf(map.get("职称")) : null);
                    user.setJob(map.get("职务") != null && !Objects.equals(map.get("职务"), "") ? String.valueOf(map.get("职务")) : null);
                    user.setOu(map.get("组织单位") != null && !Objects.equals(map.get("组织单位"), "") ? String.valueOf(map.get("组织单位")).replace(".0","") : null);
                    user.setOuDn(map.get("组织单位的dn") != null && !Objects.equals(map.get("组织单位的dn"), "") ? String.valueOf(map.get("组织单位的dn")) : null);
                    user.setEmployeenumber(map.get("员工编号") != null && !Objects.equals(map.get("员工编号"), "") ? String.valueOf(map.get("员工编号")) : null);
                    user.setComments1(map.get("备注1") != null && !Objects.equals(map.get("备注1"), "") ? String.valueOf(map.get("备注1")) : null);
                    user.setComments2(map.get("备注2") != null && !Objects.equals(map.get("备注2"), "") ? String.valueOf(map.get("备注2")) : null);
                    user.setComments3(map.get("备注3") != null && !Objects.equals(map.get("备注3"), "") ? String.valueOf(map.get("备注3")) : null);
                    user.setModifyPaswdNextLogin(String.valueOf(map.get("是否在下次登录时必须修改密码")).equals("是"));
                    user.setHomedirectory(map.get("用户家目录的位置") != null && !Objects.equals(map.get("用户家目录的位置"), "") ? String.valueOf(map.get("用户家目录的位置")) : null);

                    String error = domainUserService.save(domainId, user);

                    if (error != null) {
                        log.add("【第" + i + "行异常】 " + error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    log.add("【第" + i + "行异常】 " + e.getMessage());
                }
            } else {
                log.add("【第" + i + "行异常】 登录名已存在！");
            }
        }
        return ResponseEntity.ok(ResponseData.success(log));

    }

    @GetMapping("/exportActiveUserList")
    public void exportActiveUserList(Long domainId, String ouCN, HttpServletResponse response) {

        List resutl = ldapOuService.getActiveUserList(domainId, ouCN, null);
        List<Map<String, Object>> excelData = new ArrayList<>();
        for (Object rec : resutl) {
            try {
                Map map = (Map) rec;
                Map<String, Object> newMap = new HashMap<>();

                if (map.get("ou") != null) {
                    String[] ouItem = objectMapper.readValue(((List) map.get("ou")).get(0) + "", Map.class).get("ouDN").toString().replaceAll(",DC=.*", "").replaceAll("OU=", "").split(",");
                    if (ouItem.length >= 2) {
                        newMap.put("ou", ouItem[ouItem.length - 2]);
                    }
                }
                newMap.put("uid", ((List) map.get("uid")).get(0));
                newMap.put("cn", ((List) map.get("cn")).get(0));
                newMap.put("status", (Boolean) map.get("nsaccountlock") ? "禁用" : "启用");

                if (map.get("employeenumber") != null) {
                    newMap.put("jobno", ((List) map.get("employeenumber")).get(0));
                }
                if (map.get("krbpasswordexpiration") != null) {
                    newMap.put("passwdexpir", DateUtil.formatDate(DateUtil.parseDate((((Map) ((List) map.get("krbpasswordexpiration")).get(0)).get("__datetime__") + "").replaceAll("Z", ""), DateUtil.DATE_PATTERN.yyyyMMddHHmmss), DateUtil.DATE_PATTERN.yyyy_MM_dd_HH_mm_ss));
                }
                if (map.get("title") != null) {
                    Map info = objectMapper.readValue(((List) map.get("title")).get(0) + "", Map.class);
                    newMap.put("title", info.get("title"));
                    newMap.put("job", info.get("job"));
                }
                if (map.get("telephonenumber") != null) {
                    newMap.put("telephone", ((List) map.get("telephonenumber")).get(0));
                }
                if (map.get("mobile") != null) {
                    newMap.put("mobilephone", ((List) map.get("mobile")).get(0));
                }

                if (map.get("homedirectory") != null) {
                    newMap.put("homedir", ((List) map.get("homedirectory")).get(0));
                }
                excelData.add(newMap);
            } catch (Exception e) {
                log.error("导出域用户excel时出现错误：", e);
            }
        }

        ExcelUtil.export("域用户", new String[]{"登录名:uid", "姓名:cn", "工号:uid", "组织单位:ou", "状态:status", "密码过期时间:passwdexpir", "职称:title", "职务:job", "固定电话:telephone", "手机号码:mobilephone", "家目录:homedir"}, excelData, "domainuser_" + System.currentTimeMillis() + ".xlsx", response);
    }

    @GetMapping("/getStageUserList")
    public ResponseEntity<ResponseData> getStageUserList(Long domainId, String ouCN, String uid, String mobile, String cn) {

        return ResponseEntity.ok(ResponseData.success(ldapOuService.getStageUserList(domainId, ouCN, uid, mobile, cn)));
    }

    @GetMapping("/getPreservedUserList")
    public ResponseEntity<ResponseData> getPreservedUserList(Long domainId, String ouCN, String uid, String mobile, String cn) {

        return ResponseEntity.ok(ResponseData.success(ldapOuService.getPreservedUserList(domainId, ouCN, uid, mobile, cn)));
    }

    @GetMapping("/addUsers")
    public ResponseEntity<ResponseData> addUsers(Long domainId, OUUserRefDTO refDTO) {

        ldapOuService.addUsers(domainId, refDTO);
        return ResponseEntity.ok(ResponseData.success());
    }

    /**
     * 删除用户
     *
     * @param domainId
     * @param preserve 删除模式：0保留到域用户，1彻底删除
     * @param refDTO
     * @return
     */
    @GetMapping("/removeUsers")
    public ResponseEntity<ResponseData> removeUsers(Long domainId, Integer preserve, OUUserRefDTO refDTO) {

        ldapOuService.removeUsers(domainId, preserve == 0, refDTO);
        return ResponseEntity.ok(ResponseData.success());
    }
}
