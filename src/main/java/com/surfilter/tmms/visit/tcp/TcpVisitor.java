/**
 * Project Name:VisitConnect
 * File Name:TcpVisitor.java
 * Package Name:com.surfilter.tmms.visit
 * Date:2016年1月4日下午2:13:45
 *
*/

package com.surfilter.tmms.visit.tcp;

import org.apache.commons.lang3.StringUtils;

import com.surfilter.tmms.VisitContext;
import com.surfilter.tmms.bean.VisitBean;
import com.surfilter.tmms.bean.VisitReturn;
import com.surfilter.tmms.enums.ContentTypes;
import com.surfilter.tmms.utils.SocketUtils;

/**
 * ClassName:TcpVisitor <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016年1月4日 下午2:13:45 <br/>
 * @author   huhuan
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class TcpVisitor implements Runnable{
	
	private VisitContext context;
	
	private VisitBean visitBean;
	
	private ContentTypes contentType;
	
	private Boolean downloadFlag=false;
	
	public TcpVisitor(VisitContext context, VisitBean visitBean, ContentTypes contentType) {
		super();
		this.context = context;
		this.visitBean = visitBean;
		this.contentType = contentType;
	}

	
	public TcpVisitor(VisitContext context, VisitBean visitBean,ContentTypes contentType, Boolean downloadFlag) {
		super();
		this.context = context;
		this.visitBean = visitBean;
		this.contentType = contentType;
		this.downloadFlag = downloadFlag;
		if(this.downloadFlag==null){
			this.downloadFlag=false;
		}
	}


	public void run() {
		while(!context.isFinish()){
			VisitReturn visitReturn=new VisitReturn();
			String condition="";
			switch(contentType){
				case DOMAIN:
					if(StringUtils.isNotBlank(visitBean.getDomain())){
						condition="域名："+visitBean.getDomain();
						visitReturn=SocketUtils.connection(visitBean.getDomain(),downloadFlag);
					}
					break;
				case URL:
					if(StringUtils.isNotBlank(visitBean.getUrl())){
						condition="URL："+visitBean.getUrl();
						visitReturn=SocketUtils.connection(visitBean.getUrl(),downloadFlag);
					}
					break;
				case IPPORT:
					if(StringUtils.isNotBlank(visitBean.getIp())){
						condition="IP："+visitBean.getIp()+",端口："+visitBean.getPort();
						visitReturn=SocketUtils.connection(visitBean.getIp(),visitBean.getPort(),downloadFlag);
					}
					break;
				case DOMAIN_IPPORT:
					if(StringUtils.isNotBlank(visitBean.getDomain())){
						condition="域名："+visitBean.getDomain()+",IP："+visitBean.getIp()+",端口："+visitBean.getPort();
						visitReturn=SocketUtils.connection(visitBean.getDomain(),visitBean.getIp(),visitBean.getPort(),downloadFlag);
					}
					break;
				case URL_IPPORT:
					if(StringUtils.isNotBlank(visitBean.getUrl())){
						condition="URL："+visitBean.getUrl()+",IP:"+visitBean.getIp()+",端口:"+visitBean.getPort();
						visitReturn=SocketUtils.connection(visitBean.getUrl(),visitBean.getIp(),visitBean.getPort(),downloadFlag);
					}
					break;
			default:
				break;
			}
			context.collect(visitReturn,condition);
		}
		
	}

}

