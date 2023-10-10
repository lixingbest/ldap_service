package com.plzy.ldap.framework.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密工具
 */
@Slf4j
public class SecurityUtil{

    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * MD5 加密类型
     */
    public enum MD5_LENGTH{

        LENGTH_16(16),
        LENGTH_32(21);

        int length;
        MD5_LENGTH(int length){

            this.length = length;
        }
    }

    /**
     * 编码为 base64
     *
     * @param src
     * @return
     */
    public static String encodeForBase64(String src){

        try {
            return Base64.encodeBase64String(src.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }

    /**
     * 解码为 base64
     *
     * @param src
     * @return
     */
    public static String decodeForBase64(String src){

        try {
            return new String(Base64.decodeBase64(src), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }

    /**
     * MD5 摘要算法（16 位）
     * <br/>
     * 注意：包含汉字等特殊字符时，请使用 String MD5(String source, MD5_LENGTH length, Charset charset) 方法，并设置对应编码
     *
     * @return
     */
    public static String MD5_16(String source) {

        return MD5(source, MD5_LENGTH.LENGTH_16, null);
    }

    /**
     * MD5 摘要算法（32 位）
     * <br/>
     * 注意：包含汉字等特殊字符时，请使用 String MD5(String source, MD5_LENGTH length, Charset charset) 方法，并设置对应编码
     *
     * @return
     */
    public static String MD5_32(String source) {

        return MD5(source, MD5_LENGTH.LENGTH_32, null);
    }

    /**
     * MD5 摘要算法
     * <br/>
     * 注意：包含汉字等特殊字符时，请使用 String MD5(String source, MD5_LENGTH length, Charset charset) 方法，并设置对应编码
     *
     * @param source
     * @param length
     * @return
     */
    public static String MD5(String source, MD5_LENGTH length) {

        return MD5(source, length, null);
    }

    /**
     * MD5 摘要算法
     *
     * @param source
     * @param length
     * @param charset
     * @return
     */
    public static String MD5(String source, MD5_LENGTH length, Charset charset) {

        String result = "";
        try {

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(charset == null ? source.getBytes() : source.getBytes(charset));
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();

            return length == MD5_LENGTH.LENGTH_32 ? result : buf.toString().substring(8, 24);
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }

    /**
     * 生成密钥
     */
    public static String generateKey(){

        try {

            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(128);
            SecretKey secretKey = kg.generateKey();

            return Base64.encodeBase64String(secretKey.getEncoded());
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

        return null;
    }

    /**
     * 加密数据
     *
     * @param content
     * @param key
     * @return
     * @throws Exception
     */
    public static String encrypt(String content, String key){

        try {

            Key keyObj = new SecretKeySpec(Base64.decodeBase64(key), "AES");
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keyObj);

            return Base64.encodeBase64String(cipher.doFinal(content.getBytes()));
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

        return null;
    }

    /**
     * 解密数据
     *
     * @param content
     * @param key
     * @return
     * @throws Exception
     */
    public static String decrypt(String content, String key){

        try {

            Key keyObj = new SecretKeySpec(Base64.decodeBase64(key), "AES");
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keyObj);

            return new String(cipher.doFinal(Base64.decodeBase64(content)));
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

        return null;
    }
}
