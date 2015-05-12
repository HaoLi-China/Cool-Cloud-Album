package android.app.webAlbum;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class LoginStatus {

	private final String key_rember = "rember"; // 用户是否选择记住密码，0-否，1-是
	private final String key_auto = "auto"; // 用户是否选择自动登录，0-否，1-是
	private final String key_email = "useremail"; // 用户记住密码或者自动登录时候的帐号
	private final String key_pass = "userpass"; // 用户记住密码或者自动登录时候的密码
	// 现在用户登录的状态（只要一个人在线），0-无用户在线，1-有用户在线，只有有用户在线才获得下面帐号和昵称
	private final String key_status = "status";
	private final String key_user = "user"; // 在线用户的帐号
	private final String key_name = "name"; // 在线用户的昵称
	private final String key_filename = "filename";// 用户自己定义的文件夹的字符串
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
