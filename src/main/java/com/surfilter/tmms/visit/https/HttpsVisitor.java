/**
 * Project Name:VisitConnect
 * File Name:HttpsVisitor.java
 * Package Name:com.surfilter.tmms.visit
 * Date:2016年1月4日下午2:13:25
 *
*/

package com.surfilter.tmms.visit.https;

import org.apache.commons.lang3.StringUtils;

import com.surfilter.tmms.VisitContext;
import com.surfilter.tmms.bean.VisitBean;
import com.surfilter.tmms.bean.VisitReturn;
import com.surfilter.tmms.enums.ContentTypes;
import com.surfilter.tmms.utils.HttpsUtils;

/**
 * ClassName:HttpsVisitor <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016年1月4日 下午2:13:25 <br/>
 * @author   huhuan
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class HttpsVisitor implements Runnable{

private VisitContext context;
	
	private VisitBean visitBean;
	
	private ContentTypes contentType;
	
	private String httpsProtocol;
	
	private Boolean downloadFlag=false;
	
	
	public HttpsVisitor(VisitContext context, VisitBean visitBean, ContentTypes contentType,Boolean downloadFlag) {
		this(context, visitBean,contentType, "SSL",downloadFlag);
	}
	
	public HttpsVisitor(VisitContext context, VisitBean visitBean, ContentTypes contentType, String httpsProtocol) {
		super();
		this.context = context;
		this.visitBean = visitBean;
		this.contentType = contentType;
		this.httpsProtocol = httpsProtocol;
	}
	
	public HttpsVisitor(VisitContext context, VisitBean visitBean, ContentTypes contentType, String httpsProtocol,Boolean downloadFlag) {
		super();
		this.context = context;
		this.visitBean = visitBean;
		this.contentType = contentType;
		this.httpsProtocol = httpsProtocol;
		this.downloadFlag=downloadFlag;
		if(this.downloadFlag==null){
			downloadFlag=false;
		}
	}



	public void run() {
		while(!context.isFinish()){
			VisitReturn vr=new VisitReturn();
			String condition="";
			switch(contentType){
				case DOMAIN:
					if(StringUtils.isNotBlank(visitBean.getDomain())){
						condition="域名："+visitBean.getDomain();
						vr=HttpsUtils.connection(visitBean.getDomain(),httpsProtocol,downloadFlag);
					}
					break;
				case URL:
					if(StringUtils.isNotBlank(visitBean.getUrl())){
						condition="URL："+visitBean.getUrl();
						vr=HttpsUtils.connection(visitBean.getUrl(),httpsProtocol,downloadFlag);
					}
					break;
				case IPPORT:
					if(StringUtils.isNotBlank(visitBean.getIp())){
						condition="IP："+visitBean.getIp()+",端口："+visitBean.getPort();
						vr=HttpsUtils.connection(visitBean.getIp(),visitBean.getPort(),httpsProtocol,downloadFlag);
					}
					break;
				case DOMAIN_IPPORT:
					if(StringUtils.isNotBlank(visitBean.getDomain())){
						condition="域名："+visitBean.getDomain()+",IP："+visitBean.getIp()+",端口："+visitBean.getPort();
						vr=HttpsUtils.connection(visitBean.getDomain(),visitBean.getIp(),visitBean.getPort(),httpsProtocol,downloadFlag);
					}
					break;
				case URL_IPPORT:
					if(StringUtils.isNotBlank(visitBean.getUrl())){
						condition="URL："+visitBean.getUrl()+",IP:"+visitBean.getIp()+",端口:"+visitBean.getPort();
						vr=HttpsUtils.connection(visitBean.getUrl(),visitBean.getIp(),visitBean.getPort(),httpsProtocol,downloadFlag);
					}
					break;
			default:
				break;
					
			}
			context.collect(vr,condition);
		}
		
	}

}

