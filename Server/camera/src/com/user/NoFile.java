package com.user;

import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.utils.CDFile;

@SuppressWarnings("serial")
public class NoFile extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse res) {

	}
	public void doPost(HttpServletRequest req, HttpServletResponse res) {
		// ÖÐÎÄÂÒÂë
		res.setContentType("text/html;charset=gbk");
		try {
			String uemail = req.getParameter("uemail");
			String filename = req.getParameter("filename");
			filename = URLDecoder.decode(filename, "utf-8");
			String filepath = "../webapps/camera/Albums/" + uemail;
			String d_result = CDFile.DeleteFile(filepath, filename);
			System.out.println("delete" + uemail);
			System.out.println("delete" + filename);
			String result = d_result;
			System.out.println(d_result);
			PrintWriter pw = res.getWriter();
			pw.write(result);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
