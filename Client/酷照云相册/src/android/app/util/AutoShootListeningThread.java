package android.app.util;

import android.app.myCamera.CameraView;

/*
 * 
 * 自动拍照监听线程
 * 
 * */

public class AutoShootListeningThread extends Thread {
	private CameraView cameraView;
	private final int sleep = 5000;// 睡眠时间
	private boolean flag = true;// 标志位

	public AutoShootListeningThread(CameraView cameraView) {// 构造器
		this.cameraView = cameraView;
	}

	public void run() {
		while (flag) {
			// 判断遥控是否开启
			if (!cameraView.getPhoneState()) {
				try {
					Thread.sleep(sleep);// 睡眠sleep毫秒
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 是否处于自动拍照模式
				if (cameraView.getAutoShootState() == 1) {
					cameraView.autoShoot();// 自动拍照
				}
			}
		}
	}

	/*-----------设置标志位-------------*/
	public void setFlag(boolean bool) {
		this.flag = bool;
	}
}