package com.gk.webmagic.demo1;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * 
 * @author gk
 *
 * 2017年3月2日
 */
public class GithubRepoPageProcessor implements PageProcessor {
	
	//部分一：抓取网站的相关配置，包括编码，抓取间隔，重试次数等等
	private Site site = Site.me().setRetryTimes(5).setSleepTime(1000);

	public Site getSite() {
		return site;
	}
	
	//process方法是定制爬虫逻辑的核心入口
	public void process(Page page) {
		//部分二：定义如何抽取页面信息， 并保存下来
		page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
		System.out.println("-----------------------"+page.getUrl().toString()+"---------------------------");
		System.out.println("-----------------------"+page.getUrl().regex("https://github\\.com/(\\w+)/.*")+"---------------------------");
		page.putField("name", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
		
		if(page.getResultItems().get("name") == null) {
			//跳过这个页面
			page.setSkip(true);
		}
		
		page.putField("readme",page.getHtml().xpath("//div[@id='readme']/tidyText()"));
		
		page.addTargetRequests(page.getHtml().links().regex("https://github\\.com/\\w+/\\w+").all());
	}

	public static void main(String[] args) {
		Spider.create(new GithubRepoPageProcessor())
				.addUrl("https://github.com/gkaigk1987")//从输入的url开始爬取
				.thread(5)//开启5个线程爬取
				.run();//启动
	}
	
}
