/**
 * Project Name:VisitConnect
 * File Name:HttpVisitor.java
 * Package Name:com.surfilter.tmms.visit
 * Date:2016年1月4日下午2:13:10
 *
*/

package com.surfilter.tmms.visit.http;


import org.apache.commons.lang3.StringUtils;

import com.surfilter.tmms.VisitContext;
import com.surfilter.tmms.bean.ThreadFlagBean;
import com.surfilter.tmms.bean.VisitBean;
import com.surfilter.tmms.bean.VisitReturn;
import com.surfilter.tmms.enums.ContentTypes;
import com.surfilter.tmms.utils.HttpUtils3;

/**
 * ClassName:HttpVisitor <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016年1月4日 下午2:13:10 <br/>
 * @author   huhuan
 * @version
 * @since    JDK 1.6
 * @see
 */
public class HttpVisitor implements Runnable{

	private VisitContext context;

	private VisitBean visitBean;

	private ContentTypes contentType;

	private Boolean downloadFlag=false;

	private Boolean blockFlag=false;
	
	private ThreadFlagBean flag;


	@SuppressWarnings("unused")
	private HttpVisitor(VisitContext context, VisitBean visitBean,ContentTypes contentType) {
		super();
		this.context = context;
		this.visitBean = visitBean;
		this.contentType = contentType;
	}

	public HttpVisitor(VisitContext context, VisitBean visitBean, ContentTypes contentType,Boolean downloadFlag) {
		super();
		this.context = context;
		this.visitBean = visitBean;
		this.contentType = contentType;
		this.downloadFlag=downloadFlag;
		if(this.downloadFlag==null){
			downloadFlag=false;
		}
	}

	public HttpVisitor(VisitContext context, VisitBean visitBean, ContentTypes contentType,Boolean downloadFlag,Boolean blockFlag) {
		super();
		this.context = context;
		this.visitBean = visitBean;
		this.contentType = contentType;
		this.downloadFlag=downloadFlag;
		if(this.downloadFlag==null){
			downloadFlag=false;
		}
		this.blockFlag=blockFlag;
		if(this.blockFlag==null){
			this.blockFlag=false;
		}
	}
	
	public HttpVisitor(VisitContext context, VisitBean visitBean, ContentTypes contentType,Boolean downloadFlag,Boolean blockFlag,ThreadFlagBean flag) {
		super();
		this.flag = flag;
		this.context = context;
		this.visitBean = visitBean;
		this.contentType = contentType;
		this.downloadFlag=downloadFlag;
		if(this.downloadFlag==null){
			downloadFlag=false;
		}
		this.blockFlag=blockFlag;
		if(this.blockFlag==null){
			this.blockFlag=false;
		}
	}
	public void run() {
		if(flag==null){
			flag=new ThreadFlagBean();
		}
		while(!context.isFinish() && !flag.getIsStop()){//flag.getIsStop()：通过StrategyService中的全局变量strategyMap来维护线程中止开关，在线程执行过程中设置为true来中止线程
			VisitReturn vr=new VisitReturn();
			String condition="";
			switch(contentType){
				case DOMAIN:
					if(StringUtils.isNotBlank(visitBean.getDomain())){
						condition="域名："+visitBean.getDomain();
						vr=HttpUtils3.connection(visitBean.getDomain(),downloadFlag);
					}
					break;
				case URL:
					if(StringUtils.isNotBlank(visitBean.getUrl())){
						condition="URL："+visitBean.getUrl();
						vr=HttpUtils3.connection(visitBean.getUrl(),downloadFlag);
					}
					break;
				case IPPORT:
					if(StringUtils.isNotBlank(visitBean.getIp())){
						condition="IP："+visitBean.getIp()+",端口："+visitBean.getPort();
						vr=HttpUtils3.connection(visitBean.getIp(),visitBean.getPort(),downloadFlag);
					}
					break;
				case DOMAIN_IPPORT:
					if(StringUtils.isNotBlank(visitBean.getDomain())){
						condition="域名："+visitBean.getDomain()+",IP："+visitBean.getIp()+",端口："+visitBean.getPort();
						vr=HttpUtils3.connection(visitBean.getDomain(),visitBean.getIp(),visitBean.getPort(),downloadFlag);
					}
					break;
				case URL_IPPORT:
					if(StringUtils.isNotBlank(visitBean.getUrl())){
						condition="URL："+visitBean.getUrl()+",IP:"+visitBean.getIp()+",端口:"+visitBean.getPort();
						vr=HttpUtils3.connection(visitBean.getUrl(),visitBean.getIp(),visitBean.getPort(),downloadFlag);
					}
					break;
			default:
				break;
			}
			context.collect(vr,condition);
		}

	}


}

