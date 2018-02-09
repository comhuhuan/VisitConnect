/**
 * Project Name:visitconnect
 * File Name:NtpVisitor.java
 * Package Name:com.surfilter.tmms.visit.ntp
 * Date:2016年2月17日上午11:12:37
 * Copyright (c) 2016, CANNIKIN(http://http://code.taobao.org/p/cannikin/src/) All Rights Reserved.
 *
*/

package com.surfilter.tmms.visit.ntp;

import org.apache.commons.lang3.StringUtils;

import com.surfilter.tmms.VisitContext;
import com.surfilter.tmms.bean.VisitBean;
import com.surfilter.tmms.enums.ContentTypes;
import com.surfilter.tmms.utils.NtpUtils;

/**
 * ClassName:NtpVisitor <br/>
 * Function: ntp(网络时间协议)协议测试. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016年2月17日 上午11:12:37 <br/>
 * @author   huhuan
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class NtpVisitor implements Runnable {
    private VisitContext context;

    private VisitBean visitBean;

    private ContentTypes contentType;

    public NtpVisitor(VisitContext context, VisitBean visitBean, ContentTypes contentType) {
        super();
        this.context = context;
        this.visitBean = visitBean;
        this.contentType = contentType;
    }

    public void run() {
        while (!context.isFinish()) {
            int statusCode = 0;
            String condition = "";
            switch (contentType) {
            case IPPORT:
                if (StringUtils.isNotBlank(visitBean.getIp())) {
                    condition = "IP：" + visitBean.getIp() + ",端口：" + visitBean.getPort();
                    statusCode = NtpUtils.connectionHost(visitBean.getIp(), visitBean.getPort());
                }
                break;
            default:
                break;
            }
            context.collect(statusCode, condition);
        }

    }
}

