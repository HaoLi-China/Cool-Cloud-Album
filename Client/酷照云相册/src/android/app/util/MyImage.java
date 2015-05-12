package android.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.util.AttributeSet;
import android.widget.ImageView;

/*
 * 
 * ��������ʵ��ͼƬ����Ⱦ
 * 
 * */

public class MyImage extends ImageView {
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Bitmap mBitmap;
	private float[] array = new float[20];// ��ɫ����

	private Bitmap bitmap;

	public MyImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		invalidate();
	}

	/*----------------------����λͼ--------------------*/
	public void setBitmap(Bitmap bitmap) {
		this.mBitmap = bitmap.copy(Config.ARGB_8888, true);
		this.setImageBitmap(mBitmap);
	}

	/*----------------------���λͼ--------------------*/
	public Bitmap getBitmap() {
		this.setDrawingCacheEnabled(true);
		bitmap = this.getDrawingCache(true);
		// bitmap=this.getDrawingCache();
		return bitmap;
	}

	/*----------------------������ɫ����ֵ--------------------*/
	public void setValues(float[] a) {
		for (int i = 0; i < 20; i++) {
			array = a;
		}
	}

	@Override
	public void onDraw(Canvas canvas) {
		Paint paint = mPaint;

		paint.setColorFilter(null);
		canvas.drawBitmap(mBitmap, 0, 0, paint);

		ColorMatrix cm = new ColorMatrix();
		// ������ɫ����
		cm.set(array);
		// ��ɫ�˾�������ɫ����Ӧ����ͼƬ
		paint.setColorFilter(new ColorMatrixColorFilter(cm));
		// ��ͼ
		canvas.drawBitmap(mBitmap, 0, 0, paint);
	}
}
