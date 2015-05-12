package android.app.myCamera;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.aboutView.AboutView;
import android.app.album.AlbumDirectory;
import android.app.helpView.TurnTest;
import android.app.util.MyButton;
import android.app.webAlbum.LoginStatus;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

/*
 * 
 * 刚进入软件时的界面
 * 
 * */

public class WelcomView extends Activity implements OnClickListener {
	private final int CAMERA = 0;// 相机
	private final int ALBUM = 1;// 相册
	private final int HELP = 2;// 帮助
	private final int ABOUT = 3;// 关于
	private final int EXIT = 4;// 退出
	private final int sleep = 250;// 背景动画每帧之间睡眠时间

	private AlertDialog menuDialog;// menu菜单Dialog

	private GridView menuGrid;

	private ImageView background;
	private View menuView;

	private Button logo_button;// logo按钮

	private Context context;

	// private int state = 0;// 目前菜单状态 "0"表示出现"1"表示消失

	private boolean flag = true;

	// 菜单图片
	private int[] menu_image_array = { R.drawable.camera, R.drawable.album,
			R.drawable.help, R.drawable.about, R.drawable.exit };
	// 菜单文字
	private String[] menu_name_array = { "相机", "相册", "帮助", "关于", "退出" };

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 无title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 设置屏幕恒为竖向
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
		setContentView(R.layout.main);

		context = this;

		init();// 初始化控件
	}

	/*---------------------初始化控件-------------------*/
	private void init() {
		// 按钮图片数组
		Integer[] logo = { R.drawable.logo1, R.drawable.logo2, R.drawable.logo2 };

		// 背景
		background = (ImageView) findViewById(R.id.background);

		logo_button = (Button) findViewById(R.id.logo_button);
		MyButton myLogo = new MyButton(this);
		logo_button.setBackgroundDrawable(myLogo.setbg(logo));
		logo_button.setOnClickListener(this);
		logo_button.setEnabled(false);

		menuView = View.inflate(this, R.layout.gridview_menu, null);

		// 创建AlertDialog
		menuDialog = new AlertDialog.Builder(this).create();
		menuDialog.setView(menuView);
		menuDialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_MENU)// 监听按键
					dialog.dismiss();
				return false;
			}
		});

		menuGrid = (GridView) menuView.findViewById(R.id.gridview);
		menuGrid.setAdapter(getMenuAdapter(menu_name_array, menu_image_array));
		// 监听menu选项
		menuGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case CAMERA:// 相机
					Intent intent1 = new Intent();
					intent1.setClass(WelcomView.this, CameraView.class);
					startActivity(intent1);
					break;
				case ALBUM:// 相册
					Intent intent2 = new Intent();
					intent2.setClass(WelcomView.this, AlbumDirectory.class);
					startActivity(intent2);
					break;
				case HELP:// 帮助
					Intent intent3 = new Intent();
					intent3.setClass(WelcomView.this, TurnTest.class);
					startActivity(intent3);
					break;
				case ABOUT:// 关于
					Intent intent4 = new Intent();
					intent4.setClass(WelcomView.this, AboutView.class);
					startActivity(intent4);
					break;
				case EXIT:// 退出
					LoginStatus logSta = new LoginStatus(context);
					logSta.setValue_status("0");
					System.exit(0);
					break;
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("menu");// 必须创建一项
		return super.onCreateOptionsMenu(menu);
	}

	private SimpleAdapter getMenuAdapter(String[] menuNameArray,
			int[] imageResourceArray) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < menuNameArray.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", imageResourceArray[i]);
			map.put("itemText", menuNameArray[i]);
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				R.layout.item_menu, new String[] { "itemImage", "itemText" },
				new int[] { R.id.item_image, R.id.item_text });
		return simperAdapter;
	}

	/*----------------进入场景后播放动画----------------------*/
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// 逐帧动画AnimationDrawable对象
		if (flag) {
			AnimationDrawable focus_ani = new AnimationDrawable();
			for (int i = 0; i < 5; i++) {
				int id = getResources().getIdentifier("back" + i, "drawable",
						"android.app.myCamera");
				focus_ani.addFrame(getResources().getDrawable(id), sleep);
			}
			// 设置手否重复播放，true为不重复
			focus_ani.setOneShot(true);
			MyAnimationDrawable mad = new MyAnimationDrawable(focus_ani) {
				@Override
				void onAnimationEnd() {
					// 实现这个方法，结束后会调用
					logo_button.setEnabled(true);
					flag = false;
				}
			};
			background.setBackgroundDrawable(mad);
			mad.start();
		}
	}

	/*-----------------按钮事件监听--------------------*/
	public void onClick(View v) {
		menuDialog.show();
	}

	/*-------------捕获手机的返回键的按下事件---------------*/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			LoginStatus logSta = new LoginStatus(context);
			logSta.setValue_status("0");
			System.exit(0);
		}
		return super.onKeyDown(keyCode, event);
	}
}
