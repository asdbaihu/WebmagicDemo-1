package com.gk.webmagic.demo2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gk.webmagic.demo2.bean.WanFangJournalBean;
import com.gk.webmagic.demo2.mapper.DownPeriCataLstMapper;
import com.gk.webmagic.demo2.mapper.WanFangJournalCompareMapper;
import com.gk.webmagic.demo2.model.DownPeriCataLstExample;
import com.gk.webmagic.demo2.model.DownPeriCataLstWithBLOBs;
import com.gk.webmagic.demo2.model.WanFangJournalCompare;
import com.gk.webmagic.demo2.util.Util;

public class WanFangTask {
	
	private static Logger logger = LoggerFactory.getLogger(WanFangTask.class);
	
	//万方中爬取的所有期刊信息
	private List<WanFangJournalBean> spiderBeans = new ArrayList<>();
	//DC库中所有的CNKI期刊信息
	private List<DownPeriCataLstWithBLOBs> cnkiBeans = new ArrayList<>();
	//保存所有万方期刊比对成功信息
	private List<WanFangJournalBean> wanFangJournalBeans = new ArrayList<>();
	//保存所有万方期刊比对未成功信息
	private List<WanFangJournalBean> unSuccessBeans = new ArrayList<>();
	
	public WanFangTask() {
		cnkiBeans = getCnkiJournals();
	}
	
	public List<WanFangJournalBean> getWanFangJournalBeans() {
		return wanFangJournalBeans;
	}

	public void setWanFangJournalBeans(List<WanFangJournalBean> wanFangJournalBeans) {
		this.wanFangJournalBeans = wanFangJournalBeans;
	}
	
	public List<WanFangJournalBean> getUnSuccessBeans() {
		return unSuccessBeans;
	}

	public void setUnSuccessBeans(List<WanFangJournalBean> unSuccessBeans) {
		this.unSuccessBeans = unSuccessBeans;
	}
	
	public List<WanFangJournalBean> getSpiderBeans() {
		return spiderBeans;
	}

	/**
	 * 获取DC库中所有CNKI的期刊名称
	 * @return
	 */
	private List<DownPeriCataLstWithBLOBs> getCnkiJournals() {
		SqlSession sqlSession = MyBatisUtil.getSqlSession();
		List<DownPeriCataLstWithBLOBs> list = null;
		try {
			DownPeriCataLstMapper mapper = sqlSession.getMapper(DownPeriCataLstMapper.class);
			DownPeriCataLstExample example = new DownPeriCataLstExample();
			example.createCriteria().andIdIsNotNull(); 
			list = mapper.selectByExampleWithBLOBs(example);
			logger.info("获取CNKI期刊数据成功，总共获取{}条数据！",list.size());
		} catch (Exception e) {
			// ignore
			logger.info("获取CNKI期刊数据失败！");
		} finally {
			if(null != sqlSession) {
				sqlSession.close();
			}
		}
		return null != list && list.size() > 0? list : null;
	}
	
	/**
	 * 将万方期刊信息放入BlockingQueue中
	 * @param list
	 */
	public void addWanFangJournalInfo(List<WanFangJournalBean> list) {
		spiderBeans.addAll(list);
	}
	
	/**
	 * 期刊级别的比较
	 * @param list
	 */
	public void startCompare(List<WanFangJournalBean> list) {
		if(list.size() > 0 && null != cnkiBeans && cnkiBeans.size() > 0) {
			doCompare(list, cnkiBeans);
		}
	}
	
	public void doCompare(List<WanFangJournalBean> wanfangSortedList,List<DownPeriCataLstWithBLOBs> cnkiSortedList) {
		List<DownPeriCataLstWithBLOBs> maxList = cnkiSortedList;
		List<WanFangJournalBean> minList = wanfangSortedList;
		
		Map<String, String> targetMap = new HashMap<>();
		//将cnki数据放入map中
		for(DownPeriCataLstWithBLOBs cnki : maxList) {
			targetMap.put(Util.removeSign(cnki.getJournalTitle()), cnki.getDocId());
		}
		
		//用万方数据进行比较
		for(WanFangJournalBean wanFangJournalBean : minList) {
			boolean isExist = targetMap.containsKey(Util.removeSign(wanFangJournalBean.getName()));
			if(isExist) {
				wanFangJournalBean.setDocId(targetMap.get(Util.removeSign(wanFangJournalBean.getName())));
				wanFangJournalBeans.add(wanFangJournalBean);//匹配成功的
			}else {
				unSuccessBeans.add(wanFangJournalBean);//匹配未成功的
			}
		}
		List<WanFangJournalCompare> insertList = genInsertList();
		batchInsertData(insertList);
	}
	
	/**
	 * 生成需要导入到数据库中的期刊比对结果
	 * @return
	 */
	private List<WanFangJournalCompare> genInsertList() {
		List<WanFangJournalCompare> successList = convert2WanFangData(this.getWanFangJournalBeans(),"1");
		List<WanFangJournalCompare> unSuccessList = convert2WanFangData(this.getUnSuccessBeans(),"0");
		List<WanFangJournalCompare> insertList = new ArrayList<>();
		if(null != successList && successList.size() > 0) {
			insertList.addAll(successList);
		}
		if(null != unSuccessList && unSuccessList.size() > 0) {
			insertList.addAll(unSuccessList);
		}
		return insertList.size() > 0? insertList : null;
	}
	
	/**
	 * 将爬取的万方期刊转成可导入数据库的类型
	 * @param bean
	 * @param flag 是否成功标志
	 * @return
	 */
	private WanFangJournalCompare convert2WanFangData(WanFangJournalBean bean,String flag) {
		if(bean != null) {
			WanFangJournalCompare compare = new WanFangJournalCompare();
			String name = bean.getName();
			compare.setJournalName(name);
			String url = bean.getUrl();
			compare.setJournalUrl(url);
			String docId = bean.getDocId();
			compare.setDocId(docId);
			compare.setSuccessFlag(flag);
			return compare;
		}
		return null;
	}
	
	private List<WanFangJournalCompare> convert2WanFangData(List<WanFangJournalBean> list,String flag) {
		List<WanFangJournalCompare> compares = new ArrayList<>();
		if(null != list && list.size() > 0) {
			for (WanFangJournalBean wanFangJournalBean : list) {
				WanFangJournalCompare compare = convert2WanFangData(wanFangJournalBean,flag);
				if(null != compare) {
					compares.add(compare);
				}
			}
		}
		return compares.size() > 0? compares : null;
	}
	
	/**
	 * 批量插入比对结果
	 * @param list
	 */
	private void batchInsertData(List<WanFangJournalCompare> list) {
		if(null != list && list.size() > 0) {
			SqlSession sqlSession = MyBatisUtil.getSqlSession();
			try {
				WanFangJournalCompareMapper mapper = sqlSession.getMapper(WanFangJournalCompareMapper.class);
				mapper.batchInsert(list);
				sqlSession.commit();
				logger.info("成功插入万方期刊数据{}条！",list.size());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("插入万方期刊数据出错！");
			} finally {
				if(null != sqlSession) {
					sqlSession.close();
				}
			}
		}
	}
}
