/**
 * Project Name:visitconnect
 * File Name:SshUtils.java
 * Package Name:com.surfilter.tmms.utils
 * Date:2016年2月17日上午11:21:22
 * Copyright (c) 2016, CANNIKIN(http://http://code.taobao.org/p/cannikin/src/) All Rights Reserved.
 *
*/

package com.surfilter.tmms.utils;

import java.util.Hashtable;

import org.apache.log4j.Logger;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import expect4j.Expect4j;

/**
 * ClassName:SshUtils <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016年2月17日 上午11:21:22 <br/>
 * @author   huhuan
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class SshUtils {
    /**
     * 日志类
     */
    private static final Logger logger = Logger.getLogger(SshUtils.class);

    // 连接成功
    private static final int CONNECT_SUCCESS = 200;

    // 连接失败
    private static final int CONNECT_FAILURE = 0;

    // 请求超时
    private static final int CONNECTION_TIMEOUT = 5000;
    
    // 默认端口号
    private static final int DEFAULT_PORT = 22;
    
    /**
     * 
     * connectionHost:通过ssh协议测试主机是否连接得上. <br/>
     *
     * @author qiyongkang
     * @param ip
     * @param port
     * @param userName
     * @param pass
     * @return
     * @since JDK 1.6
     */
    public static int connectionHost(String ip, int port, String userName, String pass) {
        int status = CONNECT_FAILURE;
        Session session = null;
        ChannelShell channel = null;
        try {
            // 如果没有设置端口，则使用ftp协议的默认端口22
            if (port == 0) {
                port = DEFAULT_PORT;
            }
            
            JSch jsch = new JSch();
            session = jsch.getSession(userName, ip, port);
            session.setPassword(pass);
            Hashtable<String, String> config = new Hashtable<String, String>();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            localUserInfo ui = new localUserInfo();
            session.setUserInfo(ui);
            session.setTimeout(CONNECTION_TIMEOUT);
            session.connect();
            
            channel = (ChannelShell) session.openChannel("shell");
            Expect4j expect = new Expect4j(channel.getInputStream(), channel
                    .getOutputStream());
            channel.connect();
            
            if(expect != null){
                status = CONNECT_SUCCESS;
            }
        } catch (Exception ex) {
            logger.error("ssh连接异常", ex);
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
        return status;
    }
    
    //登入SSH时的控制信息
    //设置不提示输入密码、不显示登入信息等
    public static class localUserInfo implements UserInfo {
        String passwd;

        public String getPassword() {
            return passwd;
        }

        public boolean promptYesNo(String str) {
            return true;
        }

        public String getPassphrase() {
            return null;
        }

        public boolean promptPassphrase(String message) {
            return true;
        }

        public boolean promptPassword(String message) {
            return true;
        }

        public void showMessage(String message) {
            
        }
    }
    
    public static void main(String[] args) {
        System.out.println(("连接状态：" + connectionHost("172.31.26.200", 0, "root", "123456")));
    }
}

