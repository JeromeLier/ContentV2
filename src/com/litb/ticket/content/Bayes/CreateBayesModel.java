package com.litb.ticket.content.Bayes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.litb.ticket.content.common.Constant;
import com.litb.ticket.content.common.FileUtils;
import com.litb.ticket.content.common.Log;
import com.litb.ticket.content.common.BayesUtil;


/**
 * 运行时需要增加参数 -Xms40m -Xmx1512m
 * 
 * @author kelvem
 *
 */
public class CreateBayesModel {

	public static void main(String[] args) {

		/**
		 * 读取ContentList
		 */
		Log.info("");
		Log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>");
		Log.info("读取ContentList");
		String filePath = "data/keyword_en_R1456_20140901_20150410.txt";
		
		String[][] data = FileUtils.readFile(filePath, Constant.SplitCell, Constant.SplitRow);
		Log.info("cnt=" + data.length);

		/**
		 * Map<reason_csReason_language_keyword, count>
		 */
		Map<String, Integer> cntMap = new HashMap<String, Integer>();
				
		for (int i = 2; i < data.length; i++) {

			if (data[i][0] == null) {
				continue;
			}
			
			String reason 		= data[i][0].replace("\r\n", "");
			String csReason 	= data[i][1].replace("\r\n", "");
			String language 	= data[i][2].replace("\r\n", "");
			String content 		= data[i][4];
			
//			if (!language.equals("en")) {
//				continue;
//			}
			
			// 拆分word
			List<String> wordList = BayesUtil.getWords(content);
			for (String keyword : wordList) {
				
				String reason_csReason_language_keyword = reason + "_" + csReason + "_" + language + "_" + keyword;
				if (cntMap.containsKey(reason_csReason_language_keyword)) {
					cntMap.put(reason_csReason_language_keyword, cntMap.get(reason_csReason_language_keyword) + 1);
				} else {
					cntMap.put(reason_csReason_language_keyword, 1);
				}
			}
		}
		
		/**
		 * Map<reason_csReason_language, count>
		 * 计算 分组下reason_csReason_language出现词汇的个数 比如('5_1_en', 10000) 这个分组下出现了一万个单词
		 */
		Map<String, Integer> reason_csReason_language_cnt_map = new HashMap<String, Integer>();
		for (String reason_csReason_language_keyword : cntMap.keySet()) {
			String[] buf = reason_csReason_language_keyword.split("_");

			String reason = buf[0];
			String csReason = buf[1];
			String language = buf[2];
			String keyword = buf[3];
			Integer cnt = cntMap.get(reason_csReason_language_keyword);
			
			String key = reason + "_" + csReason + "_" + language;
			if (reason_csReason_language_cnt_map.containsKey(key)) {
				reason_csReason_language_cnt_map.put(key, reason_csReason_language_cnt_map.get(key) + cnt);
			} else {
				reason_csReason_language_cnt_map.put(key, cnt);
			}
		}
		
		/**
		 * Map<reason_language_keyword, count>
		 * 计算 分组下reason_language_keyword 出现词汇的个数 比如('5_en_want', 10000) 每一个单词在这个reason 5 下英语出现的次数
		 */
		Map<String, Integer> reason_language_keyword_cnt_map = new HashMap<String, Integer>();
		for (String reason_csReason_language_keyword : cntMap.keySet()) {
			String[] buf = reason_csReason_language_keyword.split("_");

			String reason = buf[0];
			String csReason = buf[1];
			String language = buf[2];
			String keyword = buf[3];
			Integer cnt = cntMap.get(reason_csReason_language_keyword);
			
			String key = reason + "_" + language + "_" + keyword;
			if (reason_language_keyword_cnt_map.containsKey(key)) {
				reason_language_keyword_cnt_map.put(key, reason_language_keyword_cnt_map.get(key) + cnt);
			} else {
				reason_language_keyword_cnt_map.put(key, cnt);
			}
		}
		
		/**  
		 * Map<reason_language, count>
		 * 计算 分组下reason_language 出现词汇的个数 比如('5_en', 10000) reason 5  en 单词的总数
		 */
		Map<String, Integer> reason_language_cnt_map = new HashMap<String, Integer>();
		for (String reason_csReason_language_keyword : cntMap.keySet()) {
			String[] buf = reason_csReason_language_keyword.split("_");

			String reason = buf[0];
			String csReason = buf[1];
			String language = buf[2];
			String keyword = buf[3];
			Integer cnt = cntMap.get(reason_csReason_language_keyword);
			
			String key = reason + "_" + language;
			if (reason_language_cnt_map.containsKey(key)) {
				reason_language_cnt_map.put(key, reason_language_cnt_map.get(key) + cnt);
			} else {
				reason_language_cnt_map.put(key, cnt);
			}
		}
				
		/**
		 * Map<reason_csReason_language_keyword_PA, count>
		 * 这个单词（eg： want）进入到该reason下的概率         单词出现频率cnt/这个组下单词的总个数
		 */
		Map<String, Double> reason_csReason_language_keyword_PA_map = new HashMap<String, Double>();
		for (String reason_csReason_language_keyword : cntMap.keySet()) {
			String[] buf = reason_csReason_language_keyword.split("_");

			String reason = buf[0];
			String csReason = buf[1];
			String language = buf[2];
			String keyword = buf[3];
			Integer cnt = cntMap.get(reason_csReason_language_keyword);
			
			if (reason_csReason_language_keyword_PA_map.containsKey(reason_csReason_language_keyword)) {
				throw new RuntimeException("haha");
			}
			
			String key = reason + "_" + csReason + "_" + language;
			Double PA = 1.0 * cnt / reason_csReason_language_cnt_map.get(key);
			reason_csReason_language_keyword_PA_map.put(reason_csReason_language_keyword, PA);
		}
		
		/**
		 * Map<reason_csReason_language_keyword_PB, count>
		 *  want 这个单词（总cnt - 这个reason转到CSReason的总个数）/ （这个reason 语言下总个数 - 这个reason 这个CSreason下语言的总个数） 
		 */
		Map<String, Double> reason_csReason_language_keyword_PB_map = new HashMap<String, Double>();
		for (String reason_csReason_language_keyword : cntMap.keySet()) {
			String[] buf = reason_csReason_language_keyword.split("_");

			String reason = buf[0];
			String csReason = buf[1];
			String language = buf[2];
			String keyword = buf[3];
			Integer cnt = cntMap.get(reason_csReason_language_keyword);
			
			if (reason_csReason_language_keyword_PB_map.containsKey(reason_csReason_language_keyword)) {
				throw new RuntimeException("haha");
			}
			
			String key1 = reason + "_" + csReason + "_" + language;
			String key2 = reason + "_" + language;
			String key3 = reason + "_" + language + "_" + keyword;
			Double PB = 1.0 * (reason_language_keyword_cnt_map.get(key3) - cnt) 
					/ (reason_language_cnt_map.get(key2) - reason_csReason_language_cnt_map.get(key1));
			reason_csReason_language_keyword_PB_map.put(reason_csReason_language_keyword, PB);
		}
		
		/**
		 * Map<reason_csReason_language_keyword_Bayes, count>
		 * 计算贝叶斯：PA/(PA+PB)
		 */
		Map<String, Double> reason_csReason_language_keyword_Bayes_map = new HashMap<String, Double>();
		for (String reason_csReason_language_keyword : cntMap.keySet()) {
			String[] buf = reason_csReason_language_keyword.split("_");

			String reason = buf[0];
			String csReason = buf[1];
			String language = buf[2];
			String keyword = buf[3];
			Integer cnt = cntMap.get(reason_csReason_language_keyword);
			Double PA = reason_csReason_language_keyword_PA_map.get(reason_csReason_language_keyword);
			Double PB = reason_csReason_language_keyword_PB_map.get(reason_csReason_language_keyword);
			
			if (reason_csReason_language_keyword_Bayes_map.containsKey(reason_csReason_language_keyword)) {
				throw new RuntimeException("haha");
			}
			
			if (PA+PB == 0) {
				throw new RuntimeException("hehehehehehhehe");
			}
			
			reason_csReason_language_keyword_Bayes_map.put(reason_csReason_language_keyword, PA/(PA+PB));
			if (csReason.equals("N")) {
				reason_csReason_language_keyword_Bayes_map.put(reason + "_Y_" + language + "_" + keyword, PB/(PA+PB));
			}
		}
		
		
		// output
		StringBuilder sb = new StringBuilder();
		
		sb.append("reason\tcs_reason\tlanguage\tkeyword\tcnt\tr_csR_l_cnt\tr_l_cnt\tr_l_k_cnt\tPA\tPB\tBayes\r\n");
		
		for (String reason_csReason_language_keyword : cntMap.keySet()) {
			String[] buf = reason_csReason_language_keyword.split("_");
			String reason = buf[0];
			String csReason = buf[1];
			String language = buf[2];
			String keyword = buf[3];
			
			String key1 = reason + "_" + csReason + "_" + language;
			String key2 = reason + "_" + language;
			String key3 = reason + "_" + language + "_" + keyword;
			
			Integer cnt = cntMap.get(reason_csReason_language_keyword);
			//reason csreason laniuagCode  cnt
			Integer r_csR_l_cnt = reason_csReason_language_cnt_map.get(key1);
			
			Integer r_l_cnt = reason_language_cnt_map.get(key2);
			
			Integer r_l_k_cnt = reason_language_keyword_cnt_map.get(key3);
			
			Double PA = reason_csReason_language_keyword_PA_map.get(reason_csReason_language_keyword);
			Double PB = reason_csReason_language_keyword_PB_map.get(reason_csReason_language_keyword);
			Double Bayes = reason_csReason_language_keyword_Bayes_map.get(reason_csReason_language_keyword);
			
			
			sb.append(reason);
			sb.append("\t");
			sb.append(csReason);
			sb.append("\t");
			sb.append(language);
			sb.append("\t");
			sb.append(keyword);
			sb.append("\t");
			sb.append(cnt);
			sb.append("\t");
			sb.append(r_csR_l_cnt);
			sb.append("\t");
			sb.append(r_l_cnt);
			sb.append("\t");
			sb.append(r_l_k_cnt);
			sb.append("\t");
			sb.append(PA);
			sb.append("\t");
			sb.append(PB);
			sb.append("\t");
			sb.append(Bayes);
			sb.append("\r\n");
		}
		
		Log.info(sb.toString());
		Log.output("CreateBayesModel");
	}

}
