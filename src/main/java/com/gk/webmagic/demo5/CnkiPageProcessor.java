package com.gk.webmagic.demo5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.gk.webmagic.demo5.QNode.QGroup;
import com.gk.webmagic.demo5.QNode.QGroup.ChildItems;
import com.gk.webmagic.demo5.QNode.QGroup.ChildItems.Items;
import com.gk.webmagic.demo5.util.Util;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.utils.HttpConstant;

public class CnkiPageProcessor implements PageProcessor {

	private static Logger logger = LoggerFactory.getLogger(CnkiPageProcessor.class);

	private static final String detailUrl = "http://navi.cnki.net/knavi/Common/Search/Journal";
	
	private AtomicInteger count = new AtomicInteger(0);
	
	public AtomicInteger getCount() {
		return count;
	}

	public void setCount(AtomicInteger count) {
		this.count = count;
	}

	/**
	 * 默认每页显示的条数
	 */
	private final int PAGESIZE = 50;

	@Override
	public void process(Page page) {
		String url = page.getUrl().get();
		String pcode = Util.urlReqSingleVal(url, "productcode");
		if ("CJFD".equals(pcode)) {
			Html html = page.getHtml();
			int size = html.xpath("//div[@class='guide'][1]/ul/li").all().size();
			if (size > 0) {
				for (int i = 1; i <= size; i++) {
					String firstLevel = StringUtils.trim(html.xpath("//div[@class='guide'][1]/ul/li[" + i + "]/span/a/@onclick").get());
					firstLevel = getValue(firstLevel);//一级类型Code
					String firstLevelName = StringUtils.trim(html.xpath("//div[@class='guide'][1]/ul/li[" + i + "]/span/a/i/text()").get());//一级类型名称
//					String count = StringUtils.trim(html.xpath("//div[@class='guide'][1]/ul/li[" + i + "]/span/a/em/text()").get());
//					count = getCount(count);
					logger.info("一级类型:{},类型名称:{},总数为:{}",firstLevel,firstLevelName,count);
					int secondSize = html.xpath("//div[@class='guide'][1]/ul/li[" + i + "]/dl/dd").all().size();
					if(secondSize > 0) {
						for(int j = 1; j <= secondSize; j++) {
							String secondLevel = StringUtils.trim(html.xpath("//div[@class='guide'][1]/ul/li[" + i + "]/dl/dd["+j+"]/a/@onclick").get());
							secondLevel = getValue(secondLevel);//二级类型Code
							String secondLevelName = StringUtils.trim(html.xpath("//div[@class='guide'][1]/ul/li[" + i + "]/dl/dd["+j+"]/a/i/text()").get());//二级类型名称
							String secondCount = StringUtils.trim(html.xpath("//div[@class='guide'][1]/ul/li[" + i + "]/dl/dd["+j+"]/a/em/text()").get());
							secondCount = getCount(secondCount);
							
							logger.info("二级类型:{},类型名称:{},总数为:{}",secondLevel,secondLevelName,secondCount);
							
							int pageNum = Util.getPageNum(Integer.valueOf(secondCount), PAGESIZE);
							List<Request> requests = genRequests(secondLevel, pageNum, PAGESIZE);
							if(null != requests && requests.size() > 0) {
								for (Request r : requests) {
									page.addTargetRequest(r);
								}
							}
						}
					}
				}
			}
		} else {
			String level = Util.urlReqSingleVal(page.getUrl().get(), "level");
			Html html = page.getHtml();
			int size = html.xpath("//div[@class='result']/ul/li").all().size();
			if(size > 0) {
				for(int i = 2; i <= size; i++) {
					String journalName = StringUtils.trim(html.xpath("//div[@class='result']/ul/li["+i+"]/span/h2/a/text()").get());
					String journalUrl = StringUtils.trim(html.xpath("//div[@class='result']/ul/li["+i+"]/span/h2/a/@href").get());
					journalUrl = Util.urlReplace(journalUrl);
					count.incrementAndGet();
					logger.info("类型:{},期刊名:{},期刊链接:{}",level,journalName,journalUrl);
				}
			}
		}
	}

	@Override
	public Site getSite() {
		return Site.me().setTimeOut(60 * 1000).setRetryTimes(10).setCycleRetryTimes(10).setSleepTime(10 * 1000);
	}

	/**
	 * 获取学科代码值
	 * 
	 * @param clickStr
	 * @return
	 */
	private String getValue(String clickStr) {
		if (StringUtils.isNotEmpty(clickStr)) {
			String[] strs = clickStr.split(",");
			if (strs.length >= 3) {
				String value = strs[2].replaceAll("'", "");
				return value;
			}
		}
		return null;
	}

