/**
 * Project Name:visitconnect
 * File Name:ThreadBean.java
 * Package Name:com.surfilter.tmms.bean
 * Date:2016年10月10日下午4:02:35
 *
*/

package com.surfilter.tmms.bean;

import java.io.Serializable;

/**
 * 用于封装中止线程的开关
 * ClassName:ThreadFlagBean <br/>
 * Function:  <br/>
 * Reason:	  <br/>
 * Date:     2016年10月10日 下午4:02:35 <br/>
 * @author   huhuan
 * @version  
 * @since    JDK 1.7
 * @see 	 
 */
public class ThreadFlagBean implements Serializable{

	private static final long serialVersionUID = 1L;
	/**
	 * isStop:中止线程的开关  true 中止，false 不中止
	 * @since JDK 1.7
	 */
	private boolean isStop;
	
	public boolean getIsStop() {
		return isStop;
	}
	public void setIsStop(boolean isStop) {
		this.isStop = isStop;
	}
	
	/**
	 * Creates a new instance of ThreadFlagBean.
	 * 默认设置为false（不中止）
	 * @param isStop
	 */
	public ThreadFlagBean() {
		super();
		this.isStop = false;
	}

	
	
	
}

