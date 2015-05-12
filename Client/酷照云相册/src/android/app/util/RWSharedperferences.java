package android.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/*
 * 
 * �������ļ���ȡ����
 * �������ļ�д����
 *
 */

public class RWSharedperferences {
	private String strValue = null;
	private String name[] = { "autoSave", "autoFocus", "ImageQuality",
			"ImageResolution", "dely", "shutterSound", "scene", "effect",
			"whiteBalance", "flashMode", "cameraMode", "frameId", "skin" };
	private String value[] = { "1", "0", "0", "5", "0", "0", "0", "0", "0",
			"0", "0", "0", "0" };

	/*--------------�������ļ��ж�ȡ����----------------*/
	public String read(Context context, String keyName) {
		SharedPreferences share = context.getSharedPreferences("myCamera",
				Context.MODE_PRIVATE);
		strValue = share.getString(keyName, "");// ����keyѰ��ֵ ����1 key ����2
		// ���û��value��ʾ������
		if (strValue.contentEquals("")) {
			for (int i = 0; i < name.length; i++) {
				write(context, name[i], value[i]);
			}
		}
		return strValue;
	}

	/*-------------������д�������ļ���----------------*/
	public void write(Context context, String keyName, String keyValue) {
		SharedPreferences share = context.getSharedPreferences("myCamera",
				Context.MODE_PRIVATE);
		Editor editor = share.edit();// ȡ�ñ༭��
		editor.putString(keyName, keyValue);// �洢���� ����1 ��key ����2 ��ֵ
		editor.putString(keyName, keyValue);
		editor.commit();// �ύˢ������
	}
}
