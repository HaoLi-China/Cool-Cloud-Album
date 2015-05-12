/*��û��ʵ����ȫ
 * �����ס���룬�Զ���¼
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

	private EditText et_email; // �û���¼���������
	private EditText et_pass; // �û���¼���������

	private Button bt_login; // �û���¼��ť

	private CheckBox cb_rember; // ��ѡ���û���ס����
	private CheckBox cb_auto; // ��ѡ���û��Զ���¼

	private String uemail; // �û���¼������
	private String upass; // �û���¼������

	private ProgressDialog pd_loging;// �û���¼��֤��Ӧ�Ļ���Ի���
	private ProgressDialog pd_register; // �û����ע�����ӣ�����ע�����Ļ���Ի���
	private ProgressDialog pd_autoing; // �û��ϴ�ѡ���Զ���¼���´ν��������Զ��ύ��֤�Ļ���Ի���
	private ProgressDialog pd_goshare; // �û���֤�ɹ��������������Ļ���Ի���

	private TextView regiter_link;// ע������

	private String log_back; // �û������¼��ťʱ���ص���Ӧ�ź�
	private String auto_back;// �û��Զ���¼ʱ���ص���Ӧ�ź�

	private String name;// �û���¼�ɹ���ӷ�������õ��û��ǳ�

	private LoginStatus lss; // ��¼�洢�û��ϴ���Ϣ�Ķ���

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// �������ô�ҳ�棨����ޱ�������ȫ����
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.login);

		// ��������������������
		et_email = (EditText) findViewById(R.id.et_email);
		et_pass = (EditText) findViewById(R.id.et_pass);

		/*
		 * �������ļ���ȡ����ֵ�ԣ��ж��ϴ��û�ѡ���״̬�������в�ͬ����Ӧ 1.autoֵΪ0���Զ���¼Ϊѡ��Ϊ1��ѡ�����Զ���¼
		 * 2.remberֵΪ0���ס����Ϊѡ��Ϊ1��ѡ���˼�ס���� 3.�����Ϊ0����ֻ�Ǽ�ס�ϴε�¼�ɹ��󱣴���������ʺ�
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
			// ��¼�����еĻ��壬"�Զ���¼��......"
			pd_autoing = new ProgressDialog(Login.this);
			String meg_autoing = getString(R.string.login_autoing).toString();
			pd_autoing.setMessage(meg_autoing);
			pd_autoing.show();
			Thread thread = new Thread(new Autoing());
			thread.start();
		}

		// ��õ�¼��ť����
		bt_login = (Button) findViewById(R.id.bt_go);
		bt_login.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {

				// ����������û���¼������������
				uemail = et_email.getText().toString().trim();
				upass = et_pass.getText().toString().trim();

				// ����һϵ����֤�û�������û����������Ƿ�Ϸ�
				// ��֤�û�����������Ƿ�Ϊ�Ϸ�����
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

				// ��֤�û�����������Ƿ�Ϊ6-16λ�����ֺʹ�Сд��ĸ���
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

				// ��¼�����еĻ��壬"��¼��......"
				pd_loging = new ProgressDialog(Login.this);
				String meg_log = getString(R.string.login_loging).toString();
				pd_loging.setMessage(meg_log);
				pd_loging.show();
				Thread thread = new Thread(new Loging());
				thread.start();
			}
		});

		// ��ס���븴ѡ�򣬵����ʱ���"userinfo.properties"�ļ��е�rember��ֵ��Ϊ1��
		// ���ں�����û���ȷ��¼����û������������"userinfo.properties"�ļ���
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
		// ��ס���븴ѡ�򣬵����ʱ���"userinfo.properties"�ļ��е�auto��ֵ��Ϊ1��
		// ���ں�����û���¼ʱ���Զ���¼���͸���������������һ��cookiesֵ
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

		// ���textview��ע�����ӣ��������Ӽ��ϼ���
		regiter_link = (TextView) findViewById(R.id.tv_link);
		String text = getString(R.string.login_link).toString();
		SpannableString spannableString = new SpannableString(text);
		spannableString.setSpan(new ClickableSpan() {
			// ��onClick�����п��Ա�д��������ʱ��ע�ᰴťҪִ�еĶ���
			public void onClick(View widget) {
				pd_register = new ProgressDialog(Login.this);
				String meg_next = getString(R.string.login_nextact).toString();
				pd_register.setMessage(meg_next);
				pd_register.show();
				Thread thread = new Thread(new NextAct());
				thread.start();
			}
		}, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// ʹ��SpannableString��������TextView�ؼ�������
		regiter_link.setText(spannableString);
		// �ڵ�������ʱ������Ҫִ�еĶ���������������MovementMethod����
		regiter_link.setMovementMethod(LinkMovementMethod.getInstance());

		// ���ڸ��ݷֱ�������������Ĵ�С
		int window_height = getWindowManager().getDefaultDisplay().getHeight();
		int window_width = getWindowManager().getDefaultDisplay().getWidth();
		ViewGroup v = (ViewGroup) findViewById(R.id.login_lay);
		FontAdapter.changeViewSize(v, window_height, window_width);
	}

	// �����û���¼�ӽ��̴�������Ϣ���رջ�����棬���Է��ص���Ϣ���д���
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

				// ��ʶ���û��Ѿ������ס���밴ť���ͰѴ��û����ʺź����뱣������
				String isRember = lss.getValue_rember();
				String isAuto = lss.getValue_auto();
				lss.setValue_email(uemail);
				if (isRember.equals("1") || isAuto.equals("1")) {
					lss.setValue_pass(upass);
				}
				// ��ʾ��¼�ɹ�
				String show7 = getString(R.string.login_show7).toString();
				Toast toast = Toast.makeText(getApplicationContext(), show7,
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				// ��ȡ������������name���Լ����������ļ��ĵ�¼��Ϣ
				name = gainName(log_back);
				upStatus();
				System.out.println(lss.getValue_status());
				System.out.println(lss.getValue_name());
				System.out.println(lss.getValue_user());
				// ����õ�¼�ɹ�����ѽ�������Ỻ�壬��������Ӧ������ӽ���
				pd_goshare = new ProgressDialog(Login.this);
				String goshare = getString(R.string.login_goshare).toString();
				pd_goshare.setMessage(goshare);
				pd_goshare.show();
				Thread thread = new Thread(new GoShare());
				thread.start();
			}
		}
	};
	// �����û��Զ���¼�ӽ��̴�������Ϣ���رջ�����棬���Է��ص���Ϣ���д���
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
				// ��ʶ���û��Ѿ������ס���밴ť���ͰѴ��û����ʺź����뱣������
				String isRember = lss.getValue_rember();
				String isAuto = lss.getValue_auto();
				lss.setValue_email(uemail);
				if (isRember.equals("1") || isAuto.equals("1")) {
					lss.setValue_pass(upass);
				}
				// ��ȡ������������name���Լ����������ļ��ĵ�¼��Ϣ
				name = gainName(log_back);
				upStatus();
				// ����õ�¼�ɹ�����ѽ�������Ỻ�壬��������Ӧ������ӽ���
				pd_goshare = new ProgressDialog(Login.this);
				String goshare = getString(R.string.login_goshare).toString();
				pd_goshare.setMessage(goshare);
				pd_goshare.show();
				Thread thread = new Thread(new GoShare());
				thread.start();
			}
		}
	};

	// �����û�ע�������ӽ��̴�������Ϣ���رջ������
	private Handler hl_nextact = new Handler() {
		public void handleMessage(Message msg) {
			pd_register.dismiss();
		}
	};
	// �����û������������ӽ��̴�������Ϣ���رջ������
	private Handler hl_goshare = new Handler() {
		public void handleMessage(Message msg) {
			pd_goshare.dismiss();
		}
	};

	/*
	 * ����ʹһЩ�ӽ����ڲ���
	 */

	// ������Ӧ��¼��ť���ӽ���
	private class Loging implements Runnable {
		public void run() {
			ConToTom ctt = new ConToTom(uemail, null, upass, "login");
			log_back = ctt.GetToTom();
			hl_loging.sendEmptyMessage(0);
		}
	}

	// ������Ӧ�Զ���¼��ť���ӽ���
	private class Autoing implements Runnable {
		public void run() {
			String lastEmail = lss.getValue_email();
			String lastPass = lss.getValue_pass();
			ConToTom ctt = new ConToTom(lastEmail, null, lastPass, "auto");
			auto_back = ctt.GetToTom();
			hl_autoing.sendEmptyMessage(0);
		}
	}

	// ������Ӧע�����ӵ��ӽ���
	private class NextAct implements Runnable {
		public void run() {
			Intent intent = new Intent(Login.this, Register.class);
			startActivity(intent);
			hl_nextact.sendEmptyMessage(0);
		}
	}

	// ���ڽ��������������ӽ���
	private class GoShare implements Runnable {
		public void run() {
			Intent intent = new Intent(Login.this, Share.class);
			intent.putExtra("uemail", uemail);
			startActivity(intent);
			hl_goshare.sendEmptyMessage(0);
		}
	}

	/*
	 * ������һЩ��֤�Լ����ܷ���
	 */

	// ������ʽ�ж��û�����������Ƿ�Ϸ�
	public boolean isEmail(String uemail) {

		String format = "\\w{2,15}[@][a-z0-9]{3,}[.]\\p{Lower}{2,}";
		boolean result = uemail.matches(format);
		return result;
	}

	// ������ʽ�ж��û�����������Ƿ��Ƿ�Ϊ6-16λ�����ֺʹ�Сд��ĸ���
	public boolean isPass(String upass) {
		String format = "\\w{6,16}";
		boolean result = upass.matches(format);
		return result;
	}

	// �ӷ�������������Ϣ����ȡ�û���
	private String gainName(String result) {
		String str[] = result.split("\\?");
		return str[str.length - 1];
	}

	// ���û���¼�ɹ�������û�������״̬��ע�����˳���������ʱҪ��ʾ�Ƿ�ע���Լ����û�����״̬��Ϊ0
	private void upStatus() {
		lss.setValue_status("1");
		lss.setValue_user(uemail);
		lss.setValue_name(name);
	}

	// //������磬���ɽ������ý���
	// private void checkNetwork() {
	// ConnectivityManager conMan = (ConnectivityManager)
	// getSystemService(Context.CONNECTIVITY_SERVICE);
	//
	// State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
	// .getState();
	// State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
	// .getState();
	//
	// // ���3G�����wifi���綼δ���ӣ��Ҳ��Ǵ�����������״̬ �����Network Setting���� ���û�������������
	// if (mobile == State.CONNECTED || mobile == State.CONNECTING)
	// return;
	// if (wifi == State.CONNECTED || wifi == State.CONNECTING)
	// return;
	//
	// Toast toast = Toast.makeText(getApplicationContext(),
	// "����û�����ӣ�����������������ӣ�", Toast.LENGTH_LONG);
	// toast.setGravity(Gravity.CENTER, 0, 0);
	//
	// startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));//
	// ���������������ý���
	// // startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
	// // //�����ֻ��е�wifi�������ý���
	//
	// }

}
