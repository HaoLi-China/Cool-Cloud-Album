package com.utils;

import java.io.File;

public class FileList {

	public static int ImageNum(String filepath) {
		File dir = new File(filepath);
		File[] files = dir.listFiles();
		int imagenum = 0;
		if (files == null)
			return 0;
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if ((file.getName()).endsWith(".jpg")
					|| (file.getName()).endsWith(".png"))
				imagenum++;
		}
		return imagenum;
	}

	public static String ImageName(String filepath) {
		File dir = new File(filepath);
		File[] files = dir.listFiles();
		if (files == null)
			return "error";
		String imagename = "";
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if ((file.getName()).endsWith(".jpg")
					|| (file.getName()).endsWith(".png"))
				imagename = imagename + file.getName() + "?";
		}
		return imagename;
	}

	public static int FileNum(String filepath) {
		File dir = new File(filepath);
		File[] files = dir.listFiles();
		if (files == null)
			return 0;
		int filenum = 0;
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory())
				filenum++;
		}
		return filenum;
	}

	public static String OtherFile(String filepath) {
		File dir = new File(filepath);
		File[] files = dir.listFiles();
		if (files == null)
			return "error";
		String otherfile = "";
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory())
				if (!file.getName().equals("生活")
						&& !file.getName().equals("旅游")
						&& !file.getName().equals("其他"))
					otherfile = otherfile + file.getName() + "?";
		}
		return otherfile;
	}

}
