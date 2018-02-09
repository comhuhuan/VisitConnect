/**
 * Project Name:VisitConnect
 * File Name:VisitConfig.java
 * Package Name:com.surfilter.tmms
 * Date:2016年2月1日上午9:34:50
 *
*/

package com.surfilter.tmms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.surfilter.tmms.enums.UserAgentTypes;


/**
 * ClassName:VisitConfig <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016年2月1日 上午9:34:50 <br/>
 * @author   huhuan
 * @version
 * @since    JDK 1.6
 * @see
 */
public class VisitConfig {

	private static VisitConfig config;

	private List<Integer> successList=new ArrayList<Integer>();

	private int taskThreadSize=4;

	private String ipGetWebsite="http://www.ip138.com/ips1388.asp";

	private UserAgentTypes UAType=UserAgentTypes.getEnumByValue(2);

	private VisitConfig(){
		super();
	}

	public static VisitConfig getInstance(){
		if(config==null){
			config=new VisitConfig();
			config.getSuccessList().add(200);
			//config.getSuccessList().add(302);
			Properties p=new Properties();
			config.initConfig(p);
		}
		return config;
	}

	public void initConfig(Properties p) {
		config.setTaskThreadSize(getIntValue(p, "taskThreadSize", taskThreadSize));
		config.setIpGetWebsite(getStringValue(p, "ipGetWebsite", ipGetWebsite));
//		String successCodeStr=getStringValue(p, "successCodes", "200,302");
		String successCodeStr=getStringValue(p, "successCodes", "200");
		String [] strs=successCodeStr.split(",");
		if(strs!=null && strs.length>0){
			successList.clear();
			for(String code:strs){
				successList.add(Integer.valueOf(code));
			}
		}

	}

	public Properties loadConfig(String path) throws FileNotFoundException, IOException{
		Properties p=new Properties();
		if(StringUtils.isBlank(path)){
			path =VisitConfig.class.getProtectionDomain().getCodeSource().getLocation().getFile();
			if (!path.endsWith("/")) {
				path = path.substring(0, path.lastIndexOf("/") + 1);
			}
			path=path+"config.properties";
		}
		p.load(new FileInputStream(new File(path)));
		return p;
	}

	public List<Integer> getSuccessList() {
		return successList;
	}

	public void setSuccessList(List<Integer> successList) {
		this.successList = successList;
	}

	public int getTaskThreadSize() {
		return taskThreadSize;
	}

	public void setTaskThreadSize(int taskThreadSize) {
		this.taskThreadSize = taskThreadSize;
	}

	public String getIpGetWebsite() {
		return ipGetWebsite;
	}

	public void setIpGetWebsite(String ipGetWebsite) {
		this.ipGetWebsite = ipGetWebsite;
	}

	public Long getLongValue(Properties p,String key,Long defaultValue){
		Long result=defaultValue;
		String value=p.getProperty(key);
		if(StringUtils.isNotBlank(value)){
			result=Long.valueOf(value);
		}
		return result;
	}

	public Integer getIntValue(Properties p,String key,Integer defaultValue){
		Integer result=defaultValue;
		String value=p.getProperty(key);
		if(StringUtils.isNotBlank(value)){
			result=Integer.valueOf(value);
		}
		return result;
	}

	public String getStringValue(Properties p,String key,String defaultValue){
		return p.getProperty(key, defaultValue);
	}

	public Boolean getIntValue(Properties p,String key,Boolean defaultValue){
		Boolean result=defaultValue;
		String value=p.getProperty(key);
		if(StringUtils.isNotBlank(value)){
			result=Boolean.valueOf(value);
		}
		return result;
	}

	public UserAgentTypes getUAType() {
		return UAType;
	}

	public void setUAType(UserAgentTypes uAType) {
		UAType = uAType;
	}

}

