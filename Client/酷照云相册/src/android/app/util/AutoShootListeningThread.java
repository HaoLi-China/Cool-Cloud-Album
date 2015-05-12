package android.app.util;

import android.app.myCamera.CameraView;

/*
 * 
 * �Զ����ռ����߳�
 * 
 * */

public class AutoShootListeningThread extends Thread {
	private CameraView cameraView;
	private final int sleep = 5000;// ˯��ʱ��
	private boolean flag = true;// ��־λ

	public AutoShootListeningThread(CameraView cameraView) {// ������
		this.cameraView = cameraView;
	}

	public void run() {
		while (flag) {
			// �ж�ң���Ƿ���
			if (!cameraView.getPhoneState()) {
				try {
					Thread.sleep(sleep);// ˯��sleep����
				} catch (Exception e) {
					e.printStackTrace();
				}
				// �Ƿ����Զ�����ģʽ
				if (cameraView.getAutoShootState() == 1) {
					cameraView.autoShoot();// �Զ�����
				}
			}
		}
	}

	/*-----------���ñ�־λ-------------*/
	public void setFlag(boolean bool) {
		this.flag = bool;
	}
}