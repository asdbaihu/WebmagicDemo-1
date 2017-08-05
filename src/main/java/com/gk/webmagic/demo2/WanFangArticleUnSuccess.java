package com.gk.webmagic.demo2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import us.codecraft.webmagic.Spider;

/**
 * 
 * @author gk
 *
 * 2017年3月23日
 */
public class WanFangArticleUnSuccess {
	
	private static Logger logger = LoggerFactory.getLogger(WanFangArticleUnSuccess.class);

	private SpiderStarter starter;
	
	//
	private List<String> docIdList = new ArrayList<>();
	
	//重新爬取
	private LinkedBlockingQueue<Map<String, String>> retryCrawlQueue = new LinkedBlockingQueue<>();
	
	private ReentrantLock lock = new ReentrantLock();
	
	private int idle = 0;
	
	public WanFangArticleUnSuccess(SpiderStarter starter) {
		this.starter = starter;
	}
	
	public LinkedBlockingQueue<Map<String, String>> getRetryCrawlQueue() {
		return retryCrawlQueue;
	}

	public List<String> getDocIdList() {
		return docIdList;
	}

	public void addCrawlQueue(Map<String, String> retryMap) {
		if(!retryCrawlQueue.contains(retryMap)) {
			retryCrawlQueue.add(retryMap);
			logger.info("新增未爬取成功的万方期刊卷期信息:{}",JSONObject.toJSONString(retryMap));
		}
	}
	
	private void genSpiderAndRun(Map<String, String> retryMap) {
		String docId = retryMap.get("docId");
		String journalName = retryMap.get("journalName");
		String issueUrl = retryMap.get("issueUrl");
		logger.info("重新爬取页面{}进行比对开始!",JSONObject.toJSON(retryMap));
		Spider.create(new WanFangArticlePageProcessor(docId, journalName))
			.addPipeline(new WanFangArticlePipeline(this)).addUrl(issueUrl)
			.thread(5).run();
		//判断是否需要将需要设置该期刊的比对完成标志
		if(this.getDocIdList().contains(docId)) {
			starter.doUpdateDoneJournal(docId,"1");
			this.getDocIdList().remove(docId);
		}
		logger.info("重新爬取页面{}进行比对结束!",JSONObject.toJSON(retryMap));
	}
	
	private void doCrawl() {
		if(retryCrawlQueue.size() > 0) {
			Map<String, String> retryMap = retryCrawlQueue.poll();
			genSpiderAndRun(retryMap);
		}
	}
	
	private void startCrawl() {
		lock.lock();
		try {
			int size = retryCrawlQueue.size();
			logger.info("等待重新爬取的队列大小为{}",size);
			if(size > 0) {
				if(idle >= 3) {
					doCrawl();
					idle = 0;
				}else {
					idle++;
				}
				logger.info("重新爬取当前自旋{}次",idle);
			}
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * 文章比对中异常情况下的重试进程
	 */
	public void startRetryCompare() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				logger.info("万方数据比对失败重新爬取监听开始！");
				while(!starter.isStopFlag() || retryCrawlQueue.size() > 0) {
					startCrawl();
					try {
						Thread.sleep(2 * 1000);
					} catch (InterruptedException e) {
						//
					}
				}
				logger.info("万方数据比对失败重新爬取监听结束！");
			}
		});
		t.setName("retry-crawl-thread");
		t.start();
	}
	
}
