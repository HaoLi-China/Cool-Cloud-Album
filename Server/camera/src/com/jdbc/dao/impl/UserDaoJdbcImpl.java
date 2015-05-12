package com.jdbc.dao.impl;

import java.io.File;

import com.jdbc.JdbcUtils;
import com.jdbc.dao.UserDao;
import com.jdbc.domin.User;

public class UserDaoJdbcImpl implements UserDao {

	/*
	 * 下面的register方法用于用户注册时数据库处理
	 */
	public String register(User user) {

		// 首先用search方法查找这个用户的注册邮箱是否已经注册过，并根据search方法的返回信息对这个注册方法返回响应的信息
		String result = search(user.getE_mail());
		if (result == "search error")
			return "register error";
		if (result == "user exist")
			return "user exist"; // 用户已经存在，重复注册

		// 下面是数据库基本操作，及把传入的user参数对象的用户信息属性存入user数据表中
		// 声明一些数据库连接的对象
		java.sql.Connection conn = null;
		java.sql.PreparedStatement ps = null;
		java.sql.ResultSet rs = null;

		// 用于插入一行新用户信息的插入语句
		String sql = "insert into user(e_mail,name,password)values(?,?,?)";
		try {
			// 2.建立连接
			conn = JdbcUtils.getConnection();
			// 3.创建语句
			ps = conn.prepareStatement(sql);
			// 实现插入语句中的三个问号，防止sql注入问题
			ps.setString(1, user.getE_mail());
			ps.setString(2, user.getName());
			ps.setString(3, user.getPassword());

			// 4.执行语句
			int i = ps.executeUpdate();
			System.out.println("i=" + i);

			makedir(user.getE_mail());
			return "register success"; // 注册成功
		} catch (Exception e) {
			e.printStackTrace();
			return "register error";
		} finally {
			// 6.释放资源
			JdbcUtils.free(rs, ps, conn);
		}
	}

	/*
	 * 下面的login方法用于用户登录时数据库处理
	 */
	public String login(User user) {
		java.sql.Connection conn = null;
		java.sql.PreparedStatement ps = null;
		java.sql.ResultSet rs = null;
		String sql = "select password,name from user where e_mail=?";
		try {
			// 2.建立连接
			conn = JdbcUtils.getConnection();
			// 3.创建语句
			ps = conn.prepareStatement(sql);
			ps.setString(1, user.getE_mail());
			// 4.执行查询语句
			rs = ps.executeQuery();
			if (rs.next()) {
				String pass = rs.getString("password");
				if (!pass.equals(user.getPassword()))
					return "pass wrong"; // 密码不正确
				else {
					String name = rs.getString("name");
					return "login success?" + name; // 成功登录
				}
			} else
				return "no user"; // 结果集为空，则返回不存在这个用户
		} catch (Exception e) {
			e.printStackTrace();
			return "login error";
		} finally {
			// 6.释放资源
			JdbcUtils.free(rs, ps, conn);
		}
	}

	/*
	 * 下面的search方法用于用户注册时数据库查找是否用户注册邮箱被注册过了
	 */
	public String search(String e_mail) {
		java.sql.Connection conn = null;
		java.sql.PreparedStatement ps = null;
		java.sql.ResultSet rs = null;
		String sql = "select * from user where e_mail=?";
		try {
			// 2.建立连接
			conn = JdbcUtils.getConnection();
			// 3.创建语句
			ps = conn.prepareStatement(sql);
			ps.setString(1, e_mail);
			// 4.执行语句
			rs = ps.executeQuery();
			if (rs.next()) {
				return "user exist"; // 结果集不为空，用户存在
			} else {
				return "new user"; // 结果集为空，此用户不存在，新用户
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "search error";
		} finally {
			// 6.释放资源
			JdbcUtils.free(rs, ps, conn);
		}
	}

	// 用于用户注册时给用户在服务器新建该用户的网络相册文件夹
	private static void makedir(String uemail) {
		try {
			String filepath = "..\\webapps\\camera\\Albums\\" + uemail;
			File myPath = new File(filepath);
			if (!myPath.exists()) {
				myPath.mkdir();
			}
			File Path[] = new File[3];
			String[] childfile = new String[3];
			String[] nocgfile = { "生活", "旅游", "其他" };// 默认的网络相册下的文件夹
			for (int i = 0; i < 3; i++) {
				childfile[i] = "..\\webapps\\camera\\Albums\\" + uemail + "/"
						+ nocgfile[i];
				Path[i] = new File(childfile[i]);
				if (!Path[i].exists()) {
					Path[i].mkdir();
				}
			}
		} catch (Exception e) {
			System.out.println("新建目录存在");
			e.printStackTrace();
		}

	}

}
