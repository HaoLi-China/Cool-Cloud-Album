/*
 * �˳������ڽ��������û���������¼�Ĵ���
 * �û���Ψһ��ʶΪ���䣬��¼ʱ��֤�û��������˺ź�����
 * ͬʱ��ѵ�¼�����е���Ϣ���ظ��û��Ŀͻ��ˣ�Ȼ���ڿͻ�����Ӧ����
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
		// ��������
		res.setContentType("text/html;charset=gbk");
		try {
			String auto = req.getParameter("auto");
			if (auto != null) {
				// �ӿͻ��˵õ����е�cookie��Ϣ
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
				// �ѵ�¼�����ظ��ͻ��ˣ����ڿͻ��˵�Ӧ����
				PrintWriter pw = res.getWriter();
				pw.write(result);
				return;
			}

			// ͨ��HttpServletRequest�û�����������û���¼ʱ����������˺ź�����
			String uemail = req.getParameter("uemail");
			String upass = req.getParameter("upass");

			// �ѵõ��������˺ź����뱣����user�����У��������ݿ����û�����֤
			User user = new User();
			user.setE_mail(uemail);
			user.setPassword(upass);

			// ���û����е�¼�������������ݿ���֤��ͬʱ����֤���ص���Ϣ�����ַ���result��
			UserDao userDao = UserDaoFactory.getInstance().getUserDao();
			String result = userDao.login(user);
			System.out.println(gainName(result));
			// �ѵ�¼�����ظ��ͻ��ˣ����ڿͻ��˵�Ӧ����
			PrintWriter pw = res.getWriter();
			pw.write(result);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) {
		doGet(req, res);
	}

	// ��������û���
	private String gainName(String result) {
		String str[] = result.split("\\?");
		return str[str.length - 1];
	}
}
