package com.jdbc.dao;

import java.io.InputStream;
import java.util.Properties;

public class UserDaoFactory {

	// ��������䣬���λ�û����Ļ������������getUserDao()��������ȥ��ֵ�ǿյġ�
	private static UserDao userDao = null;
	private static UserDaoFactory instance = new UserDaoFactory();

	private UserDaoFactory() {
		try {
			Properties prop = new Properties();
			// InputStream inStream = new FileInputStream(new
			// File("src/daoconfig.properties"));//�˷�������ı����ļ�Ŀ¼����Ҫ���Ĵ��룬�鷳
			InputStream inStream = UserDaoFactory.class.getClassLoader()
					.getResourceAsStream("daoconfig.properties");// ���ַ���������ļ���classpath�У��Ϳ��Ե��룬�ɱ���ǿ��
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
