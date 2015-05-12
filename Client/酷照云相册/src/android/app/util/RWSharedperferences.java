package android.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/*
 * 
 * 从配置文件读取内容
 * 向配置文件写内容
 *
 */

public class RWSharedperferences {
	private String strValue = null;
	private String name[] = { "autoSave", "autoFocus", "ImageQuality",
			"ImageResolution", "dely", "shutterSound", "scene", "effect",
			"whiteBalance", "flashMode", "cameraMode", "frameId", "skin" };
	private String value[] = { "1", "0", "0", "5", "0", "0", "0", "0", "0",
			"0", "0", "0", "0" };

	/*--------------从配置文件中读取内容----------------*/
	public String read(Context context, String keyName) {
		SharedPreferences share = context.getSharedPreferences("myCamera",
				Context.MODE_PRIVATE);
		strValue = share.getString(keyName, "");// 根据key寻找值 参数1 key 参数2
		// 如果没有value显示的内容
		if (strValue.contentEquals("")) {
			for (int i = 0; i < name.length; i++) {
				write(context, name[i], value[i]);
			}
		}
		return strValue;
	}

	/*-------------将内容写入配置文件中----------------*/
	public void write(Context context, String keyName, String keyValue) {
		SharedPreferences share = context.getSharedPreferences("myCamera",
				Context.MODE_PRIVATE);
		Editor editor = share.edit();// 取得编辑器
		editor.putString(keyName, keyValue);// 存储配置 参数1 是key 参数2 是值
		editor.putString(keyName, keyValue);
		editor.commit();// 提交刷新数据
	}
}
