package com.qg.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.qg.dao.SupportDao;
import com.qg.util.Level;
import com.qg.util.Logger;
import com.qg.util.SimpleConnectionPool;

public class SupportDaoImpl implements SupportDao{
	private static final Logger LOGGER = Logger.getLogger(SupportDaoImpl.class);
	private Connection conn = null;
	private PreparedStatement pStatement = null;
	private ResultSet rs = null;
    public void close(ResultSet rs,Statement stat,Connection conn){
        try {
            if(rs!=null)rs.close();
            if(stat!=null)stat.close();
            if(conn!=null)SimpleConnectionPool.pushConnectionBackToPool(conn);
        } catch (SQLException e) {
            e.printStackTrace();
       }
}
    public boolean addSupport(int twitterId,int supporterId){
    	boolean result = true;
    	try {
			conn = SimpleConnectionPool.getConnection();
			String sql = "insert into support(twitter_id,supporter_id) value(?,?)";
			pStatement = conn.prepareStatement(sql);
			pStatement.setInt(1, twitterId);
			pStatement.setInt(2, supporterId);
			pStatement.executeUpdate();
			new TwitterDaoImpl().addSupport(twitterId);
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "点赞异常！", e);
			result = false;
		} finally {
			close(null, pStatement, conn);
		}
    	return result;
	}
    public boolean deleteSupport(int twitterId,int supporterId){
    	boolean result = true;
    	try {
			conn = SimpleConnectionPool.getConnection();
			String sql = "DELETE FROM support WHERE twitter_id=? AND supporter_id=?";
			pStatement = conn.prepareStatement(sql);
			pStatement.setInt(1, twitterId);
			pStatement.setInt(2, supporterId);
			pStatement.executeUpdate();
			new TwitterDaoImpl().deleteSupport(twitterId);
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "取消赞异常！", e);
			result = false;
		} finally {
			close(null, pStatement, conn);
		}
    	return result;
	}
    public boolean findSupport(int twitterId,int supporterId){
    	boolean result = false;
    	
    	try {
			conn = SimpleConnectionPool.getConnection();
			String sql = "SELECT COUNT(1) FROM support WHERE twitter_id=? AND supporter_id=?";
			pStatement = conn.prepareStatement(sql);
			pStatement.setInt(1, twitterId);
			pStatement.setInt(2, supporterId);
			rs = pStatement.executeQuery();
			if(rs.next()){
				result=(rs.getInt(1)==1);
				}
    	} catch (SQLException e) {
    		LOGGER.log(Level.ERROR, "查询用户是否点赞异常！", e);
		} finally {
			close(rs, pStatement, conn);
		}
    	return result;
    }
    public List<String>getSupporterByTwitterId(int twitterId){
    	List<String> supporters = new ArrayList<String>();
    	try {
			conn = SimpleConnectionPool.getConnection();
			String sql = "SELECT supporter_id FROM support WHERE twitter_id=? ORDER BY supporter_id DESC";
			pStatement = conn.prepareStatement(sql);
			pStatement.setInt(1, twitterId);
			rs = pStatement.executeQuery();
			while(rs.next()){
				UserDaoImpl userDaoImpl = new UserDaoImpl();
				supporters.add(userDaoImpl.getUserById(rs.getInt("supporter_id")).getUserName());
				}
    	} catch (SQLException e) {
    		LOGGER.log(Level.ERROR, "获取点赞用户异常！", e);
		} finally {
			close(rs, pStatement, conn);
		}
    	return supporters;
    }
}