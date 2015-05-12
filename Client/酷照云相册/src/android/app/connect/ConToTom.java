/*
 * 程序用于响应用户登录注册对服务器的请求
 */

package android.app.connect;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.util.SetIP;

public class ConToTom {

	private String uemail;
	private String uname;
	private String upass;
	private String url;

	public ConToTom(String email, String name, String pass, String choose) {
		this.uemail = email;
		this.uname = name;
		this.upass = pass;
		if (choose.equals("login"))
			this.url = SetIP.getIP() + choose + "?uemail=" + uemail + "&upass="
					+ upass;
		if (choose.equals("auto"))
			this.url = SetIP.getIP() + "login?auto=yes&uemail=" + uemail
					+ "&upass=" + upass;
		if (choose.equals("register"))
			this.url = SetIP.getIP() + choose;
	}

	// 用于用户注册，使用POST方法，因为向服务器传参数中昵称有汉语
	public String GetToTom() {
		/* 建立HTTP Get联机 */
		HttpGet httpRequest = new HttpGet(url);
		try {
			/* 发出HTTP request */
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);
			/* 若状态码为200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* 取出响应字符串 */
				String strResult = EntityUtils.toString(httpResponse
						.getEntity());
				strResult = eregi_replace("(\r\n|\r|\n|\n\r)", "", strResult);
				return strResult;
			} else {
				return "link error";
			}
		} catch (ClientProtocolException e) {
			return "error";
		} catch (IOException e) {
			return "error";
		} catch (Exception e) {
			return "error";
		}
	}

	public String PostToTom() {
		/* 建立HTTP Post联机 */
		HttpPost httpRequest = new HttpPost(url);
		/*
		 * Post运作传送变量必须用NameValuePair[]数组储存
		 */
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("uemail", uemail));
		params.add(new BasicNameValuePair("uname", URLEncoder.encode(uname)));
		params.add(new BasicNameValuePair("upass", upass));
		try {
			/* 发出HTTP request */
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			/* 取得HTTP response */
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);
			/* 若状态码为200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* 取出响应字符串 */
				String strResult = EntityUtils.toString(httpResponse
						.getEntity());
				strResult = eregi_replace("(\r\n|\r|\n|\n\r)", "", strResult);
				return strResult;
			} else {
				return "link error";
			}
		} catch (ClientProtocolException e) {
			return "error";
		} catch (IOException e) {
			return "error";
		} catch (Exception e) {
			return "error";
		}
	}

	/* 自定义字符串取代函数 */
	public String eregi_replace(String strFrom, String strTo, String strTarget) {
		String strPattern = "(?i)" + strFrom;
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strTarget);
		if (m.find()) {
			return strTarget.replaceAll(strFrom, strTo);
		} else {
			return strTarget;
		}
	}

}
