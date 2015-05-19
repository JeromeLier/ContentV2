package com.litb.ticket.content.Bayes;

import java.math.BigDecimal;

import com.litb.ticket.content.common.FileUtils;
import com.litb.ticket.content.common.Log;

public class GetTotalRate {

	/**
	 * @param args �����������������������֤��ȷ��
	 */
	public static void main(String[] args) {
		
		/**
		 * ��ȡÿһ����������������
		 */
		Log.info("");
		Log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>");
		Log.info("��ȡresultList");
		String filePath = "log/RegressBayesModel.0514.122914.log";
	
		/**
		 *�õ������ݽ��в�� ������Ƿ��ൽ5_n_en rateA���� ��ȷ 
		 *
		 *5_n_en_rateB ���rateB < rateA ����û��ƥ���ϡ�                                         ������5_n_en count
		 *5_1_en_rateB ���rateB ��= rateA ����û��ƥ�����             ��ȷ�ʽ���         ������ɶ�أ����������������� 5_n/1/2_en_cnt��
		 *  
		 */
		String[][] model = FileUtils.readFile(filePath, "\t", "\r\n");
		Log.info("model.cnt=" + model.length);
		
		// Map<keyword, model[]>
		StringBuilder sb = new StringBuilder();	

		
		sb.append("Ȩֵ");// bayes
		sb.append("\t");
		sb.append("û��ƥ����Cnt"); // matchEror
		sb.append("\t");
		sb.append("5_N_en_cnt"); // match
		sb.append("\t");
		sb.append("����������5 ��n����"); //rightErTotal
		sb.append("\t");
		sb.append("�������cnt"); //selectTotal
		sb.append("\t");
		sb.append("reason 5cnt");   // model.length
		sb.append("\r\n");
		for(double bayesRate = 0.10; bayesRate <= 1.00; bayesRate+= 0.10) {
			  
			BigDecimal b = new BigDecimal(bayesRate);  
			double bayesRateScale   =  b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();  
			
			
			int total  = 0; //reason 5 ����
			int matchErTotal = 0;
			int matchTotal = 0;
			int match = 0;
			
			int rightErTotal = 0;
			int selectTotal = 0;
			for (int i = 1; i < model.length; i++) {
				
				//��ȡ��һ������
				String[] arr = model[i];
				if(Integer.parseInt(arr[0]) != 5) {
					continue;
				} 
				total ++;
				if(Double.parseDouble(arr[3]) >= bayesRateScale) {
					// ����Ȩֵ�˲�������
					selectTotal ++;
				}
				if (arr[1].equals("N")) {
					// 5������N�ĸ���
					match ++;
				}
				
				if((arr[1].equals("N")) && (Double.parseDouble(arr[3]) < bayesRateScale)) {
					// ��ȷ�� û��ƥ���� ����ƥ����
					matchErTotal ++;
				} 
				if((!arr[1].equals("N")) && (Double.parseDouble(arr[3]) >= bayesRateScale)) {
					// ����൱�ڲ����˴���  ����׼ȷ��
					rightErTotal ++;
				} 
				if((!arr[1].equals("N")) && (Double.parseDouble(arr[3]) < bayesRateScale)) {
					//�����������˵�� �� 5_1_en  0,02(��С) ������ȷ�ķ���
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
