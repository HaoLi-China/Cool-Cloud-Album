package android.app.helpView;

import java.io.IOException;

import android.app.Activity;
import android.app.myCamera.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;

/*
 * 
 * 实现翻页
 * 
 * */

public class TurnTest extends Activity {

	private int window_width;// 屏幕的宽
	private int window_height;// 屏幕的高

	private PageWidget mPageWidget;// PageWidget对象

	private Bitmap mCurPageBitmap, mNextPageBitmap;
	private Bitmap backBitmap[] = new Bitmap[3];

	private Canvas mCurPageCanvas, mNextPageCanvas;

	private BookPageFactory pagefactory;// BookPageFactory对象

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		window_height = getWindowManager().getDefaultDisplay().getHeight();
		window_width = getWindowManager().getDefaultDisplay().getWidth();
		mPageWidget = new PageWidget(this, window_width, window_height);

		setContentView(mPageWidget);

		mCurPageBitmap = Bitmap.createBitmap(window_width, window_height,
				Bitmap.Config.ARGB_8888);
		mNextPageBitmap = Bitmap.createBitmap(window_width, window_height,
				Bitmap.Config.ARGB_8888);

		// 每一页的背景图片数组
		backBitmap[0] = BitmapFactory.decodeResource(getResources(),
				R.drawable.help_view1);
		backBitmap[1] = BitmapFactory.decodeResource(getResources(),
				R.drawable.help_view2);
		backBitmap[2] = BitmapFactory.decodeResource(getResources(),
				R.drawable.help_view3);
		for (int i = 0; i < 3; i++) {
			backBitmap[i] = Bitmap.createScaledBitmap(backBitmap[i],
					window_width, window_height, true);
		}

		mCurPageCanvas = new Canvas(mCurPageBitmap);
		mNextPageCanvas = new Canvas(mNextPageBitmap);
		pagefactory = new BookPageFactory(window_width, window_height);

		pagefactory.setBgBitmap(backBitmap);
		pagefactory.onDraw(mCurPageCanvas);
		mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);

		/*-------------------------触摸监听-------------------------*/
		mPageWidget.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				// TODO Auto-generated method stub

				boolean ret = false;
				if (v == mPageWidget) {
					if (e.getAction() == MotionEvent.ACTION_DOWN) {
						mPageWidget.abortAnimation();
						mPageWidget.calcCornerXY(e.getX(), e.getY());

						pagefactory.onDraw(mCurPageCanvas);
						if (mPageWidget.DragToRight()) {
							try {
								pagefactory.prePage();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							if (pagefactory.isfirstPage())
								return false;
							pagefactory.onDraw(mNextPageCanvas);
						} else {
							try {
								pagefactory.nextPage();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							if (pagefactory.islastPage())
								return false;
							pagefactory.onDraw(mNextPageCanvas);
						}
						mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
					}

					ret = mPageWidget.doTouchEvent(e);
					return ret;
				}
				return false;
			}
		});
	}

	/*-------------捕获手机的返回键的按下事件-----------------------*/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			System.exit(0);
		}
		return super.onKeyDown(keyCode, event);
	}
}