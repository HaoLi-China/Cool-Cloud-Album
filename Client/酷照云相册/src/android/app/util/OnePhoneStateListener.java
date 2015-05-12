package android.app.util;

import android.app.myCamera.CameraView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/*
 * 
 * 电话状态监听
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
		case TelephonyManager.CALL_STATE_RINGING:// 等待接电话
			cameraView.setPhoneState(true);
			break;
		case TelephonyManager.CALL_STATE_IDLE:// 电话挂断
			if (cameraView.getPhoneState()) {
				cameraView.setAutoShootState(1);// 自动拍照状态
				cameraView.setPhoneState(false);
			}
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK: // 通话中
			break;
		}
		super.onCallStateChanged(state, incomingNumber);
	}
}
