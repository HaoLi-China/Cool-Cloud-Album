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

	private String uemail;// �������ļ���������û����ʺ�
	private String name;// �������ļ���������û����ǳ�

	private TextView tv_suname;// ��ʾ�����û����ǳ�

	private Button bt_backup, bt_gocloud;// ����ͼƬ���������ᰴť

	private LoginStatus lss; // ��ȡ�����û����ǳƵĶ���

	private String filenum = null; // �����õ���������µ��ļ��е���Ŀ
	private String otherfile = null;// �����õ�����������û��Զ����ļ��е����ƴ�

	// ��ȡ��������µ��ļ��е���Ŀ�Լ��û��Զ�����������ļ��еĻ���Ի���
	private ProgressDialog pd_getfile;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.share);

		lss = new LoginStatus(Share.this);
		name = lss.getValue_name();// �������ļ��õ��û��ǳ�
		uemail = lss.getValue_email();// �������ļ��õ��û��ʺ�

		tv_suname = (TextView) findViewById(R.id.tv_suname);
		tv_suname.setText(name);

		// �����ֻ���ᣬ����껸�������б���
		bt_backup = (Button) findViewById(R.id.bt_backup);
		bt_backup.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Share.this, AlbumDirectory.class);
				startActivity(intent);
			}
		});
		// ������������
		bt_gocloud = (Button) findViewById(R.id.bt_gocloud);
		bt_gocloud.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Share.this, WebFileList.class);
				startActivity(intent);
			}
		});

		// �����ȡ��������µ��ļ��е���Ŀ���û��Զ����ļ��е�����
		pd_getfile = new ProgressDialog(Share.this);
		String meg_getfile = getString(R.string.webfl_loading).toString();
		pd_getfile.setMessage(meg_getfile);
		pd_getfile.show();
		Thread thread = new Thread(new GetFile());
		thread.start();
	}

	// ���û�������������ļ�����Ŀ���û��Լ������ļ��к���Ϣ�Ĵ���
	private Handler hl_getfile = new Handler() {
		public void handleMessage(Message msg) {
			// if��䵱�û��������粻ͨ����ֱ�ӷ��أ�ֻ��ʾ������
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
				lss.setValue_num(num); // �������ļ������������Զ����ļ�����Ŀ
				System.out.println(lss.getValue_num());
				lss.setValue_filename(otherfile);// �������ļ������������Զ����ļ��ĵ��ļ�����
				System.out.println(lss.getValue_filename());
				pd_getfile.dismiss();
				return;
			}
		}
	};

	// ����������ʾ�󣬼������������ļ�����Ŀ���û��Լ������ļ���
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
