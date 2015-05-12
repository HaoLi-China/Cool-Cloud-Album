package android.app.webAlbum;

/*
 * 用户注册的Activity，注册填写用户名，以及两次输入密码
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.connect.ConToTom;
import android.app.myCamera.R;
import android.app.util.FontAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Activity {

	private EditText et_email; // 用户邮箱帐号输入框
	private EditText et_name; // 用户名输入框
	private EditText et_pass; // 第一次密码输入框
	private EditText et_again_pass; // 重复密码输入框
	private Button bt_regs; // 注册点击按钮

	String uemail; // 获得用户的注册邮箱
	String uname; // 获得用户的注册昵称
	String upass; // 获得用户的注册第一次输入的密码
	String aupass; // 获得用户的注册重复输入的密码

	// 下面两个用于注册过程及进入云相册的缓冲对话框
	private ProgressDialog pd_regisering, pd_goshare;

	private String back; // 用户注册时返回的响应信号

	private LoginStatus lss; // 记录存储用户上次信息的对象

	public void onCreate(Bundle savedInstanceState) {

		// 下面设置此页面（活动）无标题栏（全屏）
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.register);
		// 初始化
		lss = new LoginStatus(Register.this);

		// 获得注册邮箱、昵称以及密码输入框对象
		et_email = (EditText) findViewById(R.id.et_new_email);
		et_name = (EditText) findViewById(R.id.et_new_name);
		et_pass = (EditText) findViewById(R.id.et_new_pass);
		et_again_pass = (EditText) findViewById(R.id.et_again_pass);

		// 初始化注册按钮，以及给按钮添加响应
		bt_regs = (Button) findViewById(R.id.bt_regs);
		// 注册按钮监听，单击是响应
		bt_regs.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				/*
				 * 首先从邮箱帐号输入框中获得用户昵邮箱的输入，并判断邮箱的输入是否合法
				 * 如果符合格式的话，则进入下一步，不符合则返回让用户重新输入
				 */
				uemail = et_email.getText().toString().trim();
				if (!isEmail(uemail)) {
					String show1 = getString(R.string.register_show1)
							.toString();
					Toast toast = Toast.makeText(getApplicationContext(),
							show1, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					et_email.requestFocus();
					et_email.setText("");
					et_name.setText("");
					et_pass.setText("");
					et_again_pass.setText("");
					return;
				}
				/*
				 * 从昵称输入框中获得用户昵称的输入，并判断昵称是否符合规范 如果符合格式的话，则进入下一步，不符合则返回让用户重新输入
				 */
				uname = et_name.getText().toString().trim();
				if (!isName(uname)) {
					String show2 = getString(R.string.register_show2)
							.toString();
					Toast toast = Toast.makeText(getApplicationContext(),
							show2, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					et_name.requestFocus();
					et_name.setText("");
					return;
				}
				/*
				 * 从密码输入框中获得第一次密码的输入，并判断这次输入的密码是否符合密码的格式
				 * 如果符合格式的话，则进入下一步，不符合则返回让用户重新输入
				 */
				upass = et_pass.getText().toString().trim();
				if (!isPass(upass)) {
					String show3 = getString(R.string.register_show3)
							.toString();
					Toast toast = Toast.makeText(getApplicationContext(),
							show3, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					et_pass.setText("");
					et_again_pass.setText("");
					et_pass.requestFocus();
					return;
				}
				/*
				 * 从重复密码输入框中获得第二次密码的输入，并判断这次输入的密码是否和上次的一致
				 * 如果一致的话，则进入下一步，不一致则返回让用户重新输入
				 */
				aupass = et_again_pass.getText().toString().trim();
				if (!upass.equals(aupass)) {
					String show4 = getString(R.string.register_show4)
							.toString();
					Toast toast = Toast.makeText(getApplicationContext(),
							show4, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					et_pass.setText("");
					et_again_pass.setText("");
					et_pass.requestFocus();
					return;
				}
				// 如果用户注册信息全部合法正确，则把进入缓冲，并调用响应注册按钮的子进程
				pd_regisering = new ProgressDialog(Register.this);
				String resing = getString(R.string.register_resing).toString();
				pd_regisering.setMessage(resing);
				pd_regisering.show();
				Thread thread = new Thread(new Registering());
				thread.start();

			}
		});

		// 用于根据分辨率来设置字体的大小
		int window_height = getWindowManager().getDefaultDisplay().getHeight();
		int window_width = getWindowManager().getDefaultDisplay().getWidth();
		ViewGroup v = (ViewGroup) findViewById(R.id.register_lay);
		FontAdapter.changeViewSize(v, window_height, window_width);
	}

	// 接收用户注册子进程传来的信息，关闭缓冲界面，并对返回的信息进行处理
	private Handler hl_regisering = new Handler() {
		public void handleMessage(Message msg) {
			pd_regisering.dismiss();
			if (back.equals("user exist")) {
				String show5 = getString(R.string.register_show5).toString();
				Toast toast = Toast.makeText(getApplicationContext(), show5,
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				et_email.setText("");
				et_email.requestFocus();
				return;
			} else if (back.equals("register error")) {
				String show6 = getString(R.string.register_show6).toString();
				Toast toast = Toast.makeText(getApplicationContext(), show6,
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			} else if (back.equals("link error") || back.equals("error")) {
				String show7 = getString(R.string.register_show7).toString();
				Toast toast = Toast.makeText(getApplicationContext(), show7
						+ back, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			} else if (back.equals("register success")) {
				String show8 = getString(R.string.register_show8).toString();
				Toast toast = Toast.makeText(getApplicationContext(), show8,
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();

				// 获取服务器传来的name，以及更改配置文件的登录信息
				upStatus();

				// 进入云相册界面
				// 如果用登录成功，则把进入云相册缓冲，并调用响应云相册子进程
				pd_goshare = new ProgressDialog(Register.this);
				String goshare = getString(R.string.register_goshare)
						.toString();
				pd_goshare.setMessage(goshare);
				pd_goshare.show();
				Thread thread = new Thread(new GoShare());
				thread.start();
			}
		}
	};
	private Handler hl_goshare = new Handler() {
		public void handleMessage(Message msg) {
			pd_goshare.dismiss();
		}
	};

	// 正则表达式判断用户输入的邮箱是否合法
	public boolean isEmail(String uemail) {
		// String format =
		// "\\p{Alpha}\\w{2,15}[@][a-z0-9]{3,}[.]\\p{Lower}{2,}";
		String format = "\\w{2,15}[@][a-z0-9]{2,}[.]\\p{Lower}{2,}";
		boolean result = uemail.matches(format);
		return result;
	}

	// 正则表达式判断用户输入昵称是否合法，
	// 用户昵称有4到10个字符组成，可以是汉字，大小写字母或是数字
	public boolean isName(String name) {
		if (name.getBytes().length < 4 || name.getBytes().length > 10) {
			return false;
		}
		for (int i = 0; i < name.length(); i++) {
			int n = (int) name.charAt(i);
			if (n < 48 || (n > 57 && n < 65) || (n > 90 && n < 97)
					|| (n > 122 && n < 19968) || n > 40622) {
				return false;
			}
		}
		return true;
	}

	// 正则表达式判断用户输入的密码是否是否为6-16位的数字和大小写字母组成
	public boolean isPass(String upass) {
		String format = "\\w{6,16}";
		boolean result = upass.matches(format);
		return result;
	}

	// 用于响应登录按钮的子进程
	private class Registering implements Runnable {
		public void run() {
			ConToTom ctt = new ConToTom(uemail, uname, upass, "register");
			back = ctt.PostToTom();
			System.out.println(back);
			hl_regisering.sendEmptyMessage(0);
		}
	}

	private class GoShare implements Runnable {
		public void run() {
			Intent intent = new Intent(Register.this, Share.class);
			intent.putExtra("uemail", uemail);
			startActivity(intent);
			hl_goshare.sendEmptyMessage(0);
		}
	}

	private void upStatus() {
		lss.setValue_status("1");
		lss.setValue_email(uemail);
		lss.setValue_user(uemail);
		lss.setValue_name(uname);
	}

}
