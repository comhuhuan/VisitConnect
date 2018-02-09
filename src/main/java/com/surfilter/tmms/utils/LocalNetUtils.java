package com.surfilter.tmms.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class LocalNetUtils {
	
	private static Logger logger = Logger.getLogger(LocalNetUtils.class);
	
	private static String[][] privateIpSeg=new String[3][2];
	
	static {
		privateIpSeg[0][0]="10.0.0.0";
		privateIpSeg[0][1]="10.255.255.255";
		privateIpSeg[1][0]="172.16.0.0";
		privateIpSeg[1][1]="172.31.255.255";
		privateIpSeg[2][0]="192.168.0.0";
		privateIpSeg[2][1]="192.168.255.255";
	}

	/**
	 * 获取本地IP
	 * @return
	 */
	public static List<String> getLocalIp(){
		List<String> ips=new ArrayList<String>();
		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = allNetInterfaces.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					ip = addresses.nextElement();
					if (ip != null && ip instanceof Inet4Address) {
						if(!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1){
							ips.add(ip.getHostAddress());
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return ips;
	}
	
	
	public static boolean isPrivate(String ip){
		boolean resultFlag=false;
		long longIp=IPUtils.Ip2Long(ip);
		for(String[] ipSeg:privateIpSeg){
			if(longIp>=IPUtils.Ip2Long(ipSeg[0]) && longIp<=IPUtils.Ip2Long(ipSeg[1])){
				resultFlag=true;
				break;
			}
		}
		return resultFlag;
		
	}
	
	public static String getOnlineIp(String ipWeb) {
		String ip="";
		try {
			URL url = new URL(ipWeb);

			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

			String s = "";

			StringBuffer sb = new StringBuffer("");

			String webContent = "";

			while ((s = br.readLine()) != null) {
				sb.append(s + "\r\n");
			}

			br.close();
			webContent = sb.toString();
			ip = parse(webContent);
		} catch (Exception e) {
			logger.error(e);
		}
		return ip;
	}
	
	private static String parse(String html){
		String ip="";
        Pattern pattern=Pattern.compile("(\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})", Pattern.CASE_INSENSITIVE);   
        Matcher matcher=pattern.matcher(html);       
        while(matcher.find()){
        	ip=matcher.group(0);
        }   
        return ip;
    }   

	
	public static void main(String[] args) {
		System.out.println(getOnlineIp("http://www.ip138.com/ips1388.asp"));
		
		
		if(LocalNetUtils.getLocalIp().size()>0){
			for(String ip:LocalNetUtils.getLocalIp()){
				System.out.println(ip);
			}
		}
		
		System.out.println(isPrivate("192.168.0.1"));
	}
}
