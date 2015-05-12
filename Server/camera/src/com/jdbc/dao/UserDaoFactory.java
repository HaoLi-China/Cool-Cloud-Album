package com.jdbc.dao;

import java.io.InputStream;
import java.util.Properties;

public class UserDaoFactory {

	// 下面两语句，如果位置互换的话，将导致最后getUserDao()方法传出去的值是空的。
	private static UserDao userDao = null;
	private static UserDaoFactory instance = new UserDaoFactory();

	private UserDaoFactory() {
		try {
			Properties prop = new Properties();
			// InputStream inStream = new FileInputStream(new
			// File("src/daoconfig.properties"));//此方法如果改变了文件目录，将要更改代码，麻烦
			InputStream inStream = UserDaoFactory.class.getClassLoader()
					.getResourceAsStream("daoconfig.properties");// 这种方法如果次文件在classpath中，就可以导入，可变性强了
			prop.load(inStream);
			String UserDaoClass = prop.getProperty("UserDaoClass");
			userDao = (UserDao) Class.forName(UserDaoClass).newInstance();
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static UserDaoFactory getInstance() {
		return instance;
	}

	public UserDao getUserDao() {
		return userDao;
	}
}
