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
 * �������ڽ��ļ�д��SD����
 * 
 * */

public class FileWriting {
	private String SDPATH;
	private String PATH = "myCamera/";

	public String getSDPATH() {
		return SDPATH;
	}

	public FileWriting() {
		// �õ���ǰ�ⲿ�洢�豸��Ŀ¼
		// SDCARD
		SDPATH = Environment.getExternalStorageDirectory() + "/";
		creatSDDir(PATH);
		SDPATH = SDPATH + PATH;
	}

	/*----------------��SD���ϴ����ļ�---------------*/
	public File creatSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		file.createNewFile();
		return file;
	}

	/*--------------��SD���ϴ���Ŀ¼-----------------*/
	public File creatSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		dir.mkdir();
		return dir;
	}

	/*--------------�ж�SD���ϵ��ļ��Ƿ����--------------*/
	public boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.exists();
	}

	/*----------------������д�뵽SD����---------------------*/
	public void write2SDFromInput(String path, Bitmap bm) {
		// �������ļ�
		try {
			creatSDDir(path);
			File myCaptureFile = creatSDFile(path + System.currentTimeMillis()
					+ ".jpg");
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(myCaptureFile));
			bm.compress(Bitmap.CompressFormat.JPEG, 80, bos); // ����ѹ��ת������
			bos.flush();
			bos.close();
			Thread.sleep(4000);// ˯��4��
		} catch (Exception e) {
			Log.e("camera", e.getMessage());
		}
	}

	/*--------------��д�����ļ�--------------*/
	public void overWrite(String path, String name, Bitmap bm) {
		// �������ļ�
		try {
			creatSDDir(path);
			File myCaptureFile = creatSDFile(path + name);
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(myCaptureFile));
			bm.compress(Bitmap.CompressFormat.JPEG, 80, bos); // ����ѹ��ת������
			bos.flush();
			bos.close();
		} catch (Exception e) {
			Log.e("camera", e.getMessage());
		}
	}
}