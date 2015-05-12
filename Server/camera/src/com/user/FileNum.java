/*
 * 根据用户的请求实现返某个用户下相册文件夹的数目
 */
package com.user;

import com.utils.FileList;

import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class FileNum extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse res) {
		try {
			String uemail = req.getParameter("uemail");
			System.out.println(uemail);
			String filepath = "../webapps/camera/Albums/" + uemail;
			int filenum = FileList.FileNum(filepath);
			System.out.println(filenum);
			String result = "" + filenum;
			PrintWriter pw = res.getWriter();
			pw.write(result);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) {
		doGet(req, res);
	}
}