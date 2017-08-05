package com.gk.webmagic.test;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.gk.webmagic.demo2.bean.WanFangJournalBean;
import com.gk.webmagic.demo2.util.Util;

public class Demo2Test {
	
	@Test
	public void test01() {
	}
	
	private String dealDetailInfo(String info) {
		if(StringUtils.isNotEmpty(info)) {
			info = StringUtils.trim(info);
			info = info.replaceAll("：", "");
		}
		return info;
	}
	
	@Test
	public void test02() {
		System.out.println(dealDetailInfo("：Unitarian Universalist Association"));
	}
	
	@Test
	public void test03() {
		List<String> aList = new ArrayList<String>(){{
			add("1");
			add("2");
			}
		};
		
		List<String> bList = new ArrayList<String>(){{
			add("3");
			add("4");
			}
		};
		aList.addAll(bList);
		for (String string : aList) {
			System.out.println(string);
		}
	}
	
	@Test
	public void test04() {
		String url = "\10038469\1.jpeg";
		int i  = StringUtils.indexOf("\\", url);
		System.out.println(i);
	}
	
	@Test
	public void test05() {
		String str = "文化名城讲坛韩田鹿谈聊斋与环保";
		String str2 = "文化名城讲坛";
		System.out.println(Util.Snormalization(str));
		System.out.println(Util.Snormalization(str2));
//		System.out.println(Util.comparasionScore(str, str2));
		Util.getSimilarScore(str, str2);
	}
	
	@Test
	public void test06() {
		String text = "小说评点文体的独立:从子史之评到文学之评——刘辰翁《世说新语》“”\".]评点的源流及意义论析";
		System.out.println(Util.removeSign(text));
	}
	
	@Test
	public void test07() {
		LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
		queue.add("1");
		queue.add("2");
		queue.add("3");
		Iterator<String> iterator = queue.iterator();
		while(iterator.hasNext()) {
			System.out.println(iterator.next());
		}
		System.out.println(queue.size());
	}
}
