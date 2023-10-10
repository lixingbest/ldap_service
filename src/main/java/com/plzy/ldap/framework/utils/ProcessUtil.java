package com.plzy.ldap.framework.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

@Slf4j
public class ProcessUtil {

    private ProcessUtil(){}

    /**
     * 调用
     *
     * @param cmds
     * @return
     * @throws Exception
     */
    public static String exec(String[] cmds) throws Exception{

        log.info("即将发送exec命令：" + Arrays.toString(cmds));

        ProcessBuilder process = new ProcessBuilder(cmds);
        Process p;
        try {
            p = process.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            String result = builder.toString();
            log.info("exec执行结果：" + result);

            return result;

        } catch (IOException e) {
            log.error("exec时出现错误：",e);
            throw e;
        }
    }
}
