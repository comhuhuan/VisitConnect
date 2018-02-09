package com.surfilter.tmms.utils;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpHost;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.log4j.Logger;

import com.surfilter.tmms.VisitConfig;
import com.surfilter.tmms.bean.VisitReturn;
import com.surfilter.tmms.enums.UserAgentTypes;


public class HttpUtils2 {
	
	private static Logger logger = Logger.getLogger(HttpUtils2.class);
	
	private static final int CONNECTION_TIMEOUT=3000;//10s
	private static final int READ_TIMEOUT=3000;//10s
	
	/**
	 * 通过url访问地址
	 * @param url
	 * @return
	 */
	public static VisitReturn connection(String url,Boolean downloadFlag){
		VisitReturn visitReturn =new VisitReturn();
		visitReturn.setCode(0);
		HttpMethod method = null;
		HttpClient client = null;
		try {
			if(downloadFlag==null){
				downloadFlag=false;
			}
			
			if(!url.startsWith("http://") && !url.startsWith("HTTP://")){
				url="http://"+url;
			}
			client = new HttpClient();
			client.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
			client.getHttpConnectionManager().getParams().setSoTimeout(READ_TIMEOUT);
			client.getParams().setParameter(HttpMethodParams.USER_AGENT, getUserAgent(VisitConfig.getInstance().getUAType()));
			method = new GetMethod(url);
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
		    		new DefaultHttpMethodRetryHandler(0, false));
			method.setFollowRedirects(false);
			int statusCode = client.executeMethod(method);
			visitReturn.setCode(statusCode);
			if(statusCode>=200 && statusCode<500){
				hasDownload(downloadFlag, method, visitReturn);
			}else{
				visitReturn.setHtml("");
				visitReturn.setHashcode(0);
			}
			return visitReturn;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return visitReturn;
		} finally{
			if(method!=null){
				method.releaseConnection();
			}
		}
	}


	/**
	 * @param downloadFlag
	 * @param method
	 * @param visitReturn
	 * @desc 下载拨测页面
	 */
	public static void hasDownload(Boolean downloadFlag,HttpMethod method, VisitReturn visitReturn) {
		if(downloadFlag){
			String html = null;
			try {
				html = method.getResponseBodyAsString();
				if(html!=null){
					visitReturn.setHtml(html);
					visitReturn.setHashcode(html.hashCode());
				}else{
					visitReturn.setHtml("");
					visitReturn.setHashcode(0);
				}
			} catch (IOException e) {
				logger.error(e);
				visitReturn.setHtml("");
				visitReturn.setHashcode(0);
			}
			
		}
	}


	
	/**
	 * 通过IP和端口访问http请求
	 * @param ip
	 * @param port
	 * @return
	 */
	public static VisitReturn connection(String ip,int port,Boolean downloadFlag){
		VisitReturn visitReturn =new VisitReturn();
		visitReturn.setCode(0);
		HttpMethod method = null;
		HttpClient client = null;
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
			String url="http://"+ip+":"+port;
			client = new HttpClient();
			client.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
			client.getHttpConnectionManager().getParams().setSoTimeout(READ_TIMEOUT);
			client.getParams().setParameter(HttpMethodParams.USER_AGENT, getUserAgent(VisitConfig.getInstance().getUAType()));
			method = new GetMethod(url);
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
		    		new DefaultHttpMethodRetryHandler(0, false));
			method.setFollowRedirects(false);
			int statusCode = client.executeMethod(method);
			visitReturn.setCode(statusCode);
			if(statusCode>=200 && statusCode<500){
				hasDownload(downloadFlag, method, visitReturn);
			}else{
				visitReturn.setHtml("");
				visitReturn.setHashcode(0);
			}
			return visitReturn;
		} catch (Exception e) {
			logger.error(e);
			return visitReturn;
		} finally{
			if(method!=null){
				method.releaseConnection();
			}
		}
	}
	/**
	 *  通过url和目的IP访问http请求，默认端口为80
	 * @param url
	 * @param ip
	 * @return
	 */
	public static VisitReturn connection(String url,String ip,Boolean downloadFlag){
		if(downloadFlag==null){
			downloadFlag=false;
		}
		return connection(url,ip,80,downloadFlag);
	}
	/**
	 * 通过url 和目的IP 端口访问http请求
	 * @param url
	 * @param ip
	 * @param port 默认为80
	 * @return
	 */
	public static VisitReturn connection(String url,String ip,int port,Boolean downloadFlag){
		VisitReturn visitReturn =new VisitReturn();
		visitReturn.setCode(0);
		HttpMethod method = null;
		HttpClient client = null;
		try {
			if(downloadFlag==null){
				downloadFlag=false;
			}
			if(!url.startsWith("http://") && !url.startsWith("HTTP://")){
				url="http://"+url;
			}
			if(port ==0){
				port=80;
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
            client = new HttpClient();
			client.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
			client.getHttpConnectionManager().getParams().setSoTimeout(READ_TIMEOUT);
			client.getParams().setParameter(HttpMethodParams.USER_AGENT, getUserAgent(VisitConfig.getInstance().getUAType()));
			//client.getParams().setParameter(ConnRouteParams.LOCAL_ADDRESS, InetAddress.getByAddress(b));
			HttpHost proxy = new HttpHost(ip,port);
			client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
			method = new GetMethod(url);
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
		    		new DefaultHttpMethodRetryHandler(0, false));
			method.setFollowRedirects(false);
			int statusCode = client.executeMethod(method);
			visitReturn.setCode(statusCode);
			if(statusCode>=200 && statusCode<500){
				hasDownload(downloadFlag, method, visitReturn);
			}else{
				visitReturn.setHtml("");
				visitReturn.setHashcode(0);
			}
            
            
            
		/**
		 * URL u = new URL(url);
			Proxy proxy=new Proxy(Type.HTTP, new InetSocketAddress(InetAddress.getByAddress(b), port));
			connect=(HttpURLConnection) u.openConnection(proxy);
			connect.setConnectTimeout(CONNECTION_TIMEOUT);
			connect.setReadTimeout(READ_TIMEOUT);
			connect.setRequestProperty("Connection","close");
			setUserAgent(connect,VisitConfig.getInstance().getUAType());
			connect.setRequestMethod("GET");
			connect.setUseCaches(false);
			visitReturn.setCode(connect.getResponseCode());
			hasDownload(downloadFlag, connect, visitReturn);*/
			return visitReturn;
		} catch (Exception e) {
			logger.error(e);
			return visitReturn;
		} finally{
			if(method!=null){
				method.releaseConnection();
			}
		}
	}
	public static String getContent(URLConnection connect){
		return null;
	}
	
	/*public static String getContent(URLConnection connect){
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
	}*/
	
	
	public static void headerMsg(HttpURLConnection connect){
		Map<String,List<String>> map=connect.getHeaderFields();
		Iterator<Entry<String, List<String>>>  it=map.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, List<String>> entry=it.next();
			System.out.print(entry.getKey()+":");
			if(entry.getValue()!=null){
				for(String str:entry.getValue()){
					System.out.print(str+"\t");
				}
			}
			System.out.println();
		}
	}
	
