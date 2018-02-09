/**
 * Project Name:visitconnect
 * File Name:TestMain.java
 * Package Name:com.surfilter.tmms
 * Date:2017年8月30日上午11:10:45
 *
*/

package com.surfilter.tmms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.surfilter.tmms.bean.VisitReturn;
import com.surfilter.tmms.utils.HttpUtils3;
import com.surfilter.tmms.utils.IPUtils;

/**
 * ClassName:TestMain <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2017年8月30日 上午11:10:45 <br/>
 * @author   huhuan
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */


public class TestMain {

	public static void main(String[] args) {
		try {
			
			//读
			File ipListFile =  new File( "C:\\Users\\quanli\\Desktop\\IP列表.txt" );
			FileReader ipFileRead = new FileReader( ipListFile );
			BufferedReader buffRead = new BufferedReader( ipFileRead );
			
			//写
			File ipResultFile =  new File( "C:\\Users\\quanli\\Desktop\\IP列表-结果.txt" );
			FileWriter ipResultWriter = new FileWriter( ipResultFile );
			BufferedWriter bufferedWriter = new BufferedWriter(ipResultWriter);
			
			String lineTest = null;
			String[] lineArry = null;
			long startIp = 0L;
			long endIp = 0L;
			VisitReturn visitReturn = null;
			StringBuilder sb = new StringBuilder();
			int index = 0;
			while ( ( lineTest = buffRead.readLine() ) != null ) {
				lineArry = lineTest.split("	");
				//拆分
				startIp = IPUtils.Ip2Long(lineArry[0]);
				endIp = IPUtils.Ip2Long(lineArry[1]);
				for( long temp = startIp ; temp <= endIp ; temp++ ){
					sb.setLength(0);
					
					visitReturn = HttpUtils3.connection( IPUtils.long2IP(temp) , 80, false);
					sb.append("IP:");
					sb.append(IPUtils.long2IP(temp));
					sb.append("; port : 80 ; ping结果返回码：" );
					sb.append( visitReturn.getCode() );
					
					visitReturn = HttpUtils3.connection( IPUtils.long2IP(temp) ,8080, false);
					sb.append("; port : 8080 ; ping结果返回码：" );
					sb.append( visitReturn.getCode() );
					
					bufferedWriter.write( sb.toString() );
					bufferedWriter.newLine();
					
					if( index++ > 10000 ){
						System.out.println("批次处理!");
						bufferedWriter.flush();
						index = 0;
					}
				}
			}
			buffRead.close();
			ipFileRead.close();
			
			bufferedWriter.flush();
			bufferedWriter.close();
			ipResultWriter.close();
			System.out.println("处理完成!");
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}

