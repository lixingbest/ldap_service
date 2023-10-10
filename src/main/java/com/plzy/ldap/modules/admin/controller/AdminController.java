package com.plzy.ldap.modules.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.plzy.ldap.framework.ldap.service.LDAPDomainCacheService;
import com.plzy.ldap.framework.utils.TextUtil;
import com.plzy.ldap.framework.ldap.service.LDAPAuthService;
import com.plzy.ldap.framework.ldap.service.LDAPRemoteService;
import com.plzy.ldap.framework.utils.TreeDataUtil;
import com.plzy.ldap.modules.admin.domain.TSysAdmin;
import com.plzy.ldap.modules.admin.dto.ErrorLoginCount;
import com.plzy.ldap.modules.admin.dto.SysAdminWithNameDTO;
import com.plzy.ldap.modules.admin.dto.UserWithToken;
import com.plzy.ldap.modules.admin.service.TSysAdminService;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.modules.domain.domain.TLdapDomain;
import com.plzy.ldap.modules.domain.service.TLdapDomainService;
import com.plzy.ldap.modules.ou.domain.TLdapOu;
import com.plzy.ldap.modules.ou.service.TLdapOuService;
import com.plzy.ldap.modules.resource.domain.TResource;
import com.plzy.ldap.modules.resource.service.TResourceService;
import com.plzy.ldap.modules.role.domain.TRoleResRef;
import com.plzy.ldap.modules.role.domain.TRoleUserRef;
import com.plzy.ldap.modules.role.service.TRoleResRefService;
import com.plzy.ldap.modules.role.service.TRoleUserRefService;
import com.plzy.ldap.modules.sys_log.domain.TSysLog;
import com.plzy.ldap.modules.sys_log.service.TSysLogService;
import com.plzy.ldap.modules.sysconfig.domain.TSysConf;
import com.plzy.ldap.modules.sysconfig.service.TSysConfService;
import com.plzy.ldap.modules.token.domain.TSysToken;
import com.plzy.ldap.modules.token.service.TSysTokenService;
import io.netty.util.internal.ConcurrentSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private TSysLogService sysLogService;

    @Autowired
    private TSysAdminService sysAdminService;

    @Autowired
    private LDAPAuthService ldapAuthService;

    @Autowired
    private TLdapDomainService domainService;
    @Autowired
    private TLdapOuService ouService;

    @Autowired
    private LDAPDomainCacheService domainCacheService;

    @Autowired
    private TResourceService resourceService;

    @Autowired
    private TRoleUserRefService roleUserRefService;

    @Autowired
    private TRoleResRefService roleResRefService;

    @Autowired
    private TSysTokenService sysTokenService;

    @Autowired
    private Producer producer;

    // 记录全局验证码内容
    private Map<String, String> kaptchaSessionKey = new ConcurrentHashMap<>();

    // 统计用户登录错误次数
    private Map<String, ErrorLoginCount> errorCount = new ConcurrentHashMap<>();

    @Autowired
    private TSysConfService confService;

    @PostMapping("login")
    public ResponseEntity<ResponseData> login(@RequestBody TSysAdmin sysAdmin) {

        List<TSysConf> list = confService.list();
        Integer password_errors_number = 0;
        int lock_time = 0;
        boolean verification_code_enable = true;
        for (TSysConf item : list) {
            if (item.getName().equals("password_errors_number")) {
                password_errors_number = Integer.valueOf(item.getValue());
            }
            if (item.getName().equals("lock_time")) {
                lock_time = Integer.parseInt(item.getValue());
            }
            if (item.getName().equals("verification_code_enable")) {
                verification_code_enable = Boolean.parseBoolean(item.getValue());
            }
        }

        // 根据登录错误历史校验频率
        if (errorCount.containsKey(sysAdmin.getJobno())) {
            ErrorLoginCount count = errorCount.get(sysAdmin.getJobno());
            if (count.getCount() >= password_errors_number && System.currentTimeMillis() - count.getLastLoginTime() < lock_time * 60 * 1000L) {
                return ResponseEntity.ok(ResponseData.error("999999", "您登录错误次数超过" + password_errors_number + "次，请于" + lock_time + "分钟后再试!"));
            }
        }

        // 由于4位验证码重复的概率极低，因为只要缓存中存在即认为验证通过
        // 全局以大写校验
        if (verification_code_enable) {
            if (!kaptchaSessionKey.containsKey(sysAdmin.getImgcode().toUpperCase())) {
                // 统计登录错误次数
                if (!errorCount.containsKey(sysAdmin.getJobno())) {
                    errorCount.put(sysAdmin.getJobno(), new ErrorLoginCount(1, System.currentTimeMillis()));
                } else {
                    errorCount.put(sysAdmin.getJobno(), new ErrorLoginCount(errorCount.get(sysAdmin.getJobno()).getCount() + 1, System.currentTimeMillis()));
                }
                return ResponseEntity.ok(ResponseData.error("999999", "验证码输入错误！"));
            }
            kaptchaSessionKey.remove(sysAdmin.getImgcode());
        }

        TSysAdmin result = sysAdminService.getOne(new LambdaQueryWrapper<TSysAdmin>()
                .eq(TSysAdmin::getJobno, sysAdmin.getJobno())
                .eq(TSysAdmin::getPassword, sysAdmin.getPassword())
                .eq(TSysAdmin::getMgtDomainId, sysAdmin.getMgtDomainId())
        );
        if (result == null) {
            // 统计登录错误次数
            if (!errorCount.containsKey(sysAdmin.getJobno())) {
                errorCount.put(sysAdmin.getJobno(), new ErrorLoginCount(1, System.currentTimeMillis()));
            } else {
                errorCount.put(sysAdmin.getJobno(), new ErrorLoginCount(errorCount.get(sysAdmin.getJobno()).getCount() + 1, System.currentTimeMillis()));
            }
            return ResponseEntity.ok(ResponseData.error("999999", "用户名或密码错误，或所选域无管理权限！"));
        } else {
            // 清空错误统计
            errorCount.remove(sysAdmin.getJobno());

            // 获取ipa凭证
            // 只有在选择非海关总署根域时才请求，因为海关总署实际上没有对应真正的域
//            if(sysAdmin.getMgtDomainId() != 1L){
//                String cookie = ldapAuthService.authWithAdmin(sysAdmin.getMgtDomainId());
//                domainCacheService.updateCookie(sysAdmin.getMgtDomainId(), cookie);
//            }

            // 不给前端返回密码
            result.setPassword("***");

            // 为此用户创建token
            TSysToken token = sysTokenService.getToken(result);

            TSysLog log = new TSysLog();
            log.setDomainId(sysAdmin.getMgtDomainId());
            log.setUserId(result.getId());
            log.setTime(new Date());
            log.setMenu("系统登录");
            log.setType("登录");
            log.setMessage("用户登录成功，用户名：" + sysAdmin.getName());
            sysLogService.save(log);

            // 查找此用户的菜单
            List<TRoleUserRef> refs = roleUserRefService.list(new LambdaQueryWrapper<TRoleUserRef>().eq(TRoleUserRef::getUserId, result.getId()));

            if (refs != null && refs.size() > 0) {

                List<Long> roles = refs.stream().map(TRoleUserRef::getRoleId).collect(Collectors.toList());

                List<TRoleResRef> roleResRefs = roleResRefService.list(new LambdaQueryWrapper<TRoleResRef>().in(TRoleResRef::getRoleId, roles));

                Set<Long> resourceIds = roleResRefs.stream().map(TRoleResRef::getResId).collect(Collectors.toSet());

                List<TResource> resources = new ArrayList<>();
                if (resourceIds.size() > 0) {
                    resources = resourceService.listByIds(resourceIds);
                }
                //List<TResource> resources = resourceService.getListByRoleId(ref.getRoleId());
                return ResponseEntity.ok(ResponseData.success(new UserWithToken(result, token, resources)));
            } else {
                return ResponseEntity.ok(ResponseData.success(new UserWithToken(result, token, new ArrayList<>())));
            }
        }
    }

    @GetMapping("kaptcha-image")
    public void getKaptchaImage(HttpServletResponse response) throws Exception {

        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");

        // 将验证码存于session中
        // 全局以大写校验
        String capText = producer.createText().toUpperCase();
        kaptchaSessionKey.put(capText, System.currentTimeMillis() + "");

        BufferedImage bi = producer.createImage(capText);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(bi, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
    }

    @GetMapping("list")
    public ResponseEntity<ResponseData> list(Page<TSysAdmin> page, TSysAdmin condition, String organId) {

        List<TLdapDomain> domainList = domainService.list();
        List<TLdapOu> ouList = ouService.list();

        ArrayList<TreeDataUtil> nodes = new ArrayList<>();
        for (TLdapDomain domain : domainList) {
            nodes.add(new TreeDataUtil(String.valueOf(domain.getId()), String.valueOf(domain.getPid()), domain.getName(), null, domain));
        }
        for (TLdapOu ou : ouList) {
            nodes.add(new TreeDataUtil("o" + ou.getId(), ou.getPidType() == 0 ? String.valueOf(ou.getPid()) : ("o" + ou.getPid()), ou.getName(), null, ou));
        }

        String id = null;
        if (organId.split("-")[1].equals("0")) {
            id = organId.split("-")[0];
        } else {
            id = "o" + organId.split("-")[0];
        }

        Set<String> childrenIdSet = TreeDataUtil.getChildrenIdSet(nodes, String.valueOf(id));

        Set<Long> set = new HashSet<>();

        childrenIdSet.forEach(item -> {
            if (item.contains("o")) {
                set.add(Long.valueOf(item.replace("o", "")));
            }
        });

        IPage<SysAdminWithNameDTO> list = sysAdminService.list(page, condition, set);

        return ResponseEntity.ok(ResponseData.success(list));
    }

    @PostMapping("saveOrUpdate")
    public ResponseEntity<ResponseData> saveOrUpdate(@RequestBody TSysAdmin admin) {

        // 新增时检查此手机号码是否存在
        if (admin.getId() == null && StringUtils.hasText(admin.getTelephone())) {
            List result = sysAdminService.list(new LambdaQueryWrapper<TSysAdmin>().eq(TSysAdmin::getTelephone, admin.getTelephone()));
            if (result.size() > 0) {
                return ResponseEntity.ok(ResponseData.error("999999", "此手机号码已存在！"));
            }
        }

        if (admin.getRoleId() == null) {
            return ResponseEntity.ok(ResponseData.error("999997", "角色id不能为空！"));
        }

        admin.setScope((byte) 1); // 用户添加的均为用户类
        sysAdminService.saveOrUpdate(admin);

        // 设置保存-用户的关系
        // 删除已有的关系
        roleUserRefService.remove(new LambdaQueryWrapper<TRoleUserRef>().eq(TRoleUserRef::getUserId, admin.getId()));

        TRoleUserRef ref = new TRoleUserRef();
        ref.setRoleId(admin.getRoleId());
        ref.setUserId(admin.getId());
        roleUserRefService.saveOrUpdate(ref);

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("remove")
    public ResponseEntity<ResponseData> remove(String ids) {

        // 首先删除用户所属的角色设置
        List<Long> idList = TextUtil.ids2LongList(ids);
        for (Long id : idList) {
            roleUserRefService.remove(new LambdaQueryWrapper<TRoleUserRef>().eq(TRoleUserRef::getUserId, id));
        }

        sysAdminService.removeByIds(TextUtil.ids2LongList(ids));
        return ResponseEntity.ok(ResponseData.success());
    }


    @GetMapping("addTemporaryRole")
    @Transactional
    public ResponseEntity<ResponseData> addTemporaryRole(Long formUser, String toUser, String time) {

        TRoleUserRef ref = roleUserRefService.getOne(
                new LambdaQueryWrapper<TRoleUserRef>()
                        .eq(TRoleUserRef::getUserId, formUser)
                        .eq(TRoleUserRef::getIsTemporary, 0));


        if (ref != null) {
            List<Long> ids = Arrays.stream(toUser.split(",")).map(Long::valueOf).collect(Collectors.toList());
            for (Long id : ids) {
                TRoleUserRef tRoleUserRef = new TRoleUserRef();
                tRoleUserRef.setRoleId(ref.getRoleId());
                tRoleUserRef.setIsTemporary(1);
                tRoleUserRef.setUserId(id);
                roleUserRefService.save(tRoleUserRef);
            }


            String strDateFormat = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat timeformat = new SimpleDateFormat(strDateFormat);
            Runnable runnable = () -> {
                try {
                    while (true) {
                        if (timeformat.parse(time).getTime() <= new Date().getTime()) {

                            System.out.println("删除临时角色");
                            roleUserRefService.remove(new LambdaQueryWrapper<TRoleUserRef>()
                                    .eq(TRoleUserRef::getRoleId, ref.getRoleId())
                                    .eq(TRoleUserRef::getIsTemporary, 1)
                                    .in(TRoleUserRef::getUserId, ids));
                            break;
                        }
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();

        }
        return ResponseEntity.ok(ResponseData.success());
    }
}
