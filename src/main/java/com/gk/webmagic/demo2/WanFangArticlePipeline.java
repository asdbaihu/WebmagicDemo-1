package com.gk.webmagic.demo2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.gk.webmagic.demo2.bean.WanFangArticleBean;
import com.gk.webmagic.demo2.mapper.DownPeriodicalLstMapper;
import com.gk.webmagic.demo2.mapper.DownPeriodicalWanfangMapper;
import com.gk.webmagic.demo2.mapper.WanFangUnmatchMapper;
import com.gk.webmagic.demo2.model.DownPeriodicalWanfangWithBLOBs;
import com.gk.webmagic.demo2.model.WanFangUnmatch;
import com.gk.webmagic.demo2.util.Util;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class WanFangArticlePipeline implements Pipeline {
	
	private static Logger logger = LoggerFactory.getLogger(WanFangArticlePipeline.class);
	
	private WanFangArticleUnSuccess unSuccess;
	
	public WanFangArticlePipeline() {
		
	}
	
	public WanFangArticlePipeline(WanFangArticleUnSuccess unSuccess) {
		this.unSuccess = unSuccess;
	}
	
	@Override
	public void process(ResultItems resultItems, Task task) {
		List<WanFangArticleBean> list = resultItems.get("wanfangArticles");
		if(null != list && list.size() > 0) {
			String pubYear = resultItems.get("pubYear");
			String coverIssue = resultItems.get("coverIssue");
			String docId = resultItems.get("docId");
			String journalName = resultItems.get("journalName");
			String issueUrl = resultItems.get("issueUrl");
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("docId", docId);
			paramMap.put("pubYear", pubYear);
			paramMap.put("coverIssue", coverIssue);
			
			Map<String, String> retryMap = new HashMap<>();
			retryMap.put("docId", docId);
			retryMap.put("journalName", journalName);
			retryMap.put("issueUrl", issueUrl);
			List<Map<String, Object>> cnkiArticles = getCnkiArticles(paramMap,retryMap);
			startCompare(list, cnkiArticles,retryMap);
		}
	}
	
	/**
	 * 获取数据库中CNKI某一期刊某年某期的期刊文章信息
	 * @param map
	 * @return
	 */
	private List<Map<String, Object>> getCnkiArticles(Map<String, String> map,Map<String, String> retryMap) {
		SqlSession sqlSession = MyBatisUtil.getSqlSession();
		try {
			DownPeriodicalLstMapper mapper = sqlSession.getMapper(DownPeriodicalLstMapper.class);
			List<Map<String, Object>> list = mapper.selectArticlesByDocId(map);
			return null != list && list.size() > 0? list : null;
		}catch (Exception e) {
			//此处需要处理
			logger.error("获取CNKI比较数据出错！获取参数为：{}",JSONObject.toJSONString(map));
			logger.error(e.getMessage());
			int count = 0;
			while(count < 5) {
				//重试5次
				count++;
				List<Map<String, Object>> list = getCnkiArticles(map,retryMap);
				if(null != list) {
					logger.info("成功获取CNKI比较数据！获取参数为：{}",JSONObject.toJSONString(map));
					return list;
				}
			}
			logger.error("获取CNKI比较数据出错后重试5次仍然出错！获取参数为：{}",JSONObject.toJSONString(map));
			unSuccess.addCrawlQueue(retryMap);
		} finally {
			if(null != sqlSession) {
				sqlSession.close();
			}
		}
		return null;
	}
	
	/**
	 * 比较并将比较符合条件的信息插入数据库
	 * @param wanFangArticleBeans
	 * @param cnkiArticles
	 */
	private void startCompare(List<WanFangArticleBean> wanFangArticleBeans,List<Map<String, Object>> cnkiArticles,Map<String, String> retryMap) {
		if(null != cnkiArticles) {
			Map<String, Map<String, Object>> map = new HashMap<>();
			for(Map<String, Object> cnkiMap : cnkiArticles) {
				String title = Util.removeSign((String)cnkiMap.get("TITLE"));
				if(null != title) {
					map.put(title, cnkiMap);
				}
			}
			List<WanFangArticleBean> unMatchedList = new ArrayList<>();
			for(WanFangArticleBean bean : wanFangArticleBeans) {
				String name = Util.removeSign(bean.getArticleName());
				boolean isExist = map.containsKey(name);
				if(!isExist) {
					//不存在完全相同的键
					unMatchedList.add(bean);
				}else {
					//开始插入数据库
					insertWanFangInfo(genWanFangInsertBean(map.get(name),bean),retryMap);
					map.remove(name);
				}
			}
			if(unMatchedList.size() > 0 && null != map && map.size() > 0) {
				tailCompare(unMatchedList,map,retryMap);
			}else if(unMatchedList.size() > 0){
				doInsertUnmatchData(unMatchedList);
				for(WanFangArticleBean bean : unMatchedList) {
					logger.info("万方存在而知网数据库表中不存在的期刊文章信息：{}",JSONObject.toJSONString(bean));
				}
			}
		}
	}
	
	/**
	 * 第一轮全匹配未成功的信息进行第二轮相似度比较
	 * @param wanFangArticleBeans
	 * @param map
	 */
	private void tailCompare(List<WanFangArticleBean> wanFangArticleBeans,Map<String, Map<String, Object>> map,Map<String, String> retryMap) {
		for(WanFangArticleBean bean : wanFangArticleBeans) {
			String name = bean.getArticleName();
			Map<String, Object> coMap = getSimilarInfo(name,map);
			if(null != coMap) {
				insertWanFangInfo(genWanFangInsertBean(coMap,bean), retryMap);
			}else {
				doInsertUnmatchData(bean);
				logger.info("万方存在而知网数据库表中不存在的期刊文章信息：{}",JSONObject.toJSONString(bean));
			}
		}
	}

	/**
	 * 生成万方bean
	 * @param cnkiMap
	 * @param bean
	 * @return
	 */
	private DownPeriodicalWanfangWithBLOBs genWanFangInsertBean(Map<String, Object> cnkiMap,WanFangArticleBean bean) {
		Integer id = (Integer)cnkiMap.get("ID");
		String docId = (String)cnkiMap.get("DOC_ID");
		String articleName = bean.getArticleName();
		String articleUrl = bean.getArticleUrl();
		String pubYear = bean.getPubYear();
		DownPeriodicalWanfangWithBLOBs wanfangWithBLOBs = new DownPeriodicalWanfangWithBLOBs();
		wanfangWithBLOBs.setId(id);
		wanfangWithBLOBs.setDocId(docId);
		wanfangWithBLOBs.setTitle(articleName);
		wanfangWithBLOBs.setUrl(articleUrl);
		wanfangWithBLOBs.setPubYear(pubYear);
		return wanfangWithBLOBs;
	}
	
	/**
	 * 获取相似度大于等于0.8的cnki信息
	 * @param title
	 * @param map
	 * @return
	 */
	private Map<String, Object> getSimilarInfo(String title,Map<String, Map<String, Object>> map) {
		for(Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
			String key = entry.getKey();
			double score = Util.getSimilarScore(key, Util.removeSign(title));
			if(score >= 0.8) {
				Map<String, Object> info = entry.getValue();
				logger.info("相似度比对成功,Score={},万方文章名:{},CNKI文章信息:{}",score,title,JSONObject.toJSONString(info));
				return info;
			}
		}
		return null;
	}
	
	/**
	 * 数据插入
	 * @param wanfang
	 */
	private void insertWanFangInfo(DownPeriodicalWanfangWithBLOBs wanfang,Map<String, String> retryMap) {
		SqlSession sqlSession = MyBatisUtil.getSqlSession();
		try {
			DownPeriodicalWanfangMapper mapper = sqlSession.getMapper(DownPeriodicalWanfangMapper.class);
			mapper.insertSelective(wanfang);
			sqlSession.commit();
//			logger.info("成功插入万方数据：{}",JSONObject.toJSONString(wanfang));
		} catch (Exception e) {
			logger.error("插入万方数据出错：{}",JSONObject.toJSONString(wanfang));
			logger.error(e.getMessage());
			try {
				sqlSession.rollback();
			} catch (Exception ex) {
				if(null != unSuccess) {
					unSuccess.addCrawlQueue(retryMap);
				}
			}
			if(e.getMessage().indexOf("com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Duplicate entry") < 0) {
				//不是重复主键异常，需要重复处理
				//可能是网络原因引起了连接中断
				if(null != unSuccess) {
					unSuccess.addCrawlQueue(retryMap);
				}
			}
		} finally {
			if(sqlSession != null) {
				sqlSession.close();
			}
		}
	}
	
	/**
	 * 开始插入
	 * @param beans
	 */
	private void doInsertUnmatchData(List<WanFangArticleBean> beans) {
		List<WanFangUnmatch> unmatchs = convert2WanFangUnmatch(beans);
		insertWanFangUnmatchData(unmatchs);
	}
	
	@SuppressWarnings("serial")
	private void doInsertUnmatchData(WanFangArticleBean bean) {
		WanFangUnmatch unmatch = convert2WanFangUnmatch(bean);
		List<WanFangUnmatch> unmatchs = new ArrayList<WanFangUnmatch>(){
			{
				add(unmatch);
			}
		};
		insertWanFangUnmatchData(unmatchs);
	}
	
	private List<WanFangUnmatch> convert2WanFangUnmatch(List<WanFangArticleBean> beans) {
		List<WanFangUnmatch> list = new ArrayList<>();
		if(null != beans && beans.size() > 0) {
			for (WanFangArticleBean wanFangArticleBean : beans) {
				WanFangUnmatch unmatch = convert2WanFangUnmatch(wanFangArticleBean);
				if(null != unmatch) {
					list.add(unmatch);
				}
			}
		}
		return list.size() > 0? list : null;
	}
	
	/**
	 * 将爬取的文章信息转化成可插入数据库的类型
	 * @param bean
	 * @return
	 */
	private WanFangUnmatch convert2WanFangUnmatch(WanFangArticleBean bean) {
		if(null != bean) {
			WanFangUnmatch unmatch = new WanFangUnmatch();
			String journalName = bean.getJournalName();
			unmatch.setJournalName(journalName);
			String articleName = bean.getArticleName();
			unmatch.setArticleName(articleName);
			String articleUrl = bean.getArticleUrl();
			unmatch.setArticleUrl(articleUrl);
			String pubYear = bean.getPubYear();
			unmatch.setPubYear(pubYear);
			String coverIssue = bean.getCoverIssue();
			unmatch.setCoverIssue(coverIssue);
			return unmatch;
		}
		return null;
	}
	
	/**
	 * 插入万方未比对成功的期刊文章信息
	 * @param unmatchs
	 */
	private void insertWanFangUnmatchData(List<WanFangUnmatch> unmatchs) {
		if(null != unmatchs && unmatchs.size() > 0) {
			SqlSession sqlSession = MyBatisUtil.getSqlSession();
			WanFangUnmatchMapper mapper = sqlSession.getMapper(WanFangUnmatchMapper.class);
			int i = 0;
			try {
				for (WanFangUnmatch wanFangUnmatch : unmatchs) {
					i++;
					mapper.insertSelective(wanFangUnmatch);
					sqlSession.commit();
				}
			} catch (Exception e) {
				logger.error("万方未比对成功文章信息插入出错:{}",JSONObject.toJSONString(unmatchs.get(i-1)));
				logger.error(e.getMessage());
				sqlSession.rollback();
			} finally {
				if(null != sqlSession) {
					sqlSession.close();
				}
			}
			
		}
	}
	
}
