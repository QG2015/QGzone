package com.qg.servlet.fangrui;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.qg.model.AlbumModel;
import com.qg.model.PhotoModel;
import com.qg.model.UserModel;
import com.qg.service.AlbumService;
import com.qg.service.PhotoService;
import com.qg.util.JsonUtil;
import com.qg.util.Level;
import com.qg.util.Logger;

/**
 * 
 * @author zggdczfr
 * <p>
 * 用户删除相片
 * </p>
 */

@WebServlet("/DeletePhoto")
public class PhotoDelete extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(PhotoDelete.class);
	private static final int success = 1;
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		//获取用户id
		//int userId = ((UserModel)request.getSession().getAttribute("user")).getUserId();
		int userId = 1;
		int state = 602;
		AlbumService albumService = new AlbumService();
		DataOutputStream output = new DataOutputStream(response.getOutputStream());
		
		//获得Json并解析
		Gson gson = new Gson();
		String strPhoto = request.getParameter("jsonObject");
		PhotoModel photo = gson.fromJson(strPhoto, PhotoModel.class);
		
		
		AlbumModel album = albumService.getAlbumByAlbumId(photo.getAlbumId());
		//确保用户操作自己的相册
		if (userId == album.getUserId()) {
			String photoPath = getServletContext().getRealPath("/album/") + userId + "/" + photo.getAlbumId() + "/" + photo.getPhotoId() + ".jpg";
			System.out.println(photoPath);
			PhotoService photoService = new PhotoService();
			if (success == photoService.deletePhoto(photoPath)) {
				state = 601;
			}
		} else {
			state = 603;
		}
		
		LOGGER.log(Level.DEBUG, "用户 {0} 删除相册 {1} 中的图片 {2} 状态: {3}", userId, photo.getAlbumId(), photo.getPhotoId(), state);
		
		output.write(JsonUtil.tojson(state).getBytes("UTF-8"));
		output.close();
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		doPost(request, response);
	}
}
