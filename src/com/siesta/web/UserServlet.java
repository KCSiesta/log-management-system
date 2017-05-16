package com.siesta.web;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.siesta.dao.UserDao;
import com.siesta.model.User;
import com.siesta.util.DateUtil;
import com.siesta.util.DbUtil;
import com.siesta.util.PropertiesUtil;

public class UserServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	DbUtil dbUtil=new DbUtil();
	UserDao userDao=new UserDao();
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String action=request.getParameter("action");
		if("preSave".equals(action)){
			userPreSave(request,response);
		}else if("save".equals(action)){
			userSave(request,response);
		}
	}
	
	private void userPreSave(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
			request.setAttribute("mainPage", "user/userSave.jsp");
			request.getRequestDispatcher("mainTemp.jsp").forward(request, response);		
	}
	
	private void userSave(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		FileItemFactory factory=new DiskFileItemFactory();//创建磁盘工厂
		//ServletFileUpload处理表单数据，将数据封装到FileItem中
		//上传文件的所有数据都在FileItem中
		ServletFileUpload upload=new ServletFileUpload(factory);
		//使用集合接收，因为上传的可能不仅仅是一个文件
		List<FileItem> items=null;
		try {
			items=upload.parseRequest(request);
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//解析request请求中的数据
		Iterator<FileItem> itr=items.iterator();
		
		HttpSession session=request.getSession();
		
		User user=(User)session.getAttribute("currentUser");
		boolean imageChange=false;
		//集合数据进行遍历
		while(itr.hasNext()){
			FileItem item=(FileItem)itr.next();
			//普通的表单元素
			if(item.isFormField()){
				String fieldName=item.getFieldName();//获取表单值
				if("nickName".equals(fieldName)){
					//以指定的编码保存昵称数据
					user.setNickName(item.getString("utf-8"));
				}
				if("mood".equals(fieldName)){
					//以指定的编码保存心情数据
					user.setMood(item.getString("utf-8"));
				}
			}else if(!"".equals(item.getName())){//获取上传文件的字段名
				try{
		              imageChange=true;
	                    //使用格式化的时间当图片名称
	                    /*String imageName=DateUtil.getCurrentDateStr();
	                    String filePath1=PropertiesUtil.getValue("imagePath")+user.getImageName();
	                    user.setImageName(imageName+"."+item.getName().split("\\.")[1]);
	                    String filePath=PropertiesUtil.getValue("imagePath")+imageName+"."+item.getName().split("\\.")[1];
	                    item.write(new File(filePath));
	                    (new File(filePath1)).delete();
	                    Thread.sleep(5000);*/
	                     
	                    String imageName=DateUtil.getCurrentDateStr();
	                    String realPath=getServletContext().getRealPath("/userImages");
//	                  System.out.println("路径"+realPath);
	                    String imageFile=imageName+"."+item.getName().split("\\.")[1];
	                    File saveFile1=new File(realPath,user.getImageName());
	                    user.setImageName(imageFile);//为用户设置头像
//	                  System.out.println(imageFile);
	                    //创建指定路径
	                    File saveFile=new File(realPath,imageFile);
	                    item.write(saveFile);
	                    saveFile1.delete();
//	                  Thread.sleep(5000);
                       
				
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
		if(!imageChange){
			//replaceFirst,替换第一个子字符串
			//如果图片没有修改，设置为默认图片
			user.setImageName(user.getImageName().replaceFirst(PropertiesUtil.getValue("imageFile"), ""));
		}
		
		Connection con=null;
		try {
			con=dbUtil.getCon();
			int saveNums=userDao.userUpdate(con, user);
			if(saveNums>0){
				user.setImageName(PropertiesUtil.getValue("imageFile")+user.getImageName());
				session.setAttribute("currentUser", user);
				request.getRequestDispatcher("main?all=true").forward(request, response);
			}else{
				request.setAttribute("currentUser", user);
				request.setAttribute("error", "保存失败！");
				request.setAttribute("mainPage", "user/userSave.jsp");
				request.getRequestDispatcher("mainTemp.jsp").forward(request, response);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				dbUtil.closeCon(con);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
