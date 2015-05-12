package android.app.util;

import android.app.myCamera.CameraView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/*
 * 
 * �绰״̬����
 * 
 */

public class OnePhoneStateListener extends PhoneStateListener {
	public final static String TAG = "MyBroadcastReceiver";
	CameraView cameraView;

	public OnePhoneStateListener(CameraView cameraView) {
		this.cameraView = cameraView;
	}

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		switch (state) {
		case TelephonyManager.CALL_STATE_RINGING:// �ȴ��ӵ绰
			cameraView.setPhoneState(true);
			break;
		case TelephonyManager.CALL_STATE_IDLE:// �绰�Ҷ�
			if (cameraView.getPhoneState()) {
				cameraView.setAutoShootState(1);// �Զ�����״̬
				cameraView.setPhoneState(false);
			}
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK: // ͨ����
			break;
		}
		super.onCallStateChanged(state, incomingNumber);
	}
}
