package com.surfilter.tmms.utils;

import java.io.IOException;
import java.net.Socket;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.surfilter.tmms.VisitConfig;
import com.surfilter.tmms.bean.VisitReturn;
import com.surfilter.tmms.enums.UserAgentTypes;

/**
 *
 * @author XinHu
 * @category HttpClient 拨测工具
 * @version HttpClient 4.5
 */
public class HttpUtils3 {

	private static Logger logger = Logger.getLogger(HttpUtils3.class);
	private static StrickMap<String,String> contentTypeMap = new StrickMap<String,String>();
	private static final String[] ENCODING = { "utf-8", "gb2312", "gbk", "big5", "iso-8859-1" };
	private static final int SOCKET_TIMEOUT = 3500;


	/**
	 * encoding method
	 *
	 * @author Xin Hu
	 * @param url
	 * @param is
	 * @param html
	 * @param set
	 * @return
	 * @throws IOException
	 */
	private static String getProperHtml(HttpResponse response, String url, boolean downloadFlag) throws IOException {

		byte[] bytes = IOUtils.toByteArray(response.getEntity().getContent());
		if (bytes == null || bytes.length == 0) {
			return "";
		}
		if (downloadFlag) {
			//默认全部按照utf-8格式保存
			return new String(bytes, getCharset(bytes));
			
			/*String encoding = contentTypeMap.get(url);
			if (StringUtils.isNoneBlank(encoding)) {
				return new String(bytes, encoding);
			}
			encoding = "utf-8";
			for (int i = 0; i < bytes.length; i++) {
				if ((bytes[i] == 99 || bytes[i] == 67)
						&& (bytes[i + 1] == 104 || bytes[i + 1] == 72)
						&& (bytes[i + 2] == 97 || bytes[i + 2] == 65)
						&& (bytes[i + 3] == 114 || bytes[i + 3] == 82)) {
					// 99==c 104==h 97==a 114==r
					byte[] charsetBytes = Arrays.copyOfRange(bytes, i, i + 30);// 截取一段寻找
					String charsetStr = new String(charsetBytes, encoding).toLowerCase();
					if (charsetStr.contains("charset=")) {
						boolean isFound = false;
						// 先在预先定义的小范围内寻找
						for (int j = 0; j < ENCODING.length; j++) {
							if (charsetStr.contains(ENCODING[j])) {
								encoding = ENCODING[j];
								isFound = true;
								break;
							}
						}
						// 不行就在更大范围寻找
						if (!isFound) {
							for (String charset : Charset.availableCharsets().keySet()) {
								if (charsetStr.contains(charset.toLowerCase())) {
									encoding = charset;
									isFound = true;
									break;
								}
							}
						}
						if (isFound) {
							break;
						} else {
							continue;
						}
					}
				}
				if (i > 500) {// 找了500个字符还是找不到就别找了，就用utf-8吧
					break;
				}
			}
			contentTypeMap.put(url, encoding);
			return new String(bytes, encoding);*/
		} else {
			return "";
		}
	}

	private static String getCharset(byte[] bytes){
		String htmlString = new String(bytes);
		String code = "utf-8";
		if( StringUtils.isNoneBlank(htmlString) ){
			int index = htmlString.indexOf("charset");
			if( index > -1 ){
				code = htmlString.substring(index+8,htmlString.indexOf("\"",index));
			}
		}
		return code;
	}
	
	/**
	 * @param downloadFlag
	 * @param method
	 * @param visitReturn
	 * @throws IOException
	 * @throws UnsupportedOperationException
	 * @desc 下载拨测页面
	 */
	public static void hasDownload(Boolean downloadFlag, HttpResponse response, VisitReturn visitReturn,String url) throws UnsupportedOperationException, IOException {

		try {
			String html = getProperHtml(response, url, downloadFlag);
			if (html != null) {
				visitReturn.setHtml(html);
				visitReturn.setHashcode(html.hashCode());
			} else {
				visitReturn.setHtml("");
				visitReturn.setHashcode(0);
			}
		} catch (IOException e) {
			visitReturn.setHtml("");
			visitReturn.setHashcode(0);
			throw e;
		}
	}

