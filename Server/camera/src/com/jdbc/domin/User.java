/*
 * 此程序声明一个用户类，用于存储用户的一些相关信息，便于程序的管理
 * 用户只有三个属性，邮箱、昵称和密码
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
