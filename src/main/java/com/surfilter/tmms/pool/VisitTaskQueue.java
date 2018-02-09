/**
 * Project Name:VisitConnect
 * File Name:VisitTaskQueue.java
 * Package Name:com.surfilter.tmms.pool
 * Date:2016年1月4日下午3:27:14
 *
*/

package com.surfilter.tmms.pool;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.surfilter.tmms.bean.VisitBean;

/**
 * ClassName:VisitTaskQueue <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016年1月4日 下午3:27:14 <br/>
 * @author   huhuan
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class VisitTaskQueue {
	
	private static VisitTaskQueue visitTaskQueue;
	
	private static ConcurrentLinkedQueue<VisitBean> preHandleQueue=null;
	
	private VisitTaskQueue(){
		super();
	}

	public static synchronized VisitTaskQueue getInstance(){
		if(visitTaskQueue==null){
			visitTaskQueue=new VisitTaskQueue();
			preHandleQueue=new ConcurrentLinkedQueue<VisitBean>();
		}
		return null;
	}
	
	public static ConcurrentLinkedQueue<VisitBean> getQueue(){
		return preHandleQueue;
	}
	
}

