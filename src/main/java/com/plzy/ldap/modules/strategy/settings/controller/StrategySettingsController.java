package com.plzy.ldap.modules.strategy.settings.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.modules.strategy.settings.domain.TLdapOuStrategySettingsRef;
import com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsCommand;
import com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsList;
import com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsValues;
import com.plzy.ldap.modules.strategy.settings.dto.*;
import com.plzy.ldap.modules.strategy.settings.service.TLdapOuStrategySettingsRefService;
import com.plzy.ldap.modules.strategy.settings.service.TLdapStrategySettingsCommandService;
import com.plzy.ldap.modules.strategy.settings.service.TLdapStrategySettingsListService;
import com.plzy.ldap.modules.strategy.settings.service.TLdapStrategySettingsValuesService;
import com.plzy.ldap.modules.token.service.TSysTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/strategy/settings")
@Slf4j
public class StrategySettingsController {

    @Autowired
    private TLdapStrategySettingsListService strategySettingsListService;

    @Autowired
    private TLdapStrategySettingsValuesService strategySettingsValuesService;

    @Autowired
    private TLdapOuStrategySettingsRefService strategySettingsRefService;

    @Autowired
    private TLdapStrategySettingsCommandService strategySettingsCommandService;

    @Autowired
    private TSysTokenService tokenService;


    @Value("${ldap.strategy.strategy_wallpaper_upload_path}")
    private String strategyWallpaperUploadPath;

