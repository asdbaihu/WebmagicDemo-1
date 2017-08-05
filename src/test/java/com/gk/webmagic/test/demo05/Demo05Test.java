package com.gk.webmagic.test.demo05;

import org.junit.Test;

import com.gk.webmagic.demo5.util.Util;

public class Demo05Test {
	
	@Test
	public void test01() {
		String str = "Submit.naviSearch('1','168专题代码','A','基础科学')";
		String[] strs = str.split(",");
		System.out.println(strs[2].replaceAll("'", ""));
	}
	
	@Test
	public void test02() {
		String url = "http://navi.cnki.net/knavi/Common/Search/%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20/KNavi/pubDetail?pubtype=journal&pcode=CJFD&baseid=DQXX";
		url = Util.urlReplace(url);
		System.out.println(url);
	}

}
