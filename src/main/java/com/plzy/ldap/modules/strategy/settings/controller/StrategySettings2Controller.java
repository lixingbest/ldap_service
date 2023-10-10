package com.plzy.ldap.modules.strategy.settings.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.framework.utils.TreeDataUtil;
import com.plzy.ldap.modules.deploy.domain.TLdapDeployType;
import com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsCommand;
import com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsType;
import com.plzy.ldap.modules.strategy.settings.domain.TLdapStrategySettingsValues;
import com.plzy.ldap.modules.strategy.settings.dto.StrategySettingsCommandDto;
import com.plzy.ldap.modules.strategy.settings.service.TLdapStrategySettingsCommandService;
import com.plzy.ldap.modules.strategy.settings.service.TLdapStrategySettingsTypeService;
import com.plzy.ldap.modules.strategy.settings.service.TLdapStrategySettingsValuesService;
import com.plzy.ldap.modules.token.domain.TSysToken;
import com.plzy.ldap.modules.token.service.TSysTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@RequestMapping("strategy/Settings/command")
@Controller
public class StrategySettings2Controller {

    @Resource
    private TLdapStrategySettingsCommandService commandService;

    @Resource
    private TLdapStrategySettingsTypeService typeService;

    @Resource
    private TLdapStrategySettingsValuesService valuesService;

    @Resource
    private TSysTokenService tokenService;

    @PostMapping("/addOrUpdateType")
    public ResponseEntity<ResponseData> addOrUpdateType(@RequestBody TLdapStrategySettingsType data) {
        if (null != data.getId()) {
            List<TLdapStrategySettingsType> list = typeService.list(new LambdaQueryWrapper<TLdapStrategySettingsType>().eq(TLdapStrategySettingsType::getCode, data.getCode()));
            if (list.size() > 0) {
                return ResponseEntity.ok(ResponseData.error("123456", "code已存在"));
            }
        }
        return ResponseEntity.ok(ResponseData.success(typeService.saveOrUpdate(data)));
    }

    @GetMapping("/getTree")
    public ResponseEntity<ResponseData> getTree() {

        List<TreeDataUtil> nodes = getTypeNodes();

        return ResponseEntity.ok(ResponseData.success(TreeDataUtil.getTree(nodes)));
    }

    @DeleteMapping("/deleteType/{id}")
    public ResponseEntity<ResponseData> deleteType(@PathVariable Long id) {

        List<TLdapStrategySettingsCommand> list = commandService.list(new LambdaQueryWrapper<TLdapStrategySettingsCommand>().eq(TLdapStrategySettingsCommand::getTypeId, id));
        if (list.size() == 0) {
            typeService.removeById(id);
            return ResponseEntity.ok(ResponseData.success());
        } else {
            return ResponseEntity.ok(ResponseData.error("123456", "存在子项不能删除"));
        }
    }

    @GetMapping("hasTypeCode/{code}/{id}")
    public ResponseEntity<ResponseData> hasTypeCode(@PathVariable(value = "code", required = false) String code,
                                                    @PathVariable(value = "id", required = false) String id) {
        LambdaQueryWrapper<TLdapStrategySettingsType> lambdaQueryWrapper;
        if (null == id) {
            lambdaQueryWrapper = new LambdaQueryWrapper<TLdapStrategySettingsType>().eq(TLdapStrategySettingsType::getCode, code);
        } else {
            lambdaQueryWrapper = new LambdaQueryWrapper<TLdapStrategySettingsType>().eq(TLdapStrategySettingsType::getCode, code).ne(TLdapStrategySettingsType::getId, id);
        }
        List<TLdapStrategySettingsType> list = typeService
                .list(lambdaQueryWrapper);
        return ResponseEntity.ok(ResponseData.success(list.size() > 0));
    }

    @GetMapping("getCommandPage")
    public ResponseEntity<ResponseData> getCommandPage(Page page, @RequestParam("typeId") Long typeId,@RequestParam(value = "name",required = false)String name) {

        List<TreeDataUtil> nodes = getTypeNodes();

        Set<String> childrenIdSet = TreeDataUtil.getChildrenIdSet(nodes, String.valueOf(typeId));

        Set<Long> set = TreeDataUtil.toLongSet(childrenIdSet);

        IPage<StrategySettingsCommandDto> pageByTypeSet = commandService.getPageByTypeSet(page, set,name);

        return ResponseEntity.ok(ResponseData.success(pageByTypeSet));
    }

    @DeleteMapping("deleteById/{id}")
    @Transactional
    public ResponseEntity<ResponseData> deleteById(@PathVariable Long id) {

        valuesService.remove(new LambdaQueryWrapper<TLdapStrategySettingsValues>().eq(TLdapStrategySettingsValues::getCommandId, id));
        commandService.removeById(id);

        return ResponseEntity.ok(ResponseData.success());
    }

    @PostMapping("addOrUpdateCommand")
    public ResponseEntity<ResponseData> addOrUpdateCommand(@RequestHeader("token") String token, @RequestBody StrategySettingsCommandDto data) {
        if (data.getId() == null) {
            TSysToken tokenInst = tokenService.getOne(new LambdaQueryWrapper<TSysToken>().eq(TSysToken::getToken, token));
            data.setUserId(tokenInst.getUserId());
            data.setUpdateTime(new Date());
            data.setScope(0);
        }
        commandService.saveOrUpdate(data);

        return ResponseEntity.ok(ResponseData.success());
    }

    public List<TreeDataUtil> getTypeNodes() {
        List<TLdapStrategySettingsType> list = typeService.list();

        List<TreeDataUtil> nodes = new ArrayList<>();
        for (TLdapStrategySettingsType item : list) {
            nodes.add(new TreeDataUtil(String.valueOf(item.getId()), String.valueOf(item.getPid()), item.getName(), null,item));
        }
        return nodes;
    }
}
