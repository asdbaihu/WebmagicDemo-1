package com.gk.webmagic.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.gk.webmagic.demo2.MyBatisUtil;
import com.gk.webmagic.demo2.SpiderStarter;
import com.gk.webmagic.demo2.mapper.DownPeriodicalLstMapper;
import com.gk.webmagic.demo2.mapper.DownPeriodicalWanfangMapper;
import com.gk.webmagic.demo2.mapper.WanFangJournalCompareMapper;
import com.gk.webmagic.demo2.model.DownPeriodicalWanfangWithBLOBs;
import com.gk.webmagic.demo2.model.WanFangJournalCompare;

public class MyBatisTest {
	
	private static Logger logger = LoggerFactory.getLogger(MyBatisTest.class);
	
	@Test
	public void test01() {
		SqlSession sqlSession = MyBatisUtil.getSqlSession();
		try {
			DownPeriodicalLstMapper mapper = sqlSession.getMapper(DownPeriodicalLstMapper.class);
			Map<String, String> mapParam = new HashMap<>();
			mapParam.put("docId", "cnkiXDZZ100");
			mapParam.put("pubYear", "2014");
			mapParam.put("coverIssue", "10");
			List<Map<String, Object>> list = mapper.selectArticlesByDocId(mapParam);
			for (Map<String, Object> map : list) {
				System.out.println(map.get("DOC_ID")+"-"+map.get("JOURNAL_TITLE")+"-"+map.get("PUBL_YEAR")
						+"-"+map.get("COVER_ISSUE")+"-"+map.get("TITLE"));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error("{}",e.getMessage().indexOf("com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException: Could not create connection to database server"));
		} finally {
			sqlSession.close();
		}
		
	}

	@Test
	public void test02() {
		SqlSession sqlSession = MyBatisUtil.getSqlSession();
		WanFangJournalCompareMapper mapper = sqlSession.getMapper(WanFangJournalCompareMapper.class);
		List<WanFangJournalCompare> list = new ArrayList<>();
		WanFangJournalCompare compare = new WanFangJournalCompare();
		compare.setJournalName("南京");
		compare.setDocId("1111");
		compare.setJournalUrl("https://www.hao123.com/");
		compare.setSuccessFlag("1");
		list.add(compare);
		compare = new WanFangJournalCompare();
		compare.setJournalName("北京");
		compare.setJournalUrl("https://www.hao123.com/");
		compare.setDocId(null);
		compare.setSuccessFlag("0");
		list.add(compare);
		mapper.batchInsert(list);
		sqlSession.commit();
		sqlSession.close();
	}
	
	@Test
	public void test03() throws Exception {
		SpiderStarter starter = new SpiderStarter();
		List<WanFangJournalCompare> list = starter.getWanFangDoneData("1");
		if(null != list && list.size() > 0) {
			String journalUrlPattern = "^http://c.wanfangdata.com.cn/Periodical-\\w+(-\\w*)*.aspx";
			Pattern pattern = Pattern.compile(journalUrlPattern);
			for (WanFangJournalCompare wanFangJournalCompare : list) {
				Matcher m = pattern.matcher(wanFangJournalCompare.getJournalUrl());
				if(!m.find()) {
					System.out.println(JSONObject.toJSONString(wanFangJournalCompare));
				}
			}
		}
	}
	
	private void insertWanFangInfo(DownPeriodicalWanfangWithBLOBs wanfang) {
		SqlSession sqlSession = MyBatisUtil.getSqlSession();
		try {
			DownPeriodicalWanfangMapper mapper = sqlSession.getMapper(DownPeriodicalWanfangMapper.class);
			mapper.insertSelective(wanfang);
			sqlSession.commit();
//			logger.info("成功插入万方数据：{}",JSONObject.toJSONString(wanfang));
		} catch (Exception e) {
//			e.printStackTrace();
			logger.error("插入万方数据出错：{}",JSONObject.toJSONString(wanfang));
			logger.error(e.getMessage());
			logger.info("{}",e.getMessage().indexOf("com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Duplicate entry") >= 0);
			sqlSession.rollback();
		} finally {
			if(sqlSession != null) {
				sqlSession.close();
			}
		}
	}
	
	@Test
	public void test04() {
		DownPeriodicalWanfangWithBLOBs wanfangWithBLOBs = new DownPeriodicalWanfangWithBLOBs();
		wanfangWithBLOBs.setId(4344438);
		wanfangWithBLOBs.setDocId("cnkiEAST100");
		wanfangWithBLOBs.setPubYear("2001");
		wanfangWithBLOBs.setTitle("试析经济差距与苏联解体的关系--兼对多民族国家民族分离主义的思考");
		wanfangWithBLOBs.setUrl("http://d.wanfangdata.com.cn/Periodical_elszydoyj200103002.aspx");
		insertWanFangInfo(wanfangWithBLOBs);
	}
	
	@Test
	public void test05() {
		SqlSession sqlSession = MyBatisUtil.getSqlSession();
		try {
			WanFangJournalCompareMapper mapper = sqlSession.getMapper(WanFangJournalCompareMapper.class);
//			mapper.updateJournalState("cnkiXDZZ100","1");
			sqlSession.commit();
//			logger.info("成功插入万方数据：{}",JSONObject.toJSONString(wanfang));
		} catch (Exception e) {
//			e.printStackTrace();
			sqlSession.rollback();
		} finally {
			if(sqlSession != null) {
				sqlSession.close();
			}
		}
	}
	
	@Test
	public void test06() {
//		try(SqlSession sqlSession = MyBatisUtil.getSqlSession()) {
//			WanFangJournalCompareMapper mapper = sqlSession.getMapper(WanFangJournalCompareMapper.class);
//			int count = mapper.selectCount();
//			System.out.println(count);
//		}
		SpiderStarter starter = new SpiderStarter();
		starter.cleanDataBeforeRepeat();
	}
}
