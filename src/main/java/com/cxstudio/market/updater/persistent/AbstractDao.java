package com.cxstudio.market.updater.persistent;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

public abstract class AbstractDao {
	static Logger log = Logger.getLogger(AbstractDao.class.getName());
	static final String resource = "mybatis-config.xml";
	static SqlSessionFactory sqlSessionFactory;
	SqlSession session;

	static synchronized void init() {
		if (sqlSessionFactory == null) {
			InputStream inputStream = null;
			try {
				inputStream = Resources.getResourceAsStream(resource);
			} catch (IOException e) {
				log.fatal("Unable to read mybatis-config.xml");
			}
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		}
	}

	public SqlSession getSession() {
		return session;
	}

	public abstract void connect();

	public void disconnect() {
		this.session.close();
	}

}
