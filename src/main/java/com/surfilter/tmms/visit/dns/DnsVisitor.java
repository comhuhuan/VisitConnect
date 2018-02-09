package com.surfilter.tmms.visit.dns;

import org.apache.commons.lang3.StringUtils;

import com.surfilter.tmms.VisitContext;
import com.surfilter.tmms.bean.VisitBean;
import com.surfilter.tmms.enums.ContentTypes;
import com.surfilter.tmms.utils.DnsUtils;

/**
 * ClassName:DnsVisitor <br/>
 * Date:     2016年2月18日 下午2:13:10 <br/>
 * @author   huhuan
 * @since    JDK 1.7
 */
public class DnsVisitor implements Runnable {
	private VisitContext context;
	private VisitBean visitBean;
	private ContentTypes contentType;
	
	public DnsVisitor(VisitContext context, VisitBean visitBean, ContentTypes contentType){
		super();
		this.context = context;
		this.visitBean = visitBean;
		this.contentType = contentType;
	}
	
	@Override
	public void run() {
		while(!context.isFinish()){
			int statusCode=0;
			String condition="";
			switch (contentType) {
			case DNS:
				if(StringUtils.isNotBlank(visitBean.getIp())){
					condition = "地址："+visitBean.getIp();
					boolean flag = DnsUtils.connection(visitBean.getIp(),
							visitBean.getPort());
					statusCode = flag ? 200 : 0;
				}
				break;
			default:
				break;
			}
			context.collect(statusCode,condition);
		}	
	}

}
