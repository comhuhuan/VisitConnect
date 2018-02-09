package com.surfilter.tmms.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.Proxy.Type;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.surfilter.tmms.VisitConfig;
import com.surfilter.tmms.bean.VisitReturn;


public class HttpsUtils {
	private static class TrustAnyTrustManager implements X509TrustManager {
		public void checkClientTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
			
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}
	
	private static class TrustAnyHostnameVerifier implements HostnameVerifier {

		public boolean verify(String arg0, SSLSession arg1) {
			return true;
		}
		
	}
	
	private static Logger logger = Logger.getLogger(HttpsUtils.class);
	
	private static final int CONNECTION_TIMEOUT=2000;//10s
	private static final int READ_TIMEOUT=3000;//10s
	
	
	/**
	 * 
	 * httpsConnection:HTTPS方式访问连接
	 *
	 * @author Administrator
	 * @param url url链接地址
	 * @param potocol 协议, 为空时默认为SSL
	 * @return
	 * @since JDK 1.6
	 */
	public static VisitReturn connection(String url,String potocol,Boolean downloadFlag){
		VisitReturn visitReturn =new VisitReturn();
		visitReturn.setCode(0);
		HttpsURLConnection connect=null;
		try {
			if(downloadFlag==null){
				downloadFlag=false;
			}
			
			if(!url.startsWith("https://") && !url.startsWith("HTTPS://")){
				url="https://"+url;
			}
			if(StringUtils.isBlank(potocol)){
				potocol="SSL";
			}
			URL u=new URL(url);
			SSLContext ctx = SSLContext.getInstance(potocol);  
            ctx.init(new KeyManager[0], new TrustManager[] {new TrustAnyTrustManager()}, new SecureRandom());  
            SSLContext.setDefault(ctx);  
            
            connect=(HttpsURLConnection) u.openConnection();
 			connect.setSSLSocketFactory(ctx.getSocketFactory());
            connect.setHostnameVerifier(new TrustAnyHostnameVerifier());
//            connect.setDoOutput(true);
            
            connect.setConnectTimeout(CONNECTION_TIMEOUT);
            connect.setReadTimeout(READ_TIMEOUT);
            connect.setRequestProperty("Connection","close");
 			connect.setRequestMethod("GET");
 			connect.setInstanceFollowRedirects(false);
 			HttpUtils3.setUserAgent(connect, VisitConfig.getInstance().getUAType());
			connect.connect();
			
			visitReturn.setCode(connect.getResponseCode());
			hasDownload(downloadFlag, connect, visitReturn);
		} catch (Exception e) {
			logger.error(e);
		} finally{
			if(connect!=null){
				connect.disconnect();
			}
		}
		return visitReturn;
	}
	
