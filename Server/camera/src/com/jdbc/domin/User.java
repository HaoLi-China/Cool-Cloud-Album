/*
 * �˳�������һ���û��࣬���ڴ洢�û���һЩ�����Ϣ�����ڳ���Ĺ���
 * �û�ֻ���������ԣ����䡢�ǳƺ�����
 */

package com.jdbc.domin;

public class User {
    
	private String e_mail;
	private String name;
	private String password;

	public String getE_mail() {
		return e_mail;
	}

	public void setE_mail(String eMail) {
		e_mail = eMail;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
