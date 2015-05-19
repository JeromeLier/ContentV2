package com.litb.ticket.content.Bayes;

import java.util.HashMap;
import java.util.Map;

import com.litb.ticket.content.common.BayesUtil;
import com.litb.ticket.content.common.Constant;
import com.litb.ticket.content.common.FileUtils;
import com.litb.ticket.content.common.Log;

/**
 * 运行时需要增加参数 -Xms40m -Xmx1512m
 * 
 * @author kelvem
 *
 */
public class RegressBayesModel {

	public static void main(String[] args) {

		/**
		 * 读取ContentList
		 */
		Log.info("");
		Log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>");
		Log.info("读取ContentList");
		String filePath = "data/keyword_en_R1456_20140901_20150410.txt";
		
		String[][] data = FileUtils.readFile(filePath, Constant.SplitCell, Constant.SplitRow);
		Log.info("Content.cnt=" + data.length);

		/**
		 * 读取ContentList
		 */
		Log.info("");
		Log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>");
		Log.info("读取ModelList");
		filePath = "data/CreateBayesModel.0512.191627.log";
		
		String[][] model = FileUtils.readFile(filePath, "\t", "\r\n");
		Log.info("model.cnt=" + model.length);
		
		// Map<keyword, model[]>
		Map<String, String[]> modelMap = new HashMap<String, String[]>();
		for (int i = 1; i < model.length; i++) {
			String[] arr = model[i];
			String key = arr[0] + "_" + arr[1] + "_" + arr[2] + "_" + arr[3];
			modelMap.put(key, arr);
		}
		
		
		// output
		StringBuilder sb = new StringBuilder();

		sb.append("reason\tcs_reason\tlanguage\tBayes_N\tcontent\r\n");
		
//		for (int i = 2; i < 100; i++) {
		for (int i = 2; i < data.length; i++) {

			if (data[i][0] == null) {
				continue;
			}
			
			String reason 		= data[i][0].replace("\r\n", "");
			String csReason 	= data[i][1].replace("\r\n", "");
			String language 	= data[i][2].replace("\r\n", "");
			String content 		= data[i][4];
			
//			if (!language.equals("pt")) {
//				continue;
//			}
			
			Double Bayes_N = BayesUtil.getBayes(content, reason, "N", language, modelMap);
			
			content = BayesUtil.filterSpecialChar(content);
			sb.append(reason);
			sb.append("\t");
			sb.append(csReason);
			sb.append("\t");
			sb.append(language);
			sb.append("\t");
			sb.append(Bayes_N);
			sb.append("\t");
//			sb.append(content);
			sb.append("\r\n");
		}
		
		Log.info(sb.toString());
		Log.output("RegressBayesModel");
	}

}
