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
 * 相片渲染，可调节相片的RGBA值，然后保存照片
 * 
 * */

public class PhotoRenderView extends Activity implements
		SeekBar.OnSeekBarChangeListener {
	// seekbar进度
	private double progressRate1 = 50;
	private double progressRate2 = 50;
	private double progressRate3 = 50;
	private double progressRate4 = 50;

	private SeekBar R_seekbar;// 用于调节图片R值
	private SeekBar G_seekbar;// 用于调节图片G值
	private SeekBar B_seekbar;// 用于调节图片B值
	private SeekBar A_seekbar;// 用于调节图片透明度值

	private Button save_button;
	private Button quit_button;

	private Bitmap photo;

	private String imageName;// 相片名
	private String albumName;// 相册名

	private int width = 0;// 手机屏幕宽高
	private int height = 0;

	// RGBA矩阵
	private float[] carray = { 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0,
			0, 0, 1, 0 };

	private MyImage myImage;// 实验颜色变换功能的类的对象

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 全屏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 隐藏标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.render_view);

		// 初始化seekBar
		R_seekbar = (SeekBar) findViewById(R.id.r_seekbar);
		R_seekbar.setOnSeekBarChangeListener(this);// 添加事件监听
		R_seekbar.setProgress(50);
		G_seekbar = (SeekBar) findViewById(R.id.g_seekbar);
		G_seekbar.setOnSeekBarChangeListener(this);// 添加事件监听
		G_seekbar.setProgress(50);
		B_seekbar = (SeekBar) findViewById(R.id.b_seekbar);
		B_seekbar.setOnSeekBarChangeListener(this);// 添加事件监听
		B_seekbar.setProgress(50);
		A_seekbar = (SeekBar) findViewById(R.id.a_seekbar);
		A_seekbar.setOnSeekBarChangeListener(this);// 添加事件监听
		A_seekbar.setProgress(50);

		// 按钮图片数组
		Integer[] save = { R.drawable.save1, R.drawable.save2, R.drawable.save2 };
		Integer[] quit = { R.drawable.quit1, R.drawable.quit2, R.drawable.quit2 };

		// 初始化Button
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
						"照片存储路径：" + "/sdcard/myCamera/" + albumName + "/"
								+ System.currentTimeMillis() + ".jpg",
						Toast.LENGTH_LONG).show();
			}
		});

		quit_button.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				finish();// 销毁该Activity
			}
		});

		Bundle bunde = this.getIntent().getExtras();
		imageName = bunde.getString("imageName");
		albumName = bunde.getString("albumName");

		// 照片解码
		photo = BitmapFactory.decodeFile(imageName);

		width = this.getWindowManager().getDefaultDisplay().getWidth();
		height = (int) ((double) photo.getHeight() / photo.getWidth() * width);
		Bitmap bitm = Bitmap.createScaledBitmap(photo, width, height, true);

		myImage = (MyImage) findViewById(R.id.myImage);
		myImage.setBitmap(bitm);
		myImage.setValues(carray);// 设置颜色矩阵
	}

	/*----------------拖动中-----------------*/
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

	/*-------------------开始拖动-----------------*/
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	/*-------------------结束拖动------------------*/
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		myImage.setValues(carray);// 设置颜色矩阵
		myImage.invalidate();
	}
}
