package com.siesta.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.siesta.dao.DiaryDao;
import com.siesta.dao.DiaryTypeDao;
import com.siesta.model.Diary;
import com.siesta.model.PageBean;
import com.siesta.util.DbUtil;
import com.siesta.util.PropertiesUtil;
import com.siesta.util.StringUtil;

public class MainServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	DbUtil dbUtil=new DbUtil();
	DiaryDao diaryDao=new DiaryDao();
	DiaryTypeDao diaryTypeDao=new DiaryTypeDao();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		HttpSession session=request.getSession();
		String s_typeId=request.getParameter("s_typeId");
		String s_releaseDateStr=request.getParameter("s_releaseDateStr");
		String s_title=request.getParameter("s_title");
		String all=request.getParameter("all");
		String page=request.getParameter("page");
		Diary diary=new Diary();
		if("true".equals(all)){
			if(StringUtil.isNotEmpty(s_title)){
				diary.setTitle(s_title);
			}
			session.removeAttribute("s_releaseDateStr");
			session.removeAttribute("s_typeId");
			session.setAttribute("s_title", s_title);
		}else{
			if(StringUtil.isNotEmpty(s_typeId)){
				diary.setTypeId(Integer.parseInt(s_typeId));
				session.setAttribute("s_typeId", s_typeId);
				session.removeAttribute("s_releaseDateStr");
				session.removeAttribute("s_title");
			}
			if(StringUtil.isNotEmpty(s_releaseDateStr)){
				s_releaseDateStr=new String(s_releaseDateStr.getBytes("ISO-8859-1"),"UTF-8");
				diary.setReleaseDateStr(s_releaseDateStr);
				session.setAttribute("s_releaseDateStr", s_releaseDateStr);
				session.removeAttribute("s_typeId");
				session.removeAttribute("s_title");
			}
			if(StringUtil.isEmpty(s_typeId)){
				Object o=session.getAttribute("s_typeId");
				if(o!=null){
					diary.setTypeId(Integer.parseInt((String)o));
				}
			}
			if(StringUtil.isEmpty(s_releaseDateStr)){
				Object o=session.getAttribute("s_releaseDateStr");
				if(o!=null){
					diary.setReleaseDateStr((String)o);
				}
			}
			if(StringUtil.isEmpty(s_title)){
				Object o=session.getAttribute("s_title");
				if(o!=null){
					diary.setTitle((String)o);
				}
			}
		}
		if(StringUtil.isEmpty(page)){
			page="1";
		}
		Connection con=null;
		PageBean pageBean=new PageBean(Integer.parseInt(page),Integer.parseInt(PropertiesUtil.getValue("pageSize")));
		try {
			con=dbUtil.getCon();
			List<Diary> diaryList=diaryDao.diaryList(con,pageBean,diary);
			int total=diaryDao.diaryCount(con,diary);
			String pageCode=this.genPagation(total, Integer.parseInt(page), Integer.parseInt(PropertiesUtil.getValue("pageSize")));//生成分页代码
			request.setAttribute("pageCode", pageCode);//将分页代码放到request中
			request.setAttribute("diaryList", diaryList);
			//再通过diaryTypeDao实体调用dao里面的方法，并保存到session中
			session.setAttribute("diaryTypeCountList", diaryTypeDao.diaryTypeCountList(con));
			session.setAttribute("diaryCountList", diaryDao.diaryCountList(con));
			request.setAttribute("mainPage", "diary/diaryList.jsp");
			request.getRequestDispatcher("mainTemp.jsp").forward(request, response);//请求转向mainTemp.jsp页面
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
	/*实现日志记录分页功能，生成分页代码：
	 * currentPage：当前页数
       totalNum：总记录数
       pageSize：每页共多少记录
	 * */
	private String genPagation(int totalNum,int currentPage,int pageSize){
		int totalPage=totalNum%pageSize==0?totalNum/pageSize:totalNum/pageSize+1;
		StringBuffer pageCode=new StringBuffer();
		pageCode.append("<li><a href='main?page=1'>首页</a></li>");
		if(currentPage==1){
			pageCode.append("<li class='disabled'><a href='#'>上一页</a></li>");
		}else{
			pageCode.append("<li><a href='main?page="+(currentPage-1)+"'>上一页</a></li>");
		}
		for(int i=currentPage-2;i<=currentPage+2;i++){
			if(i<1||i>totalPage){
				continue;
			}
			if(i==currentPage){
				pageCode.append("<li class='active'><a href='#'>"+i+"</a></li>");
			}else{
				pageCode.append("<li><a href='main?page="+i+"'>"+i+"</a></li>");
			}
		}
		if(currentPage==totalPage){
			pageCode.append("<li class='disabled'><a href='#'>下一页</a></li>");
		}else{
			pageCode.append("<li><a href='main?page="+(currentPage+1)+"'>下一页</a></li>");
		}
		pageCode.append("<li><a href='main?page="+totalPage+"'>尾页</a></li>");
		return pageCode.toString();
	}
	
	

}
