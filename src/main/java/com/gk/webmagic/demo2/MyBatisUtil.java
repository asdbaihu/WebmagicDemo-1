package com.gk.webmagic.demo2;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MyBatisUtil {
	
	private static SqlSessionFactory sqlSessionFactory = null;
	
	static {
		Reader resourceAsReader = null;
		try {
			resourceAsReader = Resources.getResourceAsReader("SqlMapConfig.xml");
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsReader);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(null != resourceAsReader) {
				try {
					resourceAsReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static SqlSession getSqlSession() {
		return sqlSessionFactory.openSession();
	}
	
}
