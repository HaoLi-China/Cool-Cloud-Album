package android.app.album;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.app.connect.Upload;
import android.app.fileManage.FileWriting;
import android.app.myCamera.R;
import android.app.util.SharedData;
import android.app.webAlbum.Login;
import android.app.webAlbum.LoginStatus;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaRecorder;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.webview.OAuthV2AuthorizeWebView;

/*
 * 
 * 照片查看编辑界面，
 * 用于实现照片的录音，
 * 照片渲染，
 * 照片旋转，
 * 照片裁剪，
 * 照片删除，
 * 照片上传，
 * 照片分享等功能
 * 
 * */

public class PhotoView extends Activity {
	private int width = 0;
	private int height = 0;
	private int pointX = 0;
	private int pointY = 0;
	private int photo_width;
	private int photo_height;
	private int present_width;
	private int present_height;

	private String imageName;// 相片名
	private String albumName;// 相册名
	private String dirName;// 文件夹路径
	private String prefix;// 文件名(不包括扩展名)
	private String recordFileName;// 录音文件名

	private Button rotate;// 旋转按钮
	private Button record;// 录音按钮
	private Button play;// 播放按钮
	private Button stop;// 停止按钮

	private File recordFile;// 对应照片的录音名
	private File recAudioFile;

	private Bitmap photo;
	private Bitmap tmp;
	private Bitmap newBit;

	private ImageView imageView;

	private MediaRecorder mMediaRecorder;

	private AlertDialog recordDialog;// 对话框

	private GestureDetector detector;
	private myGestureListener gListener;

	private Gallery buttonGallery;// 按钮Gallery

	private Context context;

	// private float x1=0,x2=0,y1=0,y2=0;
	// private double distance=0;
	// ** Called when the activity is first created. *//*
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 全屏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 隐藏标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 加载image_view.xml Layout
		setContentView(R.layout.image_view);

		context = this;

		lss = new LoginStatus(this);// 实例化LoginStatus对象

		// 参数接收
		Bundle bunde = this.getIntent().getExtras();
		imageName = bunde.getString("imageName");
		albumName = bunde.getString("albumName");
		dirName = bunde.getString("dirName");
		prefix = bunde.getString("namePrefix");

		recordFileName = dirName + "/" + prefix + ".amr";
		recordFile = new File(recordFileName);

		// 取得屏幕宽高
		width = this.getWindowManager().getDefaultDisplay().getWidth();
		height = this.getWindowManager().getDefaultDisplay().getHeight();

