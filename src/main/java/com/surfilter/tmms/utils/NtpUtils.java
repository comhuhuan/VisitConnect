/**
 * Project Name:visitconnect
 * File Name:NtpUtils.java
 * Package Name:com.surfilter.tmms.utils
 * Date:2016年2月17日上午11:21:04
 * Copyright (c) 2016, CANNIKIN(http://http://code.taobao.org/p/cannikin/src/) All Rights Reserved.
 *
*/

package com.surfilter.tmms.utils;

import java.net.InetAddress;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.log4j.Logger;

/**
 * ClassName:NtpUtils <br/>
 * Function: 网络时间协议. <br/>
 * Date:     2016年2月17日 上午11:21:04 <br/>
 * @author   huhuan
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class NtpUtils {
    /**
     * 日志类
     */
    private static final Logger logger = Logger.getLogger(NtpUtils.class);

    // 连接成功
    private static final int CONNECT_SUCCESS = 200;

    // 连接失败
    private static final int CONNECT_FAILURE = 0;

    // 请求超时
    private static final int CONNECTION_TIMEOUT = 5000;
    
    // 默认端口号
    private static final int DEFAULT_PORT = 123;
    
    /**
     * 
     * connectionHost:连接网络时间服务器. <br/>
     *
     * @author qiyongkang
     * @param ip
     * @param port
     * @return
     * @since JDK 1.6
     */
    public static int connectionHost(String ip, int port) {
        int status = CONNECT_FAILURE;
        
        NTPUDPClient client = null;
        try {
            // 如果没有设置端口，则使用ftp协议的默认端口123
            if (port == 0) {
                port = DEFAULT_PORT;
            }
            
            client = new NTPUDPClient();
            client.setDefaultTimeout(CONNECTION_TIMEOUT);
            
            //打开客户端
            client.open();
            
            InetAddress remoteAddr = InetAddress.getByName(ip);
            
            TimeInfo info = client.getTime(remoteAddr, port);
            
            if (info != null) {
                status = CONNECT_SUCCESS;
            }
        } catch (Exception e) {
            logger.error("ntp服务器连接异常", e);
        } finally {
            if (client != null && client.isOpen()) {
                client.close();
            }
        }
        
        return status;
    }
    
    public static void main(String[] args) {
        System.out.println(("连接状态：" + connectionHost("172.31.26.200", 0)));
    }
}

