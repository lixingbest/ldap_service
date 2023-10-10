package com.plzy.ldap.modules.dict.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.framework.utils.TextUtil;
import com.plzy.ldap.modules.dict.domain.TSysDictItem;
import com.plzy.ldap.modules.dict.domain.TSysDictRecords;
import com.plzy.ldap.modules.dict.service.TSysDictItemService;
import com.plzy.ldap.modules.dict.service.TSysDictRecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/dictrecords")
public class DictRecordsController {
    @Autowired
    private TSysDictItemService dictItemService;

    @Autowired
    private TSysDictRecordsService sysDictRecordsService;

    @GetMapping("/list")
    public ResponseEntity<ResponseData> list(Page<TSysDictRecords> page, String itemId, TSysDictRecords params) {


        Page<TSysDictRecords> list = sysDictRecordsService.page(page, new QueryWrapper<TSysDictRecords>().eq("dict_item_id", itemId).allEq(TextUtil.camel2Underline(BeanMap.create(params)), false));
        return ResponseEntity.ok(ResponseData.success(list));


    }

    @GetMapping("getListByCode/{code}")
    public ResponseEntity<ResponseData> getList(@PathVariable String code) {

        TSysDictItem item = dictItemService.getOne(new LambdaQueryWrapper<TSysDictItem>().eq(TSysDictItem::getCode, code));
        List<TSysDictRecords> list = sysDictRecordsService.list(new LambdaQueryWrapper<TSysDictRecords>().eq(TSysDictRecords::getDictItemId, item.getId()));
        return ResponseEntity.ok(ResponseData.success(list));
    }

    @GetMapping("/get")
    public ResponseEntity<ResponseData> get(Long id) {

        TSysDictRecords obj = sysDictRecordsService.getById(id);
        return ResponseEntity.ok(ResponseData.success(obj));
    }

    @GetMapping("/saveOrUpdate")
    public ResponseEntity<ResponseData> saveOrUpdate(TSysDictRecords dictRecords) {

        sysDictRecordsService.saveOrUpdate(dictRecords);
        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/remove")
    public ResponseEntity<ResponseData> remove(String ids) {

        sysDictRecordsService.removeByIds(TextUtil.ids2LongList(ids));
        return ResponseEntity.ok(ResponseData.success());
    }
}
