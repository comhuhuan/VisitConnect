package com.surfilter.tmms.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.surfilter.tmms.VisitConfig;
import com.surfilter.tmms.bean.VisitReturn;
import com.surfilter.tmms.enums.UserAgentTypes;

public class SocketUtils {

	private static Logger logger = Logger.getLogger(SocketUtils.class);
	
	private static int READ_TIMEOUT=3000;
	/**
	 *TCP 请求http连接
	 *
	 * @param domainUrl 域名或url 请求的地址
	 * 
	 */
	public static VisitReturn connection(String domainUrl,boolean downloadFlag) {
		Socket socket = new Socket();
		InputStream inputStream = null;
		PrintWriter printWriter =null;
		VisitReturn visitReturn =new VisitReturn();
		visitReturn.setCode(0);
		try {
			socket.setSoTimeout(READ_TIMEOUT);
			if(!domainUrl.startsWith("http://") && !domainUrl.startsWith("HTTP://")){
				domainUrl="http://"+domainUrl;
			}
			URL url = new URL(domainUrl);
			SocketAddress dest = new InetSocketAddress(url.getHost(), url.getDefaultPort());
			socket.connect(dest);

			visitReturn = request(socket, url,VisitConfig.getInstance().getUAType(),downloadFlag);
			return visitReturn;
		} catch (Exception e) {
			logger.error(e);
			return visitReturn;
		} finally {
			try {
				if(printWriter!=null){
					printWriter.close();
				}
				if(inputStream!=null){
					inputStream.close();
				}
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				logger.error(e);
			}
		}

	}
	
	/**
	 *TCP 请求http连接
	 *
	 * @param domainUrl 域名或url 请求的地址
	 * 
	 */
	public static VisitReturn connection(String hostIp,int port,boolean downloadFlag) {
		Socket socket = new Socket();
		InputStream inputStream = null;
		PrintWriter printWriter =null;
		VisitReturn visitReturn =new VisitReturn();
		visitReturn.setCode(0);
		try {
			socket.setSoTimeout(READ_TIMEOUT);
			if(port<=0 && port>65535){
				port=80;
			}
			String urlDomain="http://"+hostIp+":"+port+"/";
			URL url=new URL(urlDomain);
			SocketAddress dest = new InetSocketAddress(url.getHost(),url.getPort());
			socket.connect(dest);
			
			visitReturn = request(socket, url,VisitConfig.getInstance().getUAType(),downloadFlag);
			return visitReturn;
		} catch (Exception e) {
			logger.error(e);
			return visitReturn;
		} finally {
			try {
				if(printWriter!=null){
					printWriter.close();
				}
				if(inputStream!=null){
					inputStream.close();
				}
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				logger.error(e);
			}
		}

	}
	
