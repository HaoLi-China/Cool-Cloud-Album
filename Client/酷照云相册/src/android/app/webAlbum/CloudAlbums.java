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

	private Bitmap[] downimage; // ��ʾ�������ϵ�ͼƬ����

	GridView gridview; // �������Ƕ���

	private TextView tv_fname; // ����ĸ����⣬��ʾ�û����ǳƣ��ļ�����
	private String imagenum; // ����������󷵻ص��ַ������ɹ�ΪͼƬ���������ɹ������
	private int num = 0; // �ɷ���������ת����ͼƬ��Ŀ
	private String imagename; // �����������ͼƬ���Ʒ��ص��ַ���
	private String[] igname; // ÿ��ͼƬ������
	private String[] url; // ÿ��ͼƬ��url·������������
	private boolean isexist[];// ͼƬ�ڻ������Ƿ����

	// �û���������������ļ�������Ƭ����Ŀ���Լ�����ͼƬ�����ƵĻ���Ի���
	private ProgressDialog pd_getimage;

	private ProgressDialog pd_noimage; // ɾ��ͼƬ�Ļ���Ի���
	private String di_result; // ɾ��ͼƬ���ʷ��������ص���Ϣ���

	private String dimagename; // ��ɾ��ͼƬ������
	private int position; // ��ɾ��ͼƬ��ID��

	private LoginStatus lss; // ��ȡ�����û����ʺź��ǳƵĶ���

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.cloud);

		// ������ļ����б��ȡ���û����ʺţ��ǳ��Լ��������������ļ��е�����
		Intent intent = getIntent();
		filename = intent.getStringExtra("filename");
		// �������ļ��л���û���¼���ʺź��ǳ�
		lss = new LoginStatus(CloudAlbums.this);
		uemail = lss.getValue_email();
		uname = lss.getValue_name();

		// ��ʼ����ḱ���Ⲣ���ı���ֵ
		tv_fname = (TextView) findViewById(R.id.tv_fname);
		tv_fname.setText(uname + "\t" + filename);

		// ���뻺�壬����û��������������ļ�������Ƭ����Ŀ���Լ�����ͼƬ������
		pd_getimage = new ProgressDialog(CloudAlbums.this);
		String meg_getnum = getString(R.string.cloud_getnum).toString();
		pd_getimage.setMessage(meg_getnum);
		pd_getimage.show();
		Thread thread = new Thread(new GetImage());
		thread.start();

	}

	// �û����������������û���ʼ��
	private void doGrid() {
		// ��ȡGridView����
		gridview = (GridView) findViewById(R.id.gridview);
		// ���Ԫ�ظ�Gridview
		gridview.setAdapter(new ImageAdapter(this));
		// �¼�����
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// �˴���positionΪID ����Ҫ+1
				Toast.makeText(CloudAlbums.this,
						"��ѡ����" + (position + 1) + "��ͼƬ", Toast.LENGTH_SHORT)
						.show();
				CreateDialog(igname[position], downimage[position]);
			}
		});
		gridview
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						menu.setHeaderTitle("ѡ��Դ�ͼƬ����");
						menu.add(0, 0, 0, "ɾ����ͼƬ");
						menu.add(0, 1, 0, "���ش�ͼƬ");
					}
				});
	}

	// �����˵���Ӧ����

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int pos = menuInfo.position;
		switch (item.getItemId()) {
		case 0:
			dimagename = igname[pos];
			position = pos;
			// ��ɾ��������Ƭ�����͸�������
			pd_noimage = new ProgressDialog(CloudAlbums.this);
			// String meg_getfile =
			// getString(R.string.webfl_loading).toString();
			String meg_getfile = "ɾ����.....";
			pd_noimage.setMessage(meg_getfile);
			pd_noimage.show();
			Thread thread = new Thread(new NoImage());
			thread.start();

			break;
		case 1:
			if (!isExist(igname[pos], "download"))
				SaveImage(downimage[pos], pos, "download");
			Toast toast = Toast.makeText(getApplicationContext(),
					"���سɹ�����ַ/sdcard/MyCamera/download" + igname[pos],
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			break;
		}
		return super.onContextItemSelected(item);
	}

	// �õ����ļ����µ�ͼƬ��Ŀ�����Ƶ���Ϣ�������
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

	// ɾ��ͼƬ����Ϣ�������
	private Handler hl_noimage = new Handler() {
		public void handleMessage(Message msg) {
			if (di_result.equals("link error") || di_result.equals("error")) {
				pd_noimage.dismiss();
				String show6 = getString(R.string.web_error).toString();
				Toast toast = Toast.makeText(getApplicationContext(), show6
						+ "ɾ��ʧ��", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			} else if (di_result.equals("dimage error")
					|| di_result.equals("dimage wrong")) {
				Toast toast = Toast.makeText(getApplicationContext(), "ɾ��ʧ��",
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			} else {
				Toast toast = Toast.makeText(getApplicationContext(), "ɾ���ɹ�",
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				updateInit(position);
				gridview.setAdapter(new ImageAdapter(getApplicationContext()));
				pd_noimage.dismiss();
			}
		}
	};

	// �ڼ���ͼƬ�Ĺ��������ڸ���GridView��ͼƬ����ʾ����
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

	// �û�����ļ����µ�ͼƬ��Ŀ�Լ�ͼƬ���Ľ���
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

	// ����ÿ������ͼƬ���ӽ���
	private class Loading implements Runnable {
		public void run() {
			for (int i = 0; i < num; i++)
				LoadImage(url[i], i);
		}
	}

	// ɾ��ͼƬ���ӽ���
	private class NoImage implements Runnable {
		public void run() {
			DImage dimage = new DImage(uemail, filename, dimagename, "noimage");
			di_result = dimage.DeleteImage();
			hl_noimage.sendEmptyMessage(0);
		}
	}

	// GridView��ʾͼƬ��������
	private class ImageAdapter extends BaseAdapter {

		// ����Context
		private Context imgContext;

		public ImageAdapter(Context c) {
			imgContext = c;
		}

		// ��ȡͼƬ����
		public int getCount() {
			return downimage.length;
		}

		// ��ȡͼƬ�ڿ��е�λ��
		public Object getItem(int position) {
			return position;
		}

		// ��ȡͼƬID
		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) {
				// ��ImageView������Դ
				imageView = new ImageView(imgContext);
				// ���ò��� ͼƬ120*120��ʾ
				imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
				// ������ʾ��������
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

	int fileSize[]; // ��ǰ���ص��ļ�һ���Ĵ�С
	int downloadSize[]; // �ļ����صĴ�С

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
		/* �������ļ� */
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
			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos); /* ����ѹ��ת������ */
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// �����Ի��򣬲���Ӵ���ͼ���
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

	// ����ɾ��һ��ͼƬ��������
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

	// �Ѵӷ������õ����ַ�������ת��ΪͼƬ�ļ�������
	private String[] StoFile(String otherfile) {
		return otherfile.split("\\?");
	}

	// �ж�ͼƬ�Ƿ���ڣ�cache�ļ��л���download�ļ���
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