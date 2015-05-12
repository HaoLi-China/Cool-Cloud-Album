/*
 * 根据用户的请求实现返回某个文件夹下图片的数目
 */
package com.user;

import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.utils.FileList;

@SuppressWarnings("serial")
public class ImageNum extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse res) {
		
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) {
		try {
			String uemail = req.getParameter("uemail");
			String filename = req.getParameter("filename");
			filename = URLDecoder.decode(filename, "utf-8");
			String filepath = "../webapps/camera/Albums/" + uemail + "/"
					+ filename;
			System.out.println(uemail);
			System.out.println(filename);
			int imagenum = FileList.ImageNum(filepath);
			String result = "" + imagenum;
			PrintWriter pw = res.getWriter();
			pw.write(result);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}