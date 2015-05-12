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
 * 该类用于实现图片的渲染
 * 
 * */

public class MyImage extends ImageView {
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Bitmap mBitmap;
	private float[] array = new float[20];// 颜色矩阵

	private Bitmap bitmap;

	public MyImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		invalidate();
	}

	/*----------------------设置位图--------------------*/
	public void setBitmap(Bitmap bitmap) {
		this.mBitmap = bitmap.copy(Config.ARGB_8888, true);
		this.setImageBitmap(mBitmap);
	}

	/*----------------------获得位图--------------------*/
	public Bitmap getBitmap() {
		this.setDrawingCacheEnabled(true);
		bitmap = this.getDrawingCache(true);
		// bitmap=this.getDrawingCache();
		return bitmap;
	}

	/*----------------------设置颜色矩阵值--------------------*/
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
		// 设置颜色矩阵
		cm.set(array);
		// 颜色滤镜，将颜色矩阵应用于图片
		paint.setColorFilter(new ColorMatrixColorFilter(cm));
		// 绘图
		canvas.drawBitmap(mBitmap, 0, 0, paint);
	}
}
