package com.qg.servlet;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.qg.model.MessageModel;
import com.qg.model.UserModel;
import com.qg.service.SearchService;
import com.qg.util.JsonUtil;
import com.qg.util.Level;
import com.qg.util.Logger;

/**
 * 
 * @author zggdczfr
 * <p>
 * 通过用户昵称来搜索用户
 * 状态码: 301-找到; 302-找不到;
 * </p>
 */

@WebServlet("SearchByUserName")
public class SearchbyUserName extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(SearchbyUserName.class);
	private static final int success = 1;
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		//获得用户id
		int userId = ((UserModel)request.getSession().getAttribute("user")).getUserId();
		String searchUserName =request.getParameter("searchUserName");
		int state = 302;
		Gson gson = new Gson();
		DataOutputStream output = new DataOutputStream(response.getOutputStream());
		SearchService searchService = new SearchService();
		//获得搜索结果
		List<MessageModel> allMessage = searchService.searchMessagesByUserName(searchUserName);
		if(allMessage.isEmpty()){
			state = 302;
		} else {
			state = 301;
		}
		
		LOGGER.log(Level.DEBUG, "用户 {0} 搜索昵称 {1}", userId, searchUserName);
	
		JsonUtil<List<MessageModel>, String> object = new JsonUtil(state, allMessage);
		output.write(gson.toJson(object).getBytes("UTF-8"));
		output.close();
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		doPost(request, response);
	}
}