/*
 * 本方法用于接收用户的请求删除某个文件夹下的图片
 */

package com.user;

import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.utils.CDFile;

@SuppressWarnings("serial")
public class NoImage extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse res) {

	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) {
		// 中文乱码
		res.setContentType("text/html;charset=gbk");
		try {
			String uemail = req.getParameter("uemail");
			String filename = req.getParameter("filename");
			filename = URLDecoder.decode(filename, "utf-8");
			String imagename = req.getParameter("imagename");
			imagename = URLDecoder.decode(imagename, "utf-8");
			String filepath = "../webapps/camera/Albums/" + uemail;
			String di_result = CDFile
					.DeleteImage(filepath, filename, imagename);
			System.out.println(filename);
			String result = di_result;
			PrintWriter pw = res.getWriter();
			pw.write(result);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
