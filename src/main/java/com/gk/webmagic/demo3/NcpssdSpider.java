package com.gk.webmagic.demo3;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.gk.webmagic.demo3.mapper.NcpssdBeanMapper;
import com.gk.webmagic.demo3.model.NcpssdBeanWithBLOBs;
import com.google.common.collect.ImmutableList;

import us.codecraft.webmagic.Spider;

public class NcpssdSpider {

	static Logger logger = LoggerFactory.getLogger(NcpssdSpider.class);
	
	private ConcurrentLinkedQueue<NcpssdBeanWithBLOBs> ncpssdBeanList = new ConcurrentLinkedQueue<NcpssdBeanWithBLOBs>();
	
	private ReentrantLock lock = new ReentrantLock();
	
	private static final int insertNum = 50;
	
	private boolean spFinished = false;
	
	private int idleNum = 0;
	
	private int currentInsertNum = 0;
	
	private int errorNum = 0;
	
	private NcpssdImageDownloader imageDownloader;
	
	public NcpssdSpider() {
		
	}
	
	public NcpssdSpider(NcpssdImageDownloader imageDownloader) {
		this.imageDownloader = imageDownloader;
	}
	
	public void addNcpssdBean(NcpssdBeanWithBLOBs ncpssdBean) {
		try {
			lock.lock();
			ncpssdBeanList.add(ncpssdBean);
			logger.info("NcpssdBeanList 增加数据->{},当前队列数量{}条",ncpssdBean.getName(),ncpssdBeanList.size());
		} finally {
			lock.unlock();
		}
	}

	private void checkInput(boolean last) {
		if (last)
			logger.debug("NcpssInfo last Checking DBInput");
		else
			logger.debug("NcpssInfo Checking DBInput");
		try {
			lock.lock();
			int size = ncpssdBeanList.size();
			if(size > 0) {
				if(last || size >= insertNum || idleNum >= 5) {
					ImmutableList<NcpssdBeanWithBLOBs> _ncpssdBeans = ImmutableList.copyOf(ncpssdBeanList);
					ncpssdBeanList = new ConcurrentLinkedQueue<NcpssdBeanWithBLOBs>();
					idleNum = 0;
					insertNcpssdList(_ncpssdBeans);
				}else {
					idleNum++;
				}
				logger.info("当前自旋{}次",idleNum);
			}
		} finally {
			lock.unlock();
		}
		
	}

	public boolean isFinish() {
		return spFinished;
	}

	public void startMonitor() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				logger.info("数据插入监控启动!!");
				while (!isFinish()) {
					checkInput(false);
					try {
						Thread.sleep(5 * 1000L);
					} catch (Exception e) {
						// ignore
					}
				}
				logger.info("数据插入监控停止!!");
			}
		});
		thread.setName("monitor-check-db-input");
		thread.start();
		
	}
	
	private void insertNcpssdList(ImmutableList<NcpssdBeanWithBLOBs> beans) {
		for (NcpssdBeanWithBLOBs ncpssdBean : beans) {
			int key = insertBean(ncpssdBean);
//			logger.info("ncpssdBean插入返回的主键是{}",ncpssdBean.getId());
//			logger.info("图片链接为{}",ncpssdBean.getImageUrl());
			if(key > 0) {
				currentInsertNum++;
				this.getImageDownloader().downloadImage(ncpssdBean.getImageUrl(), String.valueOf(ncpssdBean.getId()), "F:/ncpssd");
			}
		}
		logger.info("当前共插入数据{}条",currentInsertNum);
	}
	
	private int insertBean(NcpssdBeanWithBLOBs bean) {
		SqlSession sqlSession = MyBatisUtil.getSqlSession();
		try {
			NcpssdBeanMapper ncpssdBeanMapper = sqlSession.getMapper(NcpssdBeanMapper.class);
			int i = ncpssdBeanMapper.insert(bean);
			sqlSession.commit();
			return i;
		} catch (Exception e) {
			logger.info("NcpssdBean批量插入数据库出错！出错列表内容{}",JSONObject.toJSONString(bean));
			errorNum++;
			e.printStackTrace();
			sqlSession.rollback();
		} finally {
			if(null != sqlSession) {
				sqlSession.close();
			}
		}
		return 0;
	}
	
	public int getCurrentInsertNum() {
		return currentInsertNum;
	}

	public void setCurrentInsertNum(int currentInsertNum) {
		this.currentInsertNum = currentInsertNum;
	}

	public int getErrorNum() {
		return errorNum;
	}

	public void setErrorNum(int errorNum) {
		this.errorNum = errorNum;
	}

	public boolean isSpFinished() {
		return spFinished;
	}

	public void setSpFinished(boolean spFinished) {
		this.spFinished = spFinished;
	}
	
	public NcpssdImageDownloader getImageDownloader() {
		return imageDownloader;
	}

	public void setImageDownloader(NcpssdImageDownloader imageDownloader) {
		this.imageDownloader = imageDownloader;
	}

	public static void main(String[] args) throws Exception {
		NcpssdSpider task = new NcpssdSpider(new NcpssdImageDownloader());
		NcpssdImageDownloader downloader = task.getImageDownloader();
		downloader.setSpider(task);
		downloader.cycleDownloadImage();
		task.startMonitor();
		Spider.create(new NcpssdPageProcessor())
				.setScheduler(new NcpssdScheduler())
				.addUrl("http://www.ncpssd.org/journal/list.aspx?nav=1&langType=2&tag=1")
				.addPipeline(new NcpssdPipeline(task)).thread(10)// 开启5个线程爬取
				.run(); // 启动
		task.spFinished = true;
		task.checkInput(true);
		logger.info("总共插入数据{}条",task.getCurrentInsertNum());
		logger.info("总共插入失败数据{}条",task.getErrorNum());
	}
}