		// 按钮Gallery
		buttonGallery = (Gallery) findViewById(R.id.button_gallery);
		buttonGallery.setVisibility(8);
		buttonGallery.setAdapter(new ImageAdapter(this));
		buttonGallery.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			public void onItemClick(AdapterView parent, View v, int position,
					long id) {
				buttonGallery.setVisibility(8);
				switch (position) {
				case 0:// 转到渲染界面
					Intent intent1 = new Intent();
					intent1.setClass(PhotoView.this, PhotoRenderView.class);
					Bundle bundle1 = new Bundle();
					bundle1.putString("albumName", albumName);
					bundle1.putString("imageName", imageName);
					intent1.putExtras(bundle1);
					startActivity(intent1);
					finish();
					break;
				case 1:// 转到裁剪页面
					Intent intent2 = new Intent();
					intent2.setClass(PhotoView.this, PhotoTailorView.class);
					Bundle bundle2 = new Bundle();
					bundle2.putString("albumName", albumName);
					bundle2.putString("imageName", imageName);
					intent2.putExtras(bundle2);
					startActivity(intent2);
					finish();
					break;
				case 2:// 转到分享界面
					picpath = imageName;
					ShareDialog();
					break;
				case 3:// 上传界面
					if (lss.getValue_status().equals("1")) {
						srcPath = imageName;
						UploadDialog();
					} else {
						Intent intent = new Intent(PhotoView.this, Login.class);
						startActivity(intent);
					}
					break;
				case 4:// 删除
					File file = new File(imageName);
					file.delete();
					finish();
					break;
				}
			}
		});

		// 照片解码
		photo = BitmapFactory.decodeFile(imageName);

		// 获得照片宽度和高度
		photo_width = photo.getWidth();
		photo_height = photo.getHeight();

		present_width = width;
		present_height = present_width * photo_height / photo_width;

		// ImageView初始化
		imageView = (ImageView) findViewById(R.id.imageView);
		imageView.setImageBitmap(photo);

		record = (Button) findViewById(R.id.record);
		record.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (recordFile.exists()) {
					LayoutInflater inflater = getLayoutInflater();
					View layout = inflater.inflate(R.layout.record_dialog,
							(ViewGroup) findViewById(R.id.layout_root));
					recordDialog = new AlertDialog.Builder(PhotoView.this)
							.create();
					recordDialog.show();
					recordDialog.getWindow().setContentView(layout);

					Button no_button = (Button) recordDialog
							.findViewById(R.id.no);
					Button yes_button = (Button) recordDialog
							.findViewById(R.id.yes);

					no_button.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							recordDialog.cancel();
						}
					});

					yes_button.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							recordDialog.cancel();
							record();
						}
					});
				} else {
					record();
				}
			}
		});

		stop = (Button) findViewById(R.id.stop);
		stop.setEnabled(false);
		stop.setBackgroundResource(R.drawable.stop2);
		stop.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				stopRecord();
			}
		});

		play = (Button) findViewById(R.id.play);
		if (recordFile.exists()) {
			play.setEnabled(true);
			play.setBackgroundResource(R.drawable.start1);
		} else {
			play.setEnabled(false);
			play.setBackgroundResource(R.drawable.start2);
		}
		play.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				playRecord(recordFile);
			}
		});

		rotate = (Button) findViewById(R.id.rotate);
		rotate.setOnClickListener(new Button.OnClickListener() {// 旋转图片
					public void onClick(View v) {
						Matrix matrix = new Matrix();
						// 旋转图片 动作
						matrix.postRotate(90);
						photo = Bitmap.createBitmap(photo, 0, 0, photo
								.getWidth(), photo.getHeight(), matrix, true);
						imageView.setImageBitmap(photo);

						// 获得照片宽度和高度
						photo_width = photo.getWidth();
						photo_height = photo.getHeight();

						present_width = width;
						present_height = present_width * photo_height
								/ photo_width;
						String name = null;
						for (int i = 0; i < imageName.length(); i++) {
							if (imageName.charAt(i) == '/') {
								name = imageName.substring(i + 1);
							}
						}
						FileWriting fu = new FileWriting();
						fu.overWrite(albumName + "/", name, photo);
					}
				});

		// GestureDetector设定
		gListener = new myGestureListener();
		detector = new GestureDetector(PhotoView.this, gListener);
		// 设定GestureDetector的OnDoubleTapListener
		detector
				.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
					/*-------------------点击画面做放大的操作-------------------------*/
					@Override
					public boolean onSingleTapConfirmed(MotionEvent e) {

						present_width = (int) (1.1 * present_width);
						present_height = (int) (1.1 * present_height);
						if (present_width < photo_width
								&& present_height < photo_height) {
							tmp = Bitmap.createScaledBitmap(photo,
									present_width, present_height, true);
							pointX = (present_width - width) / 2;
							if (present_height > height) {
								pointY = (present_height - height) / 2;
								// 重新设定显示的影像
								Bitmap newB = Bitmap.createBitmap(tmp, pointX,
										pointY, width, height);
								imageView.setImageBitmap(newB);
							} else {
								pointY = 0;
								// 重新设定显示的影像
								Bitmap newB = Bitmap.createBitmap(tmp, pointX,
										pointY, width, present_height);
								imageView.setImageBitmap(newB);
							}
						} else {
							present_width = photo_width;
							present_height = photo_height;
						}
						return false;
					}

					@Override
					public boolean onDoubleTapEvent(MotionEvent e) {
						return false;
					}

					/*-----------------双击画面做缩小的操作--------------------- */
					@Override
					public boolean onDoubleTap(MotionEvent e) {
						present_width = (int) (present_width / 1.1);
						present_height = (int) (present_height / 1.1);
						// 缩最小就是跟画面几样己
						if (present_width > width) {
							tmp = Bitmap.createScaledBitmap(photo,
									present_width, present_height, true);
							// 计算X轴基准点缩小后的位置
							pointX = (present_width - width) / 2;
							// 计算Y轴基准点缩小后的位置
							if (present_height > height) {
								pointY = (present_height - height) / 2;
								newBit = Bitmap.createBitmap(tmp, pointX,
										pointY, width, height);
								imageView.setImageBitmap(newBit);
							} else {
								pointY = 0;
								newBit = Bitmap.createBitmap(tmp, pointX,
										pointY, width, present_height);
								imageView.setImageBitmap(newBit);
							}
						} else {
							pointX = 0;
							pointY = 0;
							present_width = width;
							present_height = present_width * photo_height
									/ photo_width;
							tmp = Bitmap.createScaledBitmap(photo,
									present_width, present_height, true);
							Bitmap newB = Bitmap.createBitmap(tmp, pointX,
									pointY, width, present_height);
							imageView.setImageBitmap(newB);
						}
						return false;
					}
				});
	}

	/*---当Activity的onTouchEvent()被触发时， 触发GestureDetector的onTouchEvent()---*/
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (detector.onTouchEvent(event)) {
			return detector.onTouchEvent(event);
		} else {
			return super.onTouchEvent(event);
		}
	}

	/*--------------------自定义GestureListener--------------------*/
	public class myGestureListener implements GestureDetector.OnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (present_width > width || present_height > height) {
				// 取得目前显示的Bitmap
				Bitmap b = tmp;
				int tmpW = b.getWidth();
				int tmpH = b.getHeight();
				// 计算X轴基准点移动后的吵置
				if (pointX + distanceX >= 0) {
					if ((pointX + distanceX) > (tmpW - width)) {
						pointX = tmpW - width;
					} else {
						pointX += distanceX;
					}
				} else {
					pointX = 0;
				}
				// 计算Y轴基准点移动后的吵置
				if (pointY + distanceY >= 0 && present_height > height) {
					if ((pointY + distanceY) > (tmpH - height)) {
						pointY = tmpH - height;
					} else {
						pointY += distanceY;
					}
				} else {
					pointY = 0;
				}
				// 如果有移动，则更新Bitmap设定
				if (distanceX != 0 && distanceY != 0 && tmpH > height
						&& tmpW > width) {
					Bitmap newB = Bitmap.createBitmap(b, pointX, pointY, width,
							height);
					imageView.setImageBitmap(newB);
				} else if (tmpH <= height && tmpW > width) {
					Bitmap newB = Bitmap.createBitmap(b, pointX, pointY, width,
							tmpH);
					imageView.setImageBitmap(newB);
				} else if (tmpH > height && tmpW <= width) {
					Bitmap newB = Bitmap.createBitmap(b, pointX, pointY, width,
							tmpH);
					imageView.setImageBitmap(newB);
				} else if (tmpH <= height && tmpW <= width) {
					Bitmap newB = Bitmap.createBitmap(b, pointX, pointY, tmpW,
							tmpH);
					imageView.setImageBitmap(newB);
				}
			}
			return false;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onDown(MotionEvent arg0) {
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			buttonGallery.setVisibility(0);
		}

		@Override
		public void onShowPress(MotionEvent e) {

		}
	}

	/*-----------------------录音-------------------------*/
	public void record() {
		// 建立录音档
		try {
			recAudioFile = new File(dirName + "/" + prefix + ".amr");
			System.out.println(prefix);
			mMediaRecorder = new MediaRecorder();
			// 设定录音来源为麦克风
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

			mMediaRecorder.setOutputFile(recAudioFile.getAbsolutePath());

			mMediaRecorder.prepare();

			mMediaRecorder.start();

			record.setEnabled(false);
			record.setBackgroundResource(R.drawable.record2);
			play.setEnabled(false);
			play.setBackgroundResource(R.drawable.start2);
			stop.setEnabled(true);
			stop.setBackgroundResource(R.drawable.stop1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*-----------------------停止录音--------------------------*/
	public void stopRecord() {
		if (recAudioFile != null) {
			// 停止录音
			mMediaRecorder.stop();
			mMediaRecorder.release();
			mMediaRecorder = null;
			record.setEnabled(true);
			record.setBackgroundResource(R.drawable.record1);
			play.setEnabled(true);
			play.setBackgroundResource(R.drawable.start1);
			stop.setEnabled(false);
			stop.setBackgroundResource(R.drawable.stop2);
		}
	}

	/*--------------------------播放录音--------------------------*/
	public void playRecord(File f) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		String type = getMIMEType(f);
		intent.setDataAndType(Uri.fromFile(f), type);
		startActivity(intent);
	}

	/*-------------------------获得文件类型--------------------------*/
	private String getMIMEType(File f) {
		String end = f.getName().substring(f.getName().lastIndexOf(".") + 1,
				f.getName().length()).toLowerCase();
		String type = "";
		if (end.equals("mp3") || end.equals("aac") || end.equals("aac")
				|| end.equals("amr") || end.equals("mpeg") || end.equals("mp4")) {
			type = "audio";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg")) {
			type = "image";
		} else {
			type = "*";
		}
		type += "/*";
		return type;
	}

	/*--------------自定义ImageAdapter class------------------------*/
	public class ImageAdapter extends BaseAdapter {
		private int mGalleryItemBackground;
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;
			TypedArray a = obtainStyledAttributes(R.styleable.Gallery);
			// 取得Gallery属性的Index
			mGalleryItemBackground = a.getResourceId(
					R.styleable.Gallery_android_galleryItemBackground, 0);
			// 让对象的styleable属性能够反复使用
			a.recycle();
		}

		public int getCount() {
			return myImageIds.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

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
			return i;
		}

		// 建构一Integer array并取得预加载Drawable的图片id
		private Integer[] myImageIds = { R.drawable.render, R.drawable.tailor,
				R.drawable.share, R.drawable.upload, R.drawable.delete };
	}

	/*
	 * 下面是上传图片到服务器的部分程序的
	 */
	private ProgressDialog pd_upload;// 上传文件缓冲对话框
	private String uemail;// 从配置文件获得在线用户的帐号
	private String u_result;// 上传结果
	private String filename; // 要上传到的文件夹名
	private String srcPath; // 上传文件的url路径
	private LoginStatus lss; // 获取在线用户的昵称的对象
	// 要添加到图片文件中
	private ListView upload_list;
	private SimpleAdapter upload_listItemAdapter;
	private ArrayList<HashMap<String, Object>> upload_listItem;
	private int upload_num;
	private String[] upload_file;

	/*----------------创建对话框，并添加处理和监听-------------------*/
	private void UploadDialog() {
		Builder dialog = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.uploaddialog, null);
		dialog.setView(layout);

		// 上传中
		final String meg_upload = getString(R.string.upload).toString();

		dialog.setTitle(R.string.dialog_filename);

		// 从配置文件中获得要初始化的ListView的参数，及相册文件的数目和文件的名称
		uemail = lss.getValue_email();
		upload_num = lss.getValue_num() + 3;
		String temp = lss.getValue_filename();
		upload_file = StoFile(temp);

		upload_list = (ListView) layout.findViewById(R.id.upload_list);

		// 生成动态数组，加入数据
		upload_listItem = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < upload_num; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			switch (i) {
			case 0:
				map.put("ItemImage", R.drawable.life);// 图像资源的ID
				map.put("ItemText", "生活");
				break;
			case 1:
				map.put("ItemImage", R.drawable.tourism);// 图像资源的ID
				map.put("ItemText", "旅游");
				break;
			case 2:
				map.put("ItemImage", R.drawable.other);// 图像资源的ID
				map.put("ItemText", "其他");
				break;
			default:
				map.put("ItemImage", R.drawable.own);// 图像资源的ID
				map.put("ItemText", upload_file[i - 3]);
			}
			upload_listItem.add(map);
		}
		// 生成适配器的Item和动态数组对应的元素
		upload_listItemAdapter = new SimpleAdapter(this, upload_listItem,// 数据源
				R.layout.listitems,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "ItemImage", "ItemText" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.ItemImage, R.id.ItemText });

		// 添加并且显示
		upload_list.setAdapter(upload_listItemAdapter);
		// 添加点击
		upload_list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case 0:
					System.out.println("生活");
					filename = "生活";
					// 进入上传图片到服务器
					pd_upload = new ProgressDialog(PhotoView.this);
					pd_upload.setMessage(meg_upload);
					pd_upload.show();
					Thread thread1 = new Thread(new UploadImage());
					thread1.start();
					break;
				case 1:
					System.out.println("旅游");
					filename = "旅游";
					// 进入上传图片到服务器
					pd_upload = new ProgressDialog(PhotoView.this);
					pd_upload.setMessage(meg_upload);
					pd_upload.show();
					Thread thread2 = new Thread(new UploadImage());
					thread2.start();
					break;
				case 2:
					System.out.println("其他");
					filename = "其他";
					// 进入上传图片到服务器
					pd_upload = new ProgressDialog(PhotoView.this);
					pd_upload.setMessage(meg_upload);
					pd_upload.show();
					Thread thread3 = new Thread(new UploadImage());
					thread3.start();
					break;
				default:
					System.out.println(upload_file[arg2 - 3]);
					filename = upload_file[arg2 - 3];
					// 进入上传图片到服务器
					pd_upload = new ProgressDialog(PhotoView.this);
					pd_upload.setMessage(meg_upload);
					pd_upload.show();
					Thread thread4 = new Thread(new UploadImage());
					thread4.start();
				}
			}
		});

		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// dialog.dismiss();
				dialog.cancel();
			}
		});
		dialog.show();
	}

	/*--------------------处理上传子程序的返回信息---------------------*/
	private Handler hl_upload = new Handler() {
		public void handleMessage(Message msg) {
			if (u_result.equals("link error") || u_result.equals("error")) {
				pd_upload.dismiss();
				String error = getString(R.string.web_error).toString();
				Toast toast = Toast.makeText(context, error + "上传失败",
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			} else {
				pd_upload.dismiss();
				Toast toast = Toast
						.makeText(context, "上传成功", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
		}
	};

	/*--------------------上传文件子进程------------------------*/
	private class UploadImage implements Runnable {
		public void run() {
			Upload load = new Upload(uemail, filename, srcPath, "upload");
			u_result = load.uploadImage();
			hl_upload.sendEmptyMessage(0);
		}
	}

	/*-------------把从配置文件得到的字符串序列转化为文件夹名的数组---------------*/
	private String[] StoFile(String otherfile) {
		return otherfile.split("\\?");
	}

	/*
	 * 分享照片到腾讯微博的代码部分
	 */
	private EditText et_qq;

	private SharedData share;

	private OAuthV2 oAuth;
	private WifiManager wifiManager;
	private WifiInfo wifiInfo;
	private int ipAddress;
	private String content;
	private String picpath = "";

	// 创建对话框，并添加处理和监听
	private void ShareDialog() {
		Builder dialog = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.sharedialog, null);
		dialog.setView(layout);

		dialog.setTitle("分享照片到腾讯微博");

		share = new SharedData(context);
		et_qq = (EditText) layout.findViewById(R.id.et_qq);
		oAuth = new OAuthV2("http://www.myurl.com/camera7");
		oAuth.setClientId("801159127");
		oAuth.setClientSecret("e02e6f0b91034e783bf2c0a06d677861");

		dialog.setPositiveButton("分享", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				String accessToken = share.getQQAccessToken();
				long begintime = 0;
				long length = 0;
				long nowtime = 0;
				if (accessToken != "no") {
					begintime = share.getQQBegintime();
					length = Integer.parseInt(share.getQQExpiresIn()) * 1000;
					nowtime = new Date().getTime();
				}
				System.out.println(accessToken);

				content = et_qq.getText().toString().trim();

				if (content == null || content.equals("")) {
					content = "我的图片";// 默认为图片
				}
				if (accessToken != "no" && nowtime < begintime + length) {
					getoAuth();
					// 调用API获取用户信息发微博
					TAPI tapi = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
					try {
						String response = tapi.addPic(oAuth, "json", content,
								intToIp(ipAddress), picpath);
						if (response == null) {
							Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT)
									.show();
							return;
						}
						int code = errorcode(response);
						if (code == 0) {
							Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT)
									.show();
							return;
						}
						if (code == 25) {
							Toast.makeText(context, "分享失败，两次上传相同",
									Toast.LENGTH_SHORT).show();
						}
						System.out.println(errorcode(response));
					} catch (Exception e) {
						e.printStackTrace();
					}
					tapi.shutdownConnection();
				} else {
					wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
					wifiInfo = wifiManager.getConnectionInfo();
					ipAddress = wifiInfo.getIpAddress();
					System.out.println("dfdfdfdfdf");
					Intent intent = new Intent(context,
							OAuthV2AuthorizeWebView.class);
					intent.putExtra("oauth", oAuth);
					startActivityForResult(intent, 1);
					System.out.println("dfdfdfdfgfgfgfg");
				}
			}
		});
		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// dialog.dismiss();
				dialog.cancel();
			}
		});
		dialog.show();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == OAuthV2AuthorizeWebView.RESULT_CODE) {
				oAuth = (OAuthV2) data.getExtras().getSerializable("oauth");
				System.out.println(oAuth.getAccessToken());
				System.out.println(oAuth.getExpiresIn());
				store();
				// 调用API获取用户信息发微博
				TAPI tapi = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
				try {
					String response = tapi.addPic(oAuth, "json", content,
							intToIp(ipAddress), picpath);
					if (response == null) {
						Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT)
								.show();
						return;
					}
					int code = errorcode(response);
					if (code == 0) {
						Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT)
								.show();
						return;
					}
					if (code == 25) {
						Toast.makeText(context, "分享失败，两次上传相同",
								Toast.LENGTH_SHORT).show();
					}
					System.out.println(errorcode(response));
				} catch (Exception e) {
					e.printStackTrace();
				}
				tapi.shutdownConnection();

			}
		}
	}

	private void store() {
		share.saveQQAccessToken(oAuth.getAccessToken());
		share.saveQQclientId(oAuth.getClientId());
		share.saveQQclientSecret(oAuth.getClientSecret());
		share.saveQQOpenid(oAuth.getOpenid());
		share.saveQQOpenkey(oAuth.getOpenkey());
		share.saveQQExpiresIn(oAuth.getExpiresIn());
		share.saveQQRefreshToken(oAuth.getRefreshToken());
		Date date = new Date();
		share.saveQQBegintime(date.getTime());
	}

	private void getoAuth() {
		oAuth.setAccessToken(share.getQQAccessToken());
		oAuth.setClientId(share.getQQclientId());
		oAuth.setClientSecret(share.getQQclientSecret());
		oAuth.setOpenid(share.getQQOpenid());
		oAuth.setOpenkey(share.getQQOpenkey());
		oAuth.setExpiresIn(share.getQQExpiresIn());
		oAuth.setRefreshToken(share.getQQRefreshToken());
	}

	private int errorcode(String result) {
		int startPos = result.indexOf("\"errcode\":") + 10;
		int endPos = 0;
		for (int i = startPos; i < result.length()
				&& (result.charAt(i) >= '0' && result.charAt(i) <= '9'); i++) {
			endPos = i + 1;
		}
		String accessToken = result.substring(startPos, endPos);
		int errorcode = Integer.parseInt(accessToken);
		return errorcode;
	}

	private static String intToIp(int i) {
		return ((i >> 24) & 0xFF) + "." + ((i >> 16) & 0xFF) + "."
				+ ((i >> 8) & 0xFF) + "." + (i & 0xFF);
	}

}