	/**
	 * 获取每个学科的期刊总数
	 * 
	 * @param count
	 * @return
	 */
	private String getCount(String count) {
		if (StringUtils.isNotEmpty(count)) {
			count = count.replace("(", "").replace(")", "");
			return count;
		}
		return null;
	}

	/**
	 * 生成需要的SearchStateJson
	 * @param value
	 * @return
	 */
	private String getJsonParams(String value) {
		if(StringUtils.isNotEmpty(value)) {
			value = value + "?";
			// 新建QNode节点
			Items items = new Items();
			items.setKey(1);
			items.setTitle("");
			items.setLogic(1);
			items.setName("168专题代码");
			items.setOperate("");
			items.setValue(value);
			items.setExtendType(0);
			items.setExtendValue("");
			items.setValue2("");

			ChildItems childItems = new ChildItems();
			childItems.setKey("Journal");
			childItems.setLogic(1);
			List<Items> itemsList = new ArrayList<>();
			itemsList.add(items);
			childItems.setItems(itemsList);
			childItems.setChildItems(new ArrayList<>());

			QGroup qGroup = new QGroup();
			qGroup.setKey("Navi");
			qGroup.setLogic(1);
			List<ChildItems> childItemsList = new ArrayList<>();
			childItemsList.add(childItems);
			qGroup.setChildItems(childItemsList);
			qGroup.setItems(new ArrayList<>());

			QNode qNode = new QNode();
			List<QGroup> qGroupList = new ArrayList<>();
			qGroupList.add(qGroup);
			qNode.setQGroup(qGroupList);
			qNode.setOrderBy("OTD|");
			qNode.setAdditon("");
			qNode.setGroupBy("");
			qNode.setS_DBCodes("");
			qNode.setSelect_Fields("");
			qNode.setSelectT("");

			// 新建CNode节点
			CNode cNode = new CNode();
			cNode.setOperateT("");
			cNode.setPCode("CJFD");
			cNode.setSMode("");

			// 新建SearchStateJson
			SearchStateJson searchStateJson = new SearchStateJson();
			searchStateJson.setAccount("knavi");
			searchStateJson.setClientToken("");
			searchStateJson.setCNode(cNode);
			searchStateJson.setLanguage("");
			searchStateJson.setPlatfrom("");
			searchStateJson.setQNode(qNode);
			searchStateJson.setQueryTime("");
			searchStateJson.setStateID("");

			String jsonString = JSONObject.toJSONString(searchStateJson);
			return jsonString;
		}
		return null;
	}
	
	/**
	 * 生成获取cnki列表页面所需的参数
	 * @param value
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	private List<NameValuePair> genParams(String value,String pageIndex,String pageSize) {
		String searchStateJson = getJsonParams(value);
		if(StringUtils.isNotEmpty(searchStateJson)) {
			List<NameValuePair> formParams = new ArrayList<>();
			formParams.add(new BasicNameValuePair("SearchStateJson", searchStateJson));
			formParams.add(new BasicNameValuePair("pageindex", pageIndex));
			formParams.add(new BasicNameValuePair("pagecount", pageSize));
			return formParams;
		}
		return null;
	}
	
	/**
	 * 生成待爬取的Request
	 * @param value
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	private List<Request> genRequests(String value,int pageNum,int pageSize) {
		if(StringUtils.isNotEmpty(value) && pageNum > 0) {
			List<Request> requests = new ArrayList<>();
			for(int i = 1; i <= pageNum; i++) {
				List<NameValuePair> params = genParams(value, String.valueOf(i), String.valueOf(pageSize));
				if(null != params) {
					//防止因为webmagic对url的去重而遭到过滤，所以对url后面增加了参数，实际所加的参数没有用
					Request request = new Request(detailUrl+"?level="+value+"&pageindex="+i);
					request.setMethod(HttpConstant.Method.POST);//设置成post请求
					NameValuePair[] paramsArr = params.toArray(new NameValuePair[]{});
					Map<String, Object> paramMap = new HashMap<>();
					//此处的map的key必须是nameValuePair，查看HttpClientDownloader中的selectRequestMethod方法
					paramMap.put("nameValuePair", paramsArr);
					request.setExtras(paramMap);//将post请求的参数加到Request中去
					requests.add(request);
				}
			}
			return requests;
		}
		return null;
	}

	public static void main(String[] args) {
		String startUrl = "http://navi.cnki.net/knavi/Common/LeftNavi/Journal?productcode=CJFD&index=1";
		CnkiPageProcessor processor = new CnkiPageProcessor();
		Spider.create(processor).addPipeline(new CnkiPipeline())
				.addUrl(startUrl).thread(10).run();
		System.out.println("总共爬取期刊数:" + processor.getCount().get());
		
	}

}
