package com.jdbc.dao;

import com.jdbc.domin.User;


public interface UserDao {
	
	public String register(User user);
	public String login(User user);
	public String search(String e_mail);
	
}
