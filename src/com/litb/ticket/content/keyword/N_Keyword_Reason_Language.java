package com.litb.ticket.content.keyword;

import java.util.Date;
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
public class N_Keyword_Reason_Language {

	
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

		// output
		StringBuilder sb = new StringBuilder();
		
		sb.append("reason\tcs_reason\tlanguage\tsub_reason_lv1\tkeyword\tcnt\ttotal\t关键词个数\r\n");
		
		for (Integer N = 1; N <= 8; N++) {

			Log.info("开始处理" + N + "个词的情况 " + new Date());
			
			// Map<reason_language_sbl1_sbl2_keyword, count>>
			Map<String, Integer> cntMap = new HashMap<String, Integer>();
			
			// Map<reason_language_keyword, count>>
			Map<String, Integer> totalMap = new HashMap<String, Integer>();
					
//			for (int i = 2; i < 3; i++) {
			for (int i = 2; i < data.length; i++) {

				if (data[i][0] == null) {
					continue;
				}
				
				String reason 		= data[i][0].replace("\r\n", "").replace("@", "");
				String csReason 	= data[i][1].replace("\r\n", "");
				String language 	= data[i][2].replace("\r\n", "");
				String subReasonLv1 = data[i][3].replace("\r\n", "");
				String content 		= data[i][4];
				
//				if (!language.equals("en")) {
//					continue;
//				}
				
				// 拆分word
				List<String> wordList = BayesUtil.getNWords(content, N);
				for (String word : wordList) {
					
					String reason_language_sbl1_keyword = reason + "_" + csReason + "_" + language + "_" + subReasonLv1 + "_" + word + "_";
					
					if (cntMap.containsKey(reason_language_sbl1_keyword)) {
						cntMap.put(reason_language_sbl1_keyword, cntMap.get(reason_language_sbl1_keyword) + 1);
					} else {
						cntMap.put(reason_language_sbl1_keyword, 1);
					}

					String reason_language_keyword = reason + "_" + language + "_" + word;
					
					if (totalMap.containsKey(reason_language_keyword)) {
						totalMap.put(reason_language_keyword, totalMap.get(reason_language_keyword) + 1);
					} else {
						totalMap.put(reason_language_keyword, 1);
					}
				}
			}
			
			for (String reason_language_sbl1_keyword : cntMap.keySet()) {
				String[] buf = reason_language_sbl1_keyword.split("_");

				String reason = buf[0];
				String csReason = buf[1];
				String language = buf[2];
				String subReasonLv1 = buf[3];
				String word = buf[4];
				
				Integer cnt = cntMap.get(reason_language_sbl1_keyword);
				Integer total = totalMap.get(reason + "_" + language + "_" + word);
				if (cnt < 100 || 1.0 * cnt / total < 0.5) {
					continue;
				}
				sb.append(reason);
				sb.append("\t");
				sb.append(csReason);
				sb.append("\t");
				sb.append(language);
				sb.append("\t");
				sb.append(subReasonLv1);
				sb.append("\t");
				sb.append(word);
				sb.append("\t");
				sb.append(cnt);
				sb.append("\t");
				sb.append(total);
				sb.append("\t");
				sb.append(N);
				sb.append("\r\n");
			}
			
		}
		
		Log.info(sb.toString());
		Log.output("N_Keyword_Reason_Language");
	}

}
