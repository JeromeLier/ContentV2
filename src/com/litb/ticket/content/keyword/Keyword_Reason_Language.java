package com.litb.ticket.content.keyword;

import java.util.HashMap;
import java.util.List;
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
public class Keyword_Reason_Language {

	public static void main(String[] args) {

		/**
		 * 读取ContentList
		 */
		Log.info("");
		Log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>");
		Log.info("读取ContentList");
		String filePath = "data/keyword_pt_R1456_20140901_20150410.csv";
		
		String[][] data = FileUtils.readFile(filePath, Constant.SplitCell, Constant.SplitRow);
		Log.info("cnt=" + data.length);

		// Map<reason_language_sbl1, Map<keyword, count>>
		Map<String, Map<String, Integer>> cntMap = new HashMap<String, Map<String, Integer>>();
				
//		for (int i = 2; i < 3; i++) {
		for (int i = 2; i < data.length; i++) {

			if (data[i][0] == null) {
				continue;
			}
			
			String reason 		= data[i][0].replace("\r\n", "");
			String csReason 	= data[i][1].replace("\r\n", "");
			String language 	= data[i][2].replace("\r\n", "");
			String subReasonLv1 = data[i][3].replace("\r\n", "");
			String content 		= data[i][4];
			
			String reason_language_sbl1_sbl2 = reason + "_" + csReason + "_" + language + "_" + subReasonLv1;
			// init
			if (!cntMap.containsKey(reason_language_sbl1_sbl2)) {
				cntMap.put(reason_language_sbl1_sbl2, new HashMap<String, Integer>());
			}
			Map<String, Integer> keywordMap = cntMap.get(reason_language_sbl1_sbl2);
			
			// 拆分word
			List<String> wordList = BayesUtil.getWords(content);
			for (String word : wordList) {
				
				if (keywordMap.containsKey(word)) {
					keywordMap.put(word, keywordMap.get(word) + 1);
				} else {
					keywordMap.put(word, 1);
				}
				
			}
		}
		
		// output
		StringBuilder sb = new StringBuilder();
		
		sb.append("reason\tcs_reason\tlanguage\tsub_reason_lv1\tkeyword\tcnt\r\n");
		
		for (String reason_language : cntMap.keySet()) {
			String[] buf = reason_language.split("_");
			String reason = buf[0];
			String csReason = buf[1];
			String language = buf[2];
			String subReasonLv1 = buf[3];
			
			
			Map<String, Integer> keywordMap = cntMap.get(reason_language);
			for (String keyword : keywordMap.keySet()) {
				Integer cnt = keywordMap.get(keyword);
				sb.append(reason);
				sb.append("\t");
				sb.append(csReason);
				sb.append("\t");
				sb.append(language);
				sb.append("\t");
				sb.append(subReasonLv1);
				sb.append("\t");
				sb.append(keyword);
				sb.append("\t");
				sb.append(cnt);
				sb.append("\r\n");
			}
		}
		
		Log.info(sb.toString());
		Log.output("Keyword_Reason_Language");
	}

}
