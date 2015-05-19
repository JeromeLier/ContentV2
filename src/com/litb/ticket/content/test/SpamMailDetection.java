package com.litb.ticket.content.test;

import java.io.*;
import java.util.*;

public class SpamMailDetection {
    public static final String BASE_PATH = "C:\\Users\\zhf\\Desktop\\mail";
    public static final String SPAM_PATH = BASE_PATH + "\\train_illegal.txt";//�����ʼ�����
    public static final String OK_PATH = BASE_PATH + "\\train_legal.txt";//�����ʼ�����
    public static final String EMAIL_PATH = BASE_PATH + "\\to_judge.txt";//Ҫ�б���ʼ�
    public static final String DICT_PATH = BASE_PATH + "\\dict.txt";//�ִ��õĴʵ�

    public static void main(String[] args) {
    	SpamMailDetection smc = new SpamMailDetection();
        //<word,(word/NonSpamCorpus)>
        Map<String, Double> okmap = smc.createMailMap(OK_PATH);
        //<word,(word/SpamCorpus)>
        Map<String, Double> spammap = smc.createMailMap(SPAM_PATH);
        Map<String, Double> ratemap = smc.createSpamProbabilityMap(spammap, okmap);
        double probability = smc.judgeMail(EMAIL_PATH, ratemap);
        if (probability > 0.5)//���ʴ���0.5���ж�Ϊ����
            System.out.println("It's an ok mail.");
        else
            System.out.println("It's a spam mail.");

    }

    /**
     * �����ʼ�,�ִ�,���ݷִʽ���ж��������ʼ��ĸ��� 
     * P(Spam|t1,t2,t3����tn)=��P1*P2*����PN��/(P1*P2*����PN+(1-P1)*(1-P2)*����(1-PN))
     */
    public double judgeMail(String emailPath, Map<String, Double> ratemap) {
        List<String> list = segment(readFile(emailPath));
        double rate = 1.0;
        double tempRate = 1.0;
        for (String str : list) {
            if (ratemap.containsKey(str)) {
                double tmp = ratemap.get(str);
                tempRate *= 1 - tmp;
                rate *= tmp;
            }
        }
        return rate / (rate + tempRate);
    }

    /**
     * �Ӹ����������ʼ��������ʼ������н���map <�г����Ĵ�,���ֵ�Ƶ��>
     */
    public Map<String, Double> createMailMap(String filePath) {
        String str = readFile(filePath);
        List<String> list = segment(str);
        Map<String, Integer> tmpmap = new HashMap<String, Integer>();
        Map<String, Double> retmap = new HashMap<String, Double>();
        double rate = 0.0;
        int count = 0;
        for (String s : list) {
            tmpmap.put(s, tmpmap.containsKey(s) ? count + 1 : 1);
        }
        for (Iterator iter = tmpmap.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            rate = tmpmap.get(key) / list.size();
            retmap.put(key, rate);
        }
        return retmap;
    }

    /**
     * ����map,<str,rate> �ʼ��г���tiʱ,���ʼ�Ϊ�����ʼ��ĸ���
     * P( Spam|ti) =P2(ti )/((P1 (ti ) +P2 ( ti ))
     */
    public Map<String, Double> createSpamProbabilityMap(Map<String, Double> spammap,
            Map<String, Double> okmap) {
        Map<String, Double> retmap = new HashMap<String, Double>();
        for (Iterator iter = spammap.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            double rate = spammap.get(key);
            double allRate = rate;
            if (okmap.containsKey(key)) {
                allRate += okmap.get(key);
            }
            retmap.put(key, rate / allRate);
        }
        return retmap;
    }

    /**
     * ���ķִ�
     */
    public List<String> segment(String str) {
        Map<String, Integer> map = loadDict();
        List<String> list = new ArrayList<String>();
        int len = str.length();
        String term;
        int maxSize = 6;
        int i = 0, j = 0;
        while (i < len) {
            int n = i + maxSize < len ? i + maxSize : len + 1;
            boolean findFlag = false;
            for (j = n - 1; j > i; j--) {
                term = str.substring(i, j);
                if (map.containsKey(term)) {
                    list.add(term);
                    findFlag = true;
                    i = j;
                    break;
                }
            }
            if (findFlag == false)
                i = j + 1;
        }
        return list;
    }

    /**
     * ���شʵ��ļ�
     */
    public Map<String, Integer> loadDict() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        String[] str;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(new File(DICT_PATH)), "gbk"));
            String tmp = "";
            while ((tmp = br.readLine()) != null) {
                str = tmp.split("\t");
                map.put(str[0], 0);
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * ���ļ�
     */
    public String readFile(String filePath) {
        String str = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(new File(filePath)), "gbk"));
            String tmp = "";
            while ((tmp = br.readLine()) != null)
                str += tmp;
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

}