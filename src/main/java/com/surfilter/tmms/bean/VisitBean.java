/**
 * Project Name:VisitConnect
 * File Name:VisitBean.java
 * Package Name:com.surfilter.tmms.bean
 * Date:2016年1月4日下午2:27:06
 *
*/

package com.surfilter.tmms.bean;

import java.util.UUID;

/**
 * ClassName:VisitBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016年1月4日 下午2:27:06 <br/>
 * @author   huhuan
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class VisitBean {
	/**
	 * 拨测任务Id
	 */
	private String taskId;
	/**
	 * 域名
	 */
	private String domain;
	/**
	 * url
	 */
	private String url;
	/**
	 * ip
	 */
	private String ip;
	/**
	 * 端口
	 */
	private Integer port;
	/**
	 * 代理IP
	 */
	private String proxyIp;
	/**
	 * 代理端口
	 */
	private Integer proxyPort;
	/**
	 * 代理账户用户名
	 */
	private String proxyUserName;
	/**
	 * 代理账户密码
	 */
	private String proxyPassword;
	/**
	 * 发送内容
	 */
	private String content;
	
	/**
	 * 用于拨测工具，用来记录需要拨测的次数
	 */
	private Long totalTimes;
	
	/**
	 * 拨测类型
	 */
	private Integer contentType;

	/**
	 * 是否过滤
	 * 域名已封堵为true，默认为false
	 */
	private Boolean blockFlag=false;
	/**
	 * @return 拨测类型
	 */
	public Integer getContentType() {
		return contentType;
	}

	/**
	 * @param 拨测类型
	 */
	public void setContentType(Integer contentType) {
		this.contentType = contentType;
	}

	/**
	 * @return  用于拨测工具，用来记录需要拨测的次数
	 */
	public Long getTotalTimes() {
		return totalTimes;
	}

	/**
	 * @param  用于拨测工具，用来记录需要拨测的次数
	 */
	public void setTotalTimes(Long totalTimes) {
		this.totalTimes = totalTimes;
	}

	public VisitBean() {
		super();
		this.taskId=UUID.randomUUID().toString();
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getProxyIp() {
		return proxyIp;
	}

	public void setProxyIp(String proxyIp) {
		this.proxyIp = proxyIp;
	}

	public Integer getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(Integer proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyUserName() {
		return proxyUserName;
	}

	public void setProxyUserName(String proxyUserName) {
		this.proxyUserName = proxyUserName;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Boolean getBlockFlag() {
		return blockFlag;
	}

	public void setBlockFlag(Boolean blockFlag) {
		this.blockFlag = blockFlag;
	}

	
}

