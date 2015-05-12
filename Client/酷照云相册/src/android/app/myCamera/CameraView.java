package android.app.myCamera;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.album.AlbumDirectory;
import android.app.fileManage.FileWriting;
import android.app.setting.SetView;
import android.app.util.AutoShootListeningThread;
import android.app.util.MyButton;
import android.app.util.OnePhoneStateListener;
import android.app.util.RWSharedperferences;
import android.app.webAlbum.Login;
import android.app.webAlbum.LoginStatus;
import android.app.webAlbum.WebFileList;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/*
 * 
 * 相机界面
 * 
 * */

public class CameraView extends Activity implements SurfaceHolder.Callback,
		SeekBar.OnSeekBarChangeListener, AutoFocusCallback, OnClickListener {

	private AutoShootListeningThread autoShootListeningThread;// 自动拍照监听线程

	private Camera mCamera;// 相机

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;

	private SeekBar seekBar1;// 用于调焦距
	private SeekBar seekBar2;// 用于设置曝光数

	private ImageButton flashButton;// 闪关灯按钮
	private ImageButton cameraChangeButton;// 摄像头转换按钮
	private ImageButton albumButton;// 相册按钮
	private ImageButton setButton;// 设置按钮
	private ImageButton shootButton;// 拍照按钮
	private ImageButton focusButton;// 对焦按钮
	private ImageButton frameButton;// 相框按钮
	private ImageButton webAlbumButton;// 网络相册按钮
	private ImageButton remoteControlButton;// 远程控制按钮

	private ImageView focusFrame;// 放置对焦图片
	private ImageView frameView;// 放置相框

	private Context context;

	private Gallery frameGallery;// 选择相框的Gallery

	private AlertDialog dialog;// 对话框

	private MediaPlayer shutterSound;// 快门音

	private boolean mPreviewRunning = true;
	private boolean isShoot = false;// 是否拍照
	private boolean isPhone = false;// 是否有电话呼入

	private int autoShootState = 0;// 自动拍照状态
	private int remoteControlState = 0;// 遥控状态
	private int delyTime = 0;// 延迟拍照时间
	private int strRingerMode;// 声音模式

	private int window_width;// 屏幕宽度
	private int window_height;// 屏幕高度

	private int pic_width;// 照片的宽度
	private int pic_height;// 照片的高度

	private int sleep = 300;// 动画每帧睡眠时间

	private int frame_id;// 相框图片id

	private double progressRate1 = 0;
	private double progressRate2 = 0;

	private byte[] data;

	private String albumName = "其它";
	private String album[] = { "唯美", "温馨", "欢乐", "浪漫", "城市", "自然", "时尚", "其它" };
	private String autoSaveState;// 是否自动保存（1是，0否）
	private String autoFocusState;// 是否自动对焦（1是，0否）
	private String ImageQualityState;// 照片质量标志
	private String ImageResolutionState;// 照片分辨率标志
	private String delyState;// 拍照延迟状态
	private String shutterSoundState;// 拍照音状态
	private String sceneState;// 场景模式状态
	private String effectState;// 拍照效果状态
	private String whiteBalanceState;// 白平衡状态
	private String flashModeState;// 闪光灯模式
	private String cameraModeState;// 摄像头模式
	private String frameId;// 相框选择

	private RWSharedperferences rws;// 读写配置文件对象

	// private String time;
	// private SimpleDateFormat format;
	// private ImageView littleView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 无标题
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏
		setContentView(R.layout.camera);

		context = this;
		rws = new RWSharedperferences();// 初始化读写配置文件对象

		/* -----------------------判断存储卡是否存在--------------------------- */
		if (!checkSDCard()) {
			/* 提醒User未安装存储卡 */
			Toast.makeText(CameraView.this,
					getResources().getText(R.string.sdcarderror).toString(),
					Toast.LENGTH_LONG).show();
		}

		/* ------------------------出始化变量---------------------------- */
		window_height = getWindowManager().getDefaultDisplay().getHeight();// 屏幕的宽和高
		window_width = getWindowManager().getDefaultDisplay().getWidth();

		Thread thread0 = new Thread(new initControl());
		thread0.start();// 子线程设置相机参数

		Thread thread1 = new Thread(new ReadPara());
		thread1.start();// 子线程设置相机参数

		createPhoneListener();// 电话监测
	}

	/* ------------------------设置相机参数---------------------------- */
	public class ReadPara implements Runnable {
		public void run() {
			autoSaveState = rws.read(context, "autoSave");
			autoFocusState = rws.read(context, "autoFocus");
			ImageQualityState = rws.read(context, "ImageQuality");
			ImageResolutionState = rws.read(context, "ImageResolution");
			delyState = rws.read(context, "dely");
			shutterSoundState = rws.read(context, "shutterSound");
			sceneState = rws.read(context, "scene");
			effectState = rws.read(context, "effect");
			whiteBalanceState = rws.read(context, "whiteBalance");
			flashModeState = rws.read(context, "flashMode");
			cameraModeState = rws.read(context, "cameraMode");
			frameId = rws.read(context, "frameId");

		}
	}

	/*-------------------初始化控件----------------*/
	public class initControl implements Runnable {
		public void run() {
			frameView = (ImageView) findViewById(R.id.frame_view);

			// seekBar
			seekBar1 = (SeekBar) findViewById(R.id.seekbar1);
			seekBar2 = (SeekBar) findViewById(R.id.seekbar2);

			// 设置焦距框图片
			focusFrame = (ImageView) findViewById(R.id.focus);
			mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);// 获得控件
			mSurfaceHolder = mSurfaceView.getHolder();// 获得句柄
			mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// 设置类型

			flashButton = (ImageButton) findViewById(R.id.flash);
			cameraChangeButton = (ImageButton) findViewById(R.id.camera_change);
			albumButton = (ImageButton) findViewById(R.id.album_button);
			setButton = (ImageButton) findViewById(R.id.set_button);
			shootButton = (ImageButton) findViewById(R.id.shoot_button);
			focusButton = (ImageButton) findViewById(R.id.focus_button);
			frameButton = (ImageButton) findViewById(R.id.frame_button);
			webAlbumButton = (ImageButton) findViewById(R.id.web_album_button);
			remoteControlButton = (ImageButton) findViewById(R.id.remote_control);

			frameGallery = (Gallery) findViewById(R.id.frame_gallery);
			frameGallery.setVisibility(8);

			controlListening();
		}
	}

	/*-------------------对控件设置监听----------------*/
	public void controlListening() {
		seekBar1.setOnSeekBarChangeListener(this);// 添加事件监听
		seekBar2.setOnSeekBarChangeListener(this);// 添加事件监听

		mSurfaceHolder.addCallback(this);// 添加回调
		mSurfaceView.setOnClickListener(this); // 设置监听

		flashButton.setOnClickListener(this);
		cameraChangeButton.setOnClickListener(this);
		albumButton.setOnClickListener(this);
		setButton.setOnClickListener(this);
		shootButton.setOnClickListener(this);
		focusButton.setOnClickListener(this);
		frameButton.setOnClickListener(this);
		webAlbumButton.setOnClickListener(this);
		remoteControlButton.setOnClickListener(this);

		frameGallery.setAdapter(new ImageAdapter(this)); // 新增一ImageAdapter并设定给Gallery
		frameGallery.setOnItemClickListener(new OnItemClickListener() { // 设定一个itemclickListener并Toast被点选图片的位置
					@SuppressWarnings("unchecked")
					public void onItemClick(AdapterView parent, View v,
							int position, long id) {
						frameId = position + "";
						rws.write(context, "frameId", position + "");
						setFrame();// 设置相框
						frameGallery.setVisibility(8);
					}
				});
	}

	/*-------------------获得自动拍照状态----------------*/
	public int getAutoShootState() {
		return autoShootState;
	}

	/*-------------------设置自动拍照状态----------------*/
	public void setAutoShootState(int state) {
		autoShootState = state;
	}

	/*-------------------获得电话状态----------------*/
	public boolean getPhoneState() {
		return isPhone;
	}

	/*-------------------设置电话状态----------------*/
	public void setPhoneState(boolean bool) {
		isPhone = bool;
	}

	/*-------------------设置相框----------------*/
	public void setFrame() {
		System.out.println(3);
		switch (Integer.parseInt(frameId)) {
		case 0:
			frame_id = 0;
			break;
		case 1:
			frame_id = R.drawable.frame1;
			break;
		case 2:
			frame_id = R.drawable.frame2;
			break;
		case 3:
			frame_id = R.drawable.frame3;
			break;
		case 4:
			frame_id = R.drawable.frame4;
			break;
		case 5:
			frame_id = R.drawable.frame5;
			break;
		case 6:
			frame_id = R.drawable.frame6;
			break;
		case 7:
			frame_id = R.drawable.frame7;
			break;
		case 8:
			frame_id = R.drawable.frame8;
			break;
		case 9:
			frame_id = R.drawable.frame9;
			break;
		case 10:
			frame_id = R.drawable.frame10;
			break;
		case 11:
			frame_id = R.drawable.frame11;
			break;
		case 12:
			frame_id = R.drawable.frame12;
			break;

		}
		frameView.setBackgroundResource(frame_id);
	}

	/*-------------------------设置闪光灯---------------------------*/
	public void setFlash() {
		Camera.Parameters params1 = mCamera.getParameters();
		if (flashModeState.equals("0")) {
			flashModeState = "1";
			rws.write(this, "flashMode", flashModeState);
			params1.setFlashMode("off");// 关闭闪光灯模式
			flashButton.setBackgroundResource(R.drawable.flash_off);
		} else if (flashModeState.equals("1")) {
			flashModeState = "2";
			rws.write(this, "flashMode", flashModeState);
			params1.setFlashMode("auto");// 闪光灯自动模式
			flashButton.setBackgroundResource(R.drawable.auto_flash);
		}
		/*
		 * else if (FlashState == 2) { FlashState = 3;
		 * params1.setFlashMode("red_eye");// 闪光灯消除红眼模式
		 * flash.setBackgroundResource(R.drawable.red_eye); }
		 */
		else if (flashModeState.equals("2")) {
			flashModeState = "3";
			rws.write(this, "flashMode", flashModeState);
			params1.setFlashMode("torch");// 闪光灯长亮模式
			flashButton.setBackgroundResource(R.drawable.flash_torch);
		} else {
			flashModeState = "0";
			rws.write(this, "flashMode", flashModeState);
			params1.setFlashMode("on");// 开启闪光灯模式
			flashButton.setBackgroundResource(R.drawable.flash_on);
		}
		mCamera.setParameters(params1);
	}

	/*----------------------------- 拍照----------------------------*/
	public void shoot() {
		isShoot = true;
		setCamera();
		setToSilent();// 调至静音模式
		if (autoFocusState.equals("0")) {
			try {
				Thread.sleep(delyTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mCamera.autoFocus(this);
		} else {
			try {
				Thread.sleep(delyTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mCamera.takePicture(shutterCall, null, jpegCallback);
		}
	}

	/*------------------------遥控自动拍照----------------------*/
	public void autoShoot() {
		isShoot = true;
		setToSilent();// 调至静音模式
		try {
			Thread.sleep(delyTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mCamera.takePicture(shutterCall, null, jpegCallback);
		autoShootState = 0;
	}

	/*-------------------------------对焦-------------------------*/
	public void focus() {
		isShoot = false;
		mCamera.autoFocus(this);
	}

	@Override
	/*---------------------设置参数并开始预览-----------------------*/
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// TODO Auto-generated method stub
		if (mPreviewRunning) {
			mCamera.stopPreview();
		}
		// params.set("rotation", 90);
		setCamera();// 设置相机参数
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mCamera.startPreview();
		mPreviewRunning = true;
	}

	@Override
	/*-----------------------------开启相机------------------------------*/
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mCamera = Camera.open();
	}

	@Override
	/*-------------------关闭预览并释放资源----------------------*/
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mCamera.stopPreview();
		mPreviewRunning = false;
		mCamera.release();
	}

	/*--------------------------回调函数-----------------------*/
	private ShutterCallback shutterCall = new ShutterCallback() {// 快门音
		@Override
		public void onShutter() {
			// TODO Auto-generated method stub
			if (shutterSoundState.equals("1")) {
				shutterSound = MediaPlayer
						.create(context, R.raw.shutter_sound1);
				shutterSound.setLooping(false);
				shutterSound.start();
			} else if (shutterSoundState.equals("2")) {
				shutterSound = MediaPlayer
						.create(context, R.raw.shutter_sound2);
				shutterSound.setLooping(false);
				shutterSound.start();
			} else if (shutterSoundState.equals("3")) {
				shutterSound = MediaPlayer
						.create(context, R.raw.shutter_sound3);
				shutterSound.setLooping(false);
				shutterSound.start();
			} else if (shutterSoundState.equals("4")) {
				shutterSound = MediaPlayer
						.create(context, R.raw.shutter_sound4);
				shutterSound.setLooping(false);
				shutterSound.start();
			}
			setPreMode();// 调至原来模式
		}
	};

	private PictureCallback jpegCallback = new PictureCallback() {// JPEG
		public void onPictureTaken(byte[] _data, Camera _camera) {// onPictureTaken传入的第一个参数即为相片的byte
			// 返回键监听
			OnKeyListener keyListener = new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						dialog.cancel();
						mCamera.startPreview();
						return true;
					}
					return false;
				}
			};
			data = _data;
			if (autoSaveState.equals("1")) {
				LayoutInflater inflater = getLayoutInflater();
				View layout = inflater.inflate(R.layout.album_choice,
						(ViewGroup) findViewById(R.id.directoryChoice));
				dialog = new AlertDialog.Builder(CameraView.this).create();
				dialog.setOnKeyListener(keyListener);
				dialog.show();
				dialog.getWindow().setContentView(layout);

				// 按钮图片数组
				Integer[] save = { R.drawable.yes1, R.drawable.yes2,
						R.drawable.yes2 };
				Integer[] cancel = { R.drawable.cancel1, R.drawable.cancel2,
						R.drawable.cancel2 };

				Button saveButton = (Button) dialog.findViewById(R.id.save);
				MyButton mySave = new MyButton(context);
				saveButton.setBackgroundDrawable(mySave.setbg(save));

				Button cancelButton = (Button) dialog.findViewById(R.id.cancel);
				MyButton myCancel = new MyButton(context);
				cancelButton.setBackgroundDrawable(myCancel.setbg(cancel));

				saveButton.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						savepicture(data, albumName);
						dialog.cancel();
						if (flashModeState.equals("3")) {
							// 照片储存后重置闪光灯为常亮
							Camera.Parameters params = mCamera.getParameters();
							params.setFlashMode("torch");// 闪光灯常亮模式
							mCamera.setParameters(params);
						}
					}
				});
				cancelButton.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						dialog.cancel();
						mCamera.startPreview();
						if (flashModeState.equals("3")) {
							// 照片储存后重置闪光灯为常亮
							Camera.Parameters params = mCamera.getParameters();
							params.setFlashMode("torch");// 闪光灯常亮模式
							mCamera.setParameters(params);
						}
					}
				});

				List<String> albumList = new ArrayList<String>();
				for (int i = 0; i < album.length; i++) {
					albumList.add(album[i]);
				}
				Spinner mySpinner = (Spinner) dialog
						.findViewById(R.id.mySpinner);
				// 为下拉列表定义一个适配器，用到前面定义的list。
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						CameraView.this, android.R.layout.simple_spinner_item,
						albumList);
				// 为适配器设置下拉列表下拉时的菜单样式。
				adapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// 将适配器添加到下拉列表上
				mySpinner.setAdapter(adapter);
				// 为下拉列表设置各种事件的响应，这个事响应菜单被选中
				mySpinner
						.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
							@SuppressWarnings("unchecked")
							public void onItemSelected(AdapterView arg0,
									View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								/* 将mySpinner 显示 */
								albumName = album[arg2];
								arg0.setVisibility(View.VISIBLE);
							}

							@SuppressWarnings("unchecked")
							public void onNothingSelected(AdapterView arg0) {
								// TODO Auto-generated method stub
								arg0.setVisibility(View.VISIBLE);
							}
						});
				// 下拉菜单弹出的内容选项触屏事件处理
				mySpinner.setOnTouchListener(new Spinner.OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						// 将mySpinner 隐藏，不隐藏也可以，看自己爱好
						// v.setVisibility(View.INVISIBLE);
						return false;
					}
				});
				// 下拉菜单弹出的内容选项焦点改变事件处理
				mySpinner
						.setOnFocusChangeListener(new Spinner.OnFocusChangeListener() {
							public void onFocusChange(View v, boolean hasFocus) {
								// TODO Auto-generated method stub
								v.setVisibility(View.VISIBLE);
							}
						});
			} else {
				savepicture(data, album[album.length - 1]);
			}
		}
	};

	/*-------------------------保存照片---------------------*/
	public void savepicture(byte[] _data, String anbumName) {
		Bitmap bm = BitmapFactory.decodeByteArray(_data, 0, _data.length);
		if (frame_id != 0) {
			Bitmap pic = Bitmap.createBitmap(pic_width, pic_height,
					Bitmap.Config.ARGB_8888);
			Bitmap frame = BitmapFactory.decodeResource(getResources(),
					frame_id);
			Drawable[] array = new Drawable[2];
			array[0] = new BitmapDrawable(bm);
			array[1] = new BitmapDrawable(frame);
			LayerDrawable la = new LayerDrawable(array);
			la.setLayerInset(0, 0, 0, pic_width, pic_height);
			la.setLayerInset(1, 0, 0, pic_width, pic_height);
			pic = drawableToBitmap(la);

			FileWriting fu = new FileWriting();
			// 设置时间格式
			// format=new SimpleDateFormat( "yyyy年MM月dd日");
			// time=format.format((new Date()));
			fu.write2SDFromInput(anbumName + "/", pic);
		} else {
			FileWriting fu = new FileWriting();
			fu.write2SDFromInput(anbumName + "/", bm);
		}
		mCamera.startPreview();
		Toast.makeText(
				CameraView.this,
				"照片存储路径：" + "/sdcard/myCamera/" + anbumName + "/"
						+ System.currentTimeMillis() + ".jpg",
				Toast.LENGTH_LONG).show();
	}

	/*------------------------按钮监听-------------------------*/
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.surface_camera:
			// shoot();//这里注释掉了，可以用来做扩展
			break;

		case R.id.flash:// 设置闪光灯
			setFlash();
			break;

		case R.id.camera_change:// 摄像头转换
			if (cameraModeState.equals("0")) {
				cameraModeState = "1";
				rws.write(this, "cameraMode", cameraModeState);
				cameraChangeButton
						.setBackgroundResource(R.drawable.front_camera);
			} else {
				cameraModeState = "0";
				rws.write(this, "cameraMode", cameraModeState);
				cameraChangeButton
						.setBackgroundResource(R.drawable.back_camera);
			}
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.set("camera-id", "secondary");// sumsung 2.3以前的手机　　前置
			// parameters.set("camera-id",1);//sumsung 2.3以前的手机　　后置
			// parameters.set("video_input", "secondary");// htc2.3以前的手机　　前置
			// parameters.set("video_input","main");//htc2.3以前的手机 后置
			mCamera.setParameters(parameters);
			break;

		case R.id.album_button:// 转到本地相册
			Intent intent1 = new Intent();
			intent1.setClass(CameraView.this, AlbumDirectory.class);
			startActivity(intent1);
			finish();
			break;

		case R.id.set_button:// 设置
			Camera.Parameters params = mCamera.getParameters();
			List<String> effectList = params.getSupportedColorEffects();
			List<String> sceneList = params.getSupportedSceneModes();
			List<String> whiteBalanceList = params.getSupportedWhiteBalance();
			// List<Camera.Size>
			// pictureSizeList=params.getSupportedPictureSizes();

			Intent intent2 = new Intent();
			intent2.setClass(CameraView.this, SetView.class);
			// new一个Bundle对象，并将要传递的数据传入
			Bundle bundle = new Bundle();

			// effectList
			if (effectList.contains("none")) {
				bundle.putBoolean("none_effect", true);
			} else {
				bundle.putBoolean("none_effect", false);
			}
			if (effectList.contains("mono")) {
				bundle.putBoolean("mono_effect", true);
			} else {
				bundle.putBoolean("mono_effect", false);
			}
			if (effectList.contains("negative")) {
				bundle.putBoolean("negative_effect", true);
			} else {
				bundle.putBoolean("negative_effect", false);
			}
			if (effectList.contains("solarize")) {
				bundle.putBoolean("solarize_effect", true);
			} else {
				bundle.putBoolean("solarize_effect", false);
			}
			if (effectList.contains("sepia")) {
				bundle.putBoolean("sepia_effect", true);
			} else {
				bundle.putBoolean("sepia_effect", false);
			}
			if (effectList.contains("posterize")) {
				bundle.putBoolean("posterize_effect", true);
			} else {
				bundle.putBoolean("posterize_effect", false);
			}
			if (effectList.contains("whiteboard")) {
				bundle.putBoolean("whiteboard_effect", true);
			} else {
				bundle.putBoolean("whiteboard_effect", false);
			}
			if (effectList.contains("blackboard")) {
				bundle.putBoolean("blackboard_effect", true);
			} else {
				bundle.putBoolean("blackboard_effect", false);
			}
			if (effectList.contains("aqua")) {
				bundle.putBoolean("aqua_effect", true);
			} else {
				bundle.putBoolean("aqua_effect", false);
			}

			// sceneList
			if (sceneList.contains("auto")) {
				bundle.putBoolean("auto_scene", true);
			} else {
				bundle.putBoolean("auto_scene", false);
			}
			if (sceneList.contains("beach")) {
				bundle.putBoolean("beach_scene", true);
			} else {
				bundle.putBoolean("beach_scene", false);
			}
			if (sceneList.contains("candlelight")) {
				bundle.putBoolean("candlelight_scene", true);
			} else {
				bundle.putBoolean("candlelight_scene", false);
			}
			if (sceneList.contains("fireworks")) {
				bundle.putBoolean("fireworks_scene", true);
			} else {
				bundle.putBoolean("fireworks_scene", false);
			}
			if (sceneList.contains("landscape")) {
				bundle.putBoolean("landscape_scene", true);
			} else {
				bundle.putBoolean("landscape_scene", false);
			}
			if (sceneList.contains("night")) {
				bundle.putBoolean("night_scene", true);
			} else {
				bundle.putBoolean("night_scene", false);
			}
			if (sceneList.contains("night-portrai")) {
				bundle.putBoolean("night-portrai_scene", true);
			} else {
				bundle.putBoolean("night-portrai_scene", false);
			}
			if (sceneList.contains("party")) {
				bundle.putBoolean("party_scene", true);
			} else {
				bundle.putBoolean("party_scene", false);
			}
			if (sceneList.contains("portrait")) {
				bundle.putBoolean("portrait_scene", true);
			} else {
				bundle.putBoolean("portrait_scene", false);
			}
			if (sceneList.contains("snow")) {
				bundle.putBoolean("snow_scene", true);
			} else {
				bundle.putBoolean("snow_scene", false);
			}
			if (sceneList.contains("sports")) {
				bundle.putBoolean("nsportsone_scene", true);
			} else {
				bundle.putBoolean("nsportsone_scene", false);
			}
			if (sceneList.contains("steadyphoto")) {
				bundle.putBoolean("steadyphoto_scene", true);
			} else {
				bundle.putBoolean("steadyphoto_scene", false);
			}
			if (sceneList.contains("sunset")) {
				bundle.putBoolean("sunset_scene", true);
			} else {
				bundle.putBoolean("sunset_scene", false);
			}
			if (sceneList.contains("theatre")) {
				bundle.putBoolean("theatre_scene", true);
			} else {
				bundle.putBoolean("theatre_scene", false);
			}

			// whiteBalanceList
			if (whiteBalanceList.contains("auto")) {
				bundle.putBoolean("auto_white", true);
			} else {
				bundle.putBoolean("auto_white", false);
			}
			if (whiteBalanceList.contains("cloudy-daylight")) {
				bundle.putBoolean("cloudy-daylight_white", true);
			} else {
				bundle.putBoolean("cloudy-daylight_white", false);
			}
			if (whiteBalanceList.contains("daylight")) {
				bundle.putBoolean("daylight_white", true);
			} else {
				bundle.putBoolean("daylight_white", false);
			}
			if (whiteBalanceList.contains("fluorescent")) {
				bundle.putBoolean("fluorescent_white", true);
			} else {
				bundle.putBoolean("fluorescent_white", false);
			}
			if (whiteBalanceList.contains("incandescent")) {
				bundle.putBoolean("incandescent_white", true);
			} else {
				bundle.putBoolean("incandescent_white", false);
			}
			if (whiteBalanceList.contains("shade")) {
				bundle.putBoolean("shade_white", true);
			} else {
				bundle.putBoolean("shade_white", false);
			}
			if (whiteBalanceList.contains("twilight")) {
				bundle.putBoolean("twilight_white", true);
			} else {
				bundle.putBoolean("twilight_white", false);
			}
			if (whiteBalanceList.contains("warm-fluorescent")) {
				bundle.putBoolean("nwarm-fluorescentone_white", true);
			} else {
				bundle.putBoolean("nwarm-fluorescentone_white", false);
			}

			intent2.putExtras(bundle);
			startActivity(intent2);
			break;

		case R.id.shoot_button:// 拍照
			shoot();
			break;

		case R.id.focus_button:// 对焦
			focus();
			break;

		case R.id.frame_button:// 相框选择
			frameGallery.setVisibility(0);
			break;

		case R.id.web_album_button:// 转到网络相册
			LoginStatus logSta = new LoginStatus(context);
			if (logSta.getValue_status().equals("0")) {
				Intent intent3 = new Intent();
				intent3.setClass(CameraView.this, Login.class);
				startActivity(intent3);
			} else {
				Intent intent3 = new Intent();
				intent3.setClass(CameraView.this, WebFileList.class);
				startActivity(intent3);
			}
			finish();
			break;

		case R.id.remote_control:// 远程控制开启/关闭
			if (remoteControlState == 0) {
				remoteControlState = 1;
				autoShootListeningThread = new AutoShootListeningThread(this);
				autoShootListeningThread.start();
				remoteControlButton.setBackgroundResource(R.drawable.remote);
			} else if (remoteControlState == 1) {
				remoteControlState = 0;
				autoShootListeningThread.setFlag(false);
				remoteControlButton.setBackgroundResource(R.drawable.nremote);
			}
			break;
		}
	}

	@Override
	/*-------------------------自动对焦-----------------------*/
	public void onAutoFocus(boolean success, Camera camera) {
		// TODO Auto-generated method stub
		if (success) {
			if (!isShoot) {
				// 逐帧动画AnimationDrawable对象
				AnimationDrawable focus_ani = new AnimationDrawable();
				for (int i = 0; i < 2; i++) {
					int id = getResources().getIdentifier("focus" + i,
							"drawable", "android.app.myCamera");
					focus_ani.addFrame(getResources().getDrawable(id), sleep);
				}
				// 设置手否重复播放，true为不重复
				focus_ani.setOneShot(true);
				MyAnimationDrawable mad = new MyAnimationDrawable(focus_ani) {
					@Override
					void onAnimationEnd() {
						// 实现这个方法，结束后会调用
						focusFrame.setBackgroundResource(0);
					}
				};
				focusFrame.setBackgroundDrawable(mad);
				mad.start();
			} else {
				mCamera.takePicture(shutterCall, null, jpegCallback);
			}
		}
	}

	@Override
	/*----------------seekbar拖动中-------------------*/
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		Camera.Parameters params = mCamera.getParameters();
		if (seekBar == this.seekBar1) {
			this.progressRate1 = (double) progress / 100;
			// 设置焦距
			if (params.isZoomSupported()) {
				params.setZoom((int) (params.getMaxZoom() * progressRate1));
			}
		} else if (seekBar == this.seekBar2) {
			this.progressRate2 = (double) progress / 100;
			// 设置曝光数
			params.setExposureCompensation((int) (params
					.getMaxExposureCompensation() * progressRate2));
		}
		mCamera.setParameters(params);
	}

	@Override
	/*----------------seekbar开始拖动-------------------*/
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	/*----------------seekbar结束拖动-------------------*/
	public void onStopTrackingTouch(SeekBar seekBar) {
		if (seekBar == this.seekBar1) {
			focus();// 对焦
		}
	}

	/* -----------------判断存储卡是否存在--------------- */
	private boolean checkSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/*-----------------------------设置相机参数-------------------------*/
	private void setCamera() {

		autoSaveState = rws.read(this, "autoSave");// 设置相机参数
		autoFocusState = rws.read(this, "autoFocus");
		ImageQualityState = rws.read(this, "ImageQuality");
		ImageResolutionState = rws.read(this, "ImageResolution");
		delyState = rws.read(this, "dely");
		shutterSoundState = rws.read(this, "shutterSound");
		sceneState = rws.read(this, "scene");
		effectState = rws.read(this, "effect");
		whiteBalanceState = rws.read(this, "whiteBalance");
		flashModeState = rws.read(this, "flashMode");
		cameraModeState = rws.read(this, "cameraMode");

		Camera.Parameters params = mCamera.getParameters();
		switch (Integer.parseInt(ImageQualityState)) {
		case 0:
			params.setJpegQuality(100);
			break;
		case 1:
			params.setJpegQuality(90);
			break;
		case 2:
			params.setJpegQuality(80);
			break;
		case 3:
			params.setJpegQuality(70);
			break;
		case 4:
			params.setJpegQuality(60);
			break;
		}

		switch (Integer.parseInt(ImageResolutionState)) {
		case 0:
			pic_width = 1600;
			pic_height = 1200;
			break;
		case 1:
			pic_width = 1584;
			pic_height = 1056;
			break;
		case 2:
			pic_width = 1280;
			pic_height = 960;
			break;
		case 3:
			pic_width = 1280;
			pic_height = 848;
			break;
		case 4:
			pic_width = 1280;
			pic_height = 768;
			break;
		case 5:
			pic_width = 1024;
			pic_height = 768;
			break;
		case 6:
			pic_width = 640;
			pic_height = 480;
			break;
		case 7:
			pic_width = 640;
			pic_height = 416;
			break;
		case 8:
			pic_width = 640;
			pic_height = 384;
			break;
		case 9:
			pic_width = 640;
			pic_height = 368;
			break;
		case 10:
			pic_width = 512;
			pic_height = 384;
			break;
		case 11:
			pic_width = 272;
			pic_height = 272;
			break;
		}

		switch (Integer.parseInt(delyState)) {
		case 0:
			delyTime = 0;
			break;
		case 1:
			delyTime = 5000;
			break;
		case 2:
			delyTime = 10000;
			break;
		case 3:
			delyTime = 15000;
			break;
		}

		switch (Integer.parseInt(sceneState)) {
		case 0:
			params.setSceneMode("auto");
			break;
		case 1:
			params.setSceneMode("beach");
			break;
		case 2:
			params.setSceneMode("candlelight");
			break;
		case 3:
			params.setSceneMode("fireworks");
			break;
		case 4:
			params.setSceneMode("landscape");
			break;
		case 5:
			params.setSceneMode("night");
			break;
		case 6:
			params.setSceneMode("night-portrai");
			break;
		case 7:
			params.setSceneMode("party");
			break;
		case 8:
			params.setSceneMode("portrait");
			break;
		case 9:
			params.setSceneMode("snow");
			break;
		case 10:
			params.setSceneMode("sports");
			break;
		case 11:
			params.setSceneMode("steadyphoto");
			break;
		case 12:
			params.setSceneMode("sunset");
			break;
		case 13:
			params.setSceneMode("theatre");
			break;
		}

		switch (Integer.parseInt(effectState)) {
		case 0:
			params.setColorEffect("none");
			break;
		case 1:
			params.setColorEffect("mono");
			break;
		case 2:
			params.setColorEffect("negative");
			break;
		case 3:
			params.setColorEffect("solarize");
			break;
		case 4:
			params.setColorEffect("sepia");
			break;
		case 5:
			params.setColorEffect("posterize");
			break;
		case 6:
			params.setColorEffect("whiteboard");
			break;
		case 7:
			params.setColorEffect("blackboard");
			break;
		case 8:
			params.setColorEffect("aqua");
			break;
		}

		switch (Integer.parseInt(whiteBalanceState)) {
		case 0:
			params.setWhiteBalance("auto");
			break;
		case 1:
			params.setWhiteBalance("cloudy-daylight");
			break;
		case 2:
			params.setWhiteBalance("daylight");
			break;
		case 3:
			params.setWhiteBalance("fluorescent");
			break;
		case 4:
			params.setWhiteBalance("incandescent");
			break;
		case 5:
			params.setWhiteBalance("shade");
			break;
		case 6:
			params.setWhiteBalance("twilight");
			break;
		case 7:
			params.setWhiteBalance("warm-fluorescent");
			break;
		}

		switch (Integer.parseInt(flashModeState)) {
		case 0:
			params.setFlashMode("on");// 开启闪光灯模式
			cameraChangeButton.setBackgroundResource(R.drawable.flash_on);
			break;
		case 1:
			params.setFlashMode("off");// 关闭闪光灯模式
			cameraChangeButton.setBackgroundResource(R.drawable.flash_off);
			break;
		case 2:
			params.setFlashMode("auto");// 闪光灯自动模式
			cameraChangeButton.setBackgroundResource(R.drawable.auto_flash);
			break;
		case 3:
			params.setFlashMode("torch");// 闪光灯长亮模式
			cameraChangeButton.setBackgroundResource(R.drawable.flash_torch);
			break;
		}

		switch (Integer.parseInt(cameraModeState)) {
		case 0:
			cameraChangeButton.setBackgroundResource(R.drawable.back_camera);
			break;
		case 1:
			cameraChangeButton.setBackgroundResource(R.drawable.front_camera);
			break;
		}

		params.setPictureSize(pic_width, pic_height);
		params.setPreviewSize(window_width, window_height);
		mCamera.setParameters(params);
	}

	/*----------------------设置静音模式-----------------------*/
	public void setToSilent() {
		// 调至静音模式
		try {
			AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			strRingerMode = audioManager.getRingerMode();
			if (audioManager != null) {
				// 设置为静音模式
				audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*----------------------将声音模式调到原来-----------------------*/
	public void setPreMode() {
		try {
			AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			audioManager.setRingerMode(strRingerMode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*----------------------监听电话-----------------------*/
	public void createPhoneListener() {
		TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephony.listen(new OnePhoneStateListener(this),
				PhoneStateListener.LISTEN_CALL_STATE);
	}

	/*------------------------得到相应位图------------------------*/
	public Bitmap drawableToBitmap(Drawable drawable) {
		// 取 drawable 的颜色格式
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		// 建立对应 bitmap
		Bitmap bitmap = Bitmap.createBitmap(pic_width, pic_height, config);
		// 建立对应 bitmap 的画布
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, 2 * pic_width, 2 * pic_height);
		// 把 drawable 内容画到画布中
		drawable.draw(canvas);
		return bitmap;
	}

	/*----------改写BaseAdapter自定义一ImageAdapterclass----------*/
	public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		private Context mContext; // ImageAdapter的建构子

		public ImageAdapter(Context c) {
			mContext = c;
			// 使用在res/values/attrs.xml中的定义 * 的Gallery属性
			TypedArray a = obtainStyledAttributes(R.styleable.Gallery);
			mGalleryItemBackground = a.getResourceId(
					R.styleable.Gallery_android_galleryItemBackground, 0);
			// 让对象的styleable属性能够反复使用
			a.recycle();
		}

		// 一定要重写的方法getCount,传回图片数目
		public int getCount() {
			return myImageIds.length;
		}

		// 传回position
		public Object getItem(int position) {
			return position;
		}

		// 传回position
		public long getItemId(int position) {
			return position;
		}

		// 传回一View对象
		public View getView(int position, View convertView, ViewGroup parent) {
			// 产生ImageView对象
			ImageView i = new ImageView(mContext);
			// 设定图片给imageView对象
			i.setImageResource(myImageIds[position]);
			// 重新设定图片的宽高
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			// 重新设定Layout的宽高
			i.setLayoutParams(new Gallery.LayoutParams(180, 180));
			// 设定Gallery背景图
			i.setBackgroundResource(mGalleryItemBackground);
			// 传回imageView物件
			return i;
		}

		// 建构一Integer array并取得预加载Drawable的图片id
		private Integer[] myImageIds = { R.drawable.no_frame,
				R.drawable.frame1, R.drawable.frame2, R.drawable.frame3,
				R.drawable.frame4, R.drawable.frame5, R.drawable.frame6,
				R.drawable.frame7, R.drawable.frame8, R.drawable.frame9,
				R.drawable.frame10, R.drawable.frame11, R.drawable.frame12 };
	}
}
