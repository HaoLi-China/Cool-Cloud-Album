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
 * �������
 * 
 * */

public class CameraView extends Activity implements SurfaceHolder.Callback,
		SeekBar.OnSeekBarChangeListener, AutoFocusCallback, OnClickListener {

	private AutoShootListeningThread autoShootListeningThread;// �Զ����ռ����߳�

	private Camera mCamera;// ���

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;

	private SeekBar seekBar1;// ���ڵ�����
	private SeekBar seekBar2;// ���������ع���

	private ImageButton flashButton;// ���صư�ť
	private ImageButton cameraChangeButton;// ����ͷת����ť
	private ImageButton albumButton;// ��ᰴť
	private ImageButton setButton;// ���ð�ť
	private ImageButton shootButton;// ���հ�ť
	private ImageButton focusButton;// �Խ���ť
	private ImageButton frameButton;// ���ť
	private ImageButton webAlbumButton;// ������ᰴť
	private ImageButton remoteControlButton;// Զ�̿��ư�ť

	private ImageView focusFrame;// ���öԽ�ͼƬ
	private ImageView frameView;// �������

	private Context context;

	private Gallery frameGallery;// ѡ������Gallery

	private AlertDialog dialog;// �Ի���

	private MediaPlayer shutterSound;// ������

	private boolean mPreviewRunning = true;
	private boolean isShoot = false;// �Ƿ�����
	private boolean isPhone = false;// �Ƿ��е绰����

	private int autoShootState = 0;// �Զ�����״̬
	private int remoteControlState = 0;// ң��״̬
	private int delyTime = 0;// �ӳ�����ʱ��
	private int strRingerMode;// ����ģʽ

	private int window_width;// ��Ļ���
	private int window_height;// ��Ļ�߶�

	private int pic_width;// ��Ƭ�Ŀ��
	private int pic_height;// ��Ƭ�ĸ߶�

	private int sleep = 300;// ����ÿ֡˯��ʱ��

	private int frame_id;// ���ͼƬid

	private double progressRate1 = 0;
	private double progressRate2 = 0;

	private byte[] data;

	private String albumName = "����";
	private String album[] = { "Ψ��", "��ܰ", "����", "����", "����", "��Ȼ", "ʱ��", "����" };
	private String autoSaveState;// �Ƿ��Զ����棨1�ǣ�0��
	private String autoFocusState;// �Ƿ��Զ��Խ���1�ǣ�0��
	private String ImageQualityState;// ��Ƭ������־
	private String ImageResolutionState;// ��Ƭ�ֱ��ʱ�־
	private String delyState;// �����ӳ�״̬
	private String shutterSoundState;// ������״̬
	private String sceneState;// ����ģʽ״̬
	private String effectState;// ����Ч��״̬
	private String whiteBalanceState;// ��ƽ��״̬
	private String flashModeState;// �����ģʽ
	private String cameraModeState;// ����ͷģʽ
	private String frameId;// ���ѡ��

	private RWSharedperferences rws;// ��д�����ļ�����

	// private String time;
	// private SimpleDateFormat format;
	// private ImageView littleView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// �ޱ���
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// ȫ��
		setContentView(R.layout.camera);

		context = this;
		rws = new RWSharedperferences();// ��ʼ����д�����ļ�����

		/* -----------------------�жϴ洢���Ƿ����--------------------------- */
		if (!checkSDCard()) {
			/* ����Userδ��װ�洢�� */
			Toast.makeText(CameraView.this,
					getResources().getText(R.string.sdcarderror).toString(),
					Toast.LENGTH_LONG).show();
		}

		/* ------------------------��ʼ������---------------------------- */
		window_height = getWindowManager().getDefaultDisplay().getHeight();// ��Ļ�Ŀ�͸�
		window_width = getWindowManager().getDefaultDisplay().getWidth();

		Thread thread0 = new Thread(new initControl());
		thread0.start();// ���߳������������

		Thread thread1 = new Thread(new ReadPara());
		thread1.start();// ���߳������������

		createPhoneListener();// �绰���
	}

	/* ------------------------�����������---------------------------- */
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

	/*-------------------��ʼ���ؼ�----------------*/
	public class initControl implements Runnable {
		public void run() {
			frameView = (ImageView) findViewById(R.id.frame_view);

			// seekBar
			seekBar1 = (SeekBar) findViewById(R.id.seekbar1);
			seekBar2 = (SeekBar) findViewById(R.id.seekbar2);

			// ���ý����ͼƬ
			focusFrame = (ImageView) findViewById(R.id.focus);
			mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);// ��ÿؼ�
			mSurfaceHolder = mSurfaceView.getHolder();// ��þ��
			mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// ��������

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

	/*-------------------�Կؼ����ü���----------------*/
	public void controlListening() {
		seekBar1.setOnSeekBarChangeListener(this);// ����¼�����
		seekBar2.setOnSeekBarChangeListener(this);// ����¼�����

		mSurfaceHolder.addCallback(this);// ��ӻص�
		mSurfaceView.setOnClickListener(this); // ���ü���

		flashButton.setOnClickListener(this);
		cameraChangeButton.setOnClickListener(this);
		albumButton.setOnClickListener(this);
		setButton.setOnClickListener(this);
		shootButton.setOnClickListener(this);
		focusButton.setOnClickListener(this);
		frameButton.setOnClickListener(this);
		webAlbumButton.setOnClickListener(this);
		remoteControlButton.setOnClickListener(this);

		frameGallery.setAdapter(new ImageAdapter(this)); // ����һImageAdapter���趨��Gallery
		frameGallery.setOnItemClickListener(new OnItemClickListener() { // �趨һ��itemclickListener��Toast����ѡͼƬ��λ��
					@SuppressWarnings("unchecked")
					public void onItemClick(AdapterView parent, View v,
							int position, long id) {
						frameId = position + "";
						rws.write(context, "frameId", position + "");
						setFrame();// �������
						frameGallery.setVisibility(8);
					}
				});
	}

	/*-------------------����Զ�����״̬----------------*/
	public int getAutoShootState() {
		return autoShootState;
	}

	/*-------------------�����Զ�����״̬----------------*/
	public void setAutoShootState(int state) {
		autoShootState = state;
	}

	/*-------------------��õ绰״̬----------------*/
	public boolean getPhoneState() {
		return isPhone;
	}

	/*-------------------���õ绰״̬----------------*/
	public void setPhoneState(boolean bool) {
		isPhone = bool;
	}

	/*-------------------�������----------------*/
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

	/*-------------------------���������---------------------------*/
	public void setFlash() {
		Camera.Parameters params1 = mCamera.getParameters();
		if (flashModeState.equals("0")) {
			flashModeState = "1";
			rws.write(this, "flashMode", flashModeState);
			params1.setFlashMode("off");// �ر������ģʽ
			flashButton.setBackgroundResource(R.drawable.flash_off);
		} else if (flashModeState.equals("1")) {
			flashModeState = "2";
			rws.write(this, "flashMode", flashModeState);
			params1.setFlashMode("auto");// ������Զ�ģʽ
			flashButton.setBackgroundResource(R.drawable.auto_flash);
		}
		/*
		 * else if (FlashState == 2) { FlashState = 3;
		 * params1.setFlashMode("red_eye");// �������������ģʽ
		 * flash.setBackgroundResource(R.drawable.red_eye); }
		 */
		else if (flashModeState.equals("2")) {
			flashModeState = "3";
			rws.write(this, "flashMode", flashModeState);
			params1.setFlashMode("torch");// ����Ƴ���ģʽ
			flashButton.setBackgroundResource(R.drawable.flash_torch);
		} else {
			flashModeState = "0";
			rws.write(this, "flashMode", flashModeState);
			params1.setFlashMode("on");// ���������ģʽ
			flashButton.setBackgroundResource(R.drawable.flash_on);
		}
		mCamera.setParameters(params1);
	}

	/*----------------------------- ����----------------------------*/
	public void shoot() {
		isShoot = true;
		setCamera();
		setToSilent();// ��������ģʽ
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

	/*------------------------ң���Զ�����----------------------*/
	public void autoShoot() {
		isShoot = true;
		setToSilent();// ��������ģʽ
		try {
			Thread.sleep(delyTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mCamera.takePicture(shutterCall, null, jpegCallback);
		autoShootState = 0;
	}

	/*-------------------------------�Խ�-------------------------*/
	public void focus() {
		isShoot = false;
		mCamera.autoFocus(this);
	}

	@Override
	/*---------------------���ò�������ʼԤ��-----------------------*/
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// TODO Auto-generated method stub
		if (mPreviewRunning) {
			mCamera.stopPreview();
		}
		// params.set("rotation", 90);
		setCamera();// �����������
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mCamera.startPreview();
		mPreviewRunning = true;
	}

	@Override
	/*-----------------------------�������------------------------------*/
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mCamera = Camera.open();
	}

	@Override
	/*-------------------�ر�Ԥ�����ͷ���Դ----------------------*/
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mCamera.stopPreview();
		mPreviewRunning = false;
		mCamera.release();
	}

	/*--------------------------�ص�����-----------------------*/
	private ShutterCallback shutterCall = new ShutterCallback() {// ������
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
			setPreMode();// ����ԭ��ģʽ
		}
	};

	private PictureCallback jpegCallback = new PictureCallback() {// JPEG
		public void onPictureTaken(byte[] _data, Camera _camera) {// onPictureTaken����ĵ�һ��������Ϊ��Ƭ��byte
			// ���ؼ�����
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

				// ��ťͼƬ����
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
							// ��Ƭ��������������Ϊ����
							Camera.Parameters params = mCamera.getParameters();
							params.setFlashMode("torch");// ����Ƴ���ģʽ
							mCamera.setParameters(params);
						}
					}
				});
				cancelButton.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						dialog.cancel();
						mCamera.startPreview();
						if (flashModeState.equals("3")) {
							// ��Ƭ��������������Ϊ����
							Camera.Parameters params = mCamera.getParameters();
							params.setFlashMode("torch");// ����Ƴ���ģʽ
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
				// Ϊ�����б���һ�����������õ�ǰ�涨���list��
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						CameraView.this, android.R.layout.simple_spinner_item,
						albumList);
				// Ϊ���������������б�����ʱ�Ĳ˵���ʽ��
				adapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// ����������ӵ������б���
				mySpinner.setAdapter(adapter);
				// Ϊ�����б����ø����¼�����Ӧ���������Ӧ�˵���ѡ��
				mySpinner
						.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
							@SuppressWarnings("unchecked")
							public void onItemSelected(AdapterView arg0,
									View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								/* ��mySpinner ��ʾ */
								albumName = album[arg2];
								arg0.setVisibility(View.VISIBLE);
							}

							@SuppressWarnings("unchecked")
							public void onNothingSelected(AdapterView arg0) {
								// TODO Auto-generated method stub
								arg0.setVisibility(View.VISIBLE);
							}
						});
				// �����˵�����������ѡ����¼�����
				mySpinner.setOnTouchListener(new Spinner.OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						// ��mySpinner ���أ�������Ҳ���ԣ����Լ�����
						// v.setVisibility(View.INVISIBLE);
						return false;
					}
				});
				// �����˵�����������ѡ���ı��¼�����
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

	/*-------------------------������Ƭ---------------------*/
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
			// ����ʱ���ʽ
			// format=new SimpleDateFormat( "yyyy��MM��dd��");
			// time=format.format((new Date()));
			fu.write2SDFromInput(anbumName + "/", pic);
		} else {
			FileWriting fu = new FileWriting();
			fu.write2SDFromInput(anbumName + "/", bm);
		}
		mCamera.startPreview();
		Toast.makeText(
				CameraView.this,
				"��Ƭ�洢·����" + "/sdcard/myCamera/" + anbumName + "/"
						+ System.currentTimeMillis() + ".jpg",
				Toast.LENGTH_LONG).show();
	}

	/*------------------------��ť����-------------------------*/
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.surface_camera:
			// shoot();//����ע�͵��ˣ�������������չ
			break;

		case R.id.flash:// ���������
			setFlash();
			break;

		case R.id.camera_change:// ����ͷת��
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
			parameters.set("camera-id", "secondary");// sumsung 2.3��ǰ���ֻ�����ǰ��
			// parameters.set("camera-id",1);//sumsung 2.3��ǰ���ֻ���������
			// parameters.set("video_input", "secondary");// htc2.3��ǰ���ֻ�����ǰ��
			// parameters.set("video_input","main");//htc2.3��ǰ���ֻ� ����
			mCamera.setParameters(parameters);
			break;

		case R.id.album_button:// ת���������
			Intent intent1 = new Intent();
			intent1.setClass(CameraView.this, AlbumDirectory.class);
			startActivity(intent1);
			finish();
			break;

		case R.id.set_button:// ����
			Camera.Parameters params = mCamera.getParameters();
			List<String> effectList = params.getSupportedColorEffects();
			List<String> sceneList = params.getSupportedSceneModes();
			List<String> whiteBalanceList = params.getSupportedWhiteBalance();
			// List<Camera.Size>
			// pictureSizeList=params.getSupportedPictureSizes();

			Intent intent2 = new Intent();
			intent2.setClass(CameraView.this, SetView.class);
			// newһ��Bundle���󣬲���Ҫ���ݵ����ݴ���
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

		case R.id.shoot_button:// ����
			shoot();
			break;

		case R.id.focus_button:// �Խ�
			focus();
			break;

		case R.id.frame_button:// ���ѡ��
			frameGallery.setVisibility(0);
			break;

		case R.id.web_album_button:// ת���������
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

		case R.id.remote_control:// Զ�̿��ƿ���/�ر�
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
	/*-------------------------�Զ��Խ�-----------------------*/
	public void onAutoFocus(boolean success, Camera camera) {
		// TODO Auto-generated method stub
		if (success) {
			if (!isShoot) {
				// ��֡����AnimationDrawable����
				AnimationDrawable focus_ani = new AnimationDrawable();
				for (int i = 0; i < 2; i++) {
					int id = getResources().getIdentifier("focus" + i,
							"drawable", "android.app.myCamera");
					focus_ani.addFrame(getResources().getDrawable(id), sleep);
				}
				// �����ַ��ظ����ţ�trueΪ���ظ�
				focus_ani.setOneShot(true);
				MyAnimationDrawable mad = new MyAnimationDrawable(focus_ani) {
					@Override
					void onAnimationEnd() {
						// ʵ���������������������
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
	/*----------------seekbar�϶���-------------------*/
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		Camera.Parameters params = mCamera.getParameters();
		if (seekBar == this.seekBar1) {
			this.progressRate1 = (double) progress / 100;
			// ���ý���
			if (params.isZoomSupported()) {
				params.setZoom((int) (params.getMaxZoom() * progressRate1));
			}
		} else if (seekBar == this.seekBar2) {
			this.progressRate2 = (double) progress / 100;
			// �����ع���
			params.setExposureCompensation((int) (params
					.getMaxExposureCompensation() * progressRate2));
		}
		mCamera.setParameters(params);
	}

	@Override
	/*----------------seekbar��ʼ�϶�-------------------*/
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	/*----------------seekbar�����϶�-------------------*/
	public void onStopTrackingTouch(SeekBar seekBar) {
		if (seekBar == this.seekBar1) {
			focus();// �Խ�
		}
	}

	/* -----------------�жϴ洢���Ƿ����--------------- */
	private boolean checkSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/*-----------------------------�����������-------------------------*/
	private void setCamera() {

		autoSaveState = rws.read(this, "autoSave");// �����������
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
			params.setFlashMode("on");// ���������ģʽ
			cameraChangeButton.setBackgroundResource(R.drawable.flash_on);
			break;
		case 1:
			params.setFlashMode("off");// �ر������ģʽ
			cameraChangeButton.setBackgroundResource(R.drawable.flash_off);
			break;
		case 2:
			params.setFlashMode("auto");// ������Զ�ģʽ
			cameraChangeButton.setBackgroundResource(R.drawable.auto_flash);
			break;
		case 3:
			params.setFlashMode("torch");// ����Ƴ���ģʽ
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

	/*----------------------���þ���ģʽ-----------------------*/
	public void setToSilent() {
		// ��������ģʽ
		try {
			AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			strRingerMode = audioManager.getRingerMode();
			if (audioManager != null) {
				// ����Ϊ����ģʽ
				audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*----------------------������ģʽ����ԭ��-----------------------*/
	public void setPreMode() {
		try {
			AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			audioManager.setRingerMode(strRingerMode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*----------------------�����绰-----------------------*/
	public void createPhoneListener() {
		TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephony.listen(new OnePhoneStateListener(this),
				PhoneStateListener.LISTEN_CALL_STATE);
	}

	/*------------------------�õ���Ӧλͼ------------------------*/
	public Bitmap drawableToBitmap(Drawable drawable) {
		// ȡ drawable ����ɫ��ʽ
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		// ������Ӧ bitmap
		Bitmap bitmap = Bitmap.createBitmap(pic_width, pic_height, config);
		// ������Ӧ bitmap �Ļ���
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, 2 * pic_width, 2 * pic_height);
		// �� drawable ���ݻ���������
		drawable.draw(canvas);
		return bitmap;
	}

	/*----------��дBaseAdapter�Զ���һImageAdapterclass----------*/
	public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		private Context mContext; // ImageAdapter�Ľ�����

		public ImageAdapter(Context c) {
			mContext = c;
			// ʹ����res/values/attrs.xml�еĶ��� * ��Gallery����
			TypedArray a = obtainStyledAttributes(R.styleable.Gallery);
			mGalleryItemBackground = a.getResourceId(
					R.styleable.Gallery_android_galleryItemBackground, 0);
			// �ö����styleable�����ܹ�����ʹ��
			a.recycle();
		}

		// һ��Ҫ��д�ķ���getCount,����ͼƬ��Ŀ
		public int getCount() {
			return myImageIds.length;
		}

		// ����position
		public Object getItem(int position) {
			return position;
		}

		// ����position
		public long getItemId(int position) {
			return position;
		}

		// ����һView����
		public View getView(int position, View convertView, ViewGroup parent) {
			// ����ImageView����
			ImageView i = new ImageView(mContext);
			// �趨ͼƬ��imageView����
			i.setImageResource(myImageIds[position]);
			// �����趨ͼƬ�Ŀ��
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			// �����趨Layout�Ŀ��
			i.setLayoutParams(new Gallery.LayoutParams(180, 180));
			// �趨Gallery����ͼ
			i.setBackgroundResource(mGalleryItemBackground);
			// ����imageView���
			return i;
		}

		// ����һInteger array��ȡ��Ԥ����Drawable��ͼƬid
		private Integer[] myImageIds = { R.drawable.no_frame,
				R.drawable.frame1, R.drawable.frame2, R.drawable.frame3,
				R.drawable.frame4, R.drawable.frame5, R.drawable.frame6,
				R.drawable.frame7, R.drawable.frame8, R.drawable.frame9,
				R.drawable.frame10, R.drawable.frame11, R.drawable.frame12 };
	}
}
