package com.surfilter.tmms;

import java.util.Iterator;
import java.util.Map;

import com.surfilter.tmms.bean.VisitBean;
import com.surfilter.tmms.bean.VisitResult;
import com.surfilter.tmms.bean.VisitReturn;
import com.surfilter.tmms.enums.ContentTypes;
import com.surfilter.tmms.enums.ProtocolTypes;
import com.surfilter.tmms.enums.UserAgentTypes;
import com.surfilter.tmms.visit.IResultSave;

public class VisitConnectApp {

    public static void main(String[] args) {

        try {
            for (int i = 0; i < 1; i++) {
                new Thread(new Runnable() {


                    public void run() {
                        try {
                            VisitBean visitBean = new VisitBean();
                            visitBean.setDomain("www.chinaz.com");
//							visitBean.setUrl("http://182.18.30.111/default.aspx");
                            visitBean.setBlockFlag(false);
                            visitBean.setIp("111.47.244.187");
                            visitBean.setPort(80);
                            VisitManager visitManager = new VisitManager();
                            visitManager.setDownloadFlag(false);
                            VisitConfig visitConfig = VisitConfig.getInstance();
                            visitConfig.setUAType(UserAgentTypes.getEnumByValue(2));
                            visitManager.setConfig(visitConfig);
                            visitManager.execute(visitBean, ProtocolTypes.HTTP, ContentTypes.DOMAIN_IPPORT, 20L, new IResultSave() {

                                public void save(VisitResult result) {
                                    System.out.println("拨测任务ID:" + result.getTaskId());
                                    System.out.println(result.getSuccessTime());
                                    System.out.println(result.getFailTime());
//							System.out.println("拨测任务ID:"+result.getTaskId());
//							System.out.println("拨测成功次数:"+result.getSuccessTime());
//							System.out.println("拨测失败次数:"+result.getFailTime());
                                }
                            });
                            Map<Long, VisitReturn> map = visitManager.getMap();
                            Iterator<Long> it = map.keySet().iterator();
                            while (it.hasNext()) {
                                System.out.println(map.get(it.next()).getHtml());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

            }

	/*		new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						VisitBean visitBean= new VisitBean();
//						visitBean.setDomain("baidu.com");
						visitBean.setUrl("http://www.hao123.com/");
//						visitBean.setIp("221.230.141.78");
//						visitBean.setPort(443);
						VisitManager visitManager=new VisitManager();
						VisitConfig visitConfig=VisitConfig.getInstance();
						visitConfig.setUAType(UserAgentTypes.getEnumByValue(3));
						visitManager.setConfig(visitConfig);
						visitManager.execute(visitBean, ProtocolTypes.HTTP, ContentTypes.URL, 100L, new IResultSave(){

							@Override
							public void save(VisitResult result) {
								System.err.println("拨测任务ID:"+result.getTaskId());
								System.err.println(result.getSuccessTime());
							}
							
						},System.out);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}).start();*/


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
