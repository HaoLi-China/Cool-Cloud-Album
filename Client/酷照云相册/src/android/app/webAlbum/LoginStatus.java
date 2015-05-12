package android.app.webAlbum;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class LoginStatus {

	private final String key_rember = "rember"; // �û��Ƿ�ѡ���ס���룬0-��1-��
	private final String key_auto = "auto"; // �û��Ƿ�ѡ���Զ���¼��0-��1-��
	private final String key_email = "useremail"; // �û���ס��������Զ���¼ʱ����ʺ�
	private final String key_pass = "userpass"; // �û���ס��������Զ���¼ʱ�������
	// �����û���¼��״̬��ֻҪһ�������ߣ���0-���û����ߣ�1-���û����ߣ�ֻ�����û����߲Ż�������ʺź��ǳ�
	private final String key_status = "status";
	private final String key_user = "user"; // �����û����ʺ�
	private final String key_name = "name"; // �����û����ǳ�
	private final String key_filename = "filename";// �û��Լ�������ļ��е��ַ���
	private final String key_num = "num";

	private Editor editor;
	private SharedPreferences share;

	public LoginStatus(Context context) {
		share = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
		editor = share.edit();
	}

	public String getValue_rember() {
		String value_rember = share.getString(key_rember, "");
		return value_rember;
	}

	public void setValue_rember(String value_rember) {
		editor.putString(key_rember, value_rember);
		editor.commit();
	}

	public String getValue_auto() {
		String value_auto = share.getString(key_auto, "");
		return value_auto;
	}

	public void setValue_auto(String value_auto) {
		editor.putString(key_auto, value_auto);
		editor.commit();
	}

	public String getValue_email() {
		String value_email = share.getString(key_email, "");
		return value_email;
	}

	public void setValue_email(String value_email) {
		editor.putString(key_email, value_email);
		editor.commit();
	}

	public String getValue_pass() {
		String value_pass = share.getString(key_pass, "");
		return value_pass;
	}

	public void setValue_pass(String value_pass) {
		editor.putString(key_pass, value_pass);
		editor.commit();
	}

	public String getValue_status() {
		String value_status = share.getString(key_status, "");
		return value_status;
	}

	public void setValue_status(String value_status) {
		editor.putString(key_status, value_status);
		editor.commit();
	}

	public String getValue_user() {
		String value_user = share.getString(key_user, "");
		return value_user;
	}

	public void setValue_user(String value_user) {
		editor.putString(key_user, value_user);
		editor.commit();
	}

	public String getValue_name() {
		String value_name = share.getString(key_name, "");
		return value_name;
	}

	public void setValue_name(String value_name) {
		editor.putString(key_name, value_name);
		editor.commit();
	}

	public String getValue_filename() {
		String value_filename = share.getString(key_filename, "");
		return value_filename;
	}

	public void setValue_filename(String value_filename) {
		editor.putString(key_filename, value_filename);
		editor.commit();
	}

	public int getValue_num() {
		int value_num = share.getInt(key_num, 0);
		return value_num;
	}

	public void setValue_num(int value_num) {
		editor.putInt(key_num, value_num);
		editor.commit();
	}
}
