package com.plzy.ldap.modules.deploy.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.ServiceApplication;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.framework.utils.FileUtil;
import com.plzy.ldap.framework.utils.ProcessUtil;
import com.plzy.ldap.framework.utils.TreeDataUtil;
import com.plzy.ldap.modules.deploy.dto.DeployMainDto;
import com.plzy.ldap.modules.deploy.domain.TLdapDeployDetails;
import com.plzy.ldap.modules.deploy.domain.TLdapDeployMain;
import com.plzy.ldap.modules.deploy.domain.TLdapDeployType;
import com.plzy.ldap.modules.deploy.service.TLdapDeployDetailsService;
import com.plzy.ldap.modules.deploy.service.TLdapDeployMainService;
import com.plzy.ldap.modules.deploy.service.TLdapDeployTypeService;
import com.plzy.ldap.modules.terminal.domain.TSysTerminal;
import com.plzy.ldap.modules.terminal.domain.TSysTerminalType;
import com.plzy.ldap.modules.terminal.service.TSysTerminalService;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("deploy")
@Log4j2
public class DeployController {
    @Resource
    private TLdapDeployTypeService typeService;
    @Resource
    private TLdapDeployMainService mainService;
    @Resource
    private TLdapDeployDetailsService detailsService;
    @Resource
    private TSysTerminalService terminalService;

    @Value("${ldap.tempPath}")
    private String tempPath;
    @Value("${ldap.ansiblePath}")
    private String ansiblePath;

    private static final Map<Long, Thread> THREADMAP = new HashMap<>();

    @GetMapping("typeTree")
    public ResponseEntity<ResponseData> getTypeTree() {

        List<TreeDataUtil> typeNodes = getTypeNodes();

        return ResponseEntity.ok(ResponseData.success(TreeDataUtil.getTree(typeNodes)));
    }

    @PostMapping("addOrUpdateType")
    public ResponseEntity<ResponseData> addOrUpdateType(@RequestBody TLdapDeployType deployType) {
        typeService.saveOrUpdate(deployType);
        return ResponseEntity.ok(ResponseData.success());
    }

    @DeleteMapping("deleteType/{id}")
    public ResponseEntity<ResponseData> deleteType(@PathVariable Long id) {
        int size = mainService.list(new LambdaQueryWrapper<TLdapDeployMain>().eq(TLdapDeployMain::getTypeId, id)).size();
        if (size == 0) {
            typeService.removeById(id);
            return ResponseEntity.ok(ResponseData.success());
        } else {
            return ResponseEntity.ok(ResponseData.error("123", "此类型下包含计划，不能删除"));
        }
    }

    @GetMapping("hasTypeCode/{code}/{id}")
    public ResponseEntity<ResponseData> hasTypeCode(@PathVariable(value = "code", required = false) String code, @PathVariable(value = "id", required = false) String id) {
        LambdaQueryWrapper<TLdapDeployType> lambdaQueryWrapper;
        if (null == id) {
            lambdaQueryWrapper = new LambdaQueryWrapper<TLdapDeployType>().eq(TLdapDeployType::getCode, code);
        } else {
            lambdaQueryWrapper = new LambdaQueryWrapper<TLdapDeployType>().eq(TLdapDeployType::getCode, code).ne(TLdapDeployType::getId, id);
        }
        List<TLdapDeployType> list = typeService
                .list(lambdaQueryWrapper);
        return ResponseEntity.ok(ResponseData.success(list.size() > 0));
    }

    @GetMapping("getMainPage")
    public ResponseEntity<ResponseData> getMainPage(Page<TLdapDeployMain> page,
                                                    @RequestParam("typeId") Long typeId,
                                                    @RequestParam(value = "name", required = false) String name) {

        List<TreeDataUtil> typeNodes = getTypeNodes();
        Set<String> childrenIdSet = TreeDataUtil.getChildrenIdSet(typeNodes, String.valueOf(typeId));
        HashSet<Long> set = new HashSet<>();
        for (String id : childrenIdSet) {
            set.add(Long.valueOf(id));
        }
        Page<DeployMainDto> page1 = mainService.getPage(page, set, name);
        return ResponseEntity.ok(ResponseData.success(page1));
    }

    @DeleteMapping("deleteMain/{id}")
    @Transactional
    public ResponseEntity<ResponseData> deleteMain(@PathVariable Long id) {
        detailsService.remove(new LambdaQueryWrapper<TLdapDeployDetails>().eq(TLdapDeployDetails::getMainId, id));

        if (THREADMAP.containsKey(id)) {
            Thread thread = THREADMAP.get(id);
            thread.interrupt();
        }

        mainService.removeById(id);
        return ResponseEntity.ok(ResponseData.success());
    }

