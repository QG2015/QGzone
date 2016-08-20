package com.qg.servlet.llh;

import java.io.DataOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.qg.model.TwitterCommentModel;
import com.qg.model.UserModel;
import com.qg.service.TwitterCommentService;
import com.qg.service.TwitterService;
import com.qg.util.JsonUtil;
import com.qg.util.Level;
import com.qg.util.Logger;

@WebServlet("/TwitterCommentAdd")
/***
 * 
 * @author dragon
 * <pre>
 * 添加说说评论
 * 201发表成功  202发表失败  203评论过长 
 * </pre>
 */
public class TwitterCommentAdd extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(TwitterCommentAdd.class);
	
	protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
		int state = 201;
		TwitterCommentModel twitterCommentModel = null;
		/* 获取说说id，被评论方id,评论内容 */
		int twitterId = Integer.parseInt(request.getParameter("twitterId"));
		int targetId = Integer.parseInt(request.getParameter("targetId"));
		String comment = request.getParameter("comment");
		// 获取当前登陆用户
		int commenterId = ((UserModel) request.getSession().getAttribute("user")).getUserId();
//		int commenterId=3;
		if (!(comment.length() > 150)) {

			// 获取说说评论的实体类
			 twitterCommentModel = new TwitterCommentModel(comment, twitterId, commenterId,
					targetId);
			// 存进数据库
			if (! new TwitterService().existTwitter(twitterId))
				state = 202;
			else twitterCommentModel= new TwitterCommentService().addTwitterComment(twitterCommentModel);
		} else
			state = 203;
		// 打包发送
		LOGGER.log(Level.DEBUG, " {0}想评论{1}的说说，说说id为{2}，内容为：{3}，状态码为{4}", commenterId,targetId,twitterId,comment,state);
		DataOutputStream output = new DataOutputStream(resp.getOutputStream());
		output.write(JsonUtil.tojson(state,twitterCommentModel,twitterCommentModel.getCommentId()).getBytes("UTF-8"));
		output.close();
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse resp)
			throws ServletException, IOException {
			doGet(request, resp);
	}
}
