package android.app.fileManage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;

/*
 * 
 * 该类用于照片读取
 * 
 * */

public class FileReading {
	private List<String> imageList;// 手机设备中图像列表

	public FileReading(String path) {
		imageList = new ArrayList<String>();
		File f = new File("/sdcard" + path);
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			f = new File(Environment.getExternalStorageDirectory().toString()
					+ path);
		} else {
			// Toast.makeText(FileReading.this, R.string.sdcarderror, 1).show();
		}

		File[] files = f.listFiles();

		if (files == null || files.length == 0) {

		} else {
			// 将所有图像文件的路径存入ArrayList列表
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (isImageFile(file.getPath()))
					imageList.add(file.getPath());
			}
		}
	}

	/*----------------获得图片列表---------------------*/
	public List<String> getImageList() {
		return imageList;
	}

	/*----------------判断是否为图片文件---------------------*/
	private boolean isImageFile(String fName) {
		boolean re;
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();
		// 依据文件扩展名判断是否为图像文件
		if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			re = true;
		} else {
			re = false;
		}
		return re;
	}
}