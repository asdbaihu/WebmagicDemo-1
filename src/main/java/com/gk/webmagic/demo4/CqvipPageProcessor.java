package com.gk.webmagic.demo4;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;

/**
 * 
 * @author gk
 *
 * 2017年3月31日
 */
public class CqvipPageProcessor implements PageProcessor {
	
	private static Logger logger = LoggerFactory.getLogger(CqvipPageProcessor.class);
	
	private static final String listUrlPattern = "^http://www.cqvip.com/data/main/search.aspx\\?action=so&tid=7&rid=0&curpage=\\d+&perpage=\\d+";

	@Override
	public void process(Page page) {
		String url = page.getUrl().get();
		if("1".equals(Util.urlReqSingleVal(url, "startFlag"))) {
			//起始链接
			String recordcount = new JsonPathSelector("$.recordcount").selectList(page.getRawText()).get(0);
			int pageNum = Util.getPageNum(Integer.valueOf(recordcount), 100);
			logger.info("共有{}条记录，分{}页!",recordcount,pageNum);
			List<String> urList = genCrwalUrl(pageNum);
			if(null != urList && urList.size() > 0) {
				page.addTargetRequests(urList);
			}
			
		}else if(page.getUrl().regex(listUrlPattern).match()){
			
		}
		
	}
	
	private List<String> genCrwalUrl(int pageNum) {
		if(pageNum > 1) {
			List<String> urList = new ArrayList<>();
			for (int i = 2; i <= pageNum; i++) {
				String url = "http://www.cqvip.com/data/main/search.aspx?action=so&tid=7&rid=0&curpage="+i+"&perpage=100";
				urList.add(url);
			}
			return null != urList && urList.size() > 0? urList : null;
		}
		return null;
	}

	@Override
	public Site getSite() {
		return Site.me().setTimeOut(50 * 1000).setRetryTimes(5).setCycleRetryTimes(5).setSleepTime(10 * 1000);
	}
	
	public static void main(String[] args) {
		String startUrl = "http://www.cqvip.com/data/main/search.aspx?action=so&tid=7&rid=0&curpage=1&perpage=100&startFlag=1";
		Spider.create(new CqvipPageProcessor()).addPipeline(new CqvipPipeline())
			.setDownloader(new MyHttpClientDownloader())
				.addUrl(startUrl).thread(10).run();
	}
}