    @Value("${ldap.serviceUrl}")
    private String serviceUrl;

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/updatePubStrategay")
    public ResponseEntity<ResponseData> updatePubStrategay(@RequestBody StrategySettingsCommandDto cmd) {
        strategySettingsCommandService.updateById(cmd);

        strategySettingsValuesService.update(new LambdaUpdateWrapper<TLdapStrategySettingsValues>()
                .eq(TLdapStrategySettingsValues::getStrategyId, cmd.getStrategyId())
                .eq(TLdapStrategySettingsValues::getCommandId, cmd.getId())
                .eq(TLdapStrategySettingsValues::getCommandType, "PUBLIC_STRATEGY")
                .set(TLdapStrategySettingsValues::getValue, cmd.getValue()));

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/removePubStrategay")
    public ResponseEntity<ResponseData> removePubStrategay(Long valueId) {
        TLdapStrategySettingsValues valueObj = strategySettingsValuesService.getById(valueId);

        // 先删除value记录，再删除command记录
        strategySettingsValuesService.removeById(valueObj.getId());
        strategySettingsCommandService.removeById(valueObj.getCommandId());

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/listPubStrategay")
    public ResponseEntity<ResponseData> listPubStrategay(Long strategyId) {
        List<StrategySettingsCommandDto> result = strategySettingsCommandService.listPubStrategay(strategyId);
        return ResponseEntity.ok(ResponseData.success(result));
    }

    @PostMapping("/addPubStrategay")
    public ResponseEntity<ResponseData> addPubStrategay(@RequestBody StrategySettingsCommandDto cmd) {

        if (cmd != null) {
            cmd.setScope(1);
            cmd.setUserId(tokenService.getCurrUser().getId());
            cmd.setUpdateTime(new Date());
            strategySettingsCommandService.save(cmd);

            TLdapStrategySettingsValues values = new TLdapStrategySettingsValues();
            values.setStrategyId(cmd.getStrategyId());
            values.setCommandId(cmd.getId());
            values.setCommandType("PUBLIC_STRATEGY");
            values.setValue(cmd.getValue());
            values.setUpdateTime(new Date());
            strategySettingsValuesService.save(values);
        }

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/list")
    public ResponseEntity<ResponseData> list() {

        List<LdapStrategySettingsListDTO> list = strategySettingsListService.tree();
        return ResponseEntity.ok(ResponseData.success(list));
    }

    @GetMapping("/listByDomain")
    public ResponseEntity<ResponseData> listByDomain(Long domainId) {

        List<LdapStrategySettingsListDTO> list = strategySettingsListService.treeByDomain(domainId);
        return ResponseEntity.ok(ResponseData.success(list));
    }

    @GetMapping("/save")
    public ResponseEntity<ResponseData> save(TLdapStrategySettingsList settingsList) {

        settingsList.setUpdateTime(new Date());
        settingsList.setUpdateUser(tokenService.getCurrUser().getId());
        strategySettingsListService.save(settingsList);
        return ResponseEntity.ok(ResponseData.success());
    }

    @PostMapping("/saveValues")
    public ResponseEntity<ResponseData> saveValues(@RequestBody String body) {

        log.info("接收到策略参数保存请求:" + body);

        if (StringUtils.hasText(body)) {
            try {
                List<Map<String, Object>> list = objectMapper.readValue(body, List.class);

                for (Map<String, Object> value : list) {

                    // 根据命令名称查找命令对象
                    TLdapStrategySettingsCommand command = strategySettingsCommandService.getOne(new LambdaQueryWrapper<TLdapStrategySettingsCommand>().eq(TLdapStrategySettingsCommand::getScope, 0).eq(TLdapStrategySettingsCommand::getName, value.get("name") + ""));

                    // 删除原有的记录
                    // 注意，这里需要根据type来删除，因为本地壁纸和网络壁纸只能保留一个
                    strategySettingsValuesService.remove(new QueryWrapper<TLdapStrategySettingsValues>()
                            .eq("strategy_id", value.get("strategyId"))
                            .eq("command_type", command.getType())
                    );

                    // 特殊处理，如果是网络图片的壁纸，则自动拼接后端地址，形如：http://127.0.0.1:8080/service/
                    if ("wallpaper.web".equals(value.get("name") + "")) {
                        Map<String, Object> it = objectMapper.readValue(value.get("value") + "", Map.class);
                        it.put("url", serviceUrl + it.get("url"));
                        value.put("value", objectMapper.writeValueAsString(it));
                    }

                    TLdapStrategySettingsValues item = new TLdapStrategySettingsValues();
                    item.setStrategyId(Long.valueOf(value.get("strategyId").toString()));
                    item.setCommandId(command.getId());
                    item.setCommandType(command.getType());
                    item.setValue(value.get("value") + "");
                    item.setUpdateTime(new Date());

                    strategySettingsValuesService.save(item);
                }

                // 删除所有策略缓存
                // todo:这里更好的方式是只删除所编辑策略对用的所有ou的缓存
                strategySettingsListService.clearCache();

                return ResponseEntity.ok(ResponseData.success());
            } catch (Exception e) {
                log.error("error", e);
            }
        }

        return ResponseEntity.ok(ResponseData.error("999999", "保存策略参数值时出现问题"));
    }

    @GetMapping("/update")
    public ResponseEntity<ResponseData> update(TLdapStrategySettingsList settingsList) {

        strategySettingsListService.updateById(settingsList);
        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/delete")
    public ResponseEntity<ResponseData> delete(Long id) {

        strategySettingsListService.removeById(id);

        // 清除缓存
        strategySettingsListService.clearCache();

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/get")
    public ResponseEntity<ResponseData> get(Long id) {

        return ResponseEntity.ok(ResponseData.success(strategySettingsListService.getById(id)));
    }

    @GetMapping("/enable")
    public ResponseEntity<ResponseData> enable(Long refId, Integer enable) {

        strategySettingsRefService.update(new LambdaUpdateWrapper<TLdapOuStrategySettingsRef>()
                .eq(TLdapOuStrategySettingsRef::getId, refId).set(TLdapOuStrategySettingsRef::getEnable, enable));

        // 清除缓存
        strategySettingsListService.clearCache();

        return ResponseEntity.ok(ResponseData.success());
    }


    @GetMapping("/getValues")
    public ResponseEntity<ResponseData> getValues(Long strategyId) {

        List<TLdapStrategySettingsValuesDTO> list = strategySettingsValuesService.getValues(strategyId);
        return ResponseEntity.ok(ResponseData.success(list));
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseData> upload(@RequestParam("file") MultipartFile file) {

        String fileName = System.nanoTime() + "";

        File dir = new File(strategyWallpaperUploadPath);
        if (!dir.exists()) {
            log.info("不存在文件存储目录，即将自动创建");
            dir.mkdirs();
        }

        // 获取文件后缀名
        String[] items = file.getOriginalFilename().split("\\.");
        String extType = items[items.length - 1];

        try {
            log.info("即将储存文件");
            file.transferTo(new File(strategyWallpaperUploadPath + File.separator + fileName + "." + extType));
            log.info("文件储存成功");
        } catch (IOException e) {
            log.error("上传文件时遇到错误：", e);
        }
        return ResponseEntity.ok(ResponseData.success(fileName + "." + extType));
    }

    @GetMapping(value = "/getImage", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getImage(String fileName) {

        try {
            File file = new File(strategyWallpaperUploadPath + File.separator + fileName);
            if (!file.exists()) {
                return null;
            }

            FileInputStream inputStream = new FileInputStream(file);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, inputStream.available());
            return bytes;
        } catch (Exception e) {
            log.error("get image error:", e);
        }

        return null;
    }

    @GetMapping("/getAllImages")
    public ResponseEntity<ResponseData> getAllImages() {

        File dir = new File(strategyWallpaperUploadPath);
        if (dir.exists()) {
            String[] list = dir.list((dir1, name) -> {
                name = name.toLowerCase();
                if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")) {
                    return true;
                }
                return false;
            });
            return ResponseEntity.ok(ResponseData.success(list));
        }

        return ResponseEntity.ok(ResponseData.error("999999", "没有找到图片上传目录！"));
    }

    @GetMapping("/removeImage")
    public ResponseEntity<ResponseData> removeImage(String fileName) {

        File image = new File(strategyWallpaperUploadPath + File.separator + fileName);
        if (image.exists()) {
            image.delete();
            return ResponseEntity.ok(ResponseData.success());
        }

        return ResponseEntity.ok(ResponseData.error("999999", "没有找到图片！"));
    }

    @GetMapping("/assign")
    public ResponseEntity<ResponseData> assign(TLdapOuStrategySettingsRef ref) {

        // 判断是否重复关联了此策略
        List<TLdapOuStrategySettingsRef> list = strategySettingsRefService.list(new QueryWrapper<TLdapOuStrategySettingsRef>().eq("strategy_id", ref.getStrategyId()).eq("ou_id", ref.getOuId()));
        if (list.size() > 0) {
            return ResponseEntity.ok(ResponseData.error("999999", "已关联此策略，不能重复添加！"));
        }

        ref.setUpdateTime(new Date());
        ref.setEnable(0);
        strategySettingsRefService.save(ref);

        // 清除缓存
        strategySettingsListService.clearCache();

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/assignmentList")
    public ResponseEntity<ResponseData> assignmentList(Long ouId) {

        List<LdapStrategySettingsDetails> result = strategySettingsListService.assignmentList(ouId);
        return ResponseEntity.ok(ResponseData.success(result));
    }

    @GetMapping("/removeAssignment")
    public ResponseEntity<ResponseData> removeAssignment(Long id) {

        // 查找当前id所对应的关联对象
        TLdapOuStrategySettingsRef ref = strategySettingsRefService.getById(id);

        strategySettingsRefService.removeById(id);

        // 清除缓存
        strategySettingsListService.clearCache();

        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/getAppliedOU")
    public ResponseEntity<ResponseData> getAppliedOU(Page<StrategySettingOuDto> page, Long id) {

        IPage<StrategySettingOuDto> ouList = strategySettingsListService.getAppliedOU(page, id);
        return ResponseEntity.ok(ResponseData.success(ouList));
    }

    @PostMapping("updateStrategySettingRef")
    public ResponseEntity<ResponseData> updateStrategySettingRef(@RequestBody TLdapOuStrategySettingsRef data) {
        return ResponseEntity.ok(ResponseData.success(strategySettingsRefService.updateById(data)));
    }

    @GetMapping("getStrategySettingRef")
    public ResponseEntity<ResponseData> getStrategySettingRef(TLdapOuStrategySettingsRef data) {
        return ResponseEntity.ok(ResponseData.success(strategySettingsRefService.getOne(new LambdaQueryWrapper<TLdapOuStrategySettingsRef>().eq(TLdapOuStrategySettingsRef::getOuId, data.getOuId())
                .eq(TLdapOuStrategySettingsRef::getStrategyId, data.getStrategyId()))));
    }
}
