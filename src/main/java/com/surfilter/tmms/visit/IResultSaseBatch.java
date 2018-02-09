/**
 * Project Name:VisitConnect
 * File Name:IResultSaseBatch.java
 * Package Name:com.surfilter.tmms.visit
 * Date:2016年2月1日上午9:15:15
 *
*/

package com.surfilter.tmms.visit;

import java.util.List;

import com.surfilter.tmms.bean.VisitResult;

/**
 * ClassName:IResultSaseBatch <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016年2月1日 上午9:15:15 <br/>
 * @author   huhuan
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public interface IResultSaseBatch {
	public void save(List<VisitResult> results);
}

