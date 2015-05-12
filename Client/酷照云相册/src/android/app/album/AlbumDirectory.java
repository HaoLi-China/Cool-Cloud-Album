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
 * ����������ʾ��������棬ʵ��ѡ�������Ӧ��ᣬ������ᣬ�����������������ȹ���
 * 
 * */

public class AlbumDirectory extends Activity {

	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

	// Gallery����ͼ ��͸�
	private final int WIDTH = 320;
	private final int HEIGHT = 300;

	private int skinId = 0;// Ƥ��idֵ

	// ��ťͼƬ����
	private Integer[] voice = { R.drawable.voice1, R.drawable.voice2,
			R.drawable.voice2 };
	private Integer[] search = { R.drawable.search1, R.drawable.search2,
			R.drawable.search2 };

	private boolean flag = false;// ��־λ���ж��Ƿ����������

	private ImageView skinView;// ����Ƥ��

	private Button voiceButton;// ������ť
	private Button searchButton;// ������ť
	private Button webAlbumButton;// ������ᰴť

	private EditText et;// ������

	private double y1, y2;// ��ָ������Ļʱǰ������ֵ

	private String album[] = { "Ψ��", "��ܰ", "����", "����", "����", "��Ȼ", "ʱ��", "����" };
	private String skinState = "0";// Ƥ��ģʽ

	private RWSharedperferences rws;// ��д�����ļ�����

	private Context context;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// �ޱ���
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ȫ��
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ������Ļ��Ϊ����
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

		initControl();// ��ʼ���ؼ�
	}

	/*--------------------------��ʼ���ؼ�-------------------*/
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
		if (activities.size() != 0) { // �¼�����
			voiceButton.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					startVoiceRecognitionActivity();
				}
			});
		} else {
			voiceButton.setEnabled(false);
		}

		// �¼�����
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

		Gallery g = (Gallery) findViewById(R.id.mygallery); /* ����һImageAdapter���趨��Gallery���� */
		g.setAdapter(new ImageAdapter(this)); /* �趨һ��itemclickListener��Toast����ѡͼƬ��λ�� */
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

	/*--------------������ָ������Ļ�¼�--------------*/
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
			i.setLayoutParams(new Gallery.LayoutParams(WIDTH, HEIGHT));
			// �趨Gallery����ͼ
			i.setBackgroundResource(mGalleryItemBackground);
			// ����imageView���
			return i;
		}

		// ����һInteger array��ȡ��Ԥ����Drawable��ͼƬid
		private Integer[] myImageIds = { R.drawable.album1, R.drawable.album2,
				R.drawable.album3, R.drawable.album4, R.drawable.album5,
				R.drawable.album6, R.drawable.album7, R.drawable.album8 };
	}

	/*-------------����ʶ��--------------*/
	protected void startVoiceRecognitionActivity() { // bool=false;
		// ͨ��Intent��������ʶ���ģʽ
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		// ����ģʽ��������ʽ������ʶ��
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		// ��ʾ������ʼ
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				"Speech recognition demo");
		// ��ʼִ�����ǵ�Intent������ʶ��
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	@Override
	/*------------����������ʱ�Ļص�����onActivityResult------------*/
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
				&& resultCode == RESULT_OK) {
			// ȡ���������ַ�
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