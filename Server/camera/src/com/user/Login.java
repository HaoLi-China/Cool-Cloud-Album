/*
 * 此程序用于接收来自用户网络相册登录的处理
 * 用户的唯一标识为邮箱，登录时验证用户的邮箱账号和密码
 * 同时会把登录过程中的信息返回给用户的客户端，然后在客户端响应处理
 */

package com.user;

import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jdbc.dao.UserDao;
import com.jdbc.dao.UserDaoFactory;
import com.jdbc.domin.User;

@SuppressWarnings("serial")
public class Login extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse res) {
		// 中文乱码
		res.setContentType("text/html;charset=gbk");
		try {
			String auto = req.getParameter("auto");
			if (auto != null) {
				// 从客户端得到所有的cookie信息
				String uemail = req.getParameter("uemail");
				String upass = req.getParameter("upass");
				String result = "";
				User user = new User();
				user.setE_mail(uemail);
				user.setPassword(upass);
				UserDao userDao = UserDaoFactory.getInstance().getUserDao();
				result = userDao.login(user);
				if (result.startsWith("login success")) {
					String name = gainName(result);
					result = "auto success?" + name;
				} else
					result = "auto error";
				// 把登录处理返回给客户端，用于客户端的应答处理
				PrintWriter pw = res.getWriter();
				pw.write(result);
				return;
			}

			// 通过HttpServletRequest用户请求来获得用户登录时输入的邮箱账号和密码
			String uemail = req.getParameter("uemail");
			String upass = req.getParameter("upass");

			// 把得到的邮箱账号和密码保存在user对象中，用于数据库中用户的验证
			User user = new User();
			user.setE_mail(uemail);
			user.setPassword(upass);

			// 对用户进行登录处理，及进入数据库验证，同时把验证返回的信息存入字符串result中
			UserDao userDao = UserDaoFactory.getInstance().getUserDao();
			String result = userDao.login(user);
			System.out.println(gainName(result));
			// 把登录处理返回给客户端，用于客户端的应答处理
			PrintWriter pw = res.getWriter();
			pw.write(result);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) {
		doGet(req, res);
	}

	// 解析获得用户名
	private String gainName(String result) {
		String str[] = result.split("\\?");
		return str[str.length - 1];
	}
}
