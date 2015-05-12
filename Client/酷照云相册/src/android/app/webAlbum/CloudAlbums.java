package android.app.webAlbum;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.app.connect.DImage;
import android.app.connect.GetImageInfo;
import android.app.myCamera.R;
import android.app.util.SetIP;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class CloudAlbums extends Activity {

	private String filename, uemail, uname;

	private Bitmap[] downimage; // 显示在网格上的图片数组

	GridView gridview; // 网格像是对象

	private TextView tv_fname; // 界面的副标题，提示用户的昵称，文件夹名
	private String imagenum; // 向服务器请求返回的字符串，成功为图片个数，不成功则出错
	private int num = 0; // 由服务器请求转化的图片数目
	private String imagename; // 向服务器请求图片名称返回的字符串
	private String[] igname; // 每个图片的名称
	private String[] url; // 每个图片的url路径，用于下载
	private boolean isexist[];// 图片在缓存中是否存在

	// 用户进入获得网络相册文件夹中相片的数目，以及各个图片的名称的缓冲对话框
	private ProgressDialog pd_getimage;

	private ProgressDialog pd_noimage; // 删除图片的缓冲对话框
	private String di_result; // 删除图片访问服务器返回的消息结果

	private String dimagename; // 被删除图片的名称
	private int position; // 被删除图片的ID号

	private LoginStatus lss; // 获取在线用户的帐号和昵称的对象

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.cloud);

		// 从相册文件夹列表获取到用户的帐号，昵称以及进入的网络相册文件夹的名称
		Intent intent = getIntent();
		filename = intent.getStringExtra("filename");
		// 从配置文件中获得用户登录的帐号和昵称
		lss = new LoginStatus(CloudAlbums.this);
		uemail = lss.getValue_email();
		uname = lss.getValue_name();

		// 初始化相册副标题并给文本赋值
		tv_fname = (TextView) findViewById(R.id.tv_fname);
		tv_fname.setText(uname + "\t" + filename);

		// 进入缓冲，获得用户进入的网络相册文件夹中相片的数目，以及各个图片的名称
		pd_getimage = new ProgressDialog(CloudAlbums.this);
		String meg_getnum = getString(R.string.cloud_getnum).toString();
		pd_getimage.setMessage(meg_getnum);
		pd_getimage.show();
		Thread thread = new Thread(new GetImage());
		thread.start();

	}

	// 用户网络连接正常，用户初始化
	private void doGrid() {
		// 获取GridView对象
		gridview = (GridView) findViewById(R.id.gridview);
		// 添加元素给Gridview
		gridview.setAdapter(new ImageAdapter(this));
		// 事件监听
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 此处的position为ID 所以要+1
				Toast.makeText(CloudAlbums.this,
						"你选择了" + (position + 1) + "号图片", Toast.LENGTH_SHORT)
						.show();
				CreateDialog(igname[position], downimage[position]);
			}
		});
		gridview
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						menu.setHeaderTitle("选择对此图片操作");
						menu.add(0, 0, 0, "删除此图片");
						menu.add(0, 1, 0, "下载此图片");
					}
				});
	}

	// 长按菜单响应函数

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int pos = menuInfo.position;
		switch (item.getItemId()) {
		case 0:
			dimagename = igname[pos];
			position = pos;
			// 把删除网络相片请求发送给服务器
			pd_noimage = new ProgressDialog(CloudAlbums.this);
			// String meg_getfile =
			// getString(R.string.webfl_loading).toString();
			String meg_getfile = "删除中.....";
			pd_noimage.setMessage(meg_getfile);
			pd_noimage.show();
			Thread thread = new Thread(new NoImage());
			thread.start();

			break;
		case 1:
			if (!isExist(igname[pos], "download"))
				SaveImage(downimage[pos], pos, "download");
			Toast toast = Toast.makeText(getApplicationContext(),
					"下载成功，地址/sdcard/MyCamera/download" + igname[pos],
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			break;
		}
		return super.onContextItemSelected(item);
	}

	// 得到次文件夹下的图片数目和名称的消息处理对象
	private Handler hl_getimage = new Handler() {
		public void handleMessage(Message msg) {
			pd_getimage.dismiss();
			if (imagenum.equals("link error") || imagenum.equals("error")
					|| imagename.equals("link error")
					|| imagename.equals("error")) {
				String error = getString(R.string.web_error).toString();
				Toast toast = Toast.makeText(getApplicationContext(), error
						+ imagenum, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			} else {
				Toast toast = Toast.makeText(getApplicationContext(), imagenum,
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();

				try {
					num = Integer.parseInt(imagenum);
				} catch (Exception e) {
					e.printStackTrace();
					num = 0;
				}
				igname = StoFile(imagename);
				downimage = new Bitmap[num];
				url = new String[num];
				isexist = new boolean[num];
				fileSize = new int[num];
				downloadSize = new int[num];
				for (int i = 0; i < num; i++) {
					downimage[i] = BitmapFactory.decodeResource(getResources(),
							R.drawable.jindu0);
					url[i] = SetIP.getIP() + "Albums/" + uemail + "/"
							+ URLEncoder
							.encode(filename) + "/" + URLEncoder
							.encode(igname[i]);
					System.out.println(url[i]);
					if (isExist(igname[i], "cache")) {
						isexist[i] = true;
					}
				}
				doGrid();
				Thread thread = new Thread(new Loading());
				thread.start();
				return;
			}

		}
	};

	// 删除图片的消息处理对象
	private Handler hl_noimage = new Handler() {
		public void handleMessage(Message msg) {
			if (di_result.equals("link error") || di_result.equals("error")) {
				pd_noimage.dismiss();
				String show6 = getString(R.string.web_error).toString();
				Toast toast = Toast.makeText(getApplicationContext(), show6
						+ "删除失败", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			} else if (di_result.equals("dimage error")
					|| di_result.equals("dimage wrong")) {
				Toast toast = Toast.makeText(getApplicationContext(), "删除失败",
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			} else {
				Toast toast = Toast.makeText(getApplicationContext(), "删除成功",
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				updateInit(position);
				gridview.setAdapter(new ImageAdapter(getApplicationContext()));
				pd_noimage.dismiss();
			}
		}
	};

	// 在加载图片的过程中用于更新GridView的图片的显示内容
	private Handler hl_update = new Handler() {
		public void handleMessage(Message msg) {
			int jindu = msg.arg1;
			int n = msg.arg2;
			System.out.println(jindu);
			switch (jindu) {
			case 20:
				downimage[n] = BitmapFactory.decodeResource(getResources(),
						R.drawable.jindu1);
				break;
			case 40:
				downimage[n] = BitmapFactory.decodeResource(getResources(),
						R.drawable.jindu2);
				break;
			case 60:
				downimage[n] = BitmapFactory.decodeResource(getResources(),
						R.drawable.jindu3);
				break;
			case 80:
				downimage[n] = BitmapFactory.decodeResource(getResources(),
						R.drawable.jindu4);
				break;
			case 100:
				Bitmap bitmap = (Bitmap) msg.obj;
				downimage[n] = bitmap;
				if (!isexist[n]) {
					SaveImage(bitmap, n, "cache");
					System.out.println("save");
				}
				break;
			}
			gridview.setAdapter(new ImageAdapter(getApplicationContext()));
		}
	};

	// 用户获得文件夹下的图片数目以及图片名的进程
	private class GetImage implements Runnable {
		public void run() {
			GetImageInfo getimagenum = new GetImageInfo(uemail, filename,
					"imagenum");
			imagenum = getimagenum.GetImageInfos();
			GetImageInfo getimagename = new GetImageInfo(uemail, filename,
					"imagename");
			imagename = getimagename.GetImageInfos();
			hl_getimage.sendEmptyMessage(0);
		}
	}

	// 加载每个网络图片的子进程
	private class Loading implements Runnable {
		public void run() {
			for (int i = 0; i < num; i++)
				LoadImage(url[i], i);
		}
	}

	// 删除图片的子进程
	private class NoImage implements Runnable {
		public void run() {
			DImage dimage = new DImage(uemail, filename, dimagename, "noimage");
			di_result = dimage.DeleteImage();
			hl_noimage.sendEmptyMessage(0);
		}
	}

	// GridView显示图片的适配器
	private class ImageAdapter extends BaseAdapter {

		// 定义Context
		private Context imgContext;

		public ImageAdapter(Context c) {
			imgContext = c;
		}

		// 获取图片个数
		public int getCount() {
			return downimage.length;
		}

		// 获取图片在库中的位置
		public Object getItem(int position) {
			return position;
		}

		// 获取图片ID
		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) {
				// 给ImageView设置资源
				imageView = new ImageView(imgContext);
				// 设置布局 图片120*120显示
				imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
				// 设置显示比例类型
				imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			} else {
				imageView = (ImageView) convertView;
			}
			// imageView.setImageResource(mImageIds[position]);
			Matrix matrix = new Matrix();
			matrix.postScale(0.2f, 0.2f);
			Bitmap bitmap = Bitmap.createBitmap(downimage[position], 0, 0,
					downimage[position].getWidth(), downimage[position]
							.getHeight(), matrix, true);

			imageView.setImageBitmap(bitmap);
			return imageView;
		}
	}

	int fileSize[]; // 当前下载的文件一共的大小
	int downloadSize[]; // 文件下载的大小

	public void LoadImage(String url, int n) {
		if (isexist[n]) {
			Message msg = hl_update.obtainMessage();
			msg.arg1 = 100;
			msg.arg2 = n;
			msg.obj = BitmapFactory.decodeFile("/sdcard/MyCamera/cache/"
					+ igname[n]);
			System.out.println("no down");
			hl_update.sendMessage(msg);
			return;
		}
		Bitmap downbitmap = null;
		byte[] imageBuffer = (byte[]) null;
		BufferedInputStream bfis = null;
		ByteArrayOutputStream baos = null;
		try {
			URL u = new URL(url);
			URLConnection conn = u.openConnection();
			bfis = new BufferedInputStream(conn.getInputStream());
			conn.connect();
			fileSize[n] = conn.getContentLength();
			baos = new ByteArrayOutputStream();
			int len = -1;
			if (fileSize[n] < 1 || bfis == null) {

			} else {
				int res = 0;
				while ((len = bfis.read()) != -1) {
					baos.write(len);
					downloadSize[n] += len;
					res = downloadSize[n] * 100 / fileSize[n];
					if (res == 20 || res == 40 || res == 60 || res == 80) {
						Message msg = hl_update.obtainMessage();
						msg.arg1 = res;
						msg.arg2 = n;
						hl_update.sendMessage(msg);
					}
				}
				imageBuffer = baos.toByteArray();
				downbitmap = BitmapFactory.decodeByteArray(imageBuffer, 0,
						imageBuffer.length);
				imageBuffer = null;
				Message msg = hl_update.obtainMessage();
				res = 100;
				msg.arg1 = res;
				msg.arg2 = n;
				msg.obj = downbitmap;
				hl_update.sendMessage(msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void SaveImage(Bitmap bitmap, int n, String choose) {
		/* 创建新文件 */
		try {
			File dir = new File("/sdcard/MyCamera");
			if (!dir.exists())
				dir.mkdir();
			File chsave = new File("/sdcard/MyCamera/" + choose);
			if (!chsave.exists())
				chsave.mkdir();
			String savename = "/sdcard/MyCamera/" + choose + "/";
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(savename + igname[n]));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos); /* 采用压缩转档方法 */
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 创建对话框，并添加处理和监听
	private ImageView iv_image;

	public void CreateDialog(String filename, Bitmap bitmap) {
		Builder dialog = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.imagedialog, null);
		dialog.setView(layout);
		dialog.setTitle(filename);
		iv_image = (ImageView) layout.findViewById(R.id.iv_image);
		System.out.println(iv_image);
		iv_image.setImageBitmap(bitmap);
		dialog.show();
	}

	// 用于删除一个图片更新数据
	private void updateInit(int position) {
		num = num - 1;
		String[] temp_igname = new String[num];
		Bitmap[] temp_downimage = new Bitmap[num];
		for (int i = 0; i < position; i++) {
			temp_igname[i] = igname[i];
			temp_downimage[i] = downimage[i];
		}
		for (int i = position + 1; i <= num; i++) {
			temp_igname[i - 1] = igname[i];
			temp_downimage[i - 1] = downimage[i];
		}
		igname = null;
		igname = temp_igname;
		downimage = null;
		downimage = temp_downimage;
	}

	// 把从服务器得到的字符串序列转化为图片文件名数组
	private String[] StoFile(String otherfile) {
		return otherfile.split("\\?");
	}

	// 判断图片是否存在，cache文件夹或者download文件夹
	private boolean isExist(String filename, String choose) {

		File dirff = new File("/sdcard/MyCamera");
		if (!dirff.exists())
			dirff.mkdir();
		String filepath = "/sdcard/MyCamera/" + choose;
		File myPath = new File(filepath);
		if (!myPath.exists())
			myPath.mkdir();
		File dir = new File(filepath);
		File[] files = dir.listFiles();
		if (files == null)
			return false;
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if ((file.getName()).equals(filename))
				return true;
		}
		return false;
	}

}