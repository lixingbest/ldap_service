package com.plzy.ldap.modules.domain.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.plzy.ldap.framework.ldap.service.LDAPDomainCacheService;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.framework.utils.ExcelUtil;
import com.plzy.ldap.framework.utils.TextUtil;
import com.plzy.ldap.framework.utils.TreeDataUtil;
import com.plzy.ldap.modules.admin.domain.TSysAdmin;
import com.plzy.ldap.modules.admin.service.TSysAdminService;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.dto.DomainTree;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.ou.domain.TLdapOu;
import com.plzy.ldap.modules.ou.service.TLdapOuService;
import com.plzy.ldap.modules.strategy.settings.dto.LdapStrategySettingsDetails;
import com.plzy.ldap.modules.strategy.settings.service.TLdapOuStrategySettingsRefService;
import com.plzy.ldap.modules.token.domain.TSysToken;
import com.plzy.ldap.modules.token.service.TSysTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.*;

@Controller
@RequestMapping("/domain")
@Slf4j
public class DomainController {

    @Autowired
    private TLdapDomainService tLdapDomainService;

    @Autowired
    private LDAPDomainCacheService ldapDomainCacheService;

    @Autowired
    private TLdapOuStrategySettingsRefService ouStrategySettingsRefService;

    @Resource
    private TSysTokenService tokenService;
    @Resource
    private TSysAdminService adminService;
    @Resource
    private TLdapOuService ouService;

    @GetMapping("/get")
    public ResponseEntity<ResponseData> get(Long id) {

        return ResponseEntity.ok(ResponseData.success(tLdapDomainService.getById(id)));
    }

    @GetMapping("/tree")
    public ResponseEntity<ResponseData> listDCAndOU(@RequestHeader("token") String token, Long domainId) {

        Set<String> set = new HashSet<>();
        try {
            TSysToken tokenInst = tokenService.getOne(new LambdaQueryWrapper<TSysToken>().eq(TSysToken::getToken, token).eq(TSysToken::getStatus, 0));
            TSysAdmin admin = adminService.getById(tokenInst.getUserId());

            List<TLdapDomain> list = tLdapDomainService.list();
            List<TLdapOu> list1 = ouService.list();

            ArrayList<TreeDataUtil> nodes = new ArrayList<>();
            for (TLdapDomain domain : list) {
                nodes.add(new TreeDataUtil(String.valueOf(domain.getId()), String.valueOf(domain.getPid()), domain.getName(), domain.getDn(), domain));
            }
            for (TLdapOu ou : list1) {
                nodes.add(new TreeDataUtil("o" + ou.getId(), ou.getPidType() == 0 ? String.valueOf(ou.getPid()) : ("o" + ou.getPid()), ou.getName(), ou.getDn(), ou));
            }

            Set<String> childrenIdSet = TreeDataUtil.getChildrenIdSet(nodes, String.valueOf(admin.getMgtDomainId()));

            for (String key : childrenIdSet) {
                if (key.contains("o")) {
                    set.add(key.substring(1) + "-1");
                } else {
                    set.add(key + "-0");
                }
            }

        } catch (Exception e) {
            return ResponseEntity.ok(ResponseData.error("123", "用户token有误"));
        }

        List<DomainTree> list = tLdapDomainService.tree(domainId);
        list.removeIf(domainTree -> !set.contains(String.valueOf(domainTree.getId())));

        // 插入ou所对应的组策略节点
        List<DomainTree> strategyList = new ArrayList<>();
        for (DomainTree item : list) {
            // id格式：id-0/1，0为域，1为ou
            String id = item.getId();
            String[] field = id.split("-");
            if ("1".equals(field[1])) {
                List<LdapStrategySettingsDetails> details = ouStrategySettingsRefService.getByOuId(Long.valueOf(field[0]));
                for (LdapStrategySettingsDetails d : details) {
                    DomainTree sitem = new DomainTree();
                    // 之所以需要加时间，是因为一个策略会被应用到多个OU，所以会导致树的id重复
                    sitem.setId(d.getId() + "-2-" + new Date().getTime());
                    sitem.setPid(item.getId());
                    sitem.setName(d.getName());
                    sitem.setType("2");
                    sitem.setTooltip(d.getComments());
                    sitem.setEnable(d.getEnable());
                    sitem.setRefid(d.getRefid());
                    strategyList.add(sitem);
                }
            }
        }

        list.addAll(strategyList);

        Collections.sort(list, new Comparator<DomainTree>() {

            @Override
            public int compare(DomainTree o1, DomainTree o2) {

                if (o1.getOrdIdx() != null && o2.getOrdIdx() != null) {
                    return o1.getOrdIdx() - o2.getOrdIdx();
                } else {
                    return 0;
                }
            }
        });

        return ResponseEntity.ok(ResponseData.success(list));
    }

