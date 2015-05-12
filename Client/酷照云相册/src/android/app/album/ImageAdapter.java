package android.app.album;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.Shader.TileMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/*
 * 
 * 图片适配器
 * 
 * */

public class ImageAdapter extends BaseAdapter {
	// private int mGalleryItemBackground;
	private Context mContext;
	private ImageView[] mImages;
	List<String> images;

	public ImageAdapter(Context c, List<String> images) {
		mContext = c;
		this.images = images;
		mImages = new ImageView[images.size()];
	}

	/*------------------- 创建倒影效果--------------------*/
	public boolean createReflectedImages() {
		// 倒影图和原图之间的距离
		final int reflectionGap = 4;
		int index = 0;
		for (String image : images) {
			// 返回原图解码之后的bitmap对象
			Bitmap originalImage = BitmapFactory.decodeFile(image);
			Matrix matrix0 = new Matrix();
			matrix0.postScale(0.2f, 0.2f);
			originalImage = Bitmap.createBitmap(originalImage, 0, 0,
					originalImage.getWidth(), originalImage.getHeight(),
					matrix0, true);

			int width = originalImage.getWidth();
			int height = originalImage.getHeight();
			// 创建矩阵对象
			Matrix matrix = new Matrix();

			// 指定一个角度以0,0为坐标进行旋转
			// matrix.setRotate(30);

			// 指定矩阵(x轴不变，y轴相反)
			matrix.preScale(1, -1);

			// 将矩阵应用到该原图之中，返回一个宽度不变，高度为原图1/2的倒影位图
			Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
					height / 2, width, height / 2, matrix, false);

			// 创建一个宽度不变，高度为原图+倒影图高度的位图
			Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
					(height + height / 2), Config.ARGB_8888);

			// 将上面创建的位图初始化到画布
			Canvas canvas = new Canvas(bitmapWithReflection);
			canvas.drawBitmap(originalImage, 0, 0, null);

			Paint deafaultPaint = new Paint();
			deafaultPaint.setAntiAlias(false);
			// canvas.drawRect(0, height, width, height +
			// reflectionGap,deafaultPaint);
			canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
			Paint paint = new Paint();
			paint.setAntiAlias(false);

			// 参数一:为渐变起初点坐标x位置，
			// 参数二:为y轴位置，
			// 参数三和四:分辨对应渐变终点，
			// 最后参数为平铺方式，
			// 这里设置为镜像Gradient是基于Shader类，所以我们通过Paint的setShader方法来设置这个渐变
			LinearGradient shader = new LinearGradient(0, originalImage
					.getHeight(), 0, bitmapWithReflection.getHeight()
					+ reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.MIRROR);
			// 设置阴影
			paint.setShader(shader);
			paint.setXfermode(new PorterDuffXfermode(
					android.graphics.PorterDuff.Mode.DST_IN));
			// 用已经定义好的画笔构建一个矩形阴影渐变效果
			canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
					+ reflectionGap, paint);

			// 创建一个ImageView用来显示已经画好的bitmapWithReflection
			ImageView imageView = new ImageView(mContext);
			imageView.setImageBitmap(bitmapWithReflection);
			// 设置imageView大小 ，也就是最终显示的图片大小
			imageView.setLayoutParams(new GalleryFlow.LayoutParams(250, 250));
			// imageView.setScaleType(ScaleType.MATRIX);
			mImages[index++] = imageView;
		}
		return true;
	}

	@SuppressWarnings("unused")
	private Resources getResources() {
		return null;
	}

	public int getCount() {
		return images.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		return mImages[position];
	}

	public float getScale(boolean focused, int offset) {
		return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
	}
}