	/**
	 * TCP请求HTTP地址
	 * 
	 * @param domainUrl 连接
	 * @param hostIp 主机地址
	 * @param port 端口
	 * 
	 */
	public static VisitReturn connection(String domainUrl,String hostIp,int port,boolean downloadFlag) {
		Socket socket = new Socket();
		VisitReturn visitReturn =new VisitReturn();
		visitReturn.setCode(0);
		try {
			socket.setSoTimeout(READ_TIMEOUT);
			if(!domainUrl.startsWith("http://") && !domainUrl.startsWith("HTTP://")){
				domainUrl = "http://"+domainUrl;
			}
			URL url = new URL(domainUrl);
			if(port<=0 && port>65535){
				port=url.getDefaultPort();
			}
			SocketAddress dest = new InetSocketAddress(hostIp, port);
			socket.connect(dest);

			visitReturn = request(socket, url,VisitConfig.getInstance().getUAType(),downloadFlag);
			return visitReturn;
		} catch (Exception e) {
			logger.error(e);
			return visitReturn;
		} finally {
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}

	
	public static VisitReturn request(Socket socket, URL url,UserAgentTypes userAgent,boolean downloadFlag) throws IOException {
		InputStream inputStream =null;
		PrintWriter printWriter =null;
		VisitReturn visitReturn=new VisitReturn();
		try {
			StringBuffer sb = new StringBuffer();
			String path=StringUtils.isBlank(url.getPath())?"/":url.getPath();
			sb.append("GET " + path + " HTTP/1.1\r\n");
			sb.append("Host: ").append(url.getHost()).append("\r\n");
			setUserAgent(sb,userAgent);
			sb.append("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\r\n");
			// sb.append("Accept-Encoding: gzip,deflate,sdch\r\n");
			sb.append("Accept-Encoding: \r\n");
			sb.append("Accept-Language: zh-CN,zh;q=0.8\r\n");
			sb.append("Cache-Control: max-age=0\r\n");
			sb.append("Connection: close\r\n");
			sb.append("\r\n");

			printWriter = new PrintWriter(socket.getOutputStream());
			printWriter.write(sb.toString());
			printWriter.flush();
			inputStream = socket.getInputStream();
			visitReturn = getPageInfo(inputStream,downloadFlag);
			
		} catch (Exception e) {
			logger.error(e);
		}finally{
			if(printWriter!=null){
				printWriter.close();
			}
			if(inputStream!=null){
				inputStream.close();
			}
		}
		
		return visitReturn;
	}

	public static void setUserAgent(StringBuffer sb, UserAgentTypes userAgent) {
		if(userAgent!=null){
			switch(userAgent){
				case IE_Desktop:
					sb.append("User-Agent: Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0)\r\n");
					break;
				case Chrome_Desktop:
					sb.append("User-Agent: Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36\r\n");
					break;
				case FireFox_Desktop:
					sb.append("User-Agent: Mozilla/5.0 (Windows NT 6.1; rv:2.0.1) Gecko/20100101 Firefox/4.0.1\r\n");
					break;
				case IOS_Safari:
					sb.append("User-Agent: Mozilla/5.0 (iPhone; U; CPU like Mac OS X) AppleWebKit/420.1 (KHTML, like Gecko) Version/3.0 Mobile/4A93 Safari/419.3\r\n");
					break;
				case UCWeb_mobile:
					sb.append("User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; ) Opera/UCWEB7.0.2.37/28/999\r\n");
					break;
				case Android_mobile:
					sb.append("User-Agent: Mozilla/5.0 (Linux; U; Android 3.0; en-us; Xoom Build/HRI39) AppleWebKit/534.13 (KHTML, like Gecko) Version/4.0 Safari/534.13\r\n");
					break;
				default:
					sb.append("User-Agent: Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36\r\n");
					break;
			}
			
		}
	}
	

	@SuppressWarnings("unused")
	public static String getContent(InputStream inputStream) {
		StringBuilder str = new StringBuilder();
		try {
			int count = 0;
			byte[] b = new byte[1024];

			while ((count = inputStream.read()) > 0) {
				inputStream.read(b);
				str.append(new String(b));
			}
			return str.toString();
		} catch (IOException e) {
			logger.error(e);
			return str.toString();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.error(e);
					// TODO Auto-generated catch block
				}
			}
		}
	}

	/**
	 * 获取http的状态码
	 * 
	 * @param inputStream 输入流
	 * @return
	 */
	public static VisitReturn getPageInfo(InputStream inputStream,Boolean downloadFlag) {
		VisitReturn visitReturn=new VisitReturn();
		int statusCode = 0;
		
		BufferedReader reader = null;
		
		try {
			ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[2048];
			int rc = 0;
			while ((rc = inputStream.read(buff, 0, 2048)) > 0) {
				swapStream.write(buff, 0, rc);
			}
			byte[] in2b = swapStream.toByteArray();
			swapStream.close();
			
			reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(in2b),Charset.forName("gb2312")));

			String statusLine = reader.readLine();
			if (statusLine == null) {
				logger.error("stauts line is null ");
				statusLine="";
			}
			
			if (null == statusLine || statusLine.length() == 0) {
				statusCode=0;
			}
			String[] fields = statusLine.split("\\s");
			if (fields.length < 3 || fields.length > 4) {
				statusCode=0;
			}
			if (!(fields[0].equals("HTTP/1.1")) && !(fields[0].equals("HTTP/1.0"))) {
				statusCode=0;
			}
			try {
				statusCode = Integer.parseInt(fields[1]);
			} catch (NumberFormatException e) {
				statusCode=0;
			}
			visitReturn.setCode(statusCode);
			
			if(downloadFlag && statusCode!=0){
				String line = "";
				StringBuffer header = new StringBuffer(statusLine);
				header.append("\r\n");
				StringBuffer html = new StringBuffer();
				html.append("\r\n");
				
				//读取网页的header信息
				while ((line = reader.readLine()) != null) {
					// 第一次出现空行就是header和网页内容分离的部分
					if (line.equals("")) {
						break;
					}
					header.append(line);
					header.append("\r\n");
				}
				
				//读写网页内容信息
				while((line=reader.readLine())!=null){
					html.append(line);
					html.append("\r\n");
				}
				visitReturn.setHtml(html.toString());
				if(!html.equals("")){
					visitReturn.setHashcode(html.hashCode());
				}else{
					visitReturn.setHashcode(0);
				}
			}

		} catch (Exception e) {
			logger.error(e);
		}finally{
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
		return visitReturn;
	}
	
	

	public static void main(String[] args) {
		try {
			// http://www.v2ex.com/t/24904
//			 InetAddress[] ias=InetAddress.getAllByName("bj.jumei.com");
//			 for(InetAddress ia:ias){
//			 System.out.println(ia.getHostAddress());
//			 }
			 VisitReturn visitReturn =null;
//			 visitReturn=connection("114.112.90.28",80,true);
//			 System.out.println(visitReturn.getHtml());
			visitReturn =connection("http://www.fang.com/",true);
			FileUtils.write(new File("C:/Users/Administrator/Desktop/员工资料/test.html"), visitReturn.getHtml(),Charset.forName("gb2312"));
			System.out.println(visitReturn.getHtml());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
