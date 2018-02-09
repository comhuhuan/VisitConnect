package com.surfilter.tmms.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

public class DnsUtils {

	private static final Logger logger = Logger.getLogger(DnsUtils.class);
	private static final String domain = "www.baidu.com";

	public static boolean connection(String host, Integer port) {
		boolean resultFlag = false;
		Runtime r = Runtime.getRuntime();
		StringBuffer buffer = new StringBuffer();
		Process p;
		String exStr = "";
		if (host != null) {
			exStr = "nslookup " + domain + "  " + host;
		} else {
			exStr = "nslookup " + domain;
		}
		try {
			p = r.exec(exStr);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String inline;
			while ((inline = br.readLine()) != null) {
				if (!"".equals(inline)) {
					buffer.append(inline + "/n");
				}
			}
			br.close();
			if (buffer.toString().toLowerCase().indexOf("request timed out") >= 0
					|| buffer.toString().indexOf("request timed out") >= 0) {
				resultFlag = false;
			} else {
				resultFlag = true;
			}
		} catch (IOException e) {
			logger.info(e);
		}
		return resultFlag;
	}
}
