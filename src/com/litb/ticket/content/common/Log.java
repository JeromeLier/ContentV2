package com.litb.ticket.content.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

	public static StringBuilder sb = new StringBuilder();
	
	public static void info(Object msg){
		if (msg == null) {
			msg = "null";
		}
		System.out.println(msg);
		sb.append(msg);
		sb.append("\r\n");
	}
	
	public static void info(){
		System.out.println();
		sb.append("\r\n");
	}

	public static void error(Object msg){
		if (msg == null) {
			msg = "null";
		}
		System.err.println(msg);
		sb.append(msg);
		sb.append("\r\n");
	}
	
	public static void error(){
		System.err.println();
		sb.append("\r\n");
	}

	public static void output(String key){

		SimpleDateFormat format = new SimpleDateFormat(".MMdd.HHmmss");
		String logFilePath = "log/" + key + format.format(new Date()) + ".log"; 
		FileUtils.writeFile(logFilePath, sb.toString());
		sb = new StringBuilder();
	}

	public static void output(String key, String text){

		SimpleDateFormat format = new SimpleDateFormat(".MMdd.HHmmss");
		String logFilePath = "log/" + key + format.format(new Date()) + ".log"; 
		FileUtils.writeFile(logFilePath, text);
	}
}
