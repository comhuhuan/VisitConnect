/**
 * Project Name:VisitConnect
 * File Name:VisitManager.java
 * Package Name:com.surfilter.tmms.visit
 * Date:2016年1月4日下午3:47:56
 *
*/

package com.surfilter.tmms;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.surfilter.tmms.bean.ThreadFlagBean;
import com.surfilter.tmms.bean.VisitBean;
import com.surfilter.tmms.bean.VisitResult;
import com.surfilter.tmms.bean.VisitReturn;
import com.surfilter.tmms.enums.ContentTypes;
import com.surfilter.tmms.enums.ProtocolTypes;
import com.surfilter.tmms.utils.LocalNetUtils;
import com.surfilter.tmms.visit.IResultSaseBatch;
import com.surfilter.tmms.visit.IResultSave;
import com.surfilter.tmms.visit.dns.DnsVisitor;
import com.surfilter.tmms.visit.ftp.FtpVisitor;
import com.surfilter.tmms.visit.http.HttpVisitor;
import com.surfilter.tmms.visit.https.HttpsVisitor;
import com.surfilter.tmms.visit.mail.MailVisitor;
import com.surfilter.tmms.visit.ntp.NtpVisitor;
import com.surfilter.tmms.visit.proxy.ProxySocketVisitor;
import com.surfilter.tmms.visit.snmp.SnmpVisitor;
import com.surfilter.tmms.visit.ssh.SshVisitor;
import com.surfilter.tmms.visit.tcp.TcpVisitor;
import com.surfilter.tmms.visit.udp.UdpVisitor;

/**
 * ClassName:VisitManager <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016年1月4日 下午3:47:56 <br/>
 * @author   huhuan
 * @version
 * @since    JDK 1.6
 * @see
 */
public class VisitManager {

	private Logger logger = Logger.getLogger(VisitManager.class);


	private VisitConfig config;

	private Boolean downloadFlag=false;

	private Map<Long,VisitReturn> map=new HashMap<Long,VisitReturn>();
	/**
	 * flag:用于维护执行拨测任务的线程的中止开关
	 * @since JDK 1.7
	 */
	private ThreadFlagBean flag;


	/**
	 * 调用拨测接口
	 * @param visitBean 拨测条件对象
	 * @param procotolType 拨测协议规则
	 * @param contentType  拨测内容类型
	 * @param connectTime  拨测次数
	 * @param resultSave  结果保存接口，回调接口
	 */
	public void execute(VisitBean visitBean,ProtocolTypes procotolType,ContentTypes contentType,Long connectTime,IResultSave resultSave) throws Exception{
		this.execute(visitBean, procotolType, contentType, connectTime, resultSave, null);
	}

	/**
	 * 调用拨测接口
	 * @param visitBean 拨测条件对象
	 * @param procotolType 拨测协议规则
	 * @param contentType  拨测内容类型
	 * @param connectTime  拨测次数
	 * @param resultSave  结果保存接口，回调接口
	 * @param output  输出流
	 */
	public void execute(VisitBean visitBean,ProtocolTypes procotolType,ContentTypes contentType,Long connectTime,IResultSave resultSave,OutputStream output) throws Exception{
		VisitResult result = connectExecute(visitBean, procotolType,contentType, connectTime,output,flag);
		resultSave.save(result);

	}


	/**
	 * 批量拨测接口
	 * @param visitBean 拨测条件对象
	 * @param procotolType 拨测协议规则
	 * @param contentType  拨测内容类型
	 * @param connectTime  拨测次数
	 * @param resultSave  结果保存接口，回调接口
	 */
	public void executeBatch(List<VisitBean> visitBeans,ProtocolTypes procotolType,ContentTypes contentType,Long connectTime,IResultSaseBatch resultSave) throws Exception{
		this.executeBatch(visitBeans, procotolType, contentType, connectTime, resultSave, null);

	}

	/**
	 * 批量拨测接口
	 * @param visitBean 拨测条件对象
	 * @param procotolType 拨测协议规则
	 * @param contentType  拨测内容类型
	 * @param connectTime  拨测次数
	 * @param resultSave  结果保存接口，回调接口
	 * @param output  输出流
	 */
	public void executeBatch(List<VisitBean> visitBeans,ProtocolTypes procotolType,ContentTypes contentType,Long connectTime,IResultSaseBatch resultSave,OutputStream output) throws Exception{
		if(visitBeans!=null && visitBeans.size()>0){
			List<VisitResult> lstResult=new ArrayList<VisitResult>();
			String taskId=visitBeans.get(0).getTaskId();
			for(VisitBean visitBean :visitBeans){
				VisitResult result=connectExecute(visitBean, procotolType, contentType, connectTime,output,flag);
				result.setTaskId(taskId);
				lstResult.add(result);
			}
			resultSave.save(lstResult);
		}

	}




