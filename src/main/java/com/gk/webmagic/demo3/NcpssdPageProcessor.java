package com.gk.webmagic.demo3;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.gk.webmagic.demo3.model.NcpssdBeanWithBLOBs;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

/**
 * 国家哲学社会科学文献中心爬虫
 * @author gk
 *
 * 2017年3月9日
 */
public class NcpssdPageProcessor implements PageProcessor {
	
	private static Logger logger = LoggerFactory.getLogger(NcpssdPageProcessor.class);
	
	private String prefixUrl = "http://www.ncpssd.org/journal/list.aspx?p=1";
	
	private static final String LANGTYPE = "&langType=2";
	
	private AtomicInteger count = new AtomicInteger(0);
	
	//列表页面url正则模式
	private static final String listUrlPattern = "^http://www.ncpssd.org/journal/list.aspx\\?p=\\d+&e=\\S+&h=\\S+&langType=\\d+";
//	private static final String listUrlPattern = "^http://www.ncpssd.org/journal/list.aspx\\?p=\\d+&e=\\S+&h=文化科学&langType=\\d+";
	//具体详细页面url
	private static final String detailUrlPattern = "^http://www.ncpssd.org/journal/details.aspx\\?gch=\\d+&nav=\\d+&langType=\\d+";

	public void process(Page page) {
		String tag = urlReqSingleVal(page.getUrl().toString(),"tag");
		if("1".equals(tag)) {
			//url入口
			int size = page.getHtml().xpath("//*[@id='list-subject']/li").all().size();
			if(size > 0) {
				List<String> newUrlList = new ArrayList<String>();
				for(int i = 1; i <= size; i++) {
					String cataName = page.getHtml().xpath("//*[@id='list-subject']/li["+i+"]/a/text()").get();//分类名称
					String urlC = getNcpssdC(page.getHtml().xpath("//*[@id='list-subject']/li["+i+"]").$("a", "onclick").get());
					String newUrl = prefixUrl + "&e="+urlC+"&h="+cataName+LANGTYPE;//分类链接
					newUrlList.add(newUrl);
				}
				page.addTargetRequests(newUrlList);
			}
		}else {
			if(page.getUrl().regex(listUrlPattern).match()) {
				Selectable selectable = page.getHtml().$("h3.term-title","text");
				String catalogName = formatUri(getCatalogName(selectable.get()));
				String p = urlReqSingleVal(page.getUrl().toString(),"p");
				//列表页面
				List<String> detailLisks = page.getHtml().xpath("//*[@id='form1']/div[8]/div/div[2]/div/div[1]/ul").links().all();
				detailLisks = detailLisks.stream().distinct().map(e -> e+"&h="+catalogName).collect(Collectors.toList());//去除重复连接
//				count.getAndAdd(detailLisks.size());
//				logger.info("当前已爬取的链接数{}",count.get());
				if(Integer.valueOf(p) == 1) {
					//只在获取第一页内容时得到该类别下的所有分页后的除去第一页的所有链接
					int count = getNumberFormString(selectable.get());
					if(count > 20) {
						//每页显示20条
						int model = count % 20;
						int result = count / 20;
						if(model > 0) {
							result++;
						}
						List<String> list = urlReplace(page.getUrl().get(),result);
						if(list.size() > 0) {
							detailLisks.addAll(list);
						}
					}
				}
				page.addTargetRequests(detailLisks);
			}else if(page.getUrl().regex(detailUrlPattern).match()){
				String h = formatUri2(urlReqSingleVal(page.getUrl().toString(),"h"));//分类名称
				NcpssdBeanWithBLOBs ncpssdDetail = new NcpssdBeanWithBLOBs();
				//详细页面
				Html html = page.getHtml();
				//名称
				String name = html.xpath("//div[@class='b-content']/h2/text()").get();
				ncpssdDetail.setName(name);
				//简介
				String introduce = html.xpath("//div[@class='b-content']/p/text()").get();
				ncpssdDetail.setIntroduce(introduce);
				//主管单位
				String manageDepartment = dealDetailInfo(html.xpath("//div[@class='b-content']/ul/li[1]/text()").get());
				ncpssdDetail.setManageDepartment(manageDepartment);
				//主办单位
				String sponsorDepartment = dealDetailInfo(html.xpath("//div[@class='b-content']/ul/li[2]/text()").get());
				ncpssdDetail.setSponsorDepartment(sponsorDepartment);
				//主编
				String editor = dealDetailInfo(html.xpath("//div[@class='b-content']/ul/li[3]/text()").get());
				ncpssdDetail.setEditor(editor);
				//创刊时间
				String buildTime = dealDetailInfo(html.xpath("//div[@class='b-content']/ul/li[4]/text()").get());
				ncpssdDetail.setBuildTime(buildTime);
				//出版周期
				String publishPeroid = dealDetailInfo(html.xpath("//div[@class='b-content']/ul/li[5]/text()").get());
				ncpssdDetail.setPublishPeroid(publishPeroid);
				//地址
				String address = dealDetailInfo(html.xpath("//div[@class='b-content']/ul/li[6]/text()").get());
				ncpssdDetail.setAddress(address);
				//国际标准刊号
				String internationalCode = dealDetailInfo(html.xpath("//div[@class='b-content']/ul/li[7]/text()").get());
				ncpssdDetail.setInternationalCode(internationalCode);
				//国内统一刊号
				String nationalCode = dealDetailInfo(html.xpath("//div[@class='b-content']/ul/li[8]/text()").get());
				ncpssdDetail.setNationalCode(nationalCode);
				//邮发代号
				String postCode = dealDetailInfo(html.xpath("//div[@class='b-content']/ul/li[9]/text()").get());
				ncpssdDetail.setPostCode(postCode);
				//单价
				String price = dealDetailInfo(html.xpath("//div[@class='b-content']/ul/li[10]/text()").get());
				ncpssdDetail.setPrice(price);
				//总价
				String totalPrice = dealDetailInfo(html.xpath("//div[@class='b-content']/ul/li[11]/text()").get());
				ncpssdDetail.setTotalPrice(totalPrice);
				//图片链接
				String imageUrl = html.$("div.b-picture > img","src").get();
//				page.putField("imageUrl", imageUrl);
				ncpssdDetail.setImageUrl(imageUrl);
				
				String journalInfo = getJournalInfo(html);
				ncpssdDetail.setJournalInfo(journalInfo);
				//学科分类
				ncpssdDetail.setCatalog(h);
				
				page.putField("ncpssdDetail", ncpssdDetail);
			}else {
				logger.info("未匹配链接为{}",page.getUrl().get());
			}
		}
	}

