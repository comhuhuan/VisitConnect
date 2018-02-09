/**
 * Project Name:visitconnect
 * File Name:SshVisitor.java
 * Package Name:com.surfilter.tmms.visit.ssh
 * Date:2016年2月17日上午11:11:56
 * Copyright (c) 2016, CANNIKIN(http://http://code.taobao.org/p/cannikin/src/) All Rights Reserved.
 */

package com.surfilter.tmms.visit.ssh;

import org.apache.commons.lang3.StringUtils;

import com.surfilter.tmms.VisitContext;
import com.surfilter.tmms.bean.VisitBean;
import com.surfilter.tmms.enums.ContentTypes;
import com.surfilter.tmms.utils.SshUtils;

/**
 * ClassName:SshVisitor <br/>
 * Function: ssh（安全外壳协议）协议测试. <br/>
 * Date:     2016年2月17日 上午11:11:56 <br/>
 * @author qiyongkang
 * @version
 * @since JDK 1.6
 * @see
 */
public class SshVisitor implements Runnable {
    private VisitContext context;

    private VisitBean visitBean;

    private ContentTypes contentType;

    public SshVisitor(VisitContext context, VisitBean visitBean, ContentTypes contentType) {
        super();
        this.context = context;
        this.visitBean = visitBean;
        this.contentType = contentType;
    }

    @Override
    public void run() {
        while (!context.isFinish()) {
            int statusCode = 0;
            String condition = "";
            switch (contentType) {
                case IPPORT:
                    if (StringUtils.isNotBlank(visitBean.getIp())) {
                        condition = "IP：" + visitBean.getIp() + ",端口：" + visitBean.getPort();
                        statusCode = SshUtils.connectionHost(visitBean.getIp(), visitBean.getPort(), visitBean.getProxyUserName(), visitBean.getProxyPassword());
                    }
                    break;
                default:
                    break;
            }
            context.collect(statusCode, condition);
        }

    }
}

