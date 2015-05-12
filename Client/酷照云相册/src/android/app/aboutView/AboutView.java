package android.app.aboutView;

import android.app.Activity;
import android.app.myCamera.R;
import android.app.util.RWSharedperferences;
import android.app.util.SkinSet;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

/*
 * 
 * 该类用于显示关于界面
 * 
 * */

public class AboutView extends Activity {
	private int skinId = 0;// 皮肤id号

	private double x1, x2, y1, y2;// 手指划过屏幕时前后坐标值

	private String skinState = "0";// 皮肤模式

	private RWSharedperferences rws;// 读写配置文件对象

	private ImageView skinView;// 用于放置皮肤

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 无title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 设置屏幕恒为竖向
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.about_view);

		skinView = (ImageView) findViewById(R.id.about_view_skin);

		rws = new RWSharedperferences();

		skinState = rws.read(this, "skin");
		if (!skinState.contentEquals("")) {
			skinId = Integer.parseInt(skinState);
			new SkinSet(skinView, skinState);
		}
	}

	/*--------------监听手指滑动屏幕事件------------------*/
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			x1 = event.getX();
			y1 = event.getY();
		case MotionEvent.ACTION_UP:
			x2 = event.getX();
			y2 = event.getY();
		}

		if (x1 - x2 > 100 || y1 - y2 > 100) {
			if (skinId < 3) {
				skinId++;
			} else {
				skinId = 0;
			}
			rws.write(this, "skin", skinId + "");
			new SkinSet(skinView, skinId + "");
		}
		return true;
	}
}
