package com.surfilter.tmms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.StringUtils;
import org.apache.http.annotation.ThreadSafe;

import com.surfilter.tmms.bean.VisitReturn;

@ThreadSafe
public class VisitContext {
	private AtomicLong currentTime=new AtomicLong(0);

	private Long totalTime;

	private Map<Integer,AtomicLong> connectResult=new HashMap<Integer,AtomicLong>();

	private int maxLength=1000;

	private Queue<String> queue=new ArrayBlockingQueue<String>(maxLength);

	private AtomicLong count=new AtomicLong(0);

	private List<Integer> successList=VisitConfig.getInstance().getSuccessList();

	private Map<Long,VisitReturn> htmls=new HashMap<Long,VisitReturn>();

	private final String SUCCESS_INFO = "成功";
	private final String FAIL_INFO = "失败";


	public VisitContext(Long totalTime) {
		super();
		this.totalTime = totalTime;
	}

	public synchronized boolean isFinish(){
		boolean flag=currentTime.get()>=totalTime;
		if(currentTime.get()<totalTime){
			currentTime.incrementAndGet();
		}
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return flag;
	}

	public synchronized void collect(VisitReturn vr, String condition){
		if(connectResult.containsKey(vr.getCode())){
			AtomicLong times=connectResult.get(vr.getCode());
			times.incrementAndGet();
			connectResult.put(vr.getCode(), times);
		}else{
			connectResult.put(vr.getCode(), new AtomicLong(1L));
		}
		count.incrementAndGet();
		if(!htmls.containsKey(vr.getHashcode())){
			vr.getCount().incrementAndGet();
			htmls.put(vr.getHashcode(), vr);
		}else{
			htmls.get(vr.getHashcode()).getCount().incrementAndGet();
		}
		if(queue.size()>maxLength){
			queue.poll();
		}else{
			String code = FAIL_INFO;
			if(successList.contains(vr.getCode())){
				code = SUCCESS_INFO;
			}
			if(StringUtils.isNotBlank(condition)){
				queue.add("第"+count.get()+"次拨测，拨测"+condition+"，拨测的目标IP: " + (vr.getRemoteAddress() == null ? "" : vr.getRemoteAddress()) +",返回状态为："+code);
			}else{
				queue.add("第"+count.get()+"次拨测,返回状态为："+code);
			}
		}
	}

	public synchronized void collect(int statusCode, String condition){
		if(connectResult.containsKey(statusCode)){
			AtomicLong times=connectResult.get(statusCode);
			times.incrementAndGet();
			connectResult.put(statusCode, times);
		}else{
			connectResult.put(statusCode, new AtomicLong(1L));
		}
		count.incrementAndGet();
		if(queue.size()>maxLength){
			queue.poll();
		}else{
			String code = FAIL_INFO;
			if(successList.contains(statusCode)){
				code = SUCCESS_INFO;
			}
			if(StringUtils.isNotBlank(condition)){
				queue.add("第"+count.get()+"次拨测，拨测"+condition+"，返回状态为："+code);
			}else{
				queue.add("第"+count.get()+"次拨测,返回状态为："+code);
			}
		}
	}

	public AtomicLong getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(AtomicLong currentTime) {
		this.currentTime = currentTime;
	}

	public Long getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(Long totalTime) {
		this.totalTime = totalTime;
	}

	public Map<Integer, AtomicLong> getConnectResult() {
		return connectResult;
	}

	public void setConnectResult(Map<Integer, AtomicLong> connectResult) {
		this.connectResult = connectResult;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public Queue<String> getQueue() {
		return queue;
	}

	public void setQueue(Queue<String> queue) {
		this.queue = queue;
	}

	public Map<Long, VisitReturn> getHtmls() {
		return htmls;
	}

	public void setHtmls(Map<Long, VisitReturn> htmls) {
		this.htmls = htmls;
	}


}
