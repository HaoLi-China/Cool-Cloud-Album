package android.app.webAlbum;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.app.connect.CDFile;
import android.app.connect.GetImageInfo;
import android.app.myCamera.R;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class WebFileList extends Activity {

	private String[] nocgfile = { "����", "����", "����" };// Ĭ�ϵ���������µ��ļ���
	private String[] cgfile = null; // �û������������ļ������½����ļ���

	private String uemail; // �����û����ʺ�
	private String uname; // �����û����ǳ�

	private TextView tv_name; // ��ʾ��¼�û����ǳ�

	private String filenum = null; // �����õ���������µ��ļ��е���Ŀ
	private String otherfile = null;// �����õ�����������û��Զ����ļ��е����ƴ�

	private int allnum; // ��������µ��ļ��еĸ���
	private int nocgnum = 3;// Ĭ�ϵ���������µ��ļ���
	private int cgnum;// �û��Լ��½�������ļ�����Ŀ

	// ��ȡ��������µ��ļ��е���Ŀ�Լ��û��Զ�����������ļ��еĻ���Ի���
	private ProgressDialog pd_getfile;
	private ProgressDialog pd_nofile; // ɾ���û��Զ�����������ļ��еĻ���Ի���

	private String nofilename; // �û�ɾ�����ļ��е��ļ�

	private int position; // �û�ɾ���Զ�����������ļ��е�ID��
	private String d_result; // �û�ɾ���Զ�����������ļ��дӷ��������ص���Ϣ

	private LoginStatus lss; // ��ȡ�����û����ʺź��ǳƵĶ���,�������û��Զ����ļ���

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.weblist);

		// �������ļ��л���û���¼���ʺź��ǳ�
		lss = new LoginStatus(WebFileList.this);
		uemail = lss.getValue_email();
		uname = lss.getValue_name();

		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_name.setText(uname);

		// �����ȡ��������µ��ļ��е���Ŀ���û��Զ����ļ��е�����
		pd_getfile = new ProgressDialog(WebFileList.this);
		String meg_getfile = getString(R.string.webfl_loading).toString();
		pd_getfile.setMessage(meg_getfile);
		pd_getfile.show();
		Thread thread = new Thread(new GetFile());
		thread.start();
	}

	/*
	 * ������ϵͳĬ�ϵ���������ļ����б��Զ����ɣ����õȴ�����
	 */
	private ListView nocg_filelist; // �ļ���list
	private SimpleAdapter nocg_listItemAdapter;
	private ArrayList<HashMap<String, Object>> nocg_listItem;
	private TextView tv_nocg; // ���ļ���ͷ

	private void donocgList() {
		tv_nocg = (TextView) findViewById(R.id.tv_nocg);
		tv_nocg.setVisibility(1);
		nocg_filelist = (ListView) findViewById(R.id.lv_nocgfile);
		// ���ɶ�̬���飬��������
		nocg_listItem = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < nocgnum; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			switch (i) {
			case 0:
				map.put("ItemImage", R.drawable.life);// ͼ����Դ��ID
				break;
			case 1:
				map.put("ItemImage", R.drawable.tourism);// ͼ����Դ��ID
				break;
			case 2:
				map.put("ItemImage", R.drawable.other);// ͼ����Դ��ID
				break;
			}
			map.put("ItemText", nocgfile[i]);
			nocg_listItem.add(map);
		}
		// ������������Item�Ͷ�̬�����Ӧ��Ԫ��
		nocg_listItemAdapter = new SimpleAdapter(this, nocg_listItem,// ����Դ
				R.layout.listitems,// ListItem��XMLʵ��
				// ��̬������ImageItem��Ӧ������
				new String[] { "ItemImage", "ItemText" },
				// ImageItem��XML�ļ������һ��ImageView,����TextView ID
				new int[] { R.id.ItemImage, R.id.ItemText });

		// ��Ӳ�����ʾ
		nocg_filelist.setAdapter(nocg_listItemAdapter);
		// ��ӵ��
		nocg_filelist.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(WebFileList.this, CloudAlbums.class);
				intent.putExtra("filename", nocgfile[arg2]);
				System.out.println(arg2);
				intent.putExtra("uemail", uemail);
				startActivity(intent);
				setTitle("�����" + arg2 + "����Ŀ");
			}
		});
	}

	/*
	 * �������û��Զ������������ļ����б��ȴ����������ؼ���
	 */
	private ListView cg_filelist;
	private SimpleAdapter cg_listItemAdapter;
	private ArrayList<HashMap<String, Object>> cg_listItem;
	private Button add;
	private TextView tv_cg;

	private void docgList() {
		tv_cg = (TextView) findViewById(R.id.tv_cg);
		tv_cg.setVisibility(1);
		add = (Button) findViewById(R.id.bt_add);
		add.setVisibility(1);
		add.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				CreateNewDialog();
			}
		});
		cg_filelist = (ListView) findViewById(R.id.lv_cgfile);
		// ���ɶ�̬���飬��������
		cg_listItem = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < cgnum; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", R.drawable.own);// ͼ����Դ��ID
			map.put("ItemText", cgfile[i]);
			cg_listItem.add(map);
		}
		// ������������Item�Ͷ�̬�����Ӧ��Ԫ��
		cg_listItemAdapter = new SimpleAdapter(this, cg_listItem,// ����Դ
				R.layout.listitems,// ListItem��XMLʵ��
				// ��̬������ImageItem��Ӧ������
				new String[] { "ItemImage", "ItemText" },
				// ImageItem��XML�ļ������һ��ImageView,����TextView ID
				new int[] { R.id.ItemImage, R.id.ItemText });

		// ��Ӳ�����ʾ
		cg_filelist.setAdapter(cg_listItemAdapter);
		// ��ӵ��
		cg_filelist.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				goNext(arg2);
			}
		});

		// ��ӳ������
		cg_filelist
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						menu.setHeaderTitle("ѡ�������б����");
						menu.add(0, 0, 0, "��������");
						menu.add(0, 1, 0, "ɾ�������");
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
			goNext(pos);
			break;
		case 1:
			nofilename = cgfile[pos];
			position = pos;
			CreateNoDialog();
			break;
		}
		return super.onContextItemSelected(item);
	}

	// ����Զ����ļ��������һ��Ԫ��
	private void addItem() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.own);// ͼ����Դ��ID
		map.put("ItemText", newfilename);
		cg_listItem.add(map);
		cg_listItemAdapter.notifyDataSetChanged();
	}

	private void addFile() {
		cgnum++;
		String[] temp_file = new String[cgnum];

		for (int i = 0; i < cgnum - 1; i++) {
			temp_file[i] = cgfile[i];
		}
		temp_file[cgnum - 1] = newfilename;
		cgfile = null;
		cgfile = temp_file;
		// �����������û�״̬�����������û��Զ�����ļ�����
		String definefile = "";
		for (int i = 0; i < cgfile.length; i++) {
			definefile = cgfile[i] + "?";
		}
		lss.setValue_filename(definefile);
	}

	private void deleteFile() {
		cgnum--;
		String[] temp_file = new String[cgnum];
		for (int i = 0; i < position; i++) {
			temp_file[i] = cgfile[i];
		}
		for (int i = position + 1; i <= cgnum; i++) {
			temp_file[i - 1] = cgfile[i];
		}
		cgfile = null;
		cgfile = temp_file;
		// �����������û�״̬�����������û��Զ�����ļ�����
		String definefile = "";
		for (int i = 0; i < cgfile.length; i++) {
			definefile = cgfile[i] + "?";
		}
		lss.setValue_filename(definefile);
	}

	// ɾ���Զ����ļ��������һ��Ԫ��
	private void deleteItem(int position) {
		cg_listItem.remove(position);
		cg_listItemAdapter.notifyDataSetChanged();
	}

	// ������һ���������������
	private void goNext(int position) {
		Intent intent = new Intent(WebFileList.this, CloudAlbums.class);
		intent.putExtra("filename", cgfile[position]);
		startActivity(intent);
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
				Toast toast = Toast.makeText(getApplicationContext(), filenum
						+ otherfile + "success", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				// �õ��û���������ļ�����Ŀ
				try {
					allnum = Integer.parseInt(filenum);
				} catch (Exception e) {
					e.printStackTrace();
					allnum = 0;
				}
				cgnum = allnum - nocgnum;
				cgfile = StoFile(otherfile);
				// �����������û�״̬�����������û��Զ�����ļ�����
				String definefile = "";
				for (int i = 0; i < cgfile.length; i++) {
					definefile = definefile + cgfile[i] + "?";
				}
				lss.setValue_filename(definefile + "filename");
				System.out.println(lss.getValue_filename());
				// ��ʾ�����ļ����б�
				// ����ϵͳĬ�ϵ���������ļ���
				donocgList();
				docgList();
				pd_getfile.dismiss();
				return;
			}
		}
	};

	// ���û������Զ������������ļ��к���Ϣ�Ĵ���
	private Handler hl_newfile = new Handler() {
		public void handleMessage(Message msg) {
			if (c_result.equals("link error") || c_result.equals("error")) {
				pd_newfile.dismiss();
				String show6 = getString(R.string.web_error).toString();
				Toast toast = Toast.makeText(getApplicationContext(), show6
						+ "����ʧ��", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			} else if (c_result.equals("create error")
					|| c_result.equals("create wrong")) {
				pd_newfile.dismiss();
				Toast toast = Toast.makeText(getApplicationContext(), "����ʧ��",
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			} else {
				Toast toast = Toast.makeText(getApplicationContext(), "�����ɹ�",
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				addItem();
				addFile();
				pd_newfile.dismiss();
			}
		}
	};

	// ���û�ɾ���Զ������������ļ��к���Ϣ�Ĵ���
	private Handler hl_nofile = new Handler() {
		public void handleMessage(Message msg) {
			if (d_result.equals("link error") || d_result.equals("error")) {
				pd_nofile.dismiss();
				String show6 = getString(R.string.web_error).toString();
				Toast toast = Toast.makeText(getApplicationContext(), show6
						+ "ɾ��ʧ��", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			} else if (d_result.equals("delete error")
					|| d_result.equals("delete wrong")) {
				pd_nofile.dismiss();
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
				deleteItem(position);
				deleteFile();
				pd_nofile.dismiss();
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

	// �û��½���������ļ��жԷ������ύ����
	private class NewFile implements Runnable {
		public void run() {
			CDFile cdfile = new CDFile(uemail, newfilename, "newfile");
			c_result = cdfile.ChangeFile();
			hl_newfile.sendEmptyMessage(0);
		}
	}

	// �û�ɾ���û��Զ�����������ļ��жԷ������ύ����
	private class NoFile implements Runnable {
		public void run() {
			CDFile cdfile = new CDFile(uemail, nofilename, "nofile");
			d_result = cdfile.ChangeFile();
			hl_nofile.sendEmptyMessage(0);
		}
	}

	/*
	 * �������ڴ����û������Զ�����������ļ������ĵ�����
	 */
	private EditText et_newfilename; // �½��ļ�����������
	private String newfilename; // �û�������ļ�����
	private String c_result; // �û����ش���������Ƿ񴴽��ɹ���
	private ProgressDialog pd_newfile;

	// �����Ի��򣬲���Ӵ���ͼ���
	private void CreateNewDialog() {
		Builder dialog = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.newfiledialog, null);
		dialog.setView(layout);
		// dialog.setTitle("�����½�����������ļ���");
		et_newfilename = (EditText) layout.findViewById(R.id.et_filename);

		dialog.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				newfilename = et_newfilename.getText().toString().trim();
				if (!isFile(newfilename)) {
					Toast toast = Toast.makeText(getApplicationContext(),
							"�Բ��������ļ���ʽ���Ϸ�����������", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}
				// �жϴ��ļ����Ƿ񴴽���
				for (int i = 0; i < cgnum; i++) {
					if (newfilename.equals(cgfile[i])) {
						Toast toast = Toast.makeText(getApplicationContext(),
								"�Բ��𣬸�����ļ����Ѿ����ڣ���������", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						return;
					}
				}
				// ���½���������ļ��������͸�������
				pd_newfile = new ProgressDialog(WebFileList.this);
				// String meg_getfile =
				// getString(R.string.webfl_loading).toString();
				String meg_getfile = "�½���.....";
				pd_newfile.setMessage(meg_getfile);
				pd_newfile.show();
				Thread thread = new Thread(new NewFile());
				thread.start();
			}
		});

		dialog.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// dialog.dismiss();
				dialog.cancel();
			}
		});
		dialog.show();
	}

	private void CreateNoDialog() {
		Builder dialog = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.nofiledialog, null);
		dialog.setView(layout);

		dialog.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// ��ɾ���û��Զ�����������ļ��������͸�������
				pd_nofile = new ProgressDialog(WebFileList.this);
				String meg_nofile = "ɾ����.....";
				pd_nofile.setMessage(meg_nofile);
				pd_nofile.show();
				Thread thread = new Thread(new NoFile());
				thread.start();
			}
		});
		dialog.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// dialog.dismiss();
				dialog.cancel();
			}
		});
		dialog.show();

	}

	// �Ѵӷ������õ����ַ�������ת��Ϊ�ļ�����������
	private String[] StoFile(String otherfile) {
		return otherfile.split("\\?");
	}

	// �ж��û��Զ������������ļ����ǲ��Ƿ���Ҫ��
	public boolean isFile(String Filename) {
		if (Filename.getBytes().length < 2 || Filename.getBytes().length > 8) {
			return false;
		}
		for (int i = 0; i < Filename.length(); i++) {
			int n = (int) Filename.charAt(i);
			if (n < 48 || (n > 57 && n < 65) || (n > 90 && n < 97)
					|| (n > 122 && n < 19968) || n > 40622) {
				return false;
			}
		}
		return true;
	}

}
