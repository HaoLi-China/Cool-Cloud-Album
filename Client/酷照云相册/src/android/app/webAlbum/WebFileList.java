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

	private String[] nocgfile = { "生活", "旅游", "其他" };// 默认的网络相册下的文件夹
	private String[] cgfile = null; // 用户可以在网络文件夹下新建的文件夹

	private String uemail; // 在线用户的帐号
	private String uname; // 在线用户的昵称

	private TextView tv_name; // 显示登录用户的昵称

	private String filenum = null; // 请求获得的网络相册下的文件夹的数目
	private String otherfile = null;// 请求获得的网络相册下用户自定义文件夹的名称串

	private int allnum; // 网络相册下的文件夹的个数
	private int nocgnum = 3;// 默认的网络相册下的文件夹
	private int cgnum;// 用户自己新建的相册文件夹数目

	// 获取网络相册下的文件夹的数目以及用户自定义网络相册文件夹的缓冲对话框
	private ProgressDialog pd_getfile;
	private ProgressDialog pd_nofile; // 删除用户自定义网络相册文件夹的缓冲对话框

	private String nofilename; // 用户删除的文件夹的文件

	private int position; // 用户删除自定义网络相册文件夹的ID号
	private String d_result; // 用户删除自定义网络相册文件夹从服务器返回的消息

	private LoginStatus lss; // 获取在线用户的帐号和昵称的对象,并设置用户自定义文件夹

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.weblist);

		// 从配置文件中获得用户登录的帐号和昵称
		lss = new LoginStatus(WebFileList.this);
		uemail = lss.getValue_email();
		uname = lss.getValue_name();

		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_name.setText(uname);

		// 进入获取网络相册下的文件夹的数目和用户自定义文件夹的名称
		pd_getfile = new ProgressDialog(WebFileList.this);
		String meg_getfile = getString(R.string.webfl_loading).toString();
		pd_getfile.setMessage(meg_getfile);
		pd_getfile.show();
		Thread thread = new Thread(new GetFile());
		thread.start();
	}

	/*
	 * 下面是系统默认的网络相册文件夹列表，自动生成，不用等待加载
	 */
	private ListView nocg_filelist; // 文件夹list
	private SimpleAdapter nocg_listItemAdapter;
	private ArrayList<HashMap<String, Object>> nocg_listItem;
	private TextView tv_nocg; // 此文件夹头

	private void donocgList() {
		tv_nocg = (TextView) findViewById(R.id.tv_nocg);
		tv_nocg.setVisibility(1);
		nocg_filelist = (ListView) findViewById(R.id.lv_nocgfile);
		// 生成动态数组，加入数据
		nocg_listItem = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < nocgnum; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			switch (i) {
			case 0:
				map.put("ItemImage", R.drawable.life);// 图像资源的ID
				break;
			case 1:
				map.put("ItemImage", R.drawable.tourism);// 图像资源的ID
				break;
			case 2:
				map.put("ItemImage", R.drawable.other);// 图像资源的ID
				break;
			}
			map.put("ItemText", nocgfile[i]);
			nocg_listItem.add(map);
		}
		// 生成适配器的Item和动态数组对应的元素
		nocg_listItemAdapter = new SimpleAdapter(this, nocg_listItem,// 数据源
				R.layout.listitems,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "ItemImage", "ItemText" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.ItemImage, R.id.ItemText });

		// 添加并且显示
		nocg_filelist.setAdapter(nocg_listItemAdapter);
		// 添加点击
		nocg_filelist.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(WebFileList.this, CloudAlbums.class);
				intent.putExtra("filename", nocgfile[arg2]);
				System.out.println(arg2);
				intent.putExtra("uemail", uemail);
				startActivity(intent);
				setTitle("点击第" + arg2 + "个项目");
			}
		});
	}

	/*
	 * 下面是用户自定义的网络相册文件夹列表，等待服务器加载加载
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
		// 生成动态数组，加入数据
		cg_listItem = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < cgnum; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", R.drawable.own);// 图像资源的ID
			map.put("ItemText", cgfile[i]);
			cg_listItem.add(map);
		}
		// 生成适配器的Item和动态数组对应的元素
		cg_listItemAdapter = new SimpleAdapter(this, cg_listItem,// 数据源
				R.layout.listitems,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "ItemImage", "ItemText" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.ItemImage, R.id.ItemText });

		// 添加并且显示
		cg_filelist.setAdapter(cg_listItemAdapter);
		// 添加点击
		cg_filelist.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				goNext(arg2);
			}
		});

		// 添加长按点击
		cg_filelist
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						menu.setHeaderTitle("选择对相册列表操作");
						menu.add(0, 0, 0, "进入此相册");
						menu.add(0, 1, 0, "删除此相册");
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

	// 添加自定义文件夹里面的一个元素
	private void addItem() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.own);// 图像资源的ID
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
		// 下面设置在用户状态对象中设置用户自定义的文件名称
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
		// 下面设置在用户状态对象中设置用户自定义的文件名称
		String definefile = "";
		for (int i = 0; i < cgfile.length; i++) {
			definefile = cgfile[i] + "?";
		}
		lss.setValue_filename(definefile);
	}

	// 删除自定义文件夹里面的一个元素
	private void deleteItem(int position) {
		cg_listItem.remove(position);
		cg_listItemAdapter.notifyDataSetChanged();
	}

	// 进入下一个浏览网络相册界面
	private void goNext(int position) {
		Intent intent = new Intent(WebFileList.this, CloudAlbums.class);
		intent.putExtra("filename", cgfile[position]);
		startActivity(intent);
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
				Toast toast = Toast.makeText(getApplicationContext(), filenum
						+ otherfile + "success", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				// 得到用户网络相册文件夹数目
				try {
					allnum = Integer.parseInt(filenum);
				} catch (Exception e) {
					e.printStackTrace();
					allnum = 0;
				}
				cgnum = allnum - nocgnum;
				cgfile = StoFile(otherfile);
				// 下面设置在用户状态对象中设置用户自定义的文件名称
				String definefile = "";
				for (int i = 0; i < cgfile.length; i++) {
					definefile = definefile + cgfile[i] + "?";
				}
				lss.setValue_filename(definefile + "filename");
				System.out.println(lss.getValue_filename());
				// 显示网络文件夹列表
				// 加载系统默认的网络相册文件夹
				donocgList();
				docgList();
				pd_getfile.dismiss();
				return;
			}
		}
	};

	// 对用户创建自定义的网络相册文件夹后消息的处理
	private Handler hl_newfile = new Handler() {
		public void handleMessage(Message msg) {
			if (c_result.equals("link error") || c_result.equals("error")) {
				pd_newfile.dismiss();
				String show6 = getString(R.string.web_error).toString();
				Toast toast = Toast.makeText(getApplicationContext(), show6
						+ "创建失败", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			} else if (c_result.equals("create error")
					|| c_result.equals("create wrong")) {
				pd_newfile.dismiss();
				Toast toast = Toast.makeText(getApplicationContext(), "创建失败",
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			} else {
				Toast toast = Toast.makeText(getApplicationContext(), "创建成功",
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				addItem();
				addFile();
				pd_newfile.dismiss();
			}
		}
	};

	// 对用户删除自定义的网络相册文件夹后消息的处理
	private Handler hl_nofile = new Handler() {
		public void handleMessage(Message msg) {
			if (d_result.equals("link error") || d_result.equals("error")) {
				pd_nofile.dismiss();
				String show6 = getString(R.string.web_error).toString();
				Toast toast = Toast.makeText(getApplicationContext(), show6
						+ "删除失败", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			} else if (d_result.equals("delete error")
					|| d_result.equals("delete wrong")) {
				pd_nofile.dismiss();
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
				deleteItem(position);
				deleteFile();
				pd_nofile.dismiss();
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

	// 用户新建网络相册文件夹对服务器提交请求
	private class NewFile implements Runnable {
		public void run() {
			CDFile cdfile = new CDFile(uemail, newfilename, "newfile");
			c_result = cdfile.ChangeFile();
			hl_newfile.sendEmptyMessage(0);
		}
	}

	// 用户删除用户自定义网络相册文件夹对服务器提交请求
	private class NoFile implements Runnable {
		public void run() {
			CDFile cdfile = new CDFile(uemail, nofilename, "nofile");
			d_result = cdfile.ChangeFile();
			hl_nofile.sendEmptyMessage(0);
		}
	}

	/*
	 * 下面用于创建用户输入自定义网络相册文件夹名的弹出框
	 */
	private EditText et_newfilename; // 新建文件夹输入框对象
	private String newfilename; // 用户输入的文件夹名
	private String c_result; // 用户返回创建结果（是否创建成功）
	private ProgressDialog pd_newfile;

	// 创建对话框，并添加处理和监听
	private void CreateNewDialog() {
		Builder dialog = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.newfiledialog, null);
		dialog.setView(layout);
		// dialog.setTitle("输入新建的网络相册文件名");
		et_newfilename = (EditText) layout.findViewById(R.id.et_filename);

		dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				newfilename = et_newfilename.getText().toString().trim();
				if (!isFile(newfilename)) {
					Toast toast = Toast.makeText(getApplicationContext(),
							"对不起，输入文件格式不合法，重新输入", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}
				// 判断此文件夹是否创建过
				for (int i = 0; i < cgnum; i++) {
					if (newfilename.equals(cgfile[i])) {
						Toast toast = Toast.makeText(getApplicationContext(),
								"对不起，该相册文件夹已经存在，重新输入", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						return;
					}
				}
				// 把新建网络相册文件夹请求发送给服务器
				pd_newfile = new ProgressDialog(WebFileList.this);
				// String meg_getfile =
				// getString(R.string.webfl_loading).toString();
				String meg_getfile = "新建中.....";
				pd_newfile.setMessage(meg_getfile);
				pd_newfile.show();
				Thread thread = new Thread(new NewFile());
				thread.start();
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

	private void CreateNoDialog() {
		Builder dialog = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.nofiledialog, null);
		dialog.setView(layout);

		dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// 把删除用户自定义网络相册文件夹请求发送给服务器
				pd_nofile = new ProgressDialog(WebFileList.this);
				String meg_nofile = "删除中.....";
				pd_nofile.setMessage(meg_nofile);
				pd_nofile.show();
				Thread thread = new Thread(new NoFile());
				thread.start();
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

	// 把从服务器得到的字符串序列转化为文件夹名的数组
	private String[] StoFile(String otherfile) {
		return otherfile.split("\\?");
	}

	// 判断用户自定义的网络相册文件夹是不是符合要求
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
