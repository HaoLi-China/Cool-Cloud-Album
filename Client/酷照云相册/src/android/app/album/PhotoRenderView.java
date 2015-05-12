package android.app.album;

import android.app.Activity;
import android.app.fileManage.FileWriting;
import android.app.myCamera.R;
import android.app.util.MyButton;
import android.app.util.MyImage;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

/*
 * 
 * ��Ƭ��Ⱦ���ɵ�����Ƭ��RGBAֵ��Ȼ�󱣴���Ƭ
 * 
 * */

public class PhotoRenderView extends Activity implements
		SeekBar.OnSeekBarChangeListener {
	// seekbar����
	private double progressRate1 = 50;
	private double progressRate2 = 50;
	private double progressRate3 = 50;
	private double progressRate4 = 50;

	private SeekBar R_seekbar;// ���ڵ���ͼƬRֵ
	private SeekBar G_seekbar;// ���ڵ���ͼƬGֵ
	private SeekBar B_seekbar;// ���ڵ���ͼƬBֵ
	private SeekBar A_seekbar;// ���ڵ���ͼƬ͸����ֵ

	private Button save_button;
	private Button quit_button;

	private Bitmap photo;

	private String imageName;// ��Ƭ��
	private String albumName;// �����

	private int width = 0;// �ֻ���Ļ���
	private int height = 0;

	// RGBA����
	private float[] carray = { 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0,
			0, 0, 1, 0 };

	private MyImage myImage;// ʵ����ɫ�任���ܵ���Ķ���

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ȫ��
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ���ر�����
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.render_view);

		// ��ʼ��seekBar
		R_seekbar = (SeekBar) findViewById(R.id.r_seekbar);
		R_seekbar.setOnSeekBarChangeListener(this);// ����¼�����
		R_seekbar.setProgress(50);
		G_seekbar = (SeekBar) findViewById(R.id.g_seekbar);
		G_seekbar.setOnSeekBarChangeListener(this);// ����¼�����
		G_seekbar.setProgress(50);
		B_seekbar = (SeekBar) findViewById(R.id.b_seekbar);
		B_seekbar.setOnSeekBarChangeListener(this);// ����¼�����
		B_seekbar.setProgress(50);
		A_seekbar = (SeekBar) findViewById(R.id.a_seekbar);
		A_seekbar.setOnSeekBarChangeListener(this);// ����¼�����
		A_seekbar.setProgress(50);

		// ��ťͼƬ����
		Integer[] save = { R.drawable.save1, R.drawable.save2, R.drawable.save2 };
		Integer[] quit = { R.drawable.quit1, R.drawable.quit2, R.drawable.quit2 };

		// ��ʼ��Button
		save_button = (Button) findViewById(R.id.save_img);
		MyButton save_myButton = new MyButton(this);
		save_button.setBackgroundDrawable(save_myButton.setbg(save));
		quit_button = (Button) findViewById(R.id.quit);
		MyButton quit_myButton = new MyButton(this);
		quit_button.setBackgroundDrawable(quit_myButton.setbg(quit));

		save_button.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				FileWriting fu = new FileWriting();

				Bitmap bit = myImage.getBitmap();
				fu.write2SDFromInput(albumName + "/", bit);
				Toast.makeText(
						PhotoRenderView.this,
						"��Ƭ�洢·����" + "/sdcard/myCamera/" + albumName + "/"
								+ System.currentTimeMillis() + ".jpg",
						Toast.LENGTH_LONG).show();
			}
		});

		quit_button.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				finish();// ���ٸ�Activity
			}
		});

		Bundle bunde = this.getIntent().getExtras();
		imageName = bunde.getString("imageName");
		albumName = bunde.getString("albumName");

		// ��Ƭ����
		photo = BitmapFactory.decodeFile(imageName);

		width = this.getWindowManager().getDefaultDisplay().getWidth();
		height = (int) ((double) photo.getHeight() / photo.getWidth() * width);
		Bitmap bitm = Bitmap.createScaledBitmap(photo, width, height, true);

		myImage = (MyImage) findViewById(R.id.myImage);
		myImage.setBitmap(bitm);
		myImage.setValues(carray);// ������ɫ����
	}

	/*----------------�϶���-----------------*/
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (seekBar == this.R_seekbar) {
			this.progressRate1 = (double) progress / 100;
			carray[0] = (float) (progressRate1 + 0.5);
		}
		if (seekBar == this.G_seekbar) {
			this.progressRate2 = (double) progress / 100;
			carray[6] = (float) (progressRate2 + 0.5);
		}
		if (seekBar == this.B_seekbar) {
			this.progressRate3 = (double) progress / 100;
			carray[12] = (float) (progressRate3 + 0.5);
		}
		if (seekBar == this.A_seekbar) {
			this.progressRate4 = (double) progress / 100;
			carray[18] = (float) (progressRate4 + 0.5);
		}
	}

	/*-------------------��ʼ�϶�-----------------*/
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	/*-------------------�����϶�------------------*/
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		myImage.setValues(carray);// ������ɫ����
		myImage.invalidate();
	}
}
