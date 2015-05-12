package com.user;

import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.utils.FileList;

@SuppressWarnings("serial")
public class OtherFile extends HttpServlet{
	public void doGet(HttpServletRequest req, HttpServletResponse res) {
		//ÖÐÎÄÂÒÂë
		res.setContentType("text/html;charset=gbk");
		try {
			String uemail = req.getParameter("uemail");
			System.out.println(uemail);
			String filepath = "../webapps/camera/Albums/" + uemail;
			String otherfile = FileList.OtherFile(filepath);
			System.out.println(otherfile);
			String result = otherfile;
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
