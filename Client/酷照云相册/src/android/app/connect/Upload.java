//此程序用于向服务器上传图片

package android.app.connect;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.app.util.SetIP;

public class Upload {

	private String uploadUrl;
	private String srcPath;

	public Upload(String uemail, String filename, String srcPath, String choose) {
		this.srcPath = srcPath;
		if (choose.equals("upload"))
			this.uploadUrl = SetIP.getIP() + choose + "?uemail=" + uemail
					+ "&filename="
					+ URLEncoder.encode(URLEncoder.encode(filename));
	}

	/* 上传文件至Server的方法 */
	@SuppressWarnings("unused")
	public String uploadImage() {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "******s";
		try {
			URL url = new URL(uploadUrl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			DataOutputStream dos = new DataOutputStream(httpURLConnection
					.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + end);
			dos
					.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
							+ srcPath.substring(srcPath.lastIndexOf("/") + 1)
							+ "\"" + end);
			dos.writeBytes(end);
			FileInputStream fis = new FileInputStream(srcPath);
			byte[] buffer = new byte[8192]; // 8k
			int count = 0;
			while ((count = fis.read(buffer)) != -1) {
				dos.write(buffer, 0, count);
			}
			fis.close();
			dos.writeBytes(end);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
			dos.flush();
			InputStream is = httpURLConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String result = br.readLine();
			dos.close();
			is.close();
			if (httpURLConnection.getResponseCode() == 200) { // 如果成功返回
				return "upload success";
			} else {
				return "link error";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}

	}

}
