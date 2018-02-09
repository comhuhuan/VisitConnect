/**
 * Project Name:application-util
 * File Name:IPUtils.java
 * Package Name:org.cherry.application.util
 * Date:2013-12-9上午10:44:19
 * Copyright (c) 2013, CANNIKIN(http://http://code.taobao.org/p/cannikin/src/) All Rights Reserved.
 *
 */
package com.surfilter.tmms.utils;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ClassName: IPUtils <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON(可选). <br/>
 * date: 2013-12-9 上午10:44:19 <br/>
 * 
 * @author Alex
 * @version
 * @since JDK 1.6
 */
public class IPUtils {

	public static long Ip2Long(String strIP) {
		long[] ip = new long[4];
		int position1 = strIP.indexOf(".");
		int position2 = strIP.indexOf(".", position1 + 1);
		int position3 = strIP.indexOf(".", position2 + 1);
		ip[0] = Long.parseLong(strIP.substring(0, position1));
		ip[1] = Long.parseLong(strIP.substring(position1 + 1, position2));
		ip[2] = Long.parseLong(strIP.substring(position2 + 1, position3));
		ip[3] = Long.parseLong(strIP.substring(position3 + 1));
		return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
	}

	public static String long2IP(long longIP) {
		StringBuffer sb = new StringBuffer("");
		sb.append(String.valueOf(longIP >>> 24));
		sb.append(".");
		sb.append(String.valueOf((longIP & 0x00FFFFFF) >>> 16));
		sb.append(".");
		sb.append(String.valueOf((longIP & 0x0000FFFF) >>> 8));
		sb.append(".");
		sb.append(String.valueOf(longIP & 0x000000FF));
		return sb.toString();
	}

	/**
	 * StringToBigInt:将IP地址专为BigInteger型. <br/>
	 * 
	 * @author Alex
	 * @param ipInString
	 * @return
	 * @since JDK 1.6
	 */
	public static BigInteger StringToBigInt(String ipInString) {
		ipInString = ipInString.replace(" ", "");
		byte[] bytes;
		if (ipInString.contains(":"))
			bytes = ipv6ToBytes(ipInString);
		else
			bytes = ipv4ToBytes(ipInString);
		return new BigInteger(bytes);
	}

	/**
	 * 将整数形式的ip地址转换为字符串形式，根据ipv4和ipv6自动转换
	 * 
	 * @param ipInBigInt
	 *            整数形式的ip地址
	 * @return 字符串形式的ip地址
	 */
	public static String BigIntToString(BigInteger ipInBigInt) {
		byte[] bytes = ipInBigInt.toByteArray();
		byte[] unsignedBytes = bytes;

		// 去除符号位
		try {
			String ip = InetAddress.getByAddress(unsignedBytes).toString();
			return ip.substring(ip.indexOf('/') + 1).trim();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * verifyIpStandard:判断是否是IP地址. <br/>
	 * @author Alex
	 * @param ip
	 * @return
	 * @since JDK 1.6
	 */
	public static boolean verifyIpStandard(String ip) {
		Pattern pattern = Pattern
				.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	}

	/**
	 * ipv6地址转有符号byte[17]
	 * 
	 * @param ipv6
	 *            字符串形式的IP地址
	 * @return big integer number
	 */
	private static byte[] ipv6ToBytes(String ipv6) {
		byte[] ret = new byte[17];
		ret[0] = 0;
		int ib = 16;
		boolean comFlag = false;// ipv4混合模式标记
		if (ipv6.startsWith(":"))// 去掉开头的冒号
			ipv6 = ipv6.substring(1);
		String groups[] = ipv6.split(":");
		for (int ig = groups.length - 1; ig > -1; ig--) {// 反向扫描
			if (groups[ig].contains(".")) {
				// 出现ipv4混合模式
				byte[] temp = ipv4ToBytes(groups[ig]);
				ret[ib--] = temp[4];
				ret[ib--] = temp[3];
				ret[ib--] = temp[2];
				ret[ib--] = temp[1];
				comFlag = true;
			} else if ("".equals(groups[ig])) {
				// 出现零长度压缩,计算缺少的组数
				int zlg = 9 - (groups.length + (comFlag ? 1 : 0));
				while (zlg-- > 0) {// 将这些组置0
					ret[ib--] = 0;
					ret[ib--] = 0;
				}
			} else {
				int temp = Integer.parseInt(groups[ig], 16);
				ret[ib--] = (byte) temp;
				ret[ib--] = (byte) (temp >> 8);
			}
		}
		return ret;
	}

	/**
	 * ipv4地址转有符号byte[5]
	 * 
	 * @param ipv4
	 *            字符串的IPV4地址
	 * @return big integer number
	 */
	private static byte[] ipv4ToBytes(String ipv4) {
		byte[] ret = new byte[5];
		ret[0] = 0;
		// 先找到IP地址字符串中.的位置
		int position1 = ipv4.indexOf(".");
		int position2 = ipv4.indexOf(".", position1 + 1);
		int position3 = ipv4.indexOf(".", position2 + 1);
		// 将每个.之间的字符串转换成整型
		ret[1] = (byte) Integer.parseInt(ipv4.substring(0, position1));
		ret[2] = (byte) Integer.parseInt(ipv4.substring(position1 + 1,
				position2));
		ret[3] = (byte) Integer.parseInt(ipv4.substring(position2 + 1,
				position3));
		ret[4] = (byte) Integer.parseInt(ipv4.substring(position3 + 1));
		return ret;
	}

	public static void main(String[] args) {

		System.out.println(Ip2Long("192.168.0.100"));
		System.out.println(long2IP(454110032L));
		
		
	}
}
