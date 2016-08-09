package com.qg.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;

import com.qg.dao.NoteCommentDao;
import com.qg.model.NoteCommentModel;
import com.qg.model.TwitterCommentModel;
import com.qg.util.SimpleConnectionPool;
/***
 * 
 * @author dragon
 * <pre>
 * 该类对留言评论进行操作
 * </pre>
 */
public class NoteCommentDaoImpl implements NoteCommentDao{
	private Connection conn = null;
	private PreparedStatement pStatement = null;
	private ResultSet rs = null;
	SimpleDateFormat Format = new SimpleDateFormat ("yyyy-MM-dd HH:mm");

	public List<NoteCommentModel> getNoteCommentByNoteId(int noteId) {
		 List<NoteCommentModel> noteComments= new ArrayList<NoteCommentModel>();
		 try {
				conn = SimpleConnectionPool.getConnection();
				String sql = "SELECT * FROM note_comment WHERE note_id=? ORDER BY comment_id DESC";
				pStatement = conn.prepareStatement(sql);
				pStatement.setInt(1, noteId);
				rs = pStatement.executeQuery();
				UserDao userDao = new UserDao();
				while(rs.next()){
					noteComments.add(new NoteCommentModel(rs.getInt("comment_id"),rs.getString("comment"),
							noteId,rs.getInt("commenter_id"),userDao.getNameById(rs.getInt("commenter_id")),
							rs.getInt("target_id"),userDao.getNameById(rs.getInt("target_id")),Format.format(rs.getTimestamp("time"))));
					}
	    	} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				close(rs, pStatement, conn);
			}
	    	return noteComments;
	    }
	

	@Override
	public boolean addNoteComment(NoteCommentModel noteComment) {
    	boolean result = true;
    	try {
			conn = SimpleConnectionPool.getConnection();
			String sql = "insert into note_comment(comment, note_id, commenter_id, "
					+ "target_id, time) value(?,?,?,?,?)";
			pStatement = conn.prepareStatement(sql);
			pStatement.setString(1, noteComment.getComment());
			pStatement.setInt(2, noteComment.getNoteId());
			pStatement.setInt(3, noteComment.getCommenterId());
			pStatement.setInt(4, noteComment.getTargetId());
			pStatement.setTimestamp(5,new Timestamp(new Date().getTime()));
			pStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			result = false;
		} finally {
			close(null, pStatement, conn);
		}
    	return result;
	}

	@Override
	public NoteCommentModel getNoteCommentById(int commentId) {
		NoteCommentModel noteCommentModel;
    	try {
			conn = SimpleConnectionPool.getConnection();
			String sql =  "SELECT * FROM note_comment WHERE comment_id=?";
			pStatement = conn.prepareStatement(sql);
			pStatement.setInt(1, commentId);
			if(rs.next()){
				UserDao userDao = new UserDao();
				noteCommentModel=new NoteCommentModel(commentId,rs.getString("comment"),
						rs.getInt("note_id"),rs.getInt("commenter_id"),userDao.getNameById(rs.getInt("commenter_id")),
						rs.getInt("target_id"),userDao.getNameById(rs.getInt("target_id")),Format.format(rs.getTimestamp("time")));
				}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, pStatement, conn);
		}
    	return noteCommentModel;
	}

	@Override
	public boolean deleteComment(int commentId) {
		boolean result = true;
    	try {
			conn = SimpleConnectionPool.getConnection();
			String sql = "DELETE FROM note_comment WHERE comment_id=?";
			pStatement=(PreparedStatement) conn.prepareStatement(sql);
			pStatement.setInt(1, commentId);
			pStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("失败");
			result = false;
		}finally{
			close(null, pStatement, conn);
		}
    	return result;
	}

	@Override
	public boolean deleteComments(int noteId) {
		boolean result = true;
    	try {
			conn = SimpleConnectionPool.getConnection();
			String sql = "DELETE FROM note_comment WHERE note_id=?";
			pStatement=(PreparedStatement) conn.prepareStatement(sql);
			pStatement.setInt(1, noteId);
			pStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("失败");
			result = false;
		}finally{
			close(null, pStatement, conn);
		}
    	return result;
    }
	

    public  void close(ResultSet rs,Statement stat,Connection conn){
        try {
            if(rs!=null)rs.close();
            if(stat!=null)stat.close();
            if(conn!=null)SimpleConnectionPool.pushConnectionBackToPool(conn);
        } catch (SQLException e) {
            e.printStackTrace();
       }
}

}
