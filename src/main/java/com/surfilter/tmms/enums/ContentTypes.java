/**
 * Project Name:VisitConnect
 * File Name:VisitContentTypes.java
 * Package Name:com.surfilter.tmms.bean
 * Date:2016年1月4日下午2:29:52
 *
*/

package com.surfilter.tmms.enums;
/**
 * ClassName:VisitContentTypes <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016年1月4日 下午2:29:52 <br/>
 * @author   huhuan
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public enum ContentTypes {
	DOMAIN(1,"域名"),
	URL(2,"url"),
	IPPORT(3,"IP端口"),
	DOMAIN_IPPORT(4,"指定IP端口访问域名"),
	URL_IPPORT(5,"指定IP端口访问URL"),
	SMTP(6,"SMTP发信"),
	POP(7,"POP3收信"),
	IMAP(8,"IMAP收信"),
	SMTPS(9,"SMTPS发信"),
	IMAPS(10,"IMAPS收信"),
	DNS(11,"DNS");
	
	private Integer type;
	
	private String descript;

	private ContentTypes(Integer type, String descript) {
		this.type = type;
		this.descript = descript;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getDescript() {
		return descript;
	}

	public void setDescript(String descript) {
		this.descript = descript;
	}
	
	
}

