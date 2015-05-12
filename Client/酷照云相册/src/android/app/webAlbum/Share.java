package android.app.webAlbum;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.album.AlbumDirectory;
import android.app.connect.GetImageInfo;
import android.app.myCamera.R;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Share extends Activity {

	private String uemail;// 从配置文件获得在线用户的帐号
	private String name;// 从配置文件获得在线用户的昵称

	private TextView tv_suname;// 显示在线用户的昵称

	private Button bt_backup, bt_gocloud;// 备份图片和浏览云相册按钮

	private LoginStatus lss; // 获取在线用户的昵称的对象

	private String filenum = null; // 请求获得的网络相册下的文件夹的数目
	private String otherfile = null;// 请求获得的网络相册下用户自定义文件夹的名称串

	// 获取网络相册下的文件夹的数目以及用户自定义网络相册文件夹的缓冲对话框
	private ProgressDialog pd_getfile;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.share);

		lss = new LoginStatus(Share.this);
		name = lss.getValue_name();// 从配置文件得到用户昵称
		uemail = lss.getValue_email();// 从配置文件得到用户帐号

		tv_suname = (TextView) findViewById(R.id.tv_suname);
		tv_suname.setText(name);

		// 备份手机相册，进入昊哥的相册进行备份
		bt_backup = (Button) findViewById(R.id.bt_backup);
		bt_backup.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Share.this, AlbumDirectory.class);
				startActivity(intent);
			}
		});
		// 浏览网络云相册
		bt_gocloud = (Button) findViewById(R.id.bt_gocloud);
		bt_gocloud.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Share.this, WebFileList.class);
				startActivity(intent);
			}
		});

		// 进入获取网络相册下的文件夹的数目和用户自定义文件夹的名称
		pd_getfile = new ProgressDialog(Share.this);
		String meg_getfile = getString(R.string.webfl_loading).toString();
		pd_getfile.setMessage(meg_getfile);
		pd_getfile.show();
		Thread thread = new Thread(new GetFile());
		thread.start();
	}

	// 对用户获得网络相册的文件夹数目和用户自己建的文件夹后消息的处理
	private Handler hl_getfile = new Handler() {
		public void handleMessage(Message msg) {
			// if语句当用户连接网络不通畅，直接返回，只显示主界面
			if (filenum.equals("link error") || filenum.equals("error")
					|| otherfile.equals("link error")
					|| otherfile.equals("error")) {
				pd_getfile.dismiss();
				String show6 = getString(R.string.web_error).toString();
				Toast toast = Toast.makeText(getApplicationContext(), show6
						+ filenum, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			} else {
				int num;
				try {
					num = Integer.parseInt(filenum) - 3;
				} catch (Exception e) {
					e.printStackTrace();
					num = 0;
				}
				lss.setValue_num(num); // 在配置文件中设置用字自定义文件的数目
				System.out.println(lss.getValue_num());
				lss.setValue_filename(otherfile);// 在配置文件中设置用字自定义文件的的文件名称
				System.out.println(lss.getValue_filename());
				pd_getfile.dismiss();
				return;
			}
		}
	};

	// 当主界面显示后，加载网络相册的文件夹数目和用户自己建的文件夹
	private class GetFile implements Runnable {
		public void run() {
			GetImageInfo getfilenum = new GetImageInfo(uemail, null, "filenum");
			filenum = getfilenum.GetFileInfo();
			GetImageInfo getotherfile = new GetImageInfo(uemail, null,
					"otherfile");
			otherfile = getotherfile.GetFileInfo();
			hl_getfile.sendEmptyMessage(0);
		}
	}
}
