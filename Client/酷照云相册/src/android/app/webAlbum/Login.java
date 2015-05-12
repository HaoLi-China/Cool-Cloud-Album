/*还没有实现完全
 * 例如记住密码，自动登录
 * 
 */

package android.app.webAlbum;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.connect.ConToTom;
import android.app.myCamera.R;
import android.app.util.FontAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity {

	private EditText et_email; // 用户登录邮箱输入框
	private EditText et_pass; // 用户登录密码输入框

	private Button bt_login; // 用户登录按钮

	private CheckBox cb_rember; // 复选框，用户记住密码
	private CheckBox cb_auto; // 复选框，用户自动登录

	private String uemail; // 用户登录邮箱名
	private String upass; // 用户登录的密码

	private ProgressDialog pd_loging;// 用户登录验证响应的缓冲对话框
	private ProgressDialog pd_register; // 用户点击注册链接，进入注册界面的缓冲对话框
	private ProgressDialog pd_autoing; // 用户上次选择自动登录，下次进入界面后自动提交验证的缓冲对话框
	private ProgressDialog pd_goshare; // 用户验证成功进入相册分享界面的缓冲对话框

	private TextView regiter_link;// 注册链接

	private String log_back; // 用户点击登录按钮时返回的响应信号
	private String auto_back;// 用户自动登录时返回的响应信号

	private String name;// 用户登录成功后从服务器获得的用户昵称

	private LoginStatus lss; // 记录存储用户上次信息的对象

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// 下面设置此页面（活动）无标题栏（全屏）
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.login);

		// 获得邮箱和密码输入框对象
		et_email = (EditText) findViewById(R.id.et_email);
		et_pass = (EditText) findViewById(R.id.et_pass);

		/*
		 * 从上面文件中取出键值对，判断上次用户选择的状态，来进行不同的响应 1.auto值为0则自动登录为选择，为1则选择了自动登录
		 * 2.rember值为0则记住密码为选择，为1则选择了记住密码 3.如果都为0，则只是记住上次登录成功后保存过的邮箱帐号
		 */
		lss = new LoginStatus(Login.this);
		String lastRember = lss.getValue_rember();
		String lastAuto = lss.getValue_auto();
		String lastEmail = lss.getValue_email();
		uemail = lastEmail;
		et_email.setText(lastEmail);
		if (lastRember.equals("1") || lastAuto.equals("1")) {
			String lastPass = lss.getValue_pass();
			et_pass.setText(lastPass);
			upass = lastPass;
		}
		if (lastAuto.equals("1")) {
			// 登录过程中的缓冲，"自动登录中......"
			pd_autoing = new ProgressDialog(Login.this);
			String meg_autoing = getString(R.string.login_autoing).toString();
			pd_autoing.setMessage(meg_autoing);
			pd_autoing.show();
			Thread thread = new Thread(new Autoing());
			thread.start();
		}

		// 获得登录按钮对象
		bt_login = (Button) findViewById(R.id.bt_go);
		bt_login.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {

				// 从输入框获得用户登录邮箱名和密码
				uemail = et_email.getText().toString().trim();
				upass = et_pass.getText().toString().trim();

				// 下面一系列验证用户输入的用户名和密码是否合法
				// 验证用户输入的邮箱是否为合法邮箱
				if (!isEmail(uemail)) {
					String show1 = getString(R.string.login_show1).toString();
					Toast toast = Toast.makeText(getApplicationContext(),
							show1, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					et_email.setText("");
					et_pass.setText("");
					et_email.requestFocus();
					return;
				}

				// 验证用户输入的密码是否为6-16位的数字和大小写字母组成
				if (!isPass(upass)) {
					String show2 = getString(R.string.login_show2).toString();
					Toast toast = Toast.makeText(getApplicationContext(),
							show2, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					et_pass.setText("");
					et_pass.requestFocus();
					return;
				}

				// 登录过程中的缓冲，"登录中......"
				pd_loging = new ProgressDialog(Login.this);
				String meg_log = getString(R.string.login_loging).toString();
				pd_loging.setMessage(meg_log);
				pd_loging.show();
				Thread thread = new Thread(new Loging());
				thread.start();
			}
		});

		// 记住密码复选框，当点击时会把"userinfo.properties"文件中的rember的值设为1，
		// 用于后面的用户正确登录后把用户名和密码存入"userinfo.properties"文件中
		cb_rember = (CheckBox) findViewById(R.id.cb_rember);
		if (lastRember.equals("1")) {
			cb_rember.setChecked(true);
		}
		cb_rember
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							lss.setValue_rember("1");
						} else {
							lss.setValue_rember("0");
						}
					}
				});
		// 记住密码复选框，当点击时会把"userinfo.properties"文件中的auto的值设为1，
		// 用于后面的用户登录时把自动登录发送给服务器，传出来一个cookies值
		cb_auto = (CheckBox) findViewById(R.id.cb_auto);
		if (lastAuto.equals("1")) {
			cb_auto.setChecked(true);
		}
		cb_auto
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							lss.setValue_auto("1");
						} else {
							lss.setValue_auto("0");
						}
					}
				});

		// 获得textview的注册链接，并给链接加上监听
		regiter_link = (TextView) findViewById(R.id.tv_link);
		String text = getString(R.string.login_link).toString();
		SpannableString spannableString = new SpannableString(text);
		spannableString.setSpan(new ClickableSpan() {
			// 在onClick方法中可以编写单击链接时及注册按钮要执行的动作
			public void onClick(View widget) {
				pd_register = new ProgressDialog(Login.this);
				String meg_next = getString(R.string.login_nextact).toString();
				pd_register.setMessage(meg_next);
				pd_register.show();
				Thread thread = new Thread(new NextAct());
				thread.start();
			}
		}, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 使用SpannableString对象设置TextView控件的内容
		regiter_link.setText(spannableString);
		// 在单击链接时凡是有要执行的动作，都必须设置MovementMethod对象
		regiter_link.setMovementMethod(LinkMovementMethod.getInstance());

		// 用于根据分辨率来设置字体的大小
		int window_height = getWindowManager().getDefaultDisplay().getHeight();
		int window_width = getWindowManager().getDefaultDisplay().getWidth();
		ViewGroup v = (ViewGroup) findViewById(R.id.login_lay);
		FontAdapter.changeViewSize(v, window_height, window_width);
	}

	// 接收用户登录子进程传来的信息，关闭缓冲界面，并对返回的信息进行处理
	private Handler hl_loging = new Handler() {
		public void handleMessage(Message msg) {
			pd_loging.dismiss();
			if (log_back.equals("no user")) {
				String show3 = getString(R.string.login_show3).toString();
				Toast toast = Toast.makeText(getApplicationContext(), show3,
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				et_email.setText("");
				et_pass.setText("");
				et_email.requestFocus();
				return;
			} else if (log_back.equals("pass wrong")) {
				String show4 = getString(R.string.login_show4).toString();
				Toast toast = Toast.makeText(getApplicationContext(), show4,
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				et_pass.setText("");
				et_pass.requestFocus();
				return;
			} else if (log_back.equals("login error")) {
				String show5 = getString(R.string.login_show5).toString();
				Toast toast = Toast.makeText(getApplicationContext(), show5,
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			} else if (log_back.equals("link error")
					|| log_back.equals("error")) {
				String show6 = getString(R.string.login_show6).toString();
				Toast toast = Toast.makeText(getApplicationContext(), show6,
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			} else if (log_back.startsWith("login success")) {

				// 当识别用户已经点击记住密码按钮，就把此用户的帐号和密码保存起来
				String isRember = lss.getValue_rember();
				String isAuto = lss.getValue_auto();
				lss.setValue_email(uemail);
				if (isRember.equals("1") || isAuto.equals("1")) {
					lss.setValue_pass(upass);
				}
				// 显示登录成功
				String show7 = getString(R.string.login_show7).toString();
				Toast toast = Toast.makeText(getApplicationContext(), show7,
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				// 获取服务器传来的name，以及更改配置文件的登录信息
				name = gainName(log_back);
				upStatus();
				System.out.println(lss.getValue_status());
				System.out.println(lss.getValue_name());
				System.out.println(lss.getValue_user());
				// 如果用登录成功，则把进入云相册缓冲，并调用响应云相册子进程
				pd_goshare = new ProgressDialog(Login.this);
				String goshare = getString(R.string.login_goshare).toString();
				pd_goshare.setMessage(goshare);
				pd_goshare.show();
				Thread thread = new Thread(new GoShare());
				thread.start();
			}
		}
	};
	// 接收用户自动登录子进程传来的信息，关闭缓冲界面，并对返回的信息进行处理
	private Handler hl_autoing = new Handler() {
		public void handleMessage(Message msg) {
			pd_autoing.dismiss();
			if (auto_back.equals("link error") || auto_back.equals("error")) {
				String show6 = getString(R.string.login_show6).toString();
				Toast toast = Toast.makeText(getApplicationContext(), show6,
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			} else if (auto_back.equals("auto error")) {
				String show8 = getString(R.string.login_show8).toString();
				Toast toast = Toast.makeText(getApplicationContext(), show8
						+ auto_back, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;

			} else if (auto_back.startsWith("auto success")) {
				String show9 = getString(R.string.login_show9).toString();
				Toast toast = Toast.makeText(getApplicationContext(), show9,
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				// 当识别用户已经点击记住密码按钮，就把此用户的帐号和密码保存起来
				String isRember = lss.getValue_rember();
				String isAuto = lss.getValue_auto();
				lss.setValue_email(uemail);
				if (isRember.equals("1") || isAuto.equals("1")) {
					lss.setValue_pass(upass);
				}
				// 获取服务器传来的name，以及更改配置文件的登录信息
				name = gainName(log_back);
				upStatus();
				// 如果用登录成功，则把进入云相册缓冲，并调用响应云相册子进程
				pd_goshare = new ProgressDialog(Login.this);
				String goshare = getString(R.string.login_goshare).toString();
				pd_goshare.setMessage(goshare);
				pd_goshare.show();
				Thread thread = new Thread(new GoShare());
				thread.start();
			}
		}
	};

	// 接收用户注册链接子进程传来的信息，关闭缓冲界面
	private Handler hl_nextact = new Handler() {
		public void handleMessage(Message msg) {
			pd_register.dismiss();
		}
	};
	// 接收用户进入分享界面子进程传来的信息，关闭缓冲界面
	private Handler hl_goshare = new Handler() {
		public void handleMessage(Message msg) {
			pd_goshare.dismiss();
		}
	};

	/*
	 * 下面使一些子进程内部类
	 */

	// 用于响应登录按钮的子进程
	private class Loging implements Runnable {
		public void run() {
			ConToTom ctt = new ConToTom(uemail, null, upass, "login");
			log_back = ctt.GetToTom();
			hl_loging.sendEmptyMessage(0);
		}
	}

	// 用于响应自动登录按钮的子进程
	private class Autoing implements Runnable {
		public void run() {
			String lastEmail = lss.getValue_email();
			String lastPass = lss.getValue_pass();
			ConToTom ctt = new ConToTom(lastEmail, null, lastPass, "auto");
			auto_back = ctt.GetToTom();
			hl_autoing.sendEmptyMessage(0);
		}
	}

	// 用于响应注册链接的子进程
	private class NextAct implements Runnable {
		public void run() {
			Intent intent = new Intent(Login.this, Register.class);
			startActivity(intent);
			hl_nextact.sendEmptyMessage(0);
		}
	}

	// 用于进入相册分享界面的子进程
	private class GoShare implements Runnable {
		public void run() {
			Intent intent = new Intent(Login.this, Share.class);
			intent.putExtra("uemail", uemail);
			startActivity(intent);
			hl_goshare.sendEmptyMessage(0);
		}
	}

	/*
	 * 下面是一些验证以及功能方法
	 */

	// 正则表达式判断用户输入的邮箱是否合法
	public boolean isEmail(String uemail) {

		String format = "\\w{2,15}[@][a-z0-9]{3,}[.]\\p{Lower}{2,}";
		boolean result = uemail.matches(format);
		return result;
	}

	// 正则表达式判断用户输入的密码是否是否为6-16位的数字和大小写字母组成
	public boolean isPass(String upass) {
		String format = "\\w{6,16}";
		boolean result = upass.matches(format);
		return result;
	}

	// 从服务器传来的信息中提取用户名
	private String gainName(String result) {
		String str[] = result.split("\\?");
		return str[str.length - 1];
	}

	// 当用户登录成功后更新用户的在线状态，注意在退出整个程序时要提示是否注销以及把用户在线状态设为0
	private void upStatus() {
		lss.setValue_status("1");
		lss.setValue_user(uemail);
		lss.setValue_name(name);
	}

	// //检测网络，并可进入配置界面
	// private void checkNetwork() {
	// ConnectivityManager conMan = (ConnectivityManager)
	// getSystemService(Context.CONNECTIVITY_SERVICE);
	//
	// State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
	// .getState();
	// State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
	// .getState();
	//
	// // 如果3G网络和wifi网络都未连接，且不是处于正在连接状态 则进入Network Setting界面 由用户配置网络连接
	// if (mobile == State.CONNECTED || mobile == State.CONNECTING)
	// return;
	// if (wifi == State.CONNECTED || wifi == State.CONNECTING)
	// return;
	//
	// Toast toast = Toast.makeText(getApplicationContext(),
	// "网络没有连接，请进入配置网络连接！", Toast.LENGTH_LONG);
	// toast.setGravity(Gravity.CENTER, 0, 0);
	//
	// startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));//
	// 进入无线网络配置界面
	// // startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
	// // //进入手机中的wifi网络设置界面
	//
	// }

}
