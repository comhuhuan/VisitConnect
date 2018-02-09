package com.surfilter.tmms.utils;
import java.util.Properties;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.sun.mail.smtp.SMTPTransport;

public class MailUtil {
	
	private static Logger logger = Logger.getLogger(MailUtil.class);
    // 初始化连接邮件服务器的会话信息 
    private static Properties props = null; 

	/**
	 * TEST SMTP/SMTPS协议 
	 * 服务器端口 25/465
	 */
	public static void sendSmtpEmail(String userFormMail, String passWord)
			throws Exception {
		Session session = Session.getInstance(props); // 创建Session实例对象
		MimeMessage message = new MimeMessage(session); // 创建MimeMessage实例对象
		message.setFrom(new InternetAddress(userFormMail)); // 设置发件人
		message.saveChanges(); // 保存并生成最终的邮件内容
		SMTPTransport transport = (SMTPTransport) session.getTransport();
		transport.connect(userFormMail, passWord); // 打开连接
		transport.close();
	}
    
	/**
	 * TEST POP3协议
	 * 服务器端口 110
	 */
	public static void RevPopMails(String userFormMail, String passWord)
			throws Exception {
		//创建Session实例对象
		Session session = Session.getInstance(props);
		Store store = session.getStore("pop3");
		//连接pop.xx.com邮件服务器
		store.connect(userFormMail, passWord);
		store.close();
	}
	
	/**
	 * TEST IMAP/IMAPS协议
	 * 服务器端口：25/993
	 */
	public static void RevImapMails(String userFormMail, String passWord)
			throws Exception {
		//创建Session实例对象
		Session session = Session.getInstance(props);
		Store store = session.getStore("imap");
		//连接pop.xx.com邮件服务器
		store.connect(userFormMail, passWord);
		store.close();
	}
    
	/**
	 * 获取发送邮件链接 userName 
	 * 身份验证 passWord 密码
	 * ServerName server名称
	 * port 端口
	 */
	public static int getConnectSmtp(String userFormMail, String passWord, String ServerName, String port) {
		try {
			//组装协议类容
			props = new Properties();
			props.setProperty("mail.transport.protocol", "smtp");
			props.setProperty("mail.smtp.host", ServerName); // SMTP邮件服务器
			props.setProperty("mail.smtp.port", port);
			props.setProperty("mail.smtp.auth", "true");
			props.setProperty("mail.debug", "true");
			sendSmtpEmail(userFormMail, passWord);
			return 200;
		} catch (Exception e) {
			logger.error(e);
			return 0;
		}
	}
    
	/**
	 * 获取pop收邮件连接信息 
	 * userName 身份验证 
	 * passWord 密码
	 * ServerName server名称
	 * port 端口
	 */
	public static int getConnectPop3(String userFormMail, String passWord, String ServerName, String port) {
		try {
			//组装协议类容
			props = new Properties();
			props.setProperty("mail.transport.protocol", "pop3");
			props.setProperty("mail.pop3.auth", "true");
			props.setProperty("mail.host", ServerName);
			props.setProperty("mail.pop3.port", port);
			RevPopMails(userFormMail, passWord);
			return 200;
		} catch (Exception e) {
			logger.error(e);
			return 0;
		}
	}
	
	/**
	 * 获取imap收邮件连接信息 
	 * userName 身份验证 
	 * passWord 密码
	 * ServerName server名称
	 * port 端口
	 */
	public static int getConnectImap(String userFormMail, String passWord, String ServerName, String port) {
		try {
			//组装协议类容
			props = new Properties();
			props.setProperty("mail.transport.protocol", "imap");
			props.setProperty("mail.imap.auth", "true");
			props.setProperty("mail.host", ServerName);
			props.setProperty("mail.imap.port", port);
			RevImapMails(userFormMail, passWord);
			return 200;
		} catch (Exception e) {
			logger.error(e);
			return 0;
		}
	}
    
	public static void main(String[] args) {
		//smtp
		int a = MailUtil.getConnectSmtp("xxx@act-telecom.com", "xxxxx","smtp.act-telecom.com","25");
		System.out.println("测试smtp结果展示" + a);
		//pop3
		int b = MailUtil.getConnectPop3("xxx@act-telecom.com", "xxxxx","pop.act-telecom.com","110");
		System.out.println("测试pop3结果展示" + b);
		//imap
		int c = MailUtil.getConnectImap("xxxxx@act-telecom.com", "xxxxx","imap.act-telecom.com","143");
		System.out.println("测试imap结果展示" + c);
	}
}
