package com.gk.webmagic.demo2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gk.webmagic.demo2.bean.WanFangJournalBean;
import com.gk.webmagic.demo2.mapper.DownPeriodicalWanfangMapper;
import com.gk.webmagic.demo2.mapper.WanFangJournalCompareMapper;
import com.gk.webmagic.demo2.mapper.WanFangUnmatchMapper;
import com.gk.webmagic.demo2.model.DownPeriodicalWanfangExample;
import com.gk.webmagic.demo2.model.WanFangJournalCompare;
import com.gk.webmagic.demo2.model.WanFangJournalCompareExample;
import com.gk.webmagic.demo2.model.WanFangUnmatchExample;

import us.codecraft.webmagic.Spider;

/**
 * 程序启动时需要设置启动参数
 * @author gk
 *
 * 2017年3月24日
 */
public class SpiderStarter {
	
	private static Logger logger = LoggerFactory.getLogger(SpiderStarter.class);
	
	private static final String REPEAT = "repeat";//重新爬取万方期刊进行比较
	
	private static final String DATABASE = "database";//从数据库中读取数据进行比较
	
	private boolean stopFlag = false;
	
	public boolean isStopFlag() {
		return stopFlag;
	}

	public void setStopFlag(boolean stopFlag) {
		this.stopFlag = stopFlag;
	}

	/**
	 * 重新爬取期刊并对期刊中各个文章比较
	 * @param journalBean
	 */
	public void startArticleSpider(WanFangJournalBean journalBean,WanFangArticleUnSuccess unSuccess) {
		doUpdateDoneJournal(journalBean.getDocId(), "0");//设置该期刊已开始爬取比较
		logger.info("-------万方期刊“{}”比对开始！-------",journalBean.getName());
		long start = System.currentTimeMillis();
		String url = journalBean.getUrl();
		String docId = journalBean.getDocId();
		Spider.create(new WanFangArticlePageProcessor(docId,journalBean.getName()))
				.addPipeline(new WanFangArticlePipeline(unSuccess))
				.addUrl(url).thread(10).run();
		long end = System.currentTimeMillis();
		long time = (end - start)/1000;
		setJournalCompareDone(docId,unSuccess);
		logger.info("-------万方期刊“{}”比对结束,比对时间{}秒！-------",journalBean.getName(),time);
	}
	
	/**
	 * 从数据库中获取期刊数据进行比较
	 * @param compare
	 * @param unSuccess
	 */
	public void startArticleSpider(WanFangJournalCompare compare,WanFangArticleUnSuccess unSuccess) {
		doUpdateDoneJournal(compare.getDocId(), "0");//设置该期刊已开始爬取比较
		logger.info("-------万方期刊“{}”比对开始！-------",compare.getJournalName());
		long start = System.currentTimeMillis();
		String url = compare.getJournalUrl();
		String docId = compare.getDocId();
		Spider.create(new WanFangArticlePageProcessor(docId,compare.getJournalName()))
				.addPipeline(new WanFangArticlePipeline(unSuccess))
				.addUrl(url).thread(10).run();
		long end = System.currentTimeMillis();
		long time = (end - start)/1000;
		setJournalCompareDone(docId,unSuccess);
		logger.info("-------万方期刊“{}”比对结束,比对时间{}秒！-------",compare.getJournalName(),time);
	}
	
	/**
	 * 设置
	 * @param docId
	 * @param unSuccess
	 */
	private void setJournalCompareDone(String docId,WanFangArticleUnSuccess unSuccess) {
		LinkedBlockingQueue<Map<String, String>> retryCrawlQueue = unSuccess.getRetryCrawlQueue();
		if(retryCrawlQueue.size() > 0) {
			Iterator<Map<String, String>> iterator = retryCrawlQueue.iterator();
			boolean flag = true;
			while(iterator.hasNext()) {
				Map<String, String> map = iterator.next();
				if(map.get("docId").equals(docId)) {
					flag = false;
					break;
				}
			}
			if(flag) {
				//并发队列中不存在该期刊
				//直接将该期刊的比对标志置为完成
				doUpdateDoneJournal(docId,"1");
			}else {
				//并发队列中还存在该期刊
				unSuccess.getDocIdList().add(docId);
				logger.info("Spider已执行完成，阻塞队列中还有该期刊未爬取比较完成！期刊docId为{}",docId);
			}
		}else {
			//并发队列当前为空
			doUpdateDoneJournal(docId,"1");
		}
	}
	