    @GetMapping("/treeWithoutOu")
    public ResponseEntity<ResponseData> treeWithoutOu() {

        List<DomainTree> list = tLdapDomainService.treeWithoutOu();
        return ResponseEntity.ok(ResponseData.success(list));
    }

    @GetMapping("/list")
    public ResponseEntity<ResponseData> list() {

        List<TLdapDomain> list = tLdapDomainService.list();
        return ResponseEntity.ok(ResponseData.success(list));
    }

    @GetMapping("/saveOrUpdate")
    public ResponseEntity<ResponseData> saveOrUpdate(TLdapDomain dc) {

        tLdapDomainService.saveOrUpdate(dc);
        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/remove")
    public ResponseEntity<ResponseData> remove(String ids) {

        tLdapDomainService.removeByIds(TextUtil.ids2LongList(ids));
        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/changeUpStatus")
    public ResponseEntity<ResponseData> changeUpStatus(Long domainId, Byte upStatus) {

        boolean result = tLdapDomainService.changeUpStatus(domainId, upStatus);

        // 成功上线或下线后刷新domain缓存状态
        if (result) {
            ldapDomainCacheService.reload();
        }

        return ResponseEntity.ok(ResponseData.success(result));
    }

    @GetMapping("changeOuIndex")
    public ResponseEntity<ResponseData> changeOuIndex(Long changeItem, Long itemTop) {
        if (!changeItem.equals(itemTop)) {
            TLdapOu itemTopInst = ouService.getById(itemTop);
            TLdapOu changeItemInst = ouService.getById(changeItem);
            if (itemTopInst.getPid().equals(changeItemInst.getPid())) {
                if (changeItemInst.getOrdIdx() > itemTopInst.getOrdIdx()) {
                    ouService.update(new LambdaUpdateWrapper<TLdapOu>()
                            .eq(TLdapOu::getPid, itemTopInst.getPid())
                            .ge(TLdapOu::getOrdIdx, itemTopInst.getOrdIdx())
                            .lt(TLdapOu::getOrdIdx, changeItemInst.getOrdIdx())
                            .setSql("ord_idx=ord_idx+1")
                    );
                } else {
                    ouService.update(new LambdaUpdateWrapper<TLdapOu>()
                            .eq(TLdapOu::getPid, itemTopInst.getPid())
                            .gt(TLdapOu::getOrdIdx, changeItemInst.getOrdIdx())
                            .le(TLdapOu::getOrdIdx, itemTopInst.getOrdIdx())
                            .setSql("ord_idx=ord_idx-1"));
                }
                ouService.update(new LambdaUpdateWrapper<TLdapOu>().set(TLdapOu::getOrdIdx, itemTopInst.getOrdIdx()).eq(TLdapOu::getId, changeItem));
            }
        }
        return ResponseEntity.ok(ResponseData.success());
    }

    @PostMapping("bulkImport")
    public ResponseEntity<ResponseData> bulkImport(@RequestParam("file") MultipartFile multipartFile) {

        File file = ExcelUtil.multipartFileToFile(multipartFile);

        List<LinkedHashMap<String, Object>> sheet1 = ExcelUtil.read(file, "sheet1", 14);

        if (file != null) {
            file.delete();
        }


        List<String> log = new ArrayList<>();

        Map<Long, Long> changeIdMap = new HashMap<>();

        for (int i = 0; i < sheet1.size(); i++) {

            LinkedHashMap<String, Object> map = sheet1.get(i);

            try {
                Long id = map.get("ID") != null && !Objects.equals(String.valueOf(map.get("ID")), "") ? Long.valueOf(String.valueOf(map.get("ID")).replace(".0", "")) : null;

                Long pid = map.get("父ID") != null && !Objects.equals(String.valueOf(map.get("父ID")), "") ? Long.valueOf(String.valueOf(map.get("父ID")).replace(".0", "")) : null;

                Byte pidType = map.get("父级类型 0域 1组织单位") != null && !Objects.equals(String.valueOf(map.get("父级类型 0域 1组织单位")), "") ? Byte.valueOf(String.valueOf(map.get("父级类型 0域 1组织单位")).replace(".0", "")) : null;

                String name = map.get("名称") != null ? String.valueOf(map.get("名称")) : null;

                String province = map.get("省") != null ? String.valueOf(map.get("省")) : null;

                String city = map.get("市") != null ? String.valueOf(map.get("市")) : null;

                String district = map.get("区") != null ? String.valueOf(map.get("区")) : null;

                String address = map.get("详细地址") != null ? String.valueOf(map.get("详细地址")) : null;

                String postalCode = map.get("邮政编码") != null ? String.valueOf(map.get("邮政编码")) : null;

                Integer delProtect = map.get("删除保护") != null ? Integer.valueOf(String.valueOf(map.get("删除保护")).replace(".0", "")) : null;

                String comments = map.get("备注") != null ? String.valueOf(map.get("备注")) : null;

                Long domainId = map.get("所属domain编号") != null && !Objects.equals(String.valueOf(map.get("所属domain编号")), "") ? Long.valueOf(String.valueOf(map.get("所属domain编号")).replace(".0", "")) : null;

                String dn = map.get("dn") != null ? String.valueOf(map.get("dn")) : null;

                Integer ordIdx = map.get("序号") != null && !Objects.equals(String.valueOf(map.get("序号")), "") ? Integer.valueOf(String.valueOf(map.get("序号")).replace(".0", "")) : null;

                if (id == null || pid == null) {
                    log.add("【第" + i + "行导入失败】 \"ID\"或\"父ID\"不能为空");
                    continue;
                }

                TLdapOu parent = ouService.getById(changeIdMap.getOrDefault(pid, pid));

                if (parent == null) {
                    log.add("【第" + i + "行导入失败】 \"父级\"不存在");
                    continue;
                }

                if (dn == null) {
                    log.add("【第" + i + "行导入失败】 \"DN\"不能为空");
                    continue;
                }

                String parentDN = dn.substring(dn.indexOf(',')+1);
                if (!parentDN.equals(parent.getDn())) {
                    log.add("【第" + i + "行导入失败】 \"DN\"不合法，" + dn + "没有包含父级DN:" + parent.getDn());
                    continue;
                }


                TLdapOu tLdapOu = new TLdapOu(
                        id,
                        parent.getId(),
                        pidType,
                        name,
                        province,
                        city,
                        district,
                        address,
                        postalCode,
                        comments,
                        delProtect,
                        domainId,
                        dn,
                        ordIdx);

                ouService.saveOrUpdate(tLdapOu);

                if (!tLdapOu.getId().equals(id)) {
                    changeIdMap.put(id, tLdapOu.getId());
                }

            } catch (Exception e) {
                e.printStackTrace();
                log.add("【代码异常】" + e.getMessage());
            }
        }

        return ResponseEntity.ok(ResponseData.success(log));
    }


    @GetMapping("bulkExport")
    public ResponseEntity<ResponseData> bulkExport(HttpServletResponse response, Long pid) {

        List<TLdapOu> list = ouService.bulkExport(pid);

        ArrayList<Map<String, Object>> data = new ArrayList<>();

        for (TLdapOu item : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("name", item.getName());
            map.put("pid", item.getPid());
            map.put("pidType", item.getPidType());
            map.put("province", item.getProvince());
            map.put("city", item.getCity());
            map.put("district", item.getDistrict());
            map.put("address", item.getAddress());
            map.put("postalCode", item.getPostalCode());
            map.put("delProtect", item.getDelProtect());
            map.put("comments", item.getComments());
            map.put("domainId", item.getDomainId());
            map.put("dn", item.getDn());
            map.put("ordIdx", item.getOrdIdx());
            data.add(map);
        }

        String[] columnNames = new String[]{"ID:id", "名称:name", "父ID:pid", "父级类型 0域 1组织单位:pidType", "省:province",
                "市:city", "区:district", "详细地址:address", "邮政编码:postalCode", "删除保护:delProtect", "备注:comments",
                "所属domain编号:domainId", "dn:dn", "序号:ordIdx"};

        ExcelUtil.export("sheet1", columnNames, data, "组织", response);

        return ResponseEntity.ok(ResponseData.success());
    }

}
