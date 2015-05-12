package android.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedData {
	 
	private Editor editor;
	private SharedPreferences share;

	
	public SharedData(Context context) {
		share = context.getSharedPreferences("qqapi", Context.MODE_PRIVATE);
		editor = share.edit();
	}

	public void saveQQAccessToken(String AccessToken) {
		editor.putString("AccessToken",AccessToken);
		editor.commit();
	}
	public String getQQAccessToken(){
		String AccessToken = share.getString("AccessToken", "no");
		return AccessToken;
	}
	
	public void saveQQclientId(String ClientId) {
		editor.putString("clientId",ClientId);
		editor.commit();
	}
	public String getQQclientId(){
		String clientId = share.getString("clientId", "no");
		return clientId;
	}
	
	public void saveQQclientSecret(String ClientSecret) {
		editor.putString("clientSecret",ClientSecret);
		editor.commit();
	}
	public String getQQclientSecret(){
		String clientSecret = share.getString("clientSecret", "no");
		return clientSecret;
	}
	
	public void saveQQOpenid(String Openid) {
		editor.putString("Openid",Openid);
		editor.commit();
	}
	public String getQQOpenid(){
		String Openid = share.getString("Openid", "no");
		return Openid;
	}
	public void saveQQOpenkey(String Openkey) {
		editor.putString("Openkey",Openkey);
		editor.commit();
	}
	public String getQQOpenkey(){
		String Openkey = share.getString("Openkey", "no");
		return Openkey;
	}
	
	public void saveQQExpiresIn(String ExpiresIn) {
		editor.putString("ExpiresIn",ExpiresIn);
		editor.commit();
	}
	public String getQQExpiresIn(){
		String ExpiresIn = share.getString("ExpiresIn", "no");
		return ExpiresIn;
	}


	public void saveQQRefreshToken(String RefreshToken) {
		editor.putString("RefreshToken",RefreshToken);
		editor.commit();
	}
	public String getQQRefreshToken(){
		String RefreshToken = share.getString("RefreshToken", "no");
		return RefreshToken;
	}
	
	public void saveQQBegintime(long Begintime){
		editor.putLong("Begintime",Begintime);
		editor.commit();
	}
	public long getQQBegintime(){
		long Begintime = share.getLong("Begintime", 0);
		return Begintime;
	}
}
