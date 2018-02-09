package com.surfilter.tmms.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

public class UdpUtils {

	private static final Logger logger=Logger.getLogger(UdpUtils.class);
	
	public static boolean connection(String host,int port,String content){
		boolean resultFlag=true;
		DatagramSocket datagramSocket=null;
		try {
			datagramSocket=new DatagramSocket();
			byte[] buff=content.getBytes();
			InetAddress netAddress=InetAddress.getByName(host);
			DatagramPacket dp=new DatagramPacket(buff, buff.length, netAddress, port);
			datagramSocket.send(dp);
		} catch (UnknownHostException e) {
			logger.error(e);
			resultFlag=false;
		} catch (IOException e) {
			logger.error(e);
			resultFlag=false;
		}finally{
			if(datagramSocket!=null){
				datagramSocket.close();
			}
		}
		return resultFlag;
	}
}
