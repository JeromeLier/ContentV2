package com.litb.ticket.content.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BayesUtil {

	public static List<String> getWords(String article){
		
		article = filterSpecialChar(article);
		
		String[] buf = article.split(" ");
		
		Set<String> wordSet = new HashSet<String>();
		for (String word : buf) {
			if (word.trim().length() > 0) {
				wordSet.add(word.trim().toLowerCase());
			}
		}
		List<String> result = new ArrayList<String>();
		result.addAll(wordSet);
		
		return result;
	}
	
	public static List<String> get3Words(String article){
		article = filterSpecialChar(article);
		
		String[] buf = article.split(" ");
		
		Set<String> wordSet = new HashSet<String>();
		for (String word : buf) {
			if (word.trim().length() > 0) {
				wordSet.add(word.trim().toLowerCase());
			}
		}
		List<String> result = new ArrayList<String>();
		result.addAll(wordSet);
		
		List<String> result2 = new ArrayList<String>();
		for (int i = 0; i < result.size() - 2; i++) {
			result2.add(result.get(i) + " " + result.get(i + 1) + " " + result.get(i + 2));
		}
		
		return result2;
	}
	
	public static List<String> getNWords(String article, Integer n){

		article = filterSpecialChar(article);
		
		String[] buf = article.split(" ");
		
		Set<String> wordSet = new HashSet<String>();
		for (String word : buf) {
			if (word.trim().length() > 0) {
				wordSet.add(word.trim().toLowerCase());
			}
		}
		List<String> result = new ArrayList<String>();
		result.addAll(wordSet);
		
		List<String> result2 = new ArrayList<String>();
		for (int i = 0; i < result.size() - (n-1); i++) {
			String nWord = "";
			for (int j = 0; j < n; j++) {
				nWord += result.get(i+j) + " ";
			}
			result2.add(nWord.trim());
		}
		
		return result2;
	}
	
	public static String filterSpecialChar(String article){
		
		article = article.replace(".", " ").replace("?", " ").replace("-", " ").replace("!", " ").replace(",", " ").replace("_", " ");
		article = article.replace("#", " ").replace("&", " ").replace("@", " ").replace("+", " ").replace("/", " ");
		article = article.replace("`", " ").replace("|", " ").replace("´", " ").replace("=", " ");
		article = article.replace("•", " ").replace("°", " ").replace("€", " ").replace("§", " ").replace("®", " ");
		article = article.replace("\r", " ").replace("\n", " ").replace("\t", " ").replace("*", " ");
		article = article.replace("\\r", " ").replace("\\n", " ").replace("\\t", " ").replace("\\", " ");
		article = article.replace(";", "").replace("\"", "").replace("'", "").replace("$", "").replace("%", "").replace(":", "");
		article = article.replace("(", "").replace(")", "").replace("[", "").replace("]", "").replace("{", "").replace("}", "").replace("<", "").replace(">", "");
		article = article.replaceAll("[0-9]+", " ").trim();
		article = article.replaceAll("[ ]+", " ").trim();
		
		return article;
	}
	
	public static Double getBayes(String content, String reason, String csReason, String language, Map<String, String[]> modelMap ){
		
		Double PAx = 1.0;
		Double PBx = 1.0;
		
		// 拆分word
		List<String> wordList = BayesUtil.getWords(content);
		for (String keyword : wordList) {
			
			String key = reason + "_" + csReason + "_" + language + "_" + keyword;
			
			if (!modelMap.containsKey(key)) {
				PAx = 1.0;
				PBx = 1.0;
				continue;
			}
			String[] modelArr = modelMap.get(key);

			Double PA = Double.valueOf(modelArr[8]);
			Double PB = Double.valueOf(modelArr[9]);
			
			PAx *= PA;
			PBx *= PB;
		}
		
		Double Bayes = 0.0;
		if (PAx + PBx == 0.0) {
			Bayes = -1.0;
		} else {
			Bayes = PAx / (PAx + PBx);
		}
		
		if (Bayes > 0.98) {
			Bayes = 1.0;
		}

		if (Bayes < 0.02) {
			Bayes = 0.0;
		}
		
		return Bayes;
	}

	
	public static Double getBayesV2(String content, Map<String, String[]> modelMap ){
		
		Double PAx = 1.0;
		Double PBx = 1.0;
		
		// 拆分word
		List<String> wordList = BayesUtil.getWords(content);
		for (String keyword : wordList) {
			
			if (!modelMap.containsKey(keyword)) {
//				PAx = 1.0;
//				PBx = 1.0;
				throw new RuntimeException("keyword=" + keyword);
//				System.out.println("keyword=" + keyword);
//				continue;
			}
			String[] modelArr = modelMap.get(keyword);

			Double bayes = Double.valueOf(modelArr[2]);
			if (bayes > 1) {
				throw new RuntimeException("bayes=" + bayes);
			}
			PAx *= bayes;
			PBx *= 1 - bayes;
		}
		
		Double Bayes = 0.0;
		if (PAx + PBx == 0.0) {
			throw new RuntimeException("bayes");
//			Bayes = -1.0;
		} else {
			Bayes = PAx / (PAx + PBx);
		}
		
		if (Bayes > 0.98) {
			Bayes = 1.0;
		}

//		if (Bayes < 0.02) {
//			Bayes = 0.0;
//		}
		
		return Bayes;
	}
	
	
}
