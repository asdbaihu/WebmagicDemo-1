package com.gk.webmagic.university;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.gk.webmagic.demo5.util.Util;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;

public class UniversityPageProcessor implements PageProcessor {
	
	private static Logger logger = LoggerFactory.getLogger(UniversityPageProcessor.class);
	
	@Override
	public void process(Page page) {
		String tag = Util.urlReqSingleVal(page.getUrl().toString(),"tag");
		if("1".equals(tag)) {
			//列表页
			List<String> detailUrls = new ArrayList<>();
			List<String> schools = new JsonPathSelector("$.school").selectList(page.getRawText());
			for (String string : schools) {
				JSONObject parseObject = JSONObject.parseObject(string);
				logger.info("学校名称：{}，学校ID：{}",parseObject.get("schoolname"),parseObject.get("schoolid"));
				String detailUrl = genDetailUrl(parseObject.get("schoolid").toString());
				detailUrls.add(detailUrl);
			}
			page.addTargetRequests(detailUrls);
		}else {
			//详情页
			String schoolid = Util.urlReqSingleVal(page.getUrl().toString(),"schoolid");
			String schoolName = page.getHtml().xpath("//div[@class='li-collegeUl']/p[@class='li-school-label']/span/text()").get();
			String imgUrl = page.getHtml().xpath("//*[@class='left li-collegeLogo']/img/@src").get();
			downloadImage(imgUrl,schoolid+"_"+schoolName,"F:/university");
		}
	}

	@Override
	public Site getSite() {
		return Site.me().setRetryTimes(5).setSleepTime(10000).setCycleRetryTimes(5)
				.setTimeOut(50 * 1000)
				.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
	}
	
	private static String genUrl(int i) {
		return "http://data.api.gkcx.eol.cn/soudaxue/queryschool.html?tag=1&messtype=json&page="+i+"&size=50&schoolflag=211%E5%B7%A5%E7%A8%8B";
	}
	
	private static String genDetailUrl(String schoolid) {
		return "http://gkcx.eol.cn/schoolhtm/schoolTemple/school"+schoolid+".htm?schoolid="+schoolid;
	}
	
	/**
	 * 判断是否是图片
	 * @param imageUrl
	 * @return
	 */
	private boolean isPic(String imageUrl) {
		String urlRegex = "\\S+\\.(jpg|bmp|gif|jpeg|png)$"; 
		Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(imageUrl);
		if(matcher.find()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 获取图片后缀名
	 * @param imageUrl
	 * @return
	 */
	private String getImageSuffix(String imageUrl) {
		if(StringUtils.isNotEmpty(imageUrl)) {
			int index = imageUrl.lastIndexOf(".");
			return imageUrl.substring(index);
		}
		return "";
	}
	
	/**
	 * 
	 * @param imageUrl
	 * @param name
	 * @param path
	 */
	private int downloadImage(String imageUrl,String name,String path) {
		if(StringUtils.isNotEmpty(imageUrl) && isPic(imageUrl)) {
			String suffix = getImageSuffix(imageUrl);
			Path target = Paths.get(path);
			if(!Files.isReadable(target)) {
				try {
					Files.createDirectory(target);
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("创建目录出错！");
				}
			}
			File file = new File(path + File.separator + name + suffix);
			CloseableHttpClient httpClient = HttpClients.createDefault();
			RequestConfig requestConfig  = RequestConfig.custom()
						.setSocketTimeout(30 * 1000)
						.setConnectTimeout(30 * 1000).build();
			try {
				HttpGet httpGet = new HttpGet(imageUrl);
				httpGet.setConfig(requestConfig);
				CloseableHttpResponse response = httpClient.execute(httpGet);
//				if(name.equals("3") && countNum == 0) {
//					countNum++;
//					throw new ConnectTimeoutException(); 
//				}
				HttpEntity entity = response.getEntity();
                InputStream in = entity.getContent();
                FileOutputStream fos = new FileOutputStream(file);
                try {
                    int len = 0;
                    byte[] tmp = new byte[1024];
                    while ((len = in.read(tmp)) != -1) {
                    	fos.write(tmp,0,len);
                    }
                    fos.flush();
                } finally {
                    in.close();
                    fos.close();
                }
                logger.info("下载图片成功，图片名称为{}",name + suffix);
                return 1;
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (HttpHostConnectException e) {
				//连接超时
				e.printStackTrace();
				logger.error(e.getMessage());
			} catch (ConnectTimeoutException e) {
				//连接超时
				e.printStackTrace();
				logger.error(e.getMessage());
			} catch (IOException e) {
				
			} catch (Exception e) {
				//不符合规则的url
				logger.info("不符合规则的图片链接为{}",imageUrl);
			} finally {
				try {
					httpClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		logger.error("下载图片成功，图片名称为{}",name);
		return 0;
	}
	
	public static void main(String[] args) {
		String[] urlArr = new String[3];
		for(int i = 1; i <= 3; i++) {
			String newUrl = genUrl(i);
			urlArr[i-1] =  newUrl;
		}
		Spider.create(new UniversityPageProcessor()).addUrl(urlArr).thread(5).run();
	}

}
