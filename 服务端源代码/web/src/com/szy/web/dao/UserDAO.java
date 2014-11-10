package com.szy.web.dao;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author  coolszy
 * @time    Dec 4, 2011 2:52:09 PM
 */
public class UserDAO
{
	SqlManager manager;
	String sql = "";
	ResultSet rs;
	
	public UserDAO() throws IOException, ClassNotFoundException, SQLException
	{
		manager = SqlManager.createInstance();
	}
	
	/**
	 * 验证用户名和密码是否正确
	 * @param uname 用户名
	 * @param password 密码
	 * @return
	 * @throws SQLException
	 */
	public boolean validate(String uname,String password) throws SQLException
	{
		boolean result = false;
		sql = "select count(uid) from t_user where uname=? and password=?";
		Object[] params = new Object[]{uname,password};
		manager.connectDB();
		rs = manager.executeQuery(sql, params);
		if (rs.next()&&rs.getLong(1)==1)
		{
			result = true;
		}
		manager.closeDB();
		return result;
	}
	
	/**
	 * 更新用户信息，
	 * 注意：只能根据用户名更新密码
	 * @param uname 用户名
	 * @param password 新密码
	 */
	public void update(String uname,String password)
	{
		throw new NotImplementedException();
	}
}
