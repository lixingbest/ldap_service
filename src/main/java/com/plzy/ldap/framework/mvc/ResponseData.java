package com.plzy.ldap.framework.mvc;

import lombok.Data;

import java.util.Date;

@Data
public class ResponseData {

    private Boolean success;

    private String code;

    private String message;

    private Date time;

    private Object data;

    private ResponseData(){
    }

    public static ResponseData success(){
        return success(null);
    }

    public static ResponseData success(String code, String message){
        ResponseData data = new ResponseData();
        data.setSuccess(true);
        data.setCode(code);
        data.setMessage(message);
        data.setTime(new Date());
        return data;
    }

    public static ResponseData success(Object obj){
        ResponseData data = new ResponseData();
        data.setSuccess(true);
        data.setCode("000000");
        data.setMessage("执行成功！");
        data.setTime(new Date());
        data.setData(obj);
        return data;
    }

    public static ResponseData error(String code, String message){
        ResponseData data = new ResponseData();
        data.setSuccess(false);
        data.setCode(code);
        data.setMessage(message);
        data.setTime(new Date());
        data.setData(null);
        return data;
    }
}
