package com.utils;

import java.io.File;

public class CDFile {

	public static String CreateFile(String filepath, String filename) {
		try {
			File myPath = new File(filepath + "/" + filename);
			if (!myPath.exists()) {
				myPath.mkdir();
				return "create success";
			} else
				return "create wrong";
		} catch (Exception e) {
			e.printStackTrace();
			return "create error";
		}
	}

	public static String DeleteFile(String filepath, String filename) {
		try {
			String path = filepath + "/" + filename;
			File myPath = new File(path);
			if (!myPath.exists()) {
				return "delete wrong";
			} else {
				if (!delAllFile(path)){// 删除完里面所有内容
					return "delete wrong";
				}
				myPath.delete(); // 删除空文件夹
				return "delete success";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "delete error";
		}
	}

	// 删除指定文件夹下所有文件
	public static boolean delAllFile(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			return false;
		}
		if (!dir.isDirectory()) {
			return false;
		}
		File[] files = dir.listFiles();
		if (files == null)
			return true;
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			file.delete();
		}
		return true;
	}

	public static String DeleteImage(String filepath, String filename,
			String imagename) {
		try {
			String path = filepath + "/" + filename + "/" + imagename;
			File myimage = new File(path);
			if (!myimage.exists())
				return "dimage wrong";
			myimage.delete();
			return "dimage success";
		} catch (Exception e) {
			e.printStackTrace();
			return "dimage error";
		}
	}
}
