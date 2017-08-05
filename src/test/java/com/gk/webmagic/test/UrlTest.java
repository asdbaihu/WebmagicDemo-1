package com.gk.webmagic.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.Test;

import com.gk.webmagic.demo3.NcpssdPageProcessor;
import com.gk.webmagic.demo3.util.Util;

public class UrlTest {
	
	String url = "http://www.ncpssd.org/journal/details.aspx?gch=64833&nav=1&langType=2&h=自然科学总论";
	
	public static String urlReqSingleVal(String url, String key) {
		try {
			List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), "gbk");
			for (NameValuePair p : params) {
				if (p.getName().equals(key)) {
					String value = p.getValue();
					return value;
				}
			}
			return "";
		} catch (URISyntaxException e) {
		}
		return null;
	}

	@Test
	public void test01() {
		String name = NcpssdPageProcessor.urlReqSingleVal(url, "h");
		System.out.println(name);
	}
	
	@Test
	public void test02() {
		System.out.println(urlReqSingleVal(url,"h"));
	}
	
	@Test
	public void test03() {
		System.out.println(Util.isPic("http://103.247.176.188/cover/7831/10034196/1."));
	}
	
	@Test
	public void test04() {
		String urlPattern = "^http://c.wanfangdata.com.cn/Periodical-\\w+.aspx";
		String url = "http://c.wanfangdata.com.cn/Periodical-pslyj.aspx";
		Pattern pattern = Pattern.compile(urlPattern);
		Matcher m = pattern.matcher(url);
		System.out.println(m.find());
	}
	
	@Test
	public void test05() {
		String urlPattern = "^http://c.wanfangdata.com.cn/periodical/\\w+/\\d{4}-\\w+.aspx";
		String url = "http://c.wanfangdata.com.cn/periodical/bfhj/2016-11.aspx";
		Pattern pattern = Pattern.compile(urlPattern);
		Matcher m = pattern.matcher(url);
		System.out.println(m.find());
	}
	
	@Test
	public void test06() {
		String url = "http://c.wanfangdata.com.cn/periodical/pslyj/2016-4.aspx";
		int start = url.lastIndexOf("/");
		System.out.println(start);
		int end = url.lastIndexOf(".");
		System.out.println(end);
		String part = url.substring(start+1, end);
		System.out.println(part);
		int index = part.indexOf("-");
		if(index > 0) {
			String[] strArr = part.split("-");
			String year = strArr[0];
			String peroid = strArr[1];
			if(peroid.length() <= 1) {
				peroid = "0"+peroid;
			}
			System.out.println(year+"-"+peroid);
		}
	}
	
	@Test
	public void test07() {
		String journalUrlPattern = "^http://c.wanfangdata.com.cn/Periodical-\\w+(-\\w+)*.aspx";
		String url1 = "http://c.wanfangdata.com.cn/Periodical-fkyy-j-j.aspx";
		String url2 = "http://c.wanfangdata.com.cn/Periodical-fxkjtx.aspx";
		Pattern pattern = Pattern.compile(journalUrlPattern);
		Matcher m = pattern.matcher(url1);
		System.out.println("url1:"+m.find());
		m = pattern.matcher(url2);
		System.out.println("url2:"+m.find());
	}
	
	@Test
	public void test08() {
//		String urlPattern = "^http://data.api.gkcx.eol.cn/soudaxue/queryschool.html\\?messtype=jsonp&page=\\d+&size=50&schoolflag=\\S+";
//		String url = "http://data.api.gkcx.eol.cn/soudaxue/queryschool.html\\?messtype=jsonp&page=2&size=50&schoolflag=211工程";
		
		String urlPattern = "http://data.api.gkcx.eol.cn/soudaxue/queryschool.html\\?messtype=jsonp&\\S+";
		String url = "http://data.api.gkcx.eol.cn/soudaxue/queryschool.html\\?messtype=jsonp";
		
		Pattern pattern = Pattern.compile(urlPattern);
		
		Matcher m = pattern.matcher(url);
		System.out.println(m.find());
		
		
	}
	
}