/*
 * public boolean onTouchEvent(MotionEvent event) { //判断是否是两点触控
 * if(event.getPointerCount()==2){ int p1=event.findPointerIndex(0); int
 * p2=event.findPointerIndex(1); if(x1!=0&&x2!=0){ x1=event.getX(p1);
 * y1=event.getY(p1); x2=event.getX(p2); y2=event.getY(p2); float x=x2-x1; float
 * y=y2-y1; double distance1=Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2)); double
 * differ=distance1-distance;
 * 
 * //放大图片 if(differ>0) { present_width=(int)(1.1*present_width);
 * present_height=(int)(1.1*present_height); //pointX+=(differ/8);
 * //pointY+=(differ*height/(8*width)); //
 * if(2*pointX+width<photo_width&&2*pointY+height<photo_height){
 * if(present_width<=photo_width&&present_height<=photo_height){ // 设置图片大小
 * Bitmap
 * newPhoto1=Bitmap.createScaledBitmap(photo,present_width,present_height,true);
 * // 重新设定显示的影像 Bitmap
 * newPhoto2=Bitmap.createBitmap(newPhoto1,0,0,width,height);
 * imageView.setImageBitmap(newPhoto2); //} } } //缩最小就是跟画面几样己 else if(differ<0)
 * { present_width=(int)(0.9*present_width);
 * present_height=(int)(0.9*present_height); Bitmap b=photo; int
 * tmpW=b.getWidth(); int tmpH=b.getHeight(); // 计算X轴基准点缩小后的位置
 * if(pointX-(width/2)>=0) { if(pointX-(width/2)+width<=tmpW) {
 * pointX-=(width/2); } else { pointX=tmpW-width; } } else { pointX=0; } //
 * 计算Y轴基准点缩小后的位置 if(pointY-(height/2)>=0) { if(pointY-(height/2)+height<=tmpH) {
 * pointY-=(height/2); } else { pointY=tmpH-height; } } else { pointY=0; }
 * if(present_width>=width&&present_height>=height){ // 设置图片大小 Bitmap
 * newPhoto1=Bitmap.createScaledBitmap(photo,present_width,present_height,true);
 * //重新设定显示的影像 Bitmap newPhoto2=Bitmap.createBitmap(newPhoto1,0,0,width,height);
 * imageView.setImageBitmap(newPhoto2); } } distance=distance1; } else{
 * x1=event.getX(p1); y1=event.getY(p1); x2=event.getX(p2); y2=event.getY(p2);
 * float x=x2-x1; float y=y2-y1; distance=Math.sqrt(Math.pow(x, 2)+Math.pow(y,
 * 2)); } }
 * 
 * else if(event.getPointerCount()==2){ int p1=event.findPointerIndex(0); int
 * p2=event.findPointerIndex(1); x1=event.getX(p1); y1=event.getY(p1);
 * x2=event.getX(p2); y2=event.getY(p2); float x=x2-x1; float y=y2-y1; int
 * distance1=(int)Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2)); }
 * 
 * else if(event.getPointerCount()!=2){ x1=0; y1=0; distance=0;
 * System.out.println(present_width+"    "+present_height); if
 * (detector.onTouchEvent(event)) { return detector.onTouchEvent(event); } else
 * { return super.onTouchEvent(event); } } return super.onTouchEvent(event); }
 */

