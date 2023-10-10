package com.plzy.ldap.modules.message.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.modules.message.domain.TLdapClientMsg;
import com.plzy.ldap.modules.message.domain.TLdapClientMsgReceiver;
import com.plzy.ldap.modules.message.dto.ClientMessageDto;
import com.plzy.ldap.modules.message.service.TLdapClientMsgReceiverService;
import com.plzy.ldap.modules.message.service.TLdapClientMsgService;
import com.plzy.ldap.modules.token.domain.TSysToken;
import com.plzy.ldap.modules.token.service.TSysTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.util.Date;

@RequestMapping("message")
@Controller
public class MessageController {

    @Resource
    private TSysTokenService tokenService;

    @Resource
    private TLdapClientMsgService messageService;

    @Resource
    private TLdapClientMsgReceiverService messageReceiverService;

    @GetMapping("page")
    public ResponseEntity<ResponseData> page(Page<ClientMessageDto> page, TLdapClientMsg message) {

        Page<ClientMessageDto> getpage = messageService.getpage(page, message);

        return ResponseEntity.ok(ResponseData.success(getpage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData> get(@PathVariable("id") Long id) {

        ClientMessageDto infoById = messageService.getInfoById(id);



        return ResponseEntity.ok(ResponseData.success(infoById));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData> delete(@PathVariable("id") String id) {
        messageReceiverService.remove(new LambdaQueryWrapper<TLdapClientMsgReceiver>().eq(TLdapClientMsgReceiver::getClientMsgId, id));
        messageService.removeById(id);
        return ResponseEntity.ok(ResponseData.success());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ResponseData> addOrUpdae(@RequestHeader("token") String token,@RequestBody ClientMessageDto messageDto) {

        TSysToken tokenInst = tokenService.getOne(new LambdaQueryWrapper<TSysToken>().eq(TSysToken::getToken, token));

        if (messageDto.getId() != null) {
            messageReceiverService.remove(new LambdaQueryWrapper<TLdapClientMsgReceiver>().eq(TLdapClientMsgReceiver::getClientMsgId, messageDto.getId()));
        }else {
            messageDto.setTime(new Date());
            messageDto.setUserId(tokenInst.getUserId());
        }


        messageService.saveOrUpdate(messageDto);

        for (TLdapClientMsgReceiver item : messageDto.getReceiverList()) {
            item.setClientMsgId(messageDto.getId());
            item.setIsRecv(0);
        }

        messageReceiverService.saveBatch(messageDto.getReceiverList());

        return ResponseEntity.ok(ResponseData.success());
    }

}