	/**
	 * @author Xin
	 * @param url
	 * @param downloadFlag
	 * @since default version
	 * @return
	 */
	public static VisitReturn connection(String url, Boolean downloadFlag) {
		VisitReturn result = new VisitReturn();
		int port = getPort(url, 80);
		HttpProcessor httpproc = HttpProcessorBuilder.create()
				.add(new RequestContent())
				.add(new RequestTargetHost())
				.add(new RequestConnControl1())
				.add(new RequestUserAgent(getUserAgent(VisitConfig.getInstance().getUAType())))
				.add(new RequestExpectContinue(true))
				.build();

		HttpRequestExecutor httpexecutor = new HttpRequestExecutor();

		HttpCoreContext coreContext = HttpCoreContext.create();

		HttpHost host = null;
		if (port == 80) {
			host = new HttpHost(getHost(url));
		}else{
			host = new HttpHost(getHost(url), port);
		}

		coreContext.setTargetHost(host);
		DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(8 * 1024);
		Socket socket = null;
		int statusCode=0;
		String remoteAddress = "";
		try {
			if (!conn.isOpen()) {
				socket = new Socket(host.getHostName(), port);
				conn.bind(socket);
				conn.setSocketTimeout(SOCKET_TIMEOUT);
			}
			BasicHttpRequest request = new BasicHttpRequest("GET", getURI(url));
			logger.debug("HTTP-->host:"+host);
			logger.debug("SOCKET-->host:"+host.getHostName()+"  port:"+port);
			logger.debug(request);
			httpexecutor.preProcess(request, httpproc, coreContext);
			HttpResponse response = httpexecutor.execute(request, conn, coreContext);
			httpexecutor.postProcess(response, httpproc, coreContext);
			statusCode = response.getStatusLine().getStatusCode();
			result.setCode(statusCode);
			remoteAddress = conn.getRemoteAddress().getHostAddress();
			result.setRemoteAddress(remoteAddress==null?"":remoteAddress);
			hasDownload(downloadFlag, response, result, url);
			EntityUtils.consume(response.getEntity());

		} catch (IOException e){
			logger.warn(e+";  statusCode:"+statusCode);
		} catch (Exception e) {
			logger.warn("statusCode"+statusCode,e);
		}finally {
			try {
				if (null != conn) {
					conn.close();
				}
				if (null != socket) {
					socket.close();
				}
			}catch (Exception e) {
				logger.warn("statusCode"+statusCode,e);
			}
		}
		return result;
	}

	/**
	 * @author XinHu
	 * @param url
	 * @param ip
	 * @param port
	 * @param downloadFlag
	 * @param type
	 *            use for select the type of httpClient, 1 for minimal setting,
	 *            2 for system setting, 3 for ip setting, others are default.
	 * @return
	 */
	public static VisitReturn connection(String url, String ip, int port, Boolean downloadFlag) {
		VisitReturn result = new VisitReturn();
		if (port <= 0) {
			port = 80;
		}
		port = getPort(url, port);

		HttpProcessor httpproc = HttpProcessorBuilder.create()
				.add(new RequestContent())
				.add(new RequestTargetHost())
				.add(new RequestConnControl1())
				.add(new RequestUserAgent(getUserAgent(VisitConfig.getInstance().getUAType())))
				.add(new RequestExpectContinue(true))
				.build();

		HttpRequestExecutor httpexecutor = new HttpRequestExecutor();

		HttpCoreContext coreContext = HttpCoreContext.create();
		HttpHost host = null;
		if (port == 80) {
			host = new HttpHost(getHost(url));
		}else{
			host = new HttpHost(getHost(url), port);
		}
		coreContext.setTargetHost(host);

		DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(8 * 1024);
		
		Socket socket = null;
		int statusCode=0;
		String remoteAddress = "";
		try {
			if (!conn.isOpen()) {
				socket = new Socket(ip, port);
				conn.bind(socket);
				conn.setSocketTimeout(SOCKET_TIMEOUT);
			}
			BasicHttpRequest request = new BasicHttpRequest("GET", getURI(url));
			logger.warn("HTTP-->host:"+host);
			logger.warn("SOCKET-->host:"+host.getHostName()+"  port:"+port);
			logger.warn(request);
			httpexecutor.preProcess(request, httpproc, coreContext);
			HttpResponse response = httpexecutor.execute(request, conn, coreContext);
			httpexecutor.postProcess(response, httpproc, coreContext);
			statusCode = response.getStatusLine().getStatusCode();
			result.setCode(statusCode);
			remoteAddress = conn.getRemoteAddress().getHostAddress();
			result.setRemoteAddress(remoteAddress==null?"":remoteAddress);
			hasDownload(downloadFlag, response, result, url);
			EntityUtils.consume(response.getEntity());

		} catch (IOException e){
			logger.warn(e+";  statusCode:"+statusCode);
		} catch (Exception e) {
			logger.warn("statusCode"+statusCode,e);
		}finally {
			try {
				if (null != conn) {
					conn.close();
				}
				if (null != socket) {
					socket.close();
				}
			}catch (Exception e) {
				logger.warn("statusCode"+statusCode,e);
			}
		}
		return result;
	}



