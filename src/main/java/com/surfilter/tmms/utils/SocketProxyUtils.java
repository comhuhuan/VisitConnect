package com.surfilter.tmms.utils;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Socket;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.surfilter.tmms.VisitConfig;
import com.surfilter.tmms.bean.VisitReturn;

public class SocketProxyUtils {
	
	private static Logger logger =Logger.getLogger(SocketProxyUtils.class);
	
	public static VisitReturn connection(String domainUrl,String proxyHost,int proxyPort,final String username,final String password,Boolean downloadFlag){
		Socket socket =null;
		VisitReturn visitReturn =new VisitReturn();
		visitReturn.setCode(0);
		try {
			 Authenticator.setDefault(new Authenticator(){
		            protected  PasswordAuthentication  getPasswordAuthentication(){
		                return  new PasswordAuthentication(username, password.toCharArray());
		            }
		        });
			 Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost, proxyPort));
		     socket = new Socket(proxy);
		     if(!domainUrl.startsWith("http://") && !domainUrl.startsWith("HTTP://")){
		    	 domainUrl="http://"+domainUrl;
		     }
		     URL url=new URL(domainUrl);
		     socket.connect(new InetSocketAddress(url.getHost(),url.getDefaultPort()));
		     visitReturn = SocketUtils.request(socket, url,VisitConfig.getInstance().getUAType(),downloadFlag);
		} catch (Exception e) {
			logger.error(e);
		}finally{
			try {
				if(socket!=null){
					socket.close();
				}
			} catch (IOException e) {
			}
		}
		
		return visitReturn;
	}
	
	public static VisitReturn connection(String ip,int port,String proxyHost,int proxyPort,final String username,final String password,Boolean downloadFlag){
		Socket socket =null;
		VisitReturn visitReturn =new VisitReturn();
		visitReturn.setCode(0);
		try {
			if(StringUtils.isBlank(ip)){
				return visitReturn;
			}
			if(port<=0 && port>65535){
				port=80;
			}
			 Authenticator.setDefault(new Authenticator(){
		            protected  PasswordAuthentication  getPasswordAuthentication(){
		                return  new PasswordAuthentication(username, password.toCharArray());
		            }
		        });
			 Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost, proxyPort));
		     socket = new Socket(proxy);
		     String domainUrl="http://"+ip+":"+port;
		     URL url=new URL(domainUrl);
		     socket.connect(new InetSocketAddress(ip,port));
		     visitReturn = SocketUtils.request(socket, url,VisitConfig.getInstance().getUAType(),downloadFlag);
		} catch (Exception e) {
			logger.error(e);
		}finally{
			try {
				if(socket!=null){
					socket.close();
				}
			} catch (IOException e) {
			}
		}
		
		return visitReturn;
	}
	
	public static VisitReturn connection(String domainUrl,String ip,int port,String proxyHost,int proxyPort,final String username,final String password,Boolean downloadFlag){
		Socket socket =null;
		VisitReturn visitReturn =new VisitReturn();
		visitReturn.setCode(0);
		try {
			if(StringUtils.isBlank(ip)){
				return visitReturn;
			}
			if(port<=0 && port>65535){
				port=80;
			}
			 Authenticator.setDefault(new Authenticator(){
		            protected  PasswordAuthentication  getPasswordAuthentication(){
		                return  new PasswordAuthentication(username, password.toCharArray());
		            }
		        });
			 Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost, proxyPort));
		     socket = new Socket(proxy);
		     if(!domainUrl.startsWith("http://") && !domainUrl.startsWith("HTTP://")){
		    	 domainUrl="http://"+domainUrl;
		     }
		     URL url=new URL(domainUrl);
		     socket.connect(new InetSocketAddress(ip,port));
		     visitReturn = SocketUtils.request(socket, url,VisitConfig.getInstance().getUAType(),downloadFlag);
		} catch (Exception e) {
			logger.error(e);
		}finally{
			try {
				if(socket!=null){
					socket.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
		
		return visitReturn;
	}
	
	
}