	/**
	 * 获取万方期刊数据表中还未比对完成的数据
	 * @param flag
	 * @return
	 * @throws Exception
	 */
	public List<WanFangJournalCompare> getWanFangDoneData(String flag) throws Exception{
		SqlSession sqlSession = MyBatisUtil.getSqlSession();
		List<WanFangJournalCompare> list = new ArrayList<>();
		try {
			WanFangJournalCompareMapper mapper = sqlSession.getMapper(WanFangJournalCompareMapper.class);
			WanFangJournalCompareExample example = new WanFangJournalCompareExample();
			example.createCriteria().andSuccessFlagEqualTo(flag).andRemarkNotEqualTo("1");
			example.or().andSuccessFlagEqualTo(flag).andRemarkIsNull();
			list = mapper.selectByExample(example);
		} catch (Exception e) {
			logger.error("获取万方已比对完成的数据出错！");
			throw new Exception("获取万方已比对完成的数据出错！");
		} finally {
			if(sqlSession != null) {
				sqlSession.close();
			}
		}
		return list.size() > 0? list : null;
	}
	
	/**
	 * 设置期刊爬取比较状态为完成
	 * @param docId
	 */
	public void doUpdateDoneJournal(String docId,String state) {
		SqlSession sqlSession = null;
		try {
			sqlSession = MyBatisUtil.getSqlSession();
			WanFangJournalCompareMapper mapper = sqlSession.getMapper(WanFangJournalCompareMapper.class);
			Map<String, String> map = new HashMap<>();
			map.put("state", state);
			map.put("docId", docId);
			mapper.updateJournalState(map);
			sqlSession.commit();
		} catch (Exception e) {
			sqlSession.rollback();
			logger.error("更新万方期刊爬取比较完成状态出错！期刊docId为{}",docId);
			logger.error(e.getMessage());
		} finally {
			if(null != sqlSession) {
				sqlSession.close();
			}
		}
	}
	
	/**
	 * 判断期刊级别的比较是否已经完成
	 * @return
	 */
	private boolean hasJournalCompareDone() {
		try(SqlSession sqlSession = MyBatisUtil.getSqlSession()) {
			WanFangJournalCompareMapper mapper = sqlSession.getMapper(WanFangJournalCompareMapper.class);
			int count = mapper.selectCount();
			return count > 0? true : false;
		}
	}
	
	public void cleanJournalCompareData() {
		try(SqlSession sqlSession = MyBatisUtil.getSqlSession()) {
			WanFangJournalCompareMapper mapper = sqlSession.getMapper(WanFangJournalCompareMapper.class);
			WanFangJournalCompareExample example = new WanFangJournalCompareExample();
			example.createCriteria().andIdIsNotNull();
			mapper.deleteByExample(example);
			sqlSession.commit();
		}
	}
	
	public void cleanWanFangCompareDoneData() {
		try(SqlSession sqlSession = MyBatisUtil.getSqlSession()) {
			DownPeriodicalWanfangMapper mapper = sqlSession.getMapper(DownPeriodicalWanfangMapper.class);
			DownPeriodicalWanfangExample example = new DownPeriodicalWanfangExample();
			example.createCriteria().andIdIsNotNull();
			mapper.deleteByExample(example);
			sqlSession.commit();
		}
	}
	
	public void cleanWanFangUnmatchData() {
		try(SqlSession sqlSession = MyBatisUtil.getSqlSession()) {
			WanFangUnmatchMapper mapper = sqlSession.getMapper(WanFangUnmatchMapper.class);
			WanFangUnmatchExample example = new WanFangUnmatchExample();
			example.createCriteria().andIdIsNotNull();
			mapper.deleteByExample(example);
			sqlSession.commit();
		}
	}
	
	/**
	 * 重新爬取前进行必要的数据清理
	 */
	public void cleanDataBeforeRepeat() {
		cleanJournalCompareData();
		cleanWanFangUnmatchData();
		cleanWanFangCompareDoneData();
	}
	
	/**
	 * 将未爬取比较完成的数据进行清理
	 */
	private void cleanUnfinishedDataBeforeDatabase() {
		try(SqlSession sqlSession = MyBatisUtil.getSqlSession()) {
			DownPeriodicalWanfangMapper mapper = sqlSession.getMapper(DownPeriodicalWanfangMapper.class);
			int count = mapper.deleteUnfinishedData("0");
			sqlSession.commit();
			logger.info("从万方文章表中清除{}条未完成比对的数据!",count);
		}
	}
	
