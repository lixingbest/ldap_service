package com.plzy.ldap.framework.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * H4A的解密工具
 */
@Slf4j
public class DESUtil4H4A {

    private static Cipher c; //密码器
    private static byte[] cipherByte;
    private static SecretKey deskey; //密钥

    /**
     * 把密钥参数转为byte数组
     *
     * @param parm
     * @return
     * @throws IOException
     */
    public byte[] decodeBase64(String parm) throws IOException {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] dnParm = decoder.decode(parm);
        return dnParm;
    }

    /**
     * 对 Byte 数组进行解密
     *
     * @param buff 要解密的数据
     * @return 返回加密后的 String
     */
    public static String createDecryptor(byte[] buff) throws UnsupportedEncodingException {
        try {
            //初始化密码器，用密钥deskey,进入解密模式
            c.init(Cipher.DECRYPT_MODE, deskey);
            cipherByte = c.doFinal(buff);
        } catch (Exception ex) {
            log.error("解密时出现错误", ex);
        }
        return (new String(cipherByte, "UTF-8"));
    }

    public void getKey(String key) throws IOException {
        byte[] dKey = decodeBase64(key);
        try {
            deskey = new javax.crypto.spec.SecretKeySpec(dKey, "DESede");
            c = Cipher.getInstance("DESede");
        } catch (Exception ex) {
            log.error("解密时出现错误", ex);
        }
    }
}
