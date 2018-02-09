/**
 * Project Name:visitconnect
 * File Name:SnmpUtils.java
 * Package Name:com.surfilter.tmms.utils
 * Date:2016年2月17日上午11:21:50
 * Copyright (c) 2016, CANNIKIN(http://http://code.taobao.org/p/cannikin/src/) All Rights Reserved.
 *
*/

package com.surfilter.tmms.utils;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/**
 * ClassName:SnmpUtils <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年2月17日 上午11:21:50 <br/>
 * 
 * @author qiyongkang
 * @version
 * @since JDK 1.6
 * @see
 */
public class SnmpUtils {
    /**
     * 日志类
     */
    private static final Logger logger = Logger.getLogger(SnmpUtils.class);

    // 连接成功
    private static final int CONNECT_SUCCESS = 200;

    // 连接失败
    private static final int CONNECT_FAILURE = 0;

    // 请求超时
    private static final int CONNECTION_TIMEOUT = 2000;

    // 默认端口号
    private static final int DEFAULT_PORT = 161;

    private static Snmp snmp = null;
    
    //默认版本
    private static int version = SnmpConstants.version1;
    
    /**
     * 
     * init:初始化监听. <br/>
     *
     * @author qiyongkang
     * @param version
     * @since JDK 1.6
     */
    public static void init(int version) {
        try {
            TransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            if (version == SnmpConstants.version3) {
                // 设置安全模式
                USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
                SecurityModels.getInstance().addSecurityModel(usm);
            }
            // 开始监听消息
            transport.listen();
        } catch (IOException e) {
            logger.error("snmp监听异常", e);
        }
    }
    
    /**
     * 
     * sendMessage:发送报文. <br/>
     *
     * @author qiyongkang
     * @param syn
     * @param bro
     * @param pdu
     * @param addr
     * @return
     * @throws IOException
     * @since JDK 1.6
     */
    public static int sendMessage(Boolean syn, final Boolean bro, PDU pdu, String addr) throws IOException {
        int status = CONNECT_FAILURE;

        // 初始化
        init(version);

        // 生成目标地址对象
        Address targetAddress = GenericAddress.parse(addr);
        Target target = null;
        if (version == SnmpConstants.version3) {
            // 添加用户
            snmp.getUSM().addUser(new OctetString("MD5DES"), new UsmUser(new OctetString("MD5DES"), AuthMD5.ID,
                    new OctetString("MD5DESUserAuthPassword"), PrivDES.ID, new OctetString("MD5DESUserPrivPassword")));
            target = new UserTarget();
            // 设置安全级别
            ((UserTarget) target).setSecurityLevel(SecurityLevel.AUTH_PRIV);
            ((UserTarget) target).setSecurityName(new OctetString("MD5DES"));
            target.setVersion(SnmpConstants.version3);
        } else {
            target = new CommunityTarget();
            if (version == SnmpConstants.version1) {
                target.setVersion(SnmpConstants.version1);
                ((CommunityTarget) target).setCommunity(new OctetString("public"));
            } else {
                target.setVersion(SnmpConstants.version2c);
                ((CommunityTarget) target).setCommunity(new OctetString("public"));
            }

        }
        // 目标对象相关设置
        target.setAddress(targetAddress);
        target.setRetries(1);
        target.setTimeout(CONNECTION_TIMEOUT);

        if (!syn) {
            // 发送报文 并且接受响应
            ResponseEvent response = snmp.send(pdu, target);
            // 处理响应
            System.out.println("Synchronize(同步) message(消息) from(来自) " + response.getPeerAddress() + "\r\n"
                    + "request(发送的请求):" + response.getRequest() + "\r\n" + "response(返回的响应):" + response.getResponse());

            if (response.getResponse() != null) {
                status = CONNECT_SUCCESS;
            }
        } else {
            // 设置监听对象
            ResponseListener listener = new ResponseListener() {

                public void onResponse(ResponseEvent event) {
                    if (bro.equals(false)) {
                        ((Snmp) event.getSource()).cancel(event.getRequest(), this);
                    }
                    // 处理响应
                    PDU request = event.getRequest();
                    PDU response = event.getResponse();
                    System.out.println("Asynchronise(异步) message(消息) from(来自) " + event.getPeerAddress() + "\r\n"
                            + "request(发送的请求):" + request + "\r\n" + "response(返回的响应):" + response);
                }

            };
            // 发送报文
            snmp.send(pdu, target, null, listener);
        }
        return status;
    }
    
    /**
     * 
     * connectionHost:连接snmp服务器. <br/>
     *
     * @author qiyongkang
     * @param ip
     * @param port
     * @return
     * @since JDK 1.6
     */
    public static int connectionHost(String ip, int port) {
        // 如果没有设置端口，则使用ftp协议的默认端口123
        if (port == 0) {
            port = DEFAULT_PORT;
        }

        int status = CONNECT_FAILURE;
        // Snmp的三个版本号,SnmpConstants.version3,version2c,version1
        // 构造报文
        PDU pdu = null;
        if (version == SnmpConstants.version3) {
            pdu = new ScopedPDU();
        } else {
            pdu = new PDU();
        }
        // 设置要获取的对象ID，这个OID代表远程计算机的名称，1.3.6.1.4.1.2021.11.11.0是主机CPU空闲率的oid
        OID oids1 = new OID("1.3.6.1.2.1.1.5.0");
        pdu.add(new VariableBinding(oids1));
        // 设置报文类型
        pdu.setType(PDU.GET);
        try {
            // 发送消息 其中最后一个是想要发送的目标地址
            status = sendMessage(false, true, pdu, "udp:" + ip + "/" + port);
        } catch (IOException e) {
            logger.error("snmp连接异常", e);
        }
        return status;
    }

    public static void main(String[] args) {
        System.out.println("snmp：" + connectionHost("172.31.26.200", 161));
    }
}
