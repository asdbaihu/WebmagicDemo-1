package com.gk.webmagic.demo4;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {
	
	private static Logger logger = LoggerFactory.getLogger(Util.class);

	public static String formatUrl(String url) {
		url = url.replaceAll(" ", "%20");
		return url;
	}
	
	/**
	 * 获取url中参数的值
	 * @param url
	 * @param key
	 * @return
	 */
	public static String urlReqSingleVal(String url, String key) {
		try {
			List<NameValuePair> params = URLEncodedUtils.parse(new URI(formatUrl(url)), "utf-8");
			for (NameValuePair p : params) {
				if (p.getName().equals(key)) {
					String value = p.getValue();
					return value;
				}
			}
			return "";
		} catch (URISyntaxException e) {
			logger.error("-- url解析失败", e);
		}
		return null;
	}
	
	/**
	 * 字符规范化
	 * @param normalization
	 * @return
	 */
	public static String Snormalization(String normalization){
		if(StringUtils.isBlank(normalization)){
			return null;
		}
		String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’\"。，、？《》·.-〈〉-]";
		Pattern pattern = Pattern.compile(regEx);
		normalization = normalization.replaceAll("\\s", "");//去除所有的空白字符
		Matcher m = pattern.matcher(normalization);
		return m.replaceAll("").trim().toLowerCase();
	}
	
	/** 
	 * 相似度转百分比 
	 */
	public static String similarityResult(double resule) {
		return NumberFormat.getPercentInstance(new Locale("en ", "US ")).format(resule);
	}
	
	/**
	 * 去除字符串中的无用符号
	 * @param str
	 * @return
	 */
	public static String removeSign(String str) {
		if(StringUtils.isNotEmpty(str)) {
			StringBuffer sb = new StringBuffer();
			for (char item : str.toCharArray())
				if (charReg(item)) {
					sb.append(item);
				}
			return sb.toString();
		}
		return null;
	}

	private static boolean charReg(char charValue) {
		return (charValue >= 0x4E00 && charValue <= 0X9FA5) || (charValue >= 'a' && charValue <= 'z')
				|| (charValue >= 'A' && charValue <= 'Z') || (charValue >= '0' && charValue <= '9');
	}

	/**
	 * 字符串相似度算法
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static double getSimilarScore(String str1,String str2) {  
		if(StringUtils.isEmpty(str1) || StringUtils.isEmpty(str2)) {
			return 0;
		}
        //计算两个字符串的长度。  
        int len1 = str1.length();  
        int len2 = str2.length();  
        //建立上面说的数组，比字符长度大一个空间  
        int[][] dif = new int[len1 + 1][len2 + 1];  
        //赋初值，步骤B。  
        for (int a = 0; a <= len1; a++) {  
            dif[a][0] = a;  
        }  
        for (int a = 0; a <= len2; a++) {  
            dif[0][a] = a;  
        }  
        //计算两个字符是否一样，计算左上的值  
        int temp;  
        for (int i = 1; i <= len1; i++) {  
            for (int j = 1; j <= len2; j++) {  
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {  
                    temp = 0;  
                } else {  
                    temp = 1;  
                }  
                //取三个值中最小的  
                dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1,  
                        dif[i - 1][j] + 1);  
            }  
        }  
        //取数组右下角的值，同样不同位置代表不同字符串的比较  
//        System.out.println("差异步骤："+dif[len1][len2]);  
        //计算相似度  
        double similarity =1 - (double) dif[len1][len2] / Math.max(str1.length(), str2.length());  
//        logger.info("字符串\"{}\"与\"{}\"的比较，相似度{}",str1,str2,similarity);
        return similarity;
    }  
	
	 //得到最小值  
    private static int min(int... is) {  
        int min = Integer.MAX_VALUE;  
        for (int i : is) {  
            if (min > i) {  
                min = i;  
            }  
        }  
        return min;  
    }  
    
    /**
     * 计算页数
     * @param total
     * @param pageSize
     * @return
     */
    public static int getPageNum(int total,int pageSize) {
    	if(pageSize == 0) {
    		return 0;
    	}
    	
    	int count = total / pageSize;
    	int mod = total % pageSize;
    	if(mod > 0) {
    		count ++;
    	}
    	return count;
    }
}
