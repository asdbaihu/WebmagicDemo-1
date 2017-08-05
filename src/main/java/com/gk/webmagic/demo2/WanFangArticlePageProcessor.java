package com.gk.webmagic.demo2;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gk.webmagic.demo2.bean.WanFangArticleBean;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

public class WanFangArticlePageProcessor implements PageProcessor {
	
	private static Logger logger = LoggerFactory.getLogger(WanFangArticlePageProcessor.class);

	// 期刊主页面URL
	private static final String journalUrlPattern = "^http://c.wanfangdata.com.cn/Periodical-\\w+(-\\w*)*.aspx";
	// 期刊每期页面URL
	private static final String articleUrlPattern = "^http://c.wanfangdata.com.cn/periodical/\\w+(-\\w*)*/\\d{4}-\\w+.aspx";
	//CNKI中的docId
	private String docId;
	//期刊名称
	private String journalName;
	
	public WanFangArticlePageProcessor() {
		
	}
	
	public WanFangArticlePageProcessor(String docId,String journalName) {
		this.docId = docId;
		this.journalName = journalName;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}
	
	public String getJournalName() {
		return journalName;
	}

	public void setJournalName(String journalName) {
		this.journalName = journalName;
	}

	@Override
	public void process(Page page) {
		if (page.getUrl().regex(journalUrlPattern).match()) {
			Html html = page.getHtml();
			List<String> allLinks = html.$(".new_ul5.year-issue-list").links().all();
			page.addTargetRequests(allLinks);
		}
		if (page.getUrl().regex(articleUrlPattern).match()) {
			String[] info = getArticleInfo(page.getUrl().get());
			if(null != info) {
				String pubYear = info[0];//发行年
				String coverIssue = info[1].length() > 1? info[1] : "0" + info[1];//发行期
				Html html = page.getHtml();
				int size = html.$(".qkcontent_ul > li").all().size();
				if(size > 0) {
					List<WanFangArticleBean> articleList = new ArrayList<>();
					for(int i = 1; i <= size; i++) {
						WanFangArticleBean bean = new WanFangArticleBean();
						bean.setJournalName(getJournalName());
						bean.setPubYear(pubYear);
						bean.setCoverIssue(coverIssue);
						String articleName = html.xpath("//ul[@class='qkcontent_ul']/li["+i+"]/a[2]/text()").get();
						bean.setArticleName(articleName);
						String articleUrl = html.xpath("//ul[@class='qkcontent_ul']/li["+i+"]/a[2]/@href").get();
						bean.setArticleUrl(articleUrl);
						articleList.add(bean);
					}
					page.putField("pubYear", pubYear);
					page.putField("coverIssue", coverIssue);
					page.putField("wanfangArticles", articleList);
					page.putField("journalName", getJournalName());
					page.putField("docId", getDocId());
					page.putField("issueUrl", page.getUrl().get());
					logger.info("获取docId={},pubYear={},coverIssue={}的文章列表！",getDocId(),pubYear,coverIssue);
				}
			}
		}
	}
	
	/**
	 * 获取url中发布年与期的信息
	 * @param url
	 * @return
	 */
	private String[] getArticleInfo(String url) {
		if(StringUtils.isNotEmpty(url)) {
			int start = url.lastIndexOf("/");
			int end = url.lastIndexOf(".");
			String info = url.substring(start + 1, end);
			if(info.indexOf("-") > 0) {
				return info.split("-");
			}
		}
		logger.info("获取万方链接中的年月信息出错！");
		return null;
	}
	
	@Override
	public Site getSite() {
		return Site.me().setTimeOut(50 * 1000).setRetryTimes(5).setCycleRetryTimes(5).setSleepTime(10 * 1000);
	}
	
//	public static void main(String[] args) {
//		Spider.create(new WanFangArticlePageProcessor("cnkiFJKJ110","发酵科技通讯")).addPipeline(new WanFangArticlePipeline())
//				.addUrl("http://c.wanfangdata.com.cn/Periodical-fxkjtx.aspx").thread(5).run();
//	}

}
