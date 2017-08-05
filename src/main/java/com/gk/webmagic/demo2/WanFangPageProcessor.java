package com.gk.webmagic.demo2;

import java.util.ArrayList;
import java.util.List;

import com.gk.webmagic.demo2.bean.WanFangJournalBean;
import com.gk.webmagic.demo2.util.Util;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

public class WanFangPageProcessor implements PageProcessor {
	
//	private static Logger logger = LoggerFactory.getLogger(WanFangPageProcessor.class);

	//字母列表链接匹配模式
	private static final String listUrlPattern = "^http://c.wanfangdata.com.cn/PeriodicalLetter.aspx\\?NodeId=[A-Z]&PageNo=\\d+";
	
	public void process(Page page) {
		String gkstart = Util.urlReqSingleVal(page.getUrl().get(), "gkstart");
		if("1".equals(gkstart)) {
			Html html = page.getHtml();
			//爬取入口，爬取所有首字母期刊链接
			List<String> all = html.xpath("/html/body/div[4]/div[2]/div[3]/div[2]/div").links().all();
			List<String> newUrls = new ArrayList<>();
			if(null != all && all.size() > 0) {
				for (String url : all) {
					url = url + "&PageNo=1";
					newUrls.add(url);
				}
			}
			page.addTargetRequests(newUrls);
		}else {
			if(page.getUrl().regex(listUrlPattern).match()) {
				//字母列表页面
				Selectable urlSelect = page.getUrl();
				String url = urlSelect.get();//获取当前的url
				Html html = page.getHtml();
				//获取当前总共有多少页
				String pageInfo = html.xpath("//span[@class='page_link']/text()").get();
				String pageNum = null;//不存在分页情况
				if(null != pageInfo) {
					pageNum = pageInfo.split("/")[1];
				}
				List<WanFangJournalBean> beans = getPageJournalNames(html);
				page.putField("wfBean", beans);
				//获取当前是第几页
				String pageNo = Util.urlReqSingleVal(url, "PageNo");
				if(Integer.valueOf(pageNo) == 1 && pageNum != null) {
					//仅在存在分页且当前获取的连接是第一页时，取得当前类型后面的所有分页连接
					List<String> pageUrlList = getUrlPageNoList(url,pageNum);
					page.addTargetRequests(pageUrlList);
				}
			}
		}
	}
	
	/**
	 * 获取每页的期刊信息
	 * @param html
	 * @return
	 */
	private List<WanFangJournalBean> getPageJournalNames(Html html) {
		int size = html.$(".list > span").all().size();
		List<WanFangJournalBean> beans = new ArrayList<>();
		if(size > 0) {
			for(int i = 1; i <= size; i++) {
				WanFangJournalBean bean = new WanFangJournalBean();
				String journalUrl = html.xpath("//div[@class='list']/span["+i+"]/a[2]/@href").get().trim();
				bean.setUrl(journalUrl);
				String journalName = html.xpath("//div[@class='list']/span["+i+"]/a[2]/text()").get().trim();
				bean.setName(journalName);
				beans.add(bean);
			}
		}
		return beans;
	}
	
	/**
	 * 分页链接获取
	 * @param url
	 * @param pageNo
	 * @return
	 */
	private List<String> getUrlPageNoList(String url,String pageNo) {
		int num = Integer.parseInt(pageNo);
		if(num > 1) {
			List<String> pageUrlList = new ArrayList<>();
			for(int i = 2; i <= num; i++) {
				url = url.replaceAll("PageNo=\\d+", "PageNo="+i);
				pageUrlList.add(url);
			}
			return pageUrlList;
		}
		return null;
	}

	public Site getSite() {
		return Site.me().setTimeOut(50 * 1000).setRetryTimes(5).setCycleRetryTimes(5).setSleepTime(10 * 1000);
	}
}