	public static void main(String[] args) throws InterruptedException {
		final String url = "http://42.236.127.250:2225/icinga/";
//		final String url = "http://www.hao123.com";
		long beg = System.currentTimeMillis();
		ExecutorService executorService=Executors.newFixedThreadPool(4);
		final AtomicLong errCodeSum = new AtomicLong(0);
		final List<Integer> codeList = new ArrayList<Integer>();
		final AtomicLong sum = new AtomicLong(0);

		for (int j = 0; j < 1; j++) {
			final int index = j;
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < 2; i++) {
						sum.incrementAndGet();
						VisitReturn result = connection(url, false);
//						VisitReturn result = connection(url, "111.13.100.2",80, false);
						System.out.println(index+":"+i);
						System.out.println(result.getCode());
						if (result.getCode()!=200) {
							errCodeSum.incrementAndGet();
							codeList.add(result.getCode());
						}
					}
				}
			});
		}
		executorService.shutdown();
		int a = 0;
		while(!executorService.awaitTermination(15, TimeUnit.SECONDS)){
			if (a>100) {
				System.out.println("未正确结束");
				System.out.println(System.currentTimeMillis()-beg);
				executorService.shutdownNow();
				break;
			}
			System.out.println("a="+a);
			System.out.println("sum="+sum.get());
			System.out.println("errCodeSum="+errCodeSum.get());
			System.out.println(System.currentTimeMillis()-beg);
			a++;
		}
		System.out.println("sum="+sum.get());
		System.out.println("errCodeSum="+errCodeSum.get());
		System.out.println(System.currentTimeMillis()-beg);
	}


	/**
	 * Only use ip and ip port to make the connection
	 *
	 * @author XinHu
	 * @param url
	 * @param ip
	 * @param port
	 * @param downloadFlag
	 * @return
	 */
	public static VisitReturn connection(String ip, int port, Boolean downloadFlag) {
		VisitReturn result = new VisitReturn();
		if (port <= 0) {
			port = 80;
		}
		port = getPort(ip, port);
		HttpProcessor httpproc = HttpProcessorBuilder.create()
				.add(new RequestContent())
				.add(new RequestTargetHost())
				.add(new RequestConnControl1())
				.add(new RequestUserAgent(getUserAgent(VisitConfig.getInstance().getUAType())))
				.add(new RequestExpectContinue(true))
				.build();

		HttpRequestExecutor httpexecutor = new HttpRequestExecutor();

		HttpCoreContext coreContext = HttpCoreContext.create();
		HttpHost host = null;
		if (port == 80) {
			host = new HttpHost(ip);
		}else{
			host = new HttpHost(ip, port);
		}
		coreContext.setTargetHost(host);

		DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(8 * 1024);
		Socket socket = null;
		int statusCode=0;
		String remoteAddress = "";
		try {
			if (!conn.isOpen()) {
				socket = new Socket(host.getHostName(), port);
				conn.bind(socket);
				conn.setSocketTimeout(SOCKET_TIMEOUT);
			}
			BasicHttpRequest request = new BasicHttpRequest("GET", getURI(ip));
			logger.warn("HTTP-->host:"+host);
			logger.warn("SOCKET-->host:"+host.getHostName()+"  port:"+port);
			logger.warn(request);
			httpexecutor.preProcess(request, httpproc, coreContext);
			HttpResponse response = httpexecutor.execute(request, conn, coreContext);
			httpexecutor.postProcess(response, httpproc, coreContext);
			statusCode = response.getStatusLine().getStatusCode();
			result.setCode(statusCode);
			remoteAddress = conn.getRemoteAddress().getHostAddress();
			result.setRemoteAddress(remoteAddress==null?"":remoteAddress);
			hasDownload(downloadFlag, response, result, ip);
			EntityUtils.consume(response.getEntity());

		} catch (IOException e){
			logger.warn(e+";  statusCode:"+statusCode);
		} catch (Exception e) {
			logger.warn("statusCode"+statusCode,e);
		}finally {
			try {
				if (null != conn) {
					conn.close();
				}
				if (null != socket) {
					socket.close();
				}
			}catch (Exception e) {
				logger.warn("statusCode"+statusCode,e);
			}
		}
		return result;
	}


	public static String getUserAgent(UserAgentTypes userAgent) {
		String uAget = "";
		if (userAgent != null) {
			switch (userAgent) {
			case IE_Desktop:
				uAget = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0)";
				break;
			case Chrome_Desktop:
				uAget = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36";
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

	public static void setUserAgent(URLConnection connect, UserAgentTypes userAgent) {
		if (userAgent != null) {
			switch (userAgent) {
			case IE_Desktop:
				connect.setRequestProperty("User-Agent",
						"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0)");
				break;
			case Chrome_Desktop:
				connect.setRequestProperty("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36");
				break;
			case FireFox_Desktop:
				connect.setRequestProperty("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; rv:2.0.1) Gecko/20100101 Firefox/4.0.1");
				break;
			case IOS_Safari:
				connect.setRequestProperty("User-Agent",
						"Mozilla/5.0 (iPhone; U; CPU like Mac OS X) AppleWebKit/420.1 (KHTML, like Gecko) Version/3.0 Mobile/4A93 Safari/419.3");
				break;
			case UCWeb_mobile:
				connect.setRequestProperty("User-Agent",
						"Mozilla/4.0 (compatible; MSIE 6.0; ) Opera/UCWEB7.0.2.37/28/999");
				break;
			case Android_mobile:
				connect.setRequestProperty("User-Agent",
						"Mozilla/5.0 (Linux; U; Android 3.0; en-us; Xoom Build/HRI39) AppleWebKit/534.13 (KHTML, like Gecko) Version/4.0 Safari/534.13");
				break;
			default:
				connect.setRequestProperty("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36");
				break;
			}
		}
	}

	static class StrickMap<K,V> extends HashMap<K,V>{
		private static final long serialVersionUID = 6454064806059237529L;
		private List<K> urls = new ArrayList<K>(50);
		@Override
		public synchronized V put(K key, V value) {
			if(urls.size()>=50){
				K old = urls.remove(0);
				this.remove(old);
			}
			urls.remove(key);
			urls.add(key);
			return super.put(key, value);
		}
	}
	static public class RequestConnControl1 implements HttpRequestInterceptor {
	    public RequestConnControl1() {
	        super();
	    }
	    @Override
		public void process(final HttpRequest request, final HttpContext context)
	            throws HttpException, IOException {
	        Args.notNull(request, "HTTP request");
	        final String method = request.getRequestLine().getMethod();
	        if (method.equalsIgnoreCase("CONNECT")) {
	            return;
	        }
            request.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
            request.addHeader("Cache-Control", "no-cache");
            request.addHeader("Pragma", "no-cache");
	    }
	}
	private static StrickMap<String,String> uriMap = new StrickMap<String,String>();
	private static StrickMap<String,String> hostMap = new StrickMap<String,String>();
	private static StrickMap<String,Integer> portMap = new StrickMap<String,Integer>();
	private static String getURI(String domainurl){
		if (uriMap.containsKey(domainurl)) {
			return uriMap.get(domainurl);
		}
		String result = "/" + StringUtils.removePattern(StringUtils.removePattern(domainurl, "^http[s]?://"), "^[^/]+/?");
		uriMap.put(domainurl, result);
		return result;
	}
	private static String getHost(String domainurl){//去除了前缀与端口和uri
		if (hostMap.containsKey(domainurl)) {
			return hostMap.get(domainurl);
		}
		String result = StringUtils.removePattern(StringUtils.removePattern(domainurl, "^http[s]?://"), "[:/].*$");
		hostMap.put(domainurl, result);
		return result;
	}
	private static int getPort(String domainurl, int defaultPort) {

		if (portMap.containsKey(domainurl)) {
			return portMap.get(domainurl)!=null?portMap.get(domainurl):defaultPort;
		}
		String portStr = StringUtils.removePattern(StringUtils.removePattern(StringUtils.removePattern(domainurl, "^http[s]?://"), "/.*$"),"^[^:]*:?");
		Integer port;
		try {
			port = Integer.parseInt(portStr);
		} catch (Exception e) {
			portMap.put(domainurl, null);
			return defaultPort;
		}
		portMap.put(domainurl, port);
		return port;

	}
}
