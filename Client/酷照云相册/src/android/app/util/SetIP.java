package android.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SetIP {
	public static String getIP() {
		try {
			Properties prop = new Properties();
			// class����������ļ���classpath�У��Ϳ��Ե��룬�ɱ���ǿ��
			InputStream inStream = SetIP.class.getClassLoader()
					.getResourceAsStream("ipinfo.properties");
			prop.load(inStream);
			String ip = prop.getProperty("ip");
			System.out.println(ip);
			return ip;
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
}
