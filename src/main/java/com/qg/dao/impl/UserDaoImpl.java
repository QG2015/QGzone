package com.qg.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.User;

import com.qg.dao.UserDao;
import com.qg.model.UserModel;
import com.qg.util.ConnectionPool;
import com.qg.util.Level;
import com.qg.util.Logger;
import com.qg.util.SimpleConnectionPool;

public class UserDaoImpl implements UserDao{

	private static final Logger LOGGER = Logger.getLogger(UserDaoImpl.class);
	
	
	private Connection conn;//声明Connection对象
	private PreparedStatement sql;//声明预处理语句
	private ResultSet rs;//声明结果集
	private  boolean flag=false;//判断标志
	//获得连接池
	ConnectionPool pool = ConnectionPool.getInstance();
	
	/**
	 * 类中公用关闭流的方法
	 */
	private void daoClose(){
		try {
			if(rs != null){
				rs.close();
			}
			if(sql != null){
				sql.close();
			}     
			if(conn != null){
				conn.close();
			}
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "SQL语句发送错误", e);
		}
	}

	@Override
	public boolean addUser(UserModel user) {
		conn = pool.getConnection();
		try {
			sql=conn.prepareStatement("insert into user"+" "+"values(?,?,?,?,?)");
			sql.setInt(1, user.getUserId());
			sql.setString(2, user.getUserName());
			sql.setString(3, user.getPassword());
			sql.setInt(4, user.getUserSecretId());
			sql.setString(5,user.getUserSecretAnswer());
			sql.executeUpdate();
			flag = true;
			System.out.println("saveUser is running");
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "添加用户发生异常！", e);
		}finally {
			daoClose();
		}
		if(flag){
			flag=false;
			return true;
		}
		else 
			return false;
	}

	@Override
	public UserModel getUserById(int userId) {
		conn = pool.getConnection();
		UserModel user = new UserModel();
		try {

			sql=conn.prepareStatement("select a.*,b.user_image" 
					+" from user as a ,message as b "  
					+" where a.user_id=b.user_id ;");
			rs=sql.executeQuery();
			while(rs.next()) {
				user.setUserId(rs.getInt("user_id"));
				user.setUserName(rs.getString("user_name"));
				user.setPassword(rs.getString("user_password"));
				user.setUserSecretId(rs.getInt("user_secret_id"));
				user.setUserSecretAnswer(rs.getString("user_secret_answer"));
				user.setUserImage(rs.getString("user_image"));
				if(userId==user.getUserId()) {
					flag = true; break;
				}
			}
				
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "查询用户发生异常！", e);
		}finally {
			daoClose();
		}
		if(flag){
			flag=false;
			return user;
		}
		else 
			return null;
	}

	@Override
	public List<UserModel> getUsersByName(String userName) {
		List<UserModel> users = new ArrayList<UserModel>();
		conn = pool.getConnection();
		try {

			sql=conn.prepareStatement("select a.*,b.user_image" 
					+" from user as a ,message as b "  
					+" where a.user_id=b.user_id ;");
			rs=sql.executeQuery();
			while(rs.next()) {
				UserModel user = new UserModel();
				user.setUserId(rs.getInt("user_id"));
				user.setUserName(rs.getString("user_name"));
				user.setUserImage(rs.getString("user_image"));
				if(user.getUserName().equals(userName)) {
					users.add(user);
					flag = true;
				}
			}
				
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "查询用户发生异常！", e);
		}finally {
			daoClose();
		}
		if(flag){
			flag=false;
			return users;
		}
		else 
			return null;
	}

	@Override
	public boolean changeSecret(int userId, int secretId, String newAnswer) {
		conn = pool.getConnection();
		try {

			sql=conn.prepareStatement("update user set user_secret_id=? "
					+ " ,user_secret_answer = ? "
					+ " where user_id=?");
			sql.setInt(1, secretId);
			sql.setString(2, newAnswer);
			sql.setInt(3, userId);			
			sql.executeUpdate();
			flag=true;
			System.out.println("changSecret is running");
				
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "修改密保发生异常！", e);
		}finally {
			daoClose();
		}
		if(flag){
			flag=false;
			return true;
		}
		else 
			return false;
	}


	@Override
	public boolean changePassword(int userId, String password) {
				
		conn = pool.getConnection();
		try {

			sql=conn.prepareStatement("update user set user_password=? "
					+ " where user_id=?");
			sql.setString(1, password);
			sql.setInt(2, userId);			
			sql.executeUpdate();
			flag=true;
			System.out.println("changPassword is running");
				
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "修改密码发生异常！", e);
		}finally {
			daoClose();
		}
		if(flag){
			flag=false;
			return true;
		}
		else 
			return false;
	}
	
	public List<String> selcetUserId(){
		List<String> list = new ArrayList<String>();
		conn = pool.getConnection();
		try {

			sql=conn.prepareStatement("select user_id" 
					+" from user");
			rs=sql.executeQuery();
			while(rs.next()) {
				list.add(rs.getInt("user_id")+"");
				flag = true;
			}
				
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "遍历账号发生异常！", e);
		}finally {
			daoClose();
		}
		if(flag){
			flag=false;
			return list;
		}
		else 
			return null;
	}
}