	/**
	 * 通过IP和端口访问https请求
	 * @param ip
	 * @param port
	 * 
	 * @return
	 */
	public static VisitReturn connection(String ip,int port,String potocol,Boolean downloadFlag){
		HttpsURLConnection connect=null;
		VisitReturn visitReturn =new VisitReturn();
		visitReturn.setCode(0);
		try {
			if(downloadFlag==null){
				downloadFlag=false;
			}
			
			if(port==0){
				port=80;
			}
			if(StringUtils.isBlank(ip)){
				visitReturn.setCode(-1);
				return visitReturn;
			}
			String url="https://"+ip+":"+port;
			
			URL u=new URL(url);
			
			if(StringUtils.isBlank(potocol)){
				potocol="SSL";
			}
			SSLContext ctx = SSLContext.getInstance(potocol);  
            ctx.init(new KeyManager[0], new TrustManager[] {new TrustAnyTrustManager()}, new SecureRandom());  
            SSLContext.setDefault(ctx);  
			
			connect=(HttpsURLConnection) u.openConnection();
			connect.setSSLSocketFactory(ctx.getSocketFactory());
            connect.setHostnameVerifier(new TrustAnyHostnameVerifier());
            connect.setDoOutput(true);
			
			connect.setConnectTimeout(CONNECTION_TIMEOUT);
			connect.setReadTimeout(READ_TIMEOUT);
			connect.setRequestProperty("Connection","close");
			connect.setRequestMethod("GET");
			HttpUtils3.setUserAgent(connect, VisitConfig.getInstance().getUAType());

			visitReturn.setCode(connect.getResponseCode());
			hasDownload(downloadFlag, connect, visitReturn);
			
		} catch (Exception e) {
			logger.error(e);
		} finally{
			if(connect!=null){
				connect.disconnect();
			}
		}
		return visitReturn;
	}
	/**
	 *  通过url和目的IP访问https请求，默认端口为80
	 * @param url
	 * @param ip
	 * @return
	 */
	public static VisitReturn connection(String url,String ip,String potocol,Boolean downloadFlag){
		return connection(url,ip,443,potocol,downloadFlag);
	}
	/**
	 * 通过url 和目的IP 端口访问https请求
	 * @param url
	 * @param ip
	 * @param port 默认为80
	 * @return
	 */
	public static VisitReturn connection(String url,String ip,int port,String potocol,Boolean downloadFlag){
		HttpsURLConnection connect=null;
		VisitReturn visitReturn =new VisitReturn();
		visitReturn.setCode(0);
		try {
			if(downloadFlag==null){
				downloadFlag=false;
			}
			
			if(!url.startsWith("https://") && !url.startsWith("HTTPS://")){
				url="https://"+url;
			}
			
			URL u=new URL(url);
			if(port ==0){
				port=u.getDefaultPort();
			}
			if(StringUtils.isBlank(ip)){
				visitReturn.setCode(-1);
				return visitReturn;
			}
			String str[] = ip.split("\\.");  
            byte[] b =new byte[str.length];  
            for(int i=0,len=str.length;i<len;i++){  
                b[i] = (byte)(Integer.parseInt(str[i],10));  
            }  
            
			Proxy proxy=new Proxy(Type.HTTP, new InetSocketAddress(InetAddress.getByAddress(b), port));

			if(StringUtils.isBlank(potocol)){
				potocol="SSL";
			}
			SSLContext ctx = SSLContext.getInstance(potocol);  
            ctx.init(new KeyManager[0], new TrustManager[] {new TrustAnyTrustManager()}, new SecureRandom());  
            SSLContext.setDefault(ctx);  
			connect=(HttpsURLConnection) u.openConnection(proxy);
			connect.setSSLSocketFactory(ctx.getSocketFactory());
            connect.setHostnameVerifier(new TrustAnyHostnameVerifier());
            connect.setDoOutput(true);
			
			connect.setConnectTimeout(CONNECTION_TIMEOUT);
			connect.setReadTimeout(READ_TIMEOUT);
			connect.setRequestProperty("Connection","close");
			connect.setRequestMethod("GET");
			HttpUtils3.setUserAgent(connect, VisitConfig.getInstance().getUAType());
			
			visitReturn.setCode(connect.getResponseCode());
			hasDownload(downloadFlag, connect, visitReturn);
			
		} catch (Exception e) {
			logger.error(e);
		} finally{
			if(connect!=null){
				connect.disconnect();
			}
		}
		return visitReturn;
	}
	
	public static String getContent(URLConnection connect){
		InputStream is=null;
		InputStreamReader streamReader=null;
		BufferedReader reader=null;
		StringBuffer str=new StringBuffer();
		try {
			is=connect.getInputStream();
			streamReader=new InputStreamReader(is);
			reader=new BufferedReader(streamReader);
			
			String readLine="";
			while((readLine=reader.readLine())!=null){
				str.append(readLine+"\n");
			}
			return str.toString();
		} catch (IOException e) {
			return str.toString();
		}finally{
			try {
				if(reader!=null){
					reader.close();
				}
				if(streamReader!=null){
					streamReader.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}	
	
	
	public static void hasDownload(Boolean downloadFlag,HttpURLConnection connect, VisitReturn visitReturn) {
		if(downloadFlag){
			String html= getContent(connect);
			if(html!=null){
				visitReturn.setHtml(html);
				visitReturn.setHashcode(html.hashCode());
			}else{
				visitReturn.setHtml("");
				visitReturn.setHashcode(0);
			}
		}
	}
	
	public static void main(String[] args) {
		
		try {
			System.out.println(HttpsUtils.connection("kyfw.12306.cn/otn/leftTicket/init","SSL",true));
			InetAddress[] ias=InetAddress.getAllByName("kyfw.12306.cn");
			for(InetAddress ia:ias){
				System.out.println(ia.getHostAddress());
			}
			
			System.out.println(HttpsUtils.connection("183.134.28.63", 80,"SSL",true));
//			System.out.println(HttpsUtils.httpsConnection("www.12306.cn","222.184.34.54", 80,"SSL"));
		} catch (Exception e) {
		}
		
	}
}