	public Site getSite() {
		return Site.me().setRetryTimes(5).setSleepTime(10000).setCycleRetryTimes(5)
					.setTimeOut(50 * 1000)
					.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
	}

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
	
	public static String formatUrl(String url) {
		url = url.replaceAll(" ", "%20");
		return url;
	}

	public static String formatUri(String params) {
		if(StringUtils.isNotEmpty(params)) {
			try {
				return URLEncoder.encode(params,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return "";
			}
		}
		return "";
	}
	
	public static String formatUri2(String params) {
		if(StringUtils.isNotEmpty(params)) {
			try {
				return URLDecoder.decode(params,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return "";
			}
		}
		return "";
	}
	
	private static String getNcpssdC(String waitToDeal) {
		if(StringUtils.isNotEmpty(waitToDeal)) {
			int start = waitToDeal.indexOf("(");
			int end  = waitToDeal.lastIndexOf(",");
			waitToDeal = waitToDeal.substring(start+2, end-1);
			return formatUri(waitToDeal);
		}
		return "";
	}
	
	private int getNumberFormString(String numStr) {
		if(StringUtils.isNotEmpty(numStr)) {
			String regEx="[^0-9]";   
			Pattern p = Pattern.compile(regEx);   
			Matcher m = p.matcher(numStr); 
			return Integer.valueOf(m.replaceAll("").trim());
		}
		return 0;
	}
	
	private String getCatalogName(String html) {
		if(StringUtils.isNotEmpty(html)) {
			return html.split(" ")[0];
		}
		return "";
	}
	
	/**
	 * 获取所有分页链接
	 * @param url
	 * @param result
	 * @return
	 */
	private List<String> urlReplace(String url,int result) {
		List<String> pageUrlList = new ArrayList<String>(); 
		if(result > 0) {
			for(int i = 2; i <= result; i++) {
				url = url.replaceAll("p=\\d+", "p="+i);
				pageUrlList.add(url);
			}
		}
		return pageUrlList;
	}
	
	/**
	 * 将出版周期信息转化为json字符串
	 * @param html
	 * @return
	 */
	private String getJournalInfo(Html html) {
		int yearSize = html.xpath("//div[@class='journal-vol']/div").all().size();
		if(yearSize > 0) {
			Map<String, List<String>> journalInfoMap = new LinkedHashMap<String, List<String>>();
			for(int i = 1; i <= yearSize; i++) {
				String year = html.xpath("//div[@class='journal-vol']/div["+i+"]/div/text()").get();
				int peroidSize = html.xpath("//div[@class='journal-vol']/div["+i+"]/ul/li").all().size();
				List<String> peroidList = new ArrayList<String>();
				if(peroidSize > 0) {
					for(int j = 1; j <= peroidSize; j++ ) {
						String peroid = html.xpath("//div[@class='journal-vol']/div["+i+"]/ul/li["+j+"]/a/text()").get();
						peroidList.add(peroid);
					}
				}
				journalInfoMap.put(year, peroidList);
			}
			String journalInfo = JSON.toJSONString(journalInfoMap);
			return journalInfo.length() >= 3500? null : journalInfo;//字符串过程的话直接丢弃
		}
		return null;
	}
	
	private String dealDetailInfo(String info) {
		if(StringUtils.isNotEmpty(info)) {
			info = StringUtils.trim(info);
			info = info.replaceAll("：", "");
		}
		return info;
	}

	public AtomicInteger getCount() {
		return count;
	}

	public void setCount(AtomicInteger count) {
		this.count = count;
	}
}
