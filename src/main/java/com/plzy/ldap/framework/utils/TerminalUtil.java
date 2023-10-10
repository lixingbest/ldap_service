package com.plzy.ldap.framework.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class TerminalUtil {

    public static void main(String[] args) {
        boolean rs = ping("127.0.0.3");
        System.out.print(rs);
    }

    private TerminalUtil(){}

    /**
     * ping主机
     *
     * @param host
     * @return
     */
    public static boolean ping(String host){

        try {
            InetAddress inetAddress = InetAddress.getByName(host);
            boolean reachable = inetAddress.isReachable(1*1000);
            if(reachable) {
                return true;
            }else {
                return false;
            }
        } catch (Exception e1) {
            return false;
        }
    }
}
