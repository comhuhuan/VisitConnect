/**
 * Project Name:visitconnect
 * File Name:FtpUtils.java
 * Package Name:com.surfilter.tmms.utils
 * Date:2016年2月17日上午11:20:21
 * Copyright (c) 2016, CANNIKIN(http://http://code.taobao.org/p/cannikin/src/) All Rights Reserved.
 *
*/

package com.surfilter.tmms.utils;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;

/**
 * ClassName:FtpUtils <br/>
 * Function: ftp协议工具类. <br/>
 * Date: 2016年2月17日 上午11:20:21 <br/>
 * 
 * @author qiyongkang
 * @version
 * @since JDK 1.6
 * @see
 */
public class FtpUtils {
    /**
     * 日志类
     */
    private static final Logger logger = Logger.getLogger(FtpUtils.class);

    // 连接成功
    private static final int CONNECT_SUCCESS = 200;

    // 连接失败
    private static final int CONNECT_FAILURE = 0;

    // 请求超时
    private static final int CONNECTION_TIMEOUT = 5000;
    
    // 默认端口号
    private static final int DEFAULT_PORT = 21;

    /**
     * 
     * connection:测试ftp服务器是否连得上. <br/>
     *
     * @author qiyongkang
     * @param ip
     *            主机
     * @param port
     *            端口
     * @param userName
     *            用户名
     * @param pass
     *            密码
     * @return
     * @since JDK 1.6
     */
    public static int connectionHost(String ip, int port, String userName, String pass) {
        int status = CONNECT_FAILURE;
        FTPClient ftpClient = null;
        try {
            // 如果没有设置端口，则使用ftp协议的默认端口21
            if (port == 0) {
                port = DEFAULT_PORT;
            }

            ftpClient = new FTPClient();
            ftpClient.setDefaultTimeout(CONNECTION_TIMEOUT);
            ftpClient.connect(ip, port);

            boolean flag = ftpClient.login(userName, pass);

            // 登录成功
            if (flag) {
                status = CONNECT_SUCCESS;
            }
        } catch (Exception e) {
            logger.error("ftp服务器连接异常", e);
        } finally {
            if (ftpClient != null && ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    logger.error("ftp连接断开异常", e);
                }
            }
        }
        return status;
    }

    /**
     * 
     * connectionUrl:测试ftp路径是否连得上. <br/>
     *
     * @author qiyongkang
     * @param url
     * @param port
     * @param userName
     * @param pass
     * @return
     * @since JDK 1.6
     */
    public static int connectionUrl(String url, int port, String userName, String pass) {
        int status = CONNECT_FAILURE;
        try {
            String host = url;
            status = connectionHost(host, port, userName, pass);

        } catch (Exception e) {
            logger.error("ftp服务器连接异常", e);
        }
        return status;
    }

    public static void main(String[] args) {
        System.out.println(("连接状态：" + connectionHost("172.31.27.149", 0, "yanfa", "act123")));
        System.out.println(("连接状态：" + connectionHost("172.31.27.147", 0, "yanfa", "act123")));
    }
}
