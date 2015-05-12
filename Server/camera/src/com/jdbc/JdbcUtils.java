/*
 * �˳��������ݿ�����Ĺ�����
 * ���ڽ������ݿ�����Ӻ��ͷ����ݿ���Դ
 * ���Լ��ٳ��������ݿ�������ظ�����
 * ʹ�õ���
 */

package com.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class JdbcUtils {
	
	private static String url = "jdbc:mysql://localhost:3306/camera"; //������Ҫ�������ݿ��url·��
	private static String user = "root";    //���ݿ�������û���
	private static String password = "sunion";   //���ݿ����������

	//���๹������Ϊ˽�У�ʹ�û����ܹ���һ������ʵ�������ٲ���Ҫ�Ĳ���
	private JdbcUtils() {
	}

	//��̬���û����ɷ��ʣ�ע�����ݿ������
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	
	//��̬�����������������ã��������ݿ�����
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}

	//��̬�����������������ã��ͷ����ݿ���Դ
	public static void free(ResultSet rs, Statement st, Connection conn) {
		try {
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
