/**
 * Project Name:visitconnect
 * File Name:VisitChannelResult.java
 * Package Name:com.surfilter.tmms.bean
 * Date:2016年4月14日下午3:17:53
 *
*/

package com.surfilter.tmms.bean;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * ClassName:VisitChannelResult <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016年4月14日 下午3:17:53 <br/>
 * @author   huhuan
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class VisitReturn {
	private int code;
	
	private String html;
	
	private long hashcode;
	
	private String remoteAddress;
	
	private AtomicInteger count=new AtomicInteger(0);

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public long getHashcode() {
		return hashcode;
	}

	public void setHashcode(long hashcode) {
		this.hashcode = hashcode;
	}

	public AtomicInteger getCount() {
		return count;
	}

	public void setCount(AtomicInteger count) {
		this.count = count;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

}

