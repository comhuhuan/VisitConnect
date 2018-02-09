/**
 * Project Name:VisitConnect
 * File Name:UdpVisitor.java
 * Package Name:com.surfilter.tmms.visit
 * Date:2016年1月4日下午2:13:56
 *
*/

package com.surfilter.tmms.visit.udp;

import org.apache.commons.lang3.StringUtils;

import com.surfilter.tmms.VisitContext;
import com.surfilter.tmms.bean.VisitBean;
import com.surfilter.tmms.enums.ContentTypes;
import com.surfilter.tmms.utils.UdpUtils;


/**
 * ClassName:UdpVisitor <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016年1月4日 下午2:13:56 <br/>
 * @author   huhuan
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class UdpVisitor implements Runnable{
	

	private VisitContext context;
	
	private VisitBean visitBean;
	
	private ContentTypes contentType;
	
	public UdpVisitor(VisitContext context, VisitBean visitBean, ContentTypes contentType) {
		super();
		this.context = context;
		this.visitBean = visitBean;
		this.contentType = contentType;
	}

	public void run() {
		while(!context.isFinish()){
			int statusCode=0;
			String condition="";
			switch(contentType){
				case IPPORT:
					if(StringUtils.isNotBlank(visitBean.getIp())){
						condition="IP："+visitBean.getIp()+",端口："+visitBean.getPort();
						boolean result=UdpUtils.connection(visitBean.getIp(),visitBean.getPort(),"测试UDP");
						statusCode=result?200:0;
					}
					break;
			default:
				break;
			}
			context.collect(statusCode,condition);
		}
		
	}

}