	/**
	 * 批量拨测接口  用于拨测工具专用
	 * @param visitBean 拨测条件对象
	 * @param procotolType 拨测协议规则
	 * @param contentType  拨测内容类型
	 * @param output  输出流
	 * @author yixiang 20160203
	 */
	public void executeBatch(List<VisitBean> visitBeans,ProtocolTypes procotolType,OutputStream output){
		try {
			if(visitBeans!=null && visitBeans.size()>0){
//				List<VisitResult> lstResult=new ArrayList<VisitResult>();
//				String taskId=visitBeans.get(0).getTaskId();
				for(VisitBean visitBean :visitBeans){
					//获取拨测规则
					ContentTypes contentType = getContentTypes(visitBean);
//					VisitResult result=connectExecute(visitBean, procotolType,contentType, visitBean.getTotalTimes(),output);
					this.execute(visitBean, procotolType,contentType,visitBean.getTotalTimes(),new IResultSave() {

						public void save(VisitResult result) {
						}
					},output);
//					result.setTaskId(taskId);
//					lstResult.add(result);
				}
			}
		}catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * 获取协议类型
	 * @param visitBean
	 * @return
	 */
	private ContentTypes getContentTypes(VisitBean visitBean){
		if(visitBean.getContentType()==ContentTypes.DOMAIN.getType()){
			return ContentTypes.DOMAIN;
		}
		if(visitBean.getContentType()==ContentTypes.URL.getType()){
			return ContentTypes.URL;
		}
		if(visitBean.getContentType()==ContentTypes.IPPORT.getType()){
			return ContentTypes.IPPORT;
		}
		if(visitBean.getContentType()==ContentTypes.DOMAIN_IPPORT.getType()){
			return ContentTypes.DOMAIN_IPPORT;
		}
		if(visitBean.getContentType()==ContentTypes.URL_IPPORT.getType()){
			return ContentTypes.URL_IPPORT;
		}
		return null;
	}

	/**
	 *
	 * connectExecuteForOriginal:执行拨测获取拨测原始结果
	 *
	 * @param visitBean 拨测条件对象
	 * @param procotolType 拨测协议
	 * @param contentType 拨测内容类型
	 * @param connectTime 拨测次数
	 * @return 返回状态码和对应状态码的次数
	 * @throws InterruptedException
	 * @since JDK 1.6
	 */
	public Map<Integer,AtomicLong> connectExecuteForOriginal(VisitBean visitBean,ProtocolTypes procotolType, ContentTypes contentType,Long connectTime,OutputStream output) throws InterruptedException {
		if(output==null){
			output=System.out;
		}
		VisitContext context=new VisitContext(connectTime);
		Runnable[] runs=createRunnable(visitBean, procotolType, contentType, context,null);
		int threadSize = config.getTaskThreadSize();
		if (visitBean.getBlockFlag()) {
			threadSize += 5;
		}
		ExecutorService executorService=Executors.newFixedThreadPool(threadSize);
		VisitResult result=new VisitResult();
		result.setStartTime(new Date());
		if(runs!=null){
			for(Runnable run:runs){
				executorService.submit(run);
			}
		}
		executorService.shutdown();
		while(!executorService.awaitTermination(1, TimeUnit.SECONDS)){
			printProcess(context,output);
		}
		printProcess(context,output);


		result.setTaskId(visitBean.getTaskId());
		result.setEndTime(new Date());
		result.setTaskId(visitBean.getTaskId());
		List<String> localIps=LocalNetUtils.getLocalIp();
		if(localIps!=null && localIps.size()>0){
			result.setLocalIp(localIps.get(0));
		}
		if(StringUtils.isNoneBlank(config.getIpGetWebsite())){
			result.setNetOutIP(LocalNetUtils.getOnlineIp(config.getIpGetWebsite()));
		}


		this.map=context.getHtmls();


		Map<Integer,AtomicLong> map=context.getConnectResult();
		return map;
	}

	/**
	 *
	 * connectExecute:执行拨测获取拨测结果
	 *
	 * @param visitBean 拨测条件对象
	 * @param procotolType 拨测协议
	 * @param contentType 拨测内容类型
	 * @param connectTime 拨测次数
	 * @param flag 线程中止开关
	 * @return
	 * @throws InterruptedException
	 * @since JDK 1.6
	 */
	public VisitResult connectExecute(VisitBean visitBean,ProtocolTypes procotolType, ContentTypes contentType,Long connectTime,OutputStream output,ThreadFlagBean flag) throws InterruptedException {
		if(output==null){
			output=System.out;
		}
		VisitContext context=new VisitContext(connectTime);
		Runnable[] runs=createRunnable(visitBean, procotolType, contentType, context,flag);
		int threadSize = config.getTaskThreadSize();
		if (visitBean.getBlockFlag()) {
			threadSize += 5;
		}
		ExecutorService executorService=Executors.newFixedThreadPool(threadSize);
		VisitResult result=new VisitResult();
		result.setStartTime(new Date());
		if(runs!=null){
			for(Runnable run:runs){
				executorService.submit(run);
			}
		}
		executorService.shutdown();
		while(!executorService.awaitTermination(1, TimeUnit.SECONDS)){
			printProcess(context,output);
		}
		printProcess(context,output);


		result.setTaskId(visitBean.getTaskId());
		result.setEndTime(new Date());
		result.setTaskId(visitBean.getTaskId());
		List<String> localIps=LocalNetUtils.getLocalIp();
		if(localIps!=null && localIps.size()>0){
			result.setLocalIp(localIps.get(0));
		}
		if(StringUtils.isNotBlank(config.getIpGetWebsite())){
			result.setNetOutIP(LocalNetUtils.getOnlineIp(config.getIpGetWebsite()));
		}

		Map<Integer,AtomicLong> map=context.getConnectResult();
		Iterator<Integer> its=map.keySet().iterator();
		this.map=context.getHtmls();


		while(its.hasNext()){
			Integer key=its.next();
			if(config.getSuccessList().contains(key)){
				result.setSuccessTime(result.getSuccessTime()+map.get(key).get());
			}else{
				result.setFailTime(result.getFailTime()+map.get(key).get());
			}
		}
		return result;
	}





	/**
	 *
	 * printProcess:输出
	 *
	 * @author Administrator
	 * @param context
	 * @param out
	 * @since JDK 1.6
	 */
	private void printProcess(VisitContext context,OutputStream out)  {
		try {
			int size=context.getQueue().size();
			if(size>0){
				for(int i=0;i<size;i++){
					String line=context.getQueue().poll();
					out.write(line.getBytes("UTF-8"));
					out.write("\r\n".getBytes("UTF-8"));
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}

	}


	private Runnable[] createRunnable(VisitBean visitBean, ProtocolTypes procotolType, ContentTypes contentType,VisitContext context,ThreadFlagBean flag) {
		int threadSize = config.getTaskThreadSize();
		if (visitBean.getBlockFlag()) {
			threadSize += 5;
		}
		Runnable[] runs=new Runnable[threadSize];
		switch(procotolType){
			case HTTP:
				for(int i=0;i<threadSize;i++){
					runs[i]=new HttpVisitor(context, visitBean, contentType,downloadFlag,visitBean.getBlockFlag(),flag);
				}
				break;
			case HTTPS:
				for(int i=0;i<threadSize;i++){
					runs[i]=new HttpsVisitor(context, visitBean, contentType,downloadFlag);
				}
				break;
			case SOCKET5:
				for(int i=0;i<threadSize;i++){
					runs[i]=new ProxySocketVisitor(context, visitBean, contentType,visitBean.getBlockFlag());
				}
				break;
			case TCP:
				for(int i=0;i<threadSize;i++){
					runs[i]=new TcpVisitor(context, visitBean, contentType,visitBean.getBlockFlag());
				}
				break;
			case UDP:
				for(int i=0;i<threadSize;i++){
					runs[i]=new UdpVisitor(context, visitBean, contentType);
				}
				break;
			case MAIL:
				for(int i=0;i<threadSize;i++){
					runs[i]=new MailVisitor(context, visitBean, contentType);
				}
				break;
			case DNS:
				for(int i=0;i<threadSize;i++){
					runs[i]=new DnsVisitor(context, visitBean, contentType);
				}
				break;
			case FTP:
                for(int i=0;i<threadSize;i++){
                    runs[i]=new FtpVisitor(context, visitBean, contentType);
                }
                break;
            case NTP:
                for(int i=0;i<threadSize;i++){
                    runs[i]=new NtpVisitor(context, visitBean, contentType);
                }
                break;
            case SSH:
                for(int i=0;i<threadSize;i++){
                    runs[i]=new SshVisitor(context, visitBean, contentType);
                }
                break;
            case SNMP:
                for(int i=0;i<threadSize;i++){
                    runs[i]=new SnmpVisitor(context, visitBean, contentType);
                }
                break;
			default:
				runs=null;
				break;

		}
		return runs;
	}


	public VisitConfig getConfig() {
		return config;
	}


	public void setConfig(VisitConfig config) {
		this.config = config;
	}


	public Boolean getDownloadFlag() {
		return downloadFlag;
	}


	public void setDownloadFlag(Boolean downloadFlag) {
		this.downloadFlag = downloadFlag;
	}


	public Map<Long, VisitReturn> getMap() {
		return map;
	}


	public void setMap(Map<Long, VisitReturn> map) {
		this.map = map;
	}

	public ThreadFlagBean getFlag() {
		return flag;
	}

	public void setFlag(ThreadFlagBean flag) {
		this.flag = flag;
	}
	
	


}

