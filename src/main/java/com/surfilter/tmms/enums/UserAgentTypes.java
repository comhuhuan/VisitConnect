/**
 * Project Name:VisitConnect
 * File Name:UserAgentTypes.java
 * Package Name:com.surfilter.tmms.enums
 * Date:2016年2月17日上午10:39:11
 *
*/

package com.surfilter.tmms.enums;
/**
 * ClassName:UserAgentTypes <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016年2月17日 上午10:39:11 <br/>
 * @author   huhuan
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public enum UserAgentTypes {
	IE_Desktop(1),
	Chrome_Desktop(2),
	FireFox_Desktop(3),
	IOS_Safari(4),
	UCWeb_mobile(5),
	Android_mobile(6);

	private int type;
	
	private UserAgentTypes(int type) {
		this.type = type;
	}

	public static UserAgentTypes getEnumByValue(int findType){
		UserAgentTypes UAType=null;
		for(UserAgentTypes uaType :UserAgentTypes.values()){
			if(uaType.getType()==findType){
				UAType=uaType;
				break;
			}
		}
		return UAType;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	
}

