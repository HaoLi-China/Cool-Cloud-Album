/*
 * �˳������ڽ��������û��������ע��Ĵ���
 * �û���Ψһ��ʶΪ���䣬ע������û������䡢�ǳơ�����ͳһ�������ݿ⣬ͬʱҪ��֤���䲻��ע���
 * ͬʱ���ע������е���Ϣ���ظ��û��Ŀͻ��ˣ�Ȼ���ڿͻ�����Ӧ����
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
			// ͨ��HttpServletRequest�û�����������û�ע��ʱ����������˺š��ǳƺ�����
			String uemail = req.getParameter("uemail");
			String uname = req.getParameter("uname");
			uname = URLDecoder.decode(uname, "utf-8");
			String upass = req.getParameter("upass");
			System.out.println(uname);
			// �ѵõ��������˺š��ǳƺ����뱣����user�����У����ڴ�����뵽���ݿ���
			User newuser = new User();
			newuser.setE_mail(uemail);
			newuser.setName(uname);
			newuser.setPassword(upass);

			// ���û�����ע�ᴦ���������û�ע�����Ϣ�������ݿ��У�ͬʱ����֤���ص���Ϣ�����ַ���result��
			UserDao userDao = UserDaoFactory.getInstance().getUserDao();
			String result = userDao.register(newuser);
			System.out.println(result);
			// ��ע�ᴦ���ظ��ͻ��ˣ����ڿͻ��˵�Ӧ����
			PrintWriter pw = res.getWriter();
			pw.println(result);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}