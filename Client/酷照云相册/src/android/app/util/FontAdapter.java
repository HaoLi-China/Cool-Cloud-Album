package android.app.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FontAdapter {
	// ������������
	public static void changeViewSize(ViewGroup viewGroup, int screenWidth,
			int screenHeight) {
		// ����Activity����Layout,��Ļ��,��Ļ��
		int adjustFontSize = adjustFontSize(screenWidth, screenHeight);
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			View v = viewGroup.getChildAt(i);
			if (v instanceof ViewGroup) {
				changeViewSize((ViewGroup) v, screenWidth, screenHeight);
			} else if (v instanceof Button) {
				// ��ť�Ӵ����һ��Ҫ����TextView���棬��ΪButtonҲ�̳���TextView
				((Button) v).setTextSize(adjustFontSize + 2);
			} else if (v instanceof TextView) {
				((TextView) v).setTextSize(adjustFontSize);
			}
		}
	}

	// ��ȡ�����С
	public static int adjustFontSize(int screenWidth, int screenHeight) {
		screenWidth = screenWidth > screenHeight ? screenWidth : screenHeight;
		/**
		 * ����ͼ�� onsizechanged���ȡ��ͼ��ȣ�һ�������Ĭ�Ͽ����320�����Լ���һ�����ű��� rate = (float)
		 * w/320 w��ʵ�ʿ�� 2.Ȼ������������ߴ�ʱ paint.setTextSize((int)(8*rate));
		 * 8���ڷֱ��ʿ�Ϊ320 ����Ҫ���õ������С ʵ�������С = Ĭ�������С x rate
		 */
		int rate = (int) (5 * (float) screenWidth / 320);
		return rate < 15 ? 15 : rate;
	}
}
