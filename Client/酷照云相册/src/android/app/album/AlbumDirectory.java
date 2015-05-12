package android.app.album;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.myCamera.R;
import android.app.util.MyButton;
import android.app.util.RWSharedperferences;
import android.app.util.SkinSet;
import android.app.webAlbum.Login;
import android.app.webAlbum.LoginStatus;
import android.app.webAlbum.WebFileList;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/*
 * 
 * 该类用于显示相册主界面，实现选择进入相应相册，搜索相册，换肤，进入网络相册等功能
 * 
 * */

public class AlbumDirectory extends Activity {

	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

	// Gallery背景图 宽和高
	private final int WIDTH = 320;
	private final int HEIGHT = 300;

	private int skinId = 0;// 皮肤id值

	// 按钮图片数组
	private Integer[] voice = { R.drawable.voice1, R.drawable.voice2,
			R.drawable.voice2 };
	private Integer[] search = { R.drawable.search1, R.drawable.search2,
			R.drawable.search2 };

	private boolean flag = false;// 标志位，判断是否搜索到结果

	private ImageView skinView;// 放置皮肤

	private Button voiceButton;// 声音按钮
	private Button searchButton;// 搜索按钮
	private Button webAlbumButton;// 网络相册按钮

	private EditText et;// 搜索框

	private double y1, y2;// 手指划过屏幕时前后坐标值

	private String album[] = { "唯美", "温馨", "欢乐", "浪漫", "城市", "自然", "时尚", "其它" };
	private String skinState = "0";// 皮肤模式

	private RWSharedperferences rws;// 读写配置文件对象

	private Context context;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 设置屏幕恒为竖向
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.album_directory);

		context = this;

		skinView = (ImageView) findViewById(R.id.album_dir_skin);

		rws = new RWSharedperferences();
		skinState = rws.read(this, "skin");
		if (!skinState.contentEquals("")) {
			skinId = Integer.parseInt(skinState);
			new SkinSet(skinView, skinState);
		}

		initControl();// 初始化控件
	}

	/*--------------------------初始化控件-------------------*/
	public void initControl() {
		webAlbumButton = (Button) findViewById(R.id.web_album_button);

		voiceButton = (Button) findViewById(R.id.voice);
		MyButton voiceMyButton = new MyButton(this);
		voiceButton.setBackgroundDrawable(voiceMyButton.setbg(voice));

		searchButton = (Button) findViewById(R.id.search);
		MyButton searchMyButton = new MyButton(this);
		searchButton.setBackgroundDrawable(searchMyButton.setbg(search));

		// EditText
		et = (EditText) findViewById(R.id.edit);

		// Check to see if a recognition activity is present
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() != 0) { // 事件监听
			voiceButton.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					startVoiceRecognitionActivity();
				}
			});
		} else {
			voiceButton.setEnabled(false);
		}

		// 事件监听
		searchButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				String albumName = et.getText().toString();
				for (int i = 0; i < album.length; i++) {
					if (albumName.equals(album[i])) {
						flag = true;
						Intent intent = new Intent();
						intent.setClass(AlbumDirectory.this, AlbumView.class);
						intent.putExtra("position", i + "");
						startActivity(intent);
						break;
					}
				}
				if (!flag) {
					Toast.makeText(
							AlbumDirectory.this,
							getResources().getText(R.string.searcherror)
									.toString(), Toast.LENGTH_LONG).show();
				} else {
					flag = false;
				}
			}
		});

		webAlbumButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				LoginStatus logSta = new LoginStatus(context);
				if (logSta.getValue_status().equals("0")) {
					Intent intent3 = new Intent();
					intent3.setClass(AlbumDirectory.this, Login.class);
					startActivity(intent3);
				} else {
					Intent intent3 = new Intent();
					intent3.setClass(AlbumDirectory.this, WebFileList.class);
					startActivity(intent3);
				}
			}
		});

		Gallery g = (Gallery) findViewById(R.id.mygallery); /* 新增一ImageAdapter并设定给Gallery对象 */
		g.setAdapter(new ImageAdapter(this)); /* 设定一个itemclickListener并Toast被点选图片的位置 */
		g.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			public void onItemClick(AdapterView parent, View v, int position,
					long id) {
				Intent intent = new Intent();
				intent.setClass(AlbumDirectory.this, AlbumView.class);
				intent.putExtra("position", position + "");
				startActivity(intent);
			}
		});
	}

	/*--------------监听手指滑动屏幕事件--------------*/
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			y1 = event.getY();
		case MotionEvent.ACTION_UP:
			y2 = event.getY();
		}

		if (y1 - y2 > 100) {
			if (skinId < 3) {
				skinId++;
			} else {
				skinId = 0;
			}
			rws.write(this, "skin", skinId + "");
			new SkinSet(skinView, skinId + "");
		}
		return true;
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
			i.setLayoutParams(new Gallery.LayoutParams(WIDTH, HEIGHT));
			// 设定Gallery背景图
			i.setBackgroundResource(mGalleryItemBackground);
			// 传回imageView物件
			return i;
		}

		// 建构一Integer array并取得预加载Drawable的图片id
		private Integer[] myImageIds = { R.drawable.album1, R.drawable.album2,
				R.drawable.album3, R.drawable.album4, R.drawable.album5,
				R.drawable.album6, R.drawable.album7, R.drawable.album8 };
	}

	/*-------------语音识别--------------*/
	protected void startVoiceRecognitionActivity() { // bool=false;
		// 通过Intent传递语音识别的模式
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		// 语言模式和自由形式的语音识别
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		// 提示语音开始
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				"Speech recognition demo");
		// 开始执行我们的Intent、语音识别
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	@Override
	/*------------当语音结束时的回调函数onActivityResult------------*/
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
				&& resultCode == RESULT_OK) {
			// 取得语音的字符
			ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			for (int i = 0; i < matches.size(); i++) {
				for (int j = 0; j < album.length; j++) {
					if (matches.get(i).equals(album[j])) {
						flag = true;
						Intent intent = new Intent();
						intent.setClass(AlbumDirectory.this, AlbumView.class);
						intent.putExtra("position", j + "");
						startActivity(intent);
						break;
					}
				}
				if (flag) {
					break;
				}
			}
		}
		if (!flag) {
			Toast.makeText(AlbumDirectory.this,
					getResources().getText(R.string.searcherror).toString(),
					Toast.LENGTH_LONG).show();
		} else {
			flag = false;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}