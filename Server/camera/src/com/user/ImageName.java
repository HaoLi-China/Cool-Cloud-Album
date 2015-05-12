/*
 * 根据用户的请求实现返回某个文件夹下所有图片的名称
 */
package com.user;

import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.utils.FileList;

@SuppressWarnings("serial")
public class ImageName extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse res) {
		
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) {
		// 中文乱码
		res.setContentType("text/html;charset=gbk");
		try {
			String uemail = req.getParameter("uemail");
			String filename = req.getParameter("filename");
			filename = URLDecoder.decode(filename, "utf-8");
			String filepath = "../webapps/camera/Albums/" + uemail + "/"
					+ filename;
			System.out.println(uemail);
			System.out.println(filename);
			String imagename = FileList.ImageName(filepath);
			String result = imagename;
			PrintWriter pw = res.getWriter();
			pw.write(result);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}