package android.app.util;

import android.app.myCamera.R;
import android.widget.ImageView;

/*
 * 
 * 实现换肤功能
 * 
 * */

public class SkinSet {
	public SkinSet(ImageView imageView, String skinState) {
		switch (Integer.parseInt(skinState)) {
		case 0:
			imageView.setBackgroundResource(R.drawable.back4);
			break;
		case 1:
			imageView.setBackgroundResource(R.drawable.back5);
			break;
		case 2:
			imageView.setBackgroundResource(R.drawable.back6);
			break;
		case 3:
			imageView.setBackgroundResource(R.drawable.back7);
			break;
		}
	}
}
