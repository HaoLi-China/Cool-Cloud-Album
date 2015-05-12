/*
 * 此程序用于从服务器获得用户相册文件夹的数目名称，GET方法
 * 同时获得某个文件夹下图片的数目和名称，POST方法，因为有些传入的文件夹是汉语
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

public class GetImageInfo {

	private String url = null;

	private String uemail;
	private String filename;

	public GetImageInfo(String uemail, String filename, String choose) {
		this.uemail = uemail;
		this.filename = filename;

		if (choose.equals("filenum"))
			this.url = SetIP.getIP() + choose + "?uemail=" + uemail;
		if (choose.equals("otherfile"))
			this.url = SetIP.getIP() + choose + "?uemail=" + uemail;

		if (choose.equals("imagename")) {
			this.url = SetIP.getIP() + choose;
		}
		if (choose.equals("imagenum"))
			this.url = SetIP.getIP() + choose;

	}

	// 获得用户帐号下的文件夹数目或文件夹名称
	public String GetFileInfo() {
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

	public String GetImageInfos() {
		/* 建立HTTP Post联机 */
		HttpPost httpRequest = new HttpPost(url);
		/*
		 * Post运作传送变量必须用NameValuePair[]数组储存
		 */
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("uemail", uemail));
		params.add(new BasicNameValuePair("filename", URLEncoder
				.encode(filename)));
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
