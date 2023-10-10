package com.plzy.ldap.framework.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * 文件工具类
 */
@Slf4j
public class FileUtil{

    /**
     * 字符集
     */
    public enum CHARSET{

        UTF_8("UTF-8"),
        GBK("GBK"),
        GB2312("GB2312"),
        GB18030("GB18030");

        private String value;

        private CHARSET(String value){
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * 读取指定路径下的文档(以字符方式读取)
     *
     * @param filePath
     * @return
     */
    public static String read(String filePath){

        BufferedReader br = null;

        try {

            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));

            StringBuilder content = new StringBuilder();

            String data = null;
            while((data = br.readLine())!=null){
                content.append(data).append("\r\n");
            }

            if(content.length() > "\r\n".length()){
                return content.substring(0, content.length() - "\r\n".length()).toString();
            }
            return content.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {

            try {
                if(br != null){
                    br.close();
                }
            } catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }
    }

    /**
     * 读取指定路径下的文档(以字节方式读取)
     *
     * @param filePath
     * @param charset
     * @return
     */
    public static String read(String filePath, CHARSET charset){

        StringBuilder content = new StringBuilder();

        File file = new File(filePath);
        InputStream is = null;

        try {

            is = new FileInputStream(file);

            byte[] tmp = new byte[512];
            int len;

            while((len = is.read(tmp)) != -1){

                content.append(new String(tmp, 0, len, charset.toString()));
            }

            is.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {

            try {

                if(is != null){
                    is.close();
                }
            } catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }

        return content.toString();
    }

    /**
     * 读取流，并以文本返回
     *
     * @param is
     * @return
     */
    public static String read(InputStream is){

        StringBuilder content = new StringBuilder();

        try {

            byte[] tmp = new byte[512];
            int len;

            while((len = is.read(tmp)) != -1){

                content.append(new String(tmp, 0, len));
            }

            is.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {

            try {

                if(is != null){
                    is.close();
                }
            } catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }

        return content.toString();
    }

    /**
     * 向已有文件中追加内容
     *
     * @param content
     * @param path
     */
    public static void append(String content, String path){

        write(content, path, true);
    }

    /**
     * 写入文件
     *
     * @param content
     * @param path
     */
    public static void write(String content, String path){

        write(content, path, false);
    }

    /**
     * 将字符串写入文件
     *
     * @param content 待写入内容
     * @param path 文件路径
     */
    private static void write(String content, String path, boolean append){

        File dir = new File(path).getParentFile();
        if(!dir.exists()){

            dir.mkdirs();
        }

        try {
            Writer writer = new FileWriter(path, append);
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
