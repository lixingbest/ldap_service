package com.plzy.ldap.modules.dict.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.framework.utils.TextUtil;
import com.plzy.ldap.modules.dict.domain.TSysDictItem;
import com.plzy.ldap.modules.dict.service.TSysDictItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dictitem")
public class DictItemController {

    @Autowired
    private TSysDictItemService sysDictItemService;

    @GetMapping("/list")
    public ResponseEntity<ResponseData> list(Page<TSysDictItem> page, Long dictId, TSysDictItem parmas){

        Page<TSysDictItem> list = sysDictItemService.page(page, new QueryWrapper<TSysDictItem>().eq("dict_id", dictId).allEq(TextUtil.camel2Underline(BeanMap.create(parmas)),false));
        return ResponseEntity.ok(ResponseData.success(list));
    }

    @GetMapping("/get")
    public ResponseEntity<ResponseData> get(Long id){

        TSysDictItem obj = sysDictItemService.getById(id);
        return ResponseEntity.ok(ResponseData.success(obj));
    }

    @GetMapping("/saveOrUpdate")
    public ResponseEntity<ResponseData> saveOrUpdate(TSysDictItem dictItem){

        sysDictItemService.saveOrUpdate(dictItem);
        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/remove")
    public ResponseEntity<ResponseData> remove(String ids){

        sysDictItemService.removeByIds(TextUtil.ids2LongList(ids));
        return ResponseEntity.ok(ResponseData.success());
    }
}
