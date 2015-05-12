/*
 * 此程序用于接收来自用户网络相册注册的处理
 * 用户的唯一标识为邮箱，注册需把用户的邮箱、昵称、密码统一存入数据库，同时要保证邮箱不能注册过
 * 同时会把注册过程中的信息返回给用户的客户端，然后在客户端响应处理
 */
package com.user;

import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jdbc.dao.UserDao;
import com.jdbc.dao.UserDaoFactory;
import com.jdbc.domin.User;

@SuppressWarnings("serial")
public class Register extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse res) {

	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) {
		try {
			// 通过HttpServletRequest用户请求来获得用户注册时输入的邮箱账号、昵称和密码
			String uemail = req.getParameter("uemail");
			String uname = req.getParameter("uname");
			uname = URLDecoder.decode(uname, "utf-8");
			String upass = req.getParameter("upass");
			System.out.println(uname);
			// 把得到的邮箱账号、昵称和密码保存在user对象中，用于处理存入到数据库中
			User newuser = new User();
			newuser.setE_mail(uemail);
			newuser.setName(uname);
			newuser.setPassword(upass);

			// 对用户进行注册处理，及把新用户注册的信息存入数据库中，同时把验证返回的信息存入字符串result中
			UserDao userDao = UserDaoFactory.getInstance().getUserDao();
			String result = userDao.register(newuser);
			System.out.println(result);
			// 把注册处理返回给客户端，用于客户端的应答处理
			PrintWriter pw = res.getWriter();
			pw.println(result);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}