    @DeleteMapping("deleteDetail/{id}")
    @Transactional
    public ResponseEntity<ResponseData> deleteDetail(@PathVariable Long id) {
        detailsService.removeById(id);
        return ResponseEntity.ok(ResponseData.success());
    }


    @PostMapping("addOrUpdateMain")
    //@Transactional
    public ResponseEntity<ResponseData> addOrUpdateMain(@RequestBody DeployMainDto mainDto) {

        mainDto.setTime(new Date());
        mainService.saveOrUpdate(mainDto);

        Thread thread = null;

        if (mainDto.getExecType().equals(0)) {

            thread = new Thread(() -> {
                exec(mainDto);
            });


        } else if (mainDto.getExecType().equals(1)) {
            String execExpr = mainDto.getExecExpr();
            String strDateFormat = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat timeformat = new SimpleDateFormat(strDateFormat);

            Date date;
            try {
                date = timeformat.parse(execExpr);
            } catch (ParseException e) {
                return ResponseEntity.ok(ResponseData.error("123", "时间不正确" + e.getMessage()));
            }

            long time = date.getTime() - new Date().getTime();

            log.info(time / 1000 + "s 后执行..");

            Runnable runnable = () -> {
                try {
                    log.info("开始休眠【${}】毫秒", time);
                    Thread.sleep(time);
                    log.info("开始执行定时任务");

                    exec(mainDto);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            };
            thread = new Thread(runnable);


        } else if (mainDto.getExecType().equals(2)) {
            String execExpr = mainDto.getExecExpr();
            String[] split = execExpr.split("/");
            int time = Integer.parseInt(split[0]);
            int num = Integer.parseInt(split[1]);

            Runnable runnable = () -> {
                for (int i = 0; i < num; i++) {
                    try {
                        Thread.sleep((long) time * 60 * 1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    exec(mainDto);
                }
            };
            thread = new Thread(runnable);

        }

        if (thread != null) {

            thread.start();

            THREADMAP.put(mainDto.getId(), thread);
        }


        return ResponseEntity.ok(ResponseData.success());
    }


    public void exec(DeployMainDto mainDto) {

        HashSet<Long> terminalIdSet = new HashSet<>();

        for (TLdapDeployDetails detail : mainDto.getDetails()) {
            terminalIdSet.add(detail.getTerminalId());
        }

        log.info("---------------开始创建脚本：" + System.currentTimeMillis());
        /*创建脚本**/
        File dir = new File(tempPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String shContent =
                "#!/usr/bin/bash\n" +
                        mainDto.getShell();
        String shPath = tempPath + File.separator + "s_" + System.currentTimeMillis() + ".sh";
        FileUtil.write(shContent, shPath);

        /*------------**/

        List<TLdapDeployDetails> list = new ArrayList<>();


        List<TSysTerminal> terminalList = terminalService.listByIds(terminalIdSet);


        for (TSysTerminal terminal : terminalList) {

            TLdapDeployDetails detail = new TLdapDeployDetails();

            detail.setMainId(mainDto.getId());
            detail.setTerminalId(terminal.getId());


            String[] commandArgs = new String[]{
                    ansiblePath, "all",
                    "--inventory=" + terminal.getIpv4() + ":" + terminal.getSshPort() + ", ", //必须要有这个逗号，否则会导致错误！
                    "--extra-vars", "ansible_user=" + terminal.getAccount().trim() + "  ansible_password=" + terminal.getPassword().trim() + "  ansible_sudo_pass=" + terminal.getPassword().trim(),
                    "-m", "script",
                    "-a", "\"" + shPath + "\"",
                    "-b", "--become" // 以root方式运行
            };


            try {
                detail.setBeginTime(new Date());
                log.info("---------------开始调接口IP:" + terminal.getIpv4());
                long l = System.currentTimeMillis();
                log.info("---------------开始调接口：" + l);
                String execResult = ProcessUtil.exec(commandArgs);
                long l1 = System.currentTimeMillis();
                log.info("---------------调接口结束：" + (l1 - l));
                detail.setExecLog(execResult);
                detail.setEndTime(new Date());
                if (execResult.contains("CHANGED")) {
                    detail.setStatus(0);
                } else {
                    detail.setStatus(1);
                }

            } catch (Exception e) {
                e.printStackTrace();
                log.error(e);
                detail.setEndTime(new Date());
                detail.setExecLog(e.getMessage());
                detail.setStatus(1);
            }

            list.add(detail);
        }


        detailsService.saveOrUpdateBatch(list);

    }

    @GetMapping("reloadcommand")
    public ResponseEntity<ResponseData> reloadcommand(Long detailId) {

        TLdapDeployDetails deployDetails = detailsService.getById(detailId);

        TLdapDeployMain main = mainService.getById(deployDetails.getMainId());

        DeployMainDto deployMainDto = new DeployMainDto();

        deployMainDto.setId(main.getId());

        deployMainDto.setShell(main.getShell());

        List<TLdapDeployDetails> list = new ArrayList<>();

        list.add(deployDetails);

        deployMainDto.setDetails(list);

        exec(deployMainDto);

//        TLdapDeployDetails detail = detailsService.getById(detailId);
//
//        TLdapDeployMain main = mainService.getById(detail.getMainId());
//
//        File dir = new File(tempPath);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//        String shContent =
//                "#!/usr/bin/bash\n" +
//                        main.getShell();
//        String shPath = tempPath + File.separator + "s_" + System.currentTimeMillis() + ".sh";
//        FileUtil.write(shContent, shPath);
//
//
//        TSysTerminal terminal = terminalService.getById(detail.getTerminalId());
//
//
//        String[] commandArgs = new String[]{
//                ansiblePath, "all",
//                "--inventory=" + terminal.getIpv4() + ":" + terminal.getSshPort() + ", ", //必须要有这个逗号，否则会导致错误！
//                "--extra-vars", "ansible_user=" + terminal.getAccount().trim() + "  ansible_password=" + terminal.getPassword().trim() + "  ansible_sudo_pass=" + terminal.getPassword().trim(),
//                "-m", "script",
//                "-a", "\"" + shPath + "\"",
//                "-b", "--become" // 以root方式运行
//        };
//
//
//        try {
//            detail.setBeginTime(new Date());
//            String execResult = ProcessUtil.exec(commandArgs);
//            detail.setExecLog(execResult);
//            detail.setEndTime(new Date());
//            if (execResult.contains("CHANGED")) {
//                detail.setStatus(0);
//            } else {
//                detail.setStatus(1);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error(e);
//            detail.setEndTime(new Date());
//            detail.setExecLog(e.getMessage());
//            detail.setStatus(1);
//        }
//
//        detailsService.updateById(detail);


        return ResponseEntity.ok(ResponseData.success());
    }

    public List<TreeDataUtil> getTypeNodes() {

        List<TLdapDeployType> types = typeService.list();

        ArrayList<TreeDataUtil> nodes = new ArrayList<>();

        for (TLdapDeployType item : types) {
            nodes.add(new TreeDataUtil(String.valueOf(item.getId()), String.valueOf(item.getPid()), item.getName(), null, item));
        }
        return nodes;
    }

    @GetMapping("clearDeployPlan/{typeId}")
    public ResponseEntity<ResponseData> clearDeployPlan(@PathVariable Long typeId) {
        List<TreeDataUtil> typeNodes = getTypeNodes();

        log.info("---------getIdset--------------" + System.currentTimeMillis());
        Set<String> childrenIdSet = TreeDataUtil.getChildrenIdSet(typeNodes, String.valueOf(typeId));
        Set<Long> set = new HashSet<>();
        for (String id : childrenIdSet) {
            set.add(Long.valueOf(id));
        }
        log.info("-----------getIdset完----------" + System.currentTimeMillis());
        List<TLdapDeployMain> list = mainService.list(new LambdaQueryWrapper<TLdapDeployMain>().in(TLdapDeployMain::getTypeId, set));

        Set<Long> mainSet = new HashSet<>();
        for (TLdapDeployMain main : list) {
            mainSet.add(main.getId());
        }

        detailsService.remove(new LambdaQueryWrapper<TLdapDeployDetails>().in(TLdapDeployDetails::getMainId, mainSet));

        for (TLdapDeployMain main : list) {
            if (THREADMAP.containsKey(main.getId())) {
                Thread thread = THREADMAP.get(main.getId());
                thread.interrupt();
            }
        }

        mainService.remove(new LambdaQueryWrapper<TLdapDeployMain>().in(TLdapDeployMain::getTypeId, set));

        log.info("-----------getIdset完22222----------" + System.currentTimeMillis());
        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("detailList")
    public ResponseEntity<ResponseData> detailList(Long mainId) {

        DeployMainDto data = mainService.getMain(mainId);
        return ResponseEntity.ok(ResponseData.success(data));
    }


}
