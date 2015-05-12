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
 * ����������ʾ���ڽ���
 * 
 * */

public class AboutView extends Activity {
	private int skinId = 0;// Ƥ��id��

	private double x1, x2, y1, y2;// ��ָ������Ļʱǰ������ֵ

	private String skinState = "0";// Ƥ��ģʽ

	private RWSharedperferences rws;// ��д�����ļ�����

	private ImageView skinView;// ���ڷ���Ƥ��

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ��title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ȫ��
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ������Ļ��Ϊ����
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

	/*--------------������ָ������Ļ�¼�------------------*/
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
