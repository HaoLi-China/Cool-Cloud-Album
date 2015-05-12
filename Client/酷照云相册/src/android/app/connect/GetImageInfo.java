/*
 * �˳������ڴӷ���������û�����ļ��е���Ŀ���ƣ�GET����
 * ͬʱ���ĳ���ļ�����ͼƬ����Ŀ�����ƣ�POST��������Ϊ��Щ������ļ����Ǻ���
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

	// ����û��ʺ��µ��ļ�����Ŀ���ļ�������
	public String GetFileInfo() {
		/* ����HTTP Get���� */
		HttpGet httpRequest = new HttpGet(url);
		try {
			/* ����HTTP request */
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);
			/* ��״̬��Ϊ200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* ȡ����Ӧ�ַ��� */
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
		/* ����HTTP Post���� */
		HttpPost httpRequest = new HttpPost(url);
		/*
		 * Post�������ͱ���������NameValuePair[]���鴢��
		 */
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("uemail", uemail));
		params.add(new BasicNameValuePair("filename", URLEncoder
				.encode(filename)));
		try {
			/* ����HTTP request */
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			/* ȡ��HTTP response */
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);
			/* ��״̬��Ϊ200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* ȡ����Ӧ�ַ��� */
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

	/* �Զ����ַ���ȡ������ */
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