//	public static void main(String[] args) {
//		long startTime=System.currentTimeMillis();
//		VisitReturn vr=HttpUtils.connection("http://www.sina.com.cn/",false);
//		long endTime=System.currentTimeMillis();
//		System.out.println(vr.getHtml());
//		System.out.println(endTime-startTime);
//		System.out.println(vr.getHtml().length());
//		System.out.println(vr.getCode());
//		/*try {
//			InetAddress[] ias=InetAddress.getAllByName("www.1218.com.cn");
//			for(InetAddress ia:ias){
//				System.out.println(ia.getHostAddress());
//			}
//			VisitConfig.getInstance().setUAType(UserAgentTypes.getEnumByValue(7));
//			System.out.println(HttpUtils.connection("jingyan.baidu.com","220.181.163.54", 80));
//			
//			
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//		}*/
//		
//	}
	
	
	public static String getUserAgent(UserAgentTypes userAgent) {
		String uAget = "";
		if(userAgent!=null){
			switch(userAgent){
				case IE_Desktop:
					uAget="Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0)";
					break;
				case Chrome_Desktop:
					uAget ="Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36"; 
					break;
				case FireFox_Desktop:
					uAget = "Mozilla/5.0 (Windows NT 6.1; rv:2.0.1) Gecko/20100101 Firefox/4.0.1";
					break;
				case IOS_Safari:
					uAget = "Mozilla/5.0 (iPhone; U; CPU like Mac OS X) AppleWebKit/420.1 (KHTML, like Gecko) Version/3.0 Mobile/4A93 Safari/419.3";
					break;
				case UCWeb_mobile:
					uAget = "Mozilla/4.0 (compatible; MSIE 6.0; ) Opera/UCWEB7.0.2.37/28/999";
					break;
				case Android_mobile:
					uAget = "Mozilla/5.0 (Linux; U; Android 3.0; en-us; Xoom Build/HRI39) AppleWebKit/534.13 (KHTML, like Gecko) Version/4.0 Safari/534.13";
					break;
				default:
					uAget = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36";
					break;
			}
		}
		return uAget;
	}
	
	
}
