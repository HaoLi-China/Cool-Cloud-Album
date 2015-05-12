package android.app.album;

import android.app.Activity;
import android.app.fileManage.FileWriting;
import android.app.myCamera.R;
import android.app.util.CropCanvas;
import android.app.util.MyButton;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

/*
 * 
 * ��������ͼƬ�Ĳü�
 * 
 * */

public class PhotoTailorView extends Activity {
	private CropCanvas canvas = null;

	private String imageName;
	private String albumName;

	private Button save_button;
	private Button quit_button;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ȫ��
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ���ر�����
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tailor_view);

		Bundle bunde = this.getIntent().getExtras();
		imageName = bunde.getString("imageName");
		albumName = bunde.getString("albumName");

		this.init();
	}

	/*----------------��ʼ���ؼ�--------------------*/
	private void init() {

		// ��ťͼƬ����
		Integer[] save = { R.drawable.save1, R.drawable.save2, R.drawable.save2 };
		Integer[] quit = { R.drawable.quit1, R.drawable.quit2, R.drawable.quit2 };

		// ��ʼ��Button
		save_button = (Button) findViewById(R.id.save_button);
		MyButton save_myButton = new MyButton(this);
		save_button.setBackgroundDrawable(save_myButton.setbg(save));
		quit_button = (Button) findViewById(R.id.quit_button);
		MyButton quit_myButton = new MyButton(this);
		quit_button.setBackgroundDrawable(quit_myButton.setbg(quit));

		canvas = (CropCanvas) findViewById(R.id.myCanvas);
		Bitmap bitmap = BitmapFactory.decodeFile(imageName);
		canvas.setBitmap(bitmap);
	}

	/*----------------������水ť�󱣴�ͼƬ---------------------*/
	public void confirmFunction(View view) {
		Bitmap bit = canvas.getSubsetBitmap();
		canvas.setBitmap(bit);
		FileWriting fu = new FileWriting();
		fu.write2SDFromInput(albumName + "/", bit);
		Toast.makeText(
				PhotoTailorView.this,
				"��Ƭ�洢·����" + "/sdcard/myCamera/" + albumName + "/"
						+ System.currentTimeMillis() + ".jpg",
				Toast.LENGTH_LONG).show();
	}

	/*----------------------ȡ����ť--------------------------*/
	public void exitFunction(View view) {
		this.finish();// ����
	}
}