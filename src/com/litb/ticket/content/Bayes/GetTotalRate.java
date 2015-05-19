package com.litb.ticket.content.Bayes;

import java.math.BigDecimal;

import com.litb.ticket.content.common.FileUtils;
import com.litb.ticket.content.common.Log;

public class GetTotalRate {

	/**
	 * @param args 结果分析分析这个结果，来验证正确率
	 */
	public static void main(String[] args) {
		
		/**
		 * 读取每一个结果集。计算比率
		 */
		Log.info("");
		Log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>");
		Log.info("读取resultList");
		String filePath = "log/RegressBayesModel.0514.122914.log";
	
		/**
		 *拿到的数据进行拆分 分类就是分类到5_n_en rateA算作 正确 
		 *
		 *5_n_en_rateB 如果rateB < rateA 代表没有匹配上。                                         总数是5_n_en count
		 *5_1_en_rateB 如果rateB 》= rateA 代表没有匹配错误             正确率降低         总数是啥呢（分类进这里面的数据 5_n/1/2_en_cnt）
		 *  
		 */
		String[][] model = FileUtils.readFile(filePath, "\t", "\r\n");
		Log.info("model.cnt=" + model.length);
		
		// Map<keyword, model[]>
		StringBuilder sb = new StringBuilder();	

		
		sb.append("权值");// bayes
		sb.append("\t");
		sb.append("没有匹配上Cnt"); // matchEror
		sb.append("\t");
		sb.append("5_N_en_cnt"); // match
		sb.append("\t");
		sb.append("分组内其他5 非n个数"); //rightErTotal
		sb.append("\t");
		sb.append("进入规则cnt"); //selectTotal
		sb.append("\t");
		sb.append("reason 5cnt");   // model.length
		sb.append("\r\n");
		for(double bayesRate = 0.10; bayesRate <= 1.00; bayesRate+= 0.10) {
			  
			BigDecimal b = new BigDecimal(bayesRate);  
			double bayesRateScale   =  b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();  
			
			
			int total  = 0; //reason 5 个数
			int matchErTotal = 0;
			int matchTotal = 0;
			int match = 0;
			
			int rightErTotal = 0;
			int selectTotal = 0;
			for (int i = 1; i < model.length; i++) {
				
				//获取这一行数据
				String[] arr = model[i];
				if(Integer.parseInt(arr[0]) != 5) {
					continue;
				} 
				total ++;
				if(Double.parseDouble(arr[3]) >= bayesRateScale) {
					// 进入权值了部分数据
					selectTotal ++;
				}
				if (arr[1].equals("N")) {
					// 5下面有N的个数
					match ++;
				}
				
				if((arr[1].equals("N")) && (Double.parseDouble(arr[3]) < bayesRateScale)) {
					// 正确的 没有匹配上 这是匹配率
					matchErTotal ++;
				} 
				if((!arr[1].equals("N")) && (Double.parseDouble(arr[3]) >= bayesRateScale)) {
					// 这就相当于产生了错误  这是准确率
					rightErTotal ++;
				} 
				if((!arr[1].equals("N")) && (Double.parseDouble(arr[3]) < bayesRateScale)) {
					//进到这个里面说明 是 5_1_en  0,02(很小) 就是正确的分类
				}
				
			}
			sb.append(">=" +bayesRateScale);
			sb.append("\t");
			sb.append(matchErTotal);
			sb.append("\t");
			sb.append(match);
			sb.append("\t");
			sb.append(rightErTotal);
			sb.append("\t");
			sb.append(selectTotal);
			sb.append("\t");
			sb.append(total);
			sb.append("\r\n");
			Log.info(sb.toString());
		}
		
		//Log.output("getRateBayesCnt");

	}

}