	/**
	 * 清除未比对结束中未比对成功的数据
	 */
	private void cleanUnfinishUnmatchedData() {
		try(SqlSession sqlSession = MyBatisUtil.getSqlSession()) {
			WanFangUnmatchMapper mapper = sqlSession.getMapper(WanFangUnmatchMapper.class);
			int count = mapper.deleteUnfinishUnmatchData();
			sqlSession.commit();
			logger.info("从万方未比对成功文章表中清除{}条未比对成功的数据!",count);
		}
	}
	
	public void cleanUnfinishedData() {
		cleanUnfinishedDataBeforeDatabase();
		cleanUnfinishUnmatchedData();
	}
	
	public static void main(String[] args) throws Exception {
		String operate = args[0];
		SpiderStarter starter = new SpiderStarter();
		WanFangArticleUnSuccess unSuccess = new WanFangArticleUnSuccess(starter);
		
		if(null == operate || operate.equals(DATABASE)) {
			//默认从数据库中读取进行比较
			boolean doneFlag = starter.hasJournalCompareDone();
			if(doneFlag) {
				//从数据库读取数据进行爬取
				//期刊级别的比较已经完成
				List<WanFangJournalCompare> wanFangDoneData = starter.getWanFangDoneData("1");
				logger.info("List<WanFangJournalCompare> wanFangDoneData size {}",wanFangDoneData.size());
				if(null != wanFangDoneData && wanFangDoneData.size() > 0) {
					//重试线程启动
					unSuccess.startRetryCompare();
					//如果期刊已经比对过，直接从数据库中取数据
//					for (WanFangJournalCompare compare : wanFangDoneData) {
//						starter.startArticleSpider(compare,unSuccess);
//					}
					logger.info("清除上次执行未完成的数据开始！");
					starter.cleanUnfinishedData();
					logger.info("清除上次执行未完成的数据结束！");
					starter.executeCompare(wanFangDoneData, starter, unSuccess);
					logger.info("数据库中所有需要比较的数据已经比较完成！");
				}else {
					logger.info("数据库中没有需要进行比较的数据！");
				}
			}else {
				logger.info("数据库中没有数据，可能您还未进行期刊爬取比较，请您在启动程序时输入参数\"repeat\"进行重试！");
			}
		}else if(operate.equals(REPEAT)) {
			logger.info("数据库清理工作开始！");
			starter.cleanDataBeforeRepeat();
			logger.info("数据库清理工作完成！");
			//重新爬取万方期刊进行比较
			WanFangTask wanFangTask = new WanFangTask();
			WanFangPageProcessor processor = new WanFangPageProcessor();
			Spider.create(processor)
				.addPipeline(new WanFangPipeline(wanFangTask))
				.addUrl("http://c.wanfangdata.com.cn/Periodical.aspx?gkstart=1")// 从输入的url开始爬取
				.thread(10)// 开启10个线程爬取
				.run();// 启动
			logger.info("期刊级别比较结束！");
			wanFangTask.startCompare(wanFangTask.getSpiderBeans());
			
			List<WanFangJournalBean> successBeans = wanFangTask.getWanFangJournalBeans();
			logger.info("期刊比对成功{}条",successBeans.size());
			List<WanFangJournalBean> unsuccessBeans = wanFangTask.getUnSuccessBeans();
			logger.info("期刊比对失败{}条",unsuccessBeans.size());
			if(null != successBeans && successBeans.size() > 0) {
				//重试线程启动
				unSuccess.startRetryCompare();
//				for (WanFangJournalBean wanFangJournalBean : successBeans) {
//					starter.startArticleSpider(wanFangJournalBean,unSuccess);
//				}
				starter.executeBean(successBeans,starter,unSuccess);
				logger.info("所有期刊文章数据已经比较完成!");
			}else {
				logger.info("没有相同的期刊数据进行比较!");
			}
		}
//		starter.setStopFlag(true);
	}
	
	private void executeCompare(List<WanFangJournalCompare> compares,SpiderStarter starter,WanFangArticleUnSuccess unSuccess) {
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		for (WanFangJournalCompare wanFangJournalCompare : compares) {
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					starter.startArticleSpider(wanFangJournalCompare, unSuccess);
					try {
						Thread.sleep(5 * 1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
					}
				}
			});
		}
		executorService.shutdown();
		while(!executorService.isTerminated()) {
		}
		starter.setStopFlag(true);
	}

	private void executeBean(List<WanFangJournalBean> successBeans,SpiderStarter starter,WanFangArticleUnSuccess unSuccess) {
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		for (WanFangJournalBean bean : successBeans) {
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					starter.startArticleSpider(bean, unSuccess);
					try {
						Thread.sleep(5 * 1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
					}
				}
			});
		}
		executorService.shutdown();
		while(!executorService.isTerminated()) {
		}
		starter.setStopFlag(true);
	}

}

