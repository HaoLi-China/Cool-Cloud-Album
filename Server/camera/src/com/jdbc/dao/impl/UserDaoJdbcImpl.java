package com.jdbc.dao.impl;

import java.io.File;

import com.jdbc.JdbcUtils;
import com.jdbc.dao.UserDao;
import com.jdbc.domin.User;

public class UserDaoJdbcImpl implements UserDao {

	/*
	 * �����register���������û�ע��ʱ���ݿ⴦��
	 */
	public String register(User user) {

		// ������search������������û���ע�������Ƿ��Ѿ�ע�����������search�����ķ�����Ϣ�����ע�᷽��������Ӧ����Ϣ
		String result = search(user.getE_mail());
		if (result == "search error")
			return "register error";
		if (result == "user exist")
			return "user exist"; // �û��Ѿ����ڣ��ظ�ע��

		// ���������ݿ�������������Ѵ����user����������û���Ϣ���Դ���user���ݱ���
		// ����һЩ���ݿ����ӵĶ���
		java.sql.Connection conn = null;
		java.sql.PreparedStatement ps = null;
		java.sql.ResultSet rs = null;

		// ���ڲ���һ�����û���Ϣ�Ĳ������
		String sql = "insert into user(e_mail,name,password)values(?,?,?)";
		try {
			// 2.��������
			conn = JdbcUtils.getConnection();
			// 3.�������
			ps = conn.prepareStatement(sql);
			// ʵ�ֲ�������е������ʺţ���ֹsqlע������
			ps.setString(1, user.getE_mail());
			ps.setString(2, user.getName());
			ps.setString(3, user.getPassword());

			// 4.ִ�����
			int i = ps.executeUpdate();
			System.out.println("i=" + i);

			makedir(user.getE_mail());
			return "register success"; // ע��ɹ�
		} catch (Exception e) {
			e.printStackTrace();
			return "register error";
		} finally {
			// 6.�ͷ���Դ
			JdbcUtils.free(rs, ps, conn);
		}
	}

	/*
	 * �����login���������û���¼ʱ���ݿ⴦��
	 */
	public String login(User user) {
		java.sql.Connection conn = null;
		java.sql.PreparedStatement ps = null;
		java.sql.ResultSet rs = null;
		String sql = "select password,name from user where e_mail=?";
		try {
			// 2.��������
			conn = JdbcUtils.getConnection();
			// 3.�������
			ps = conn.prepareStatement(sql);
			ps.setString(1, user.getE_mail());
			// 4.ִ�в�ѯ���
			rs = ps.executeQuery();
			if (rs.next()) {
				String pass = rs.getString("password");
				if (!pass.equals(user.getPassword()))
					return "pass wrong"; // ���벻��ȷ
				else {
					String name = rs.getString("name");
					return "login success?" + name; // �ɹ���¼
				}
			} else
				return "no user"; // �����Ϊ�գ��򷵻ز���������û�
		} catch (Exception e) {
			e.printStackTrace();
			return "login error";
		} finally {
			// 6.�ͷ���Դ
			JdbcUtils.free(rs, ps, conn);
		}
	}

	/*
	 * �����search���������û�ע��ʱ���ݿ�����Ƿ��û�ע�����䱻ע�����
	 */
	public String search(String e_mail) {
		java.sql.Connection conn = null;
		java.sql.PreparedStatement ps = null;
		java.sql.ResultSet rs = null;
		String sql = "select * from user where e_mail=?";
		try {
			// 2.��������
			conn = JdbcUtils.getConnection();
			// 3.�������
			ps = conn.prepareStatement(sql);
			ps.setString(1, e_mail);
			// 4.ִ�����
			rs = ps.executeQuery();
			if (rs.next()) {
				return "user exist"; // �������Ϊ�գ��û�����
			} else {
				return "new user"; // �����Ϊ�գ����û������ڣ����û�
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "search error";
		} finally {
			// 6.�ͷ���Դ
			JdbcUtils.free(rs, ps, conn);
		}
	}

	// �����û�ע��ʱ���û��ڷ������½����û�����������ļ���
	private static void makedir(String uemail) {
		try {
			String filepath = "..\\webapps\\camera\\Albums\\" + uemail;
			File myPath = new File(filepath);
			if (!myPath.exists()) {
				myPath.mkdir();
			}
			File Path[] = new File[3];
			String[] childfile = new String[3];
			String[] nocgfile = { "����", "����", "����" };// Ĭ�ϵ���������µ��ļ���
			for (int i = 0; i < 3; i++) {
				childfile[i] = "..\\webapps\\camera\\Albums\\" + uemail + "/"
						+ nocgfile[i];
				Path[i] = new File(childfile[i]);
				if (!Path[i].exists()) {
					Path[i].mkdir();
				}
			}
		} catch (Exception e) {
			System.out.println("�½�Ŀ¼����");
			e.printStackTrace();
		}

	}

}
