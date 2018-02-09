/**
 * Project Name:VisitConnect
 * File Name:HttpVisitor.java
 * Package Name:com.surfilter.tmms.visit
 * Date:2016年1月4日下午2:13:10
 *
*/

package com.surfilter.tmms.visit.mail;
import com.surfilter.tmms.VisitContext;
import com.surfilter.tmms.bean.VisitBean;
import com.surfilter.tmms.enums.ContentTypes;
import com.surfilter.tmms.utils.MailUtil;

/**
 * ClassName:MailVisitor <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016年2月17日 下午2:13:10 <br/>
 * @author   huhuan
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class MailVisitor implements Runnable{

	private VisitContext context;
	
	private VisitBean visitBean;
	
	private ContentTypes contentType;
	
	public MailVisitor(VisitContext context, VisitBean visitBean, ContentTypes contentType) {
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
				case SMTP: //"SMTP发信"
				    statusCode= MailUtil.getConnectSmtp(visitBean.getProxyUserName(), visitBean.getProxyPassword(),visitBean.getIp(),visitBean.getPort()+"");
					break;
				case POP: //"POP3收信"
					statusCode= MailUtil.getConnectPop3(visitBean.getProxyUserName(), visitBean.getProxyPassword(),visitBean.getIp(),visitBean.getPort()+"");
					break;
				case IMAP:  //IMAP收信
					statusCode= MailUtil.getConnectImap(visitBean.getProxyUserName(), visitBean.getProxyPassword(),visitBean.getIp(),visitBean.getPort()+"");
					break;
				case SMTPS: //SMTPS发信
					statusCode= MailUtil.getConnectSmtp(visitBean.getProxyUserName(), visitBean.getProxyPassword(),visitBean.getIp(),visitBean.getPort()+"");
					break;
				case IMAPS://IMAPS收信
					statusCode= MailUtil.getConnectImap(visitBean.getProxyUserName(), visitBean.getProxyPassword(),visitBean.getIp(),visitBean.getPort()+"");
					break;
			default:
				break;
			}
			context.collect(statusCode,condition);
		}
		
	}
}

