package android.app.fileManage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

/*
 * 
 * 该类用于将文件写到SD卡中
 * 
 * */

public class FileWriting {
	private String SDPATH;
	private String PATH = "myCamera/";

	public String getSDPATH() {
		return SDPATH;
	}

	public FileWriting() {
		// 得到当前外部存储设备的目录
		// SDCARD
		SDPATH = Environment.getExternalStorageDirectory() + "/";
		creatSDDir(PATH);
		SDPATH = SDPATH + PATH;
	}

	/*----------------在SD卡上创建文件---------------*/
	public File creatSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		file.createNewFile();
		return file;
	}

	/*--------------在SD卡上创建目录-----------------*/
	public File creatSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		dir.mkdir();
		return dir;
	}

	/*--------------判断SD卡上的文件是否存在--------------*/
	public boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.exists();
	}

	/*----------------将数据写入到SD卡中---------------------*/
	public void write2SDFromInput(String path, Bitmap bm) {
		// 创建新文件
		try {
			creatSDDir(path);
			File myCaptureFile = creatSDFile(path + System.currentTimeMillis()
					+ ".jpg");
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(myCaptureFile));
			bm.compress(Bitmap.CompressFormat.JPEG, 80, bos); // 采用压缩转档方法
			bos.flush();
			bos.close();
			Thread.sleep(4000);// 睡眠4秒
		} catch (Exception e) {
			Log.e("camera", e.getMessage());
		}
	}

	/*--------------重写覆盖文件--------------*/
	public void overWrite(String path, String name, Bitmap bm) {
		// 创建新文件
		try {
			creatSDDir(path);
			File myCaptureFile = creatSDFile(path + name);
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(myCaptureFile));
			bm.compress(Bitmap.CompressFormat.JPEG, 80, bos); // 采用压缩转档方法
			bos.flush();
			bos.close();
		} catch (Exception e) {
			Log.e("camera", e.getMessage());
		}
	}
}