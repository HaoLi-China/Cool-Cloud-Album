package android.app.webAlbum;

/*
 * �û�ע���Activity��ע����д�û������Լ�������������
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

	private EditText et_email; // �û������ʺ������
	private EditText et_name; // �û��������
	private EditText et_pass; // ��һ�����������
	private EditText et_again_pass; // �ظ����������
	private Button bt_regs; // ע������ť

	String uemail; // ����û���ע������
	String uname; // ����û���ע���ǳ�
	String upass; // ����û���ע���һ�����������
	String aupass; // ����û���ע���ظ����������

	// ������������ע����̼����������Ļ���Ի���
	private ProgressDialog pd_regisering, pd_goshare;

	private String back; // �û�ע��ʱ���ص���Ӧ�ź�

	private LoginStatus lss; // ��¼�洢�û��ϴ���Ϣ�Ķ���

	public void onCreate(Bundle savedInstanceState) {

		// �������ô�ҳ�棨����ޱ�������ȫ����
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.register);
		// ��ʼ��
		lss = new LoginStatus(Register.this);

		// ���ע�����䡢�ǳ��Լ�������������
		et_email = (EditText) findViewById(R.id.et_new_email);
		et_name = (EditText) findViewById(R.id.et_new_name);
		et_pass = (EditText) findViewById(R.id.et_new_pass);
		et_again_pass = (EditText) findViewById(R.id.et_again_pass);

		// ��ʼ��ע�ᰴť���Լ�����ť�����Ӧ
		bt_regs = (Button) findViewById(R.id.bt_regs);
		// ע�ᰴť��������������Ӧ
		bt_regs.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				/*
				 * ���ȴ������ʺ�������л���û�����������룬���ж�����������Ƿ�Ϸ�
				 * ������ϸ�ʽ�Ļ����������һ�����������򷵻����û���������
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
				 * ���ǳ�������л���û��ǳƵ����룬���ж��ǳ��Ƿ���Ϲ淶 ������ϸ�ʽ�Ļ����������һ�����������򷵻����û���������
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
				 * ������������л�õ�һ����������룬���ж��������������Ƿ��������ĸ�ʽ
				 * ������ϸ�ʽ�Ļ����������һ�����������򷵻����û���������
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
				 * ���ظ�����������л�õڶ�����������룬���ж��������������Ƿ���ϴε�һ��
				 * ���һ�µĻ����������һ������һ���򷵻����û���������
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
				// ����û�ע����Ϣȫ���Ϸ���ȷ����ѽ��뻺�壬��������Ӧע�ᰴť���ӽ���
				pd_regisering = new ProgressDialog(Register.this);
				String resing = getString(R.string.register_resing).toString();
				pd_regisering.setMessage(resing);
				pd_regisering.show();
				Thread thread = new Thread(new Registering());
				thread.start();

			}
		});

		// ���ڸ��ݷֱ�������������Ĵ�С
		int window_height = getWindowManager().getDefaultDisplay().getHeight();
		int window_width = getWindowManager().getDefaultDisplay().getWidth();
		ViewGroup v = (ViewGroup) findViewById(R.id.register_lay);
		FontAdapter.changeViewSize(v, window_height, window_width);
	}

	// �����û�ע���ӽ��̴�������Ϣ���رջ�����棬���Է��ص���Ϣ���д���
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

				// ��ȡ������������name���Լ����������ļ��ĵ�¼��Ϣ
				upStatus();

				// ������������
				// ����õ�¼�ɹ�����ѽ�������Ỻ�壬��������Ӧ������ӽ���
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

	// ������ʽ�ж��û�����������Ƿ�Ϸ�
	public boolean isEmail(String uemail) {
		// String format =
		// "\\p{Alpha}\\w{2,15}[@][a-z0-9]{3,}[.]\\p{Lower}{2,}";
		String format = "\\w{2,15}[@][a-z0-9]{2,}[.]\\p{Lower}{2,}";
		boolean result = uemail.matches(format);
		return result;
	}

	// ������ʽ�ж��û������ǳ��Ƿ�Ϸ���
	// �û��ǳ���4��10���ַ���ɣ������Ǻ��֣���Сд��ĸ��������
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

	// ������ʽ�ж��û�����������Ƿ��Ƿ�Ϊ6-16λ�����ֺʹ�Сд��ĸ���
	public boolean isPass(String upass) {
		String format = "\\w{6,16}";
		boolean result = upass.matches(format);
		return result;
	}

	// ������Ӧ��¼��ť���ӽ���
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
