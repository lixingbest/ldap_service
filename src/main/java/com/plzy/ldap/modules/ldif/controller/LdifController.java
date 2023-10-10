package com.plzy.ldap.modules.ldif.controller;

import com.plzy.ldap.framework.mvc.ResponseData;
import com.plzy.ldap.modules.ldif.dto.UploadParams;
import com.plzy.ldap.modules.ldif.service.LdifService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/import_objs")
@Slf4j
public class LdifController {

    @Autowired
    private LdifService importObjsService;

    @Value("${ldap.ldif_upload_path}")
    private String ldifUploadPath;

    @PostMapping("/uploadOULdifFile")
    public ResponseEntity<ResponseData> uploadOULdifFile(@RequestParam("ouFile") MultipartFile ouFile){

        String fileName = System.nanoTime() + "";

        try {
            ouFile.transferTo(new File(ldifUploadPath + File.separator + fileName+".ldif"));
        } catch (IOException e) {
            log.error("上传文件时遇到错误：", e);
        }
        return ResponseEntity.ok(ResponseData.success(fileName));
    }

    @PostMapping("/uploadUserLdifFile")
    public ResponseEntity<ResponseData> uploadUserLdifFile(@RequestParam("userFile") MultipartFile userFile){

        String fileName = System.nanoTime() + "";

        try {
            userFile.transferTo(new File(ldifUploadPath + File.separator+fileName+".ldif"));
        } catch (IOException e) {
            log.error("上传文件时遇到错误：", e);
        }
        return ResponseEntity.ok(ResponseData.success(fileName));
    }

    @GetMapping("/submit")
    public ResponseEntity<ResponseData> submit(UploadParams params){

        String dn = "," + params.getOriginalDomainDN();

        importObjsService.ouImport(params.getDomainId(),params.getId(), dn,ldifUploadPath + File.separator+params.getOuLdifFileName()+".ldif");
        importObjsService.domainUserImport(params.getDomainId(),dn,params.getDefaultPasswd(),ldifUploadPath + File.separator+params.getUserLdifFileName()+".ldif");

        return ResponseEntity.ok(ResponseData.success(importObjsService.getLogs()));
    }
}
