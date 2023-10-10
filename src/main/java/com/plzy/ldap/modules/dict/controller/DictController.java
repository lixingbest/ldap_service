package com.plzy.ldap.modules.dict.controller;

import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.framework.utils.TextUtil;
import com.plzy.ldap.modules.dict.domain.TSysDict;
import com.plzy.ldap.modules.dict.service.TSysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/dict")
public class DictController {

    @Autowired
    private TSysDictService sysDictService;

    @GetMapping("/list")
    public ResponseEntity<ResponseData> list(TSysDict sysDict){

        List<TSysDict> list = sysDictService.list();
        return ResponseEntity.ok(ResponseData.success(list));
    }

    @GetMapping("/get")
    public ResponseEntity<ResponseData> get(Long id){

        TSysDict obj = sysDictService.getById(id);
        return ResponseEntity.ok(ResponseData.success(obj));
    }

    /**
     * 查询数据字段内容
     *
     * @param codeList 语法：数据字典编号.主条目编号，例如 sys.zhiwei
     * @return
     */
    @GetMapping("/query")
    public ResponseEntity<ResponseData> query(String codeList){

        if(!StringUtils.hasText(codeList) || !codeList.contains(".")){
            return ResponseEntity.ok(ResponseData.error("999999","code语法错误：不能为空并必须包含.！"));
        }

        String[] items = codeList.split("\\.");

        return ResponseEntity.ok(ResponseData.success(sysDictService.query(items[0], items[1])));
    }

    @GetMapping("/saveOrUpdate")
    public ResponseEntity<ResponseData> saveOrUpdate(TSysDict dict){

        sysDictService.saveOrUpdate(dict);
        return ResponseEntity.ok(ResponseData.success());
    }

    @GetMapping("/remove")
    public ResponseEntity<ResponseData> remove(String ids){

        sysDictService.removeByIds(TextUtil.ids2LongList(ids));
        return ResponseEntity.ok(ResponseData.success());
    }
}
