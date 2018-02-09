/**
 * Project Name:VisitConnect
 * File Name:VisitResult.java
 * Package Name:com.surfilter.tmms.bean
 * Date:2016年1月4日下午2:40:59
 *
*/

package com.surfilter.tmms.bean;

import java.util.Date;

/**
 * ClassName:VisitResult <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016年1月4日 下午2:40:59 <br/>
 * @author   huhuan
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class VisitResult {
	/**
	 * 拨测任务的ID
	 */
	private String taskId;
	private Long successTime=0L;
	private Long failTime=0L;
	private String localIp="";
	private String netOutIP;
	private Date startTime;
	private Date endTime;
	
	
	
	public VisitResult(){
		super();
		this.localIp="";
	}
	public String getTaskId() {
		return taskId;
	}
	public Long getSuccessTime() {
		return successTime;
	}
	public void setSuccessTime(Long successTime) {
		this.successTime = successTime;
	}
	public Long getFailTime() {
		return failTime;
	}
	public void setFailTime(Long failTime) {
		this.failTime = failTime;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getLocalIp() {
		return localIp;
	}
	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}
	public String getNetOutIP() {
		return netOutIP;
	}
	public void setNetOutIP(String netOutIP) {
		this.netOutIP = netOutIP;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
}

