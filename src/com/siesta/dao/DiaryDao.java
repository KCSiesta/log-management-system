package com.siesta.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.siesta.model.Diary;
import com.siesta.model.PageBean;
import com.siesta.util.DateUtil;
import com.siesta.util.StringUtil;

public class DiaryDao {
    /**
     * 获取所有日志记录
     * @param con
     * @param pageBean
     * @param s_diary
     * @return
     * @throws Exception
     */
	public List<Diary> diaryList(Connection con,PageBean pageBean,Diary s_diary)throws Exception{
		List<Diary> diaryList=new ArrayList<Diary>();//new一个ArrayList<Diary>的对象
		//用StringBuffer获取sql查询
		StringBuffer sb=new StringBuffer("select * from t_diary t1,t_diaryType t2 where t1.typeId=t2.diaryTypeId ");
		if(StringUtil.isNotEmpty(s_diary.getTitle())){
			sb.append(" and t1.title like '%"+s_diary.getTitle()+"%'");
		}
		if(s_diary.getTypeId()!=-1){
			sb.append(" and t1.typeId="+s_diary.getTypeId());
		}
		if(StringUtil.isNotEmpty(s_diary.getReleaseDateStr())){
			sb.append(" and DATE_FORMAT(t1.releaseDate,'%Y年%m月')='"+s_diary.getReleaseDateStr()+"'");
		}
		//append一个根据时间降序的sql语句
		sb.append(" order by t1.releaseDate desc");//
		if(pageBean!=null){
			sb.append(" limit "+pageBean.getStart()+","+pageBean.getPageSize());
		}
		PreparedStatement pstmt=con.prepareStatement(sb.toString());//使用prepareStatement执行，参数为sb.toString，返回一个resultSet
		//如果resultSet里有值，则往一个新new的diary里填充获得的数据diaryId、title、content。
		ResultSet rs=pstmt.executeQuery();
		while(rs.next()){
			Diary diary=new Diary();
			diary.setDiaryId(rs.getInt("diaryId"));
			diary.setTitle(rs.getString("title"));
			diary.setContent(rs.getString("content"));
			diary.setReleaseDate(DateUtil.formatString(rs.getString("releaseDate"), "yyyy-MM-dd HH:mm:ss"));
			diaryList.add(diary);
		}
		return diaryList;
	}
    /**
     * 获取日志记录总数
     * @param con
     * @param s_diary
     * @return
     * @throws Exception
     */
	public int diaryCount(Connection con,Diary s_diary)throws Exception{
		//用stringBuffer获得sql查询
		StringBuffer sb=new StringBuffer("select count(*) as total from t_diary t1,t_diaryType t2 where t1.typeId=t2.diaryTypeId ");
		if(StringUtil.isNotEmpty(s_diary.getTitle())){
			sb.append(" and t1.title like '%"+s_diary.getTitle()+"%'");
		}
		if(s_diary.getTypeId()!=-1){
			sb.append(" and t1.typeId="+s_diary.getTypeId());
		}
		if(StringUtil.isNotEmpty(s_diary.getReleaseDateStr())){
			sb.append(" and DATE_FORMAT(t1.releaseDate,'%Y年%m月')='"+s_diary.getReleaseDateStr()+"'");
		}
		//使用prepareStatement执行，参数为sb.toString，返回一个resultSet
		PreparedStatement pstmt=con.prepareStatement(sb.toString());
		//如果resultSet里有值，返回total值，没有则返回0。
		ResultSet rs=pstmt.executeQuery();
		if(rs.next()){
			return rs.getInt("total");
		}else{
			return 0;
		}
	}
    /**
     * 按日志时间分类展示
     * @param con
     * @return
     * @throws Exception
     */
	public List<Diary> diaryCountList(Connection con)throws Exception{
		List<Diary> diaryCountList=new ArrayList<Diary>();
		String sql="SELECT DATE_FORMAT(releaseDate,'%Y年%m月') as releaseDateStr ,COUNT(*) AS diaryCount  FROM t_diary GROUP BY DATE_FORMAT(releaseDate,'%Y年%m月') ORDER BY DATE_FORMAT(releaseDate,'%Y年%m月') DESC;";
		PreparedStatement pstmt=con.prepareStatement(sql);
		ResultSet rs=pstmt.executeQuery();
		while(rs.next()){
			Diary diary=new Diary();
			diary.setReleaseDateStr(rs.getString("releaseDateStr"));
			diary.setDiaryCount(rs.getInt("diaryCount"));
			diaryCountList.add(diary);
		}
		return diaryCountList;
	}
    /**
     * 展示日志
     * @param con
     * @param diaryId
     * @return
     * @throws Exception
     */
	public Diary diaryShow(Connection con,String diaryId)throws Exception{
		String sql="select * from t_diary t1,t_diaryType t2 where t1.typeId=t2.diaryTypeId and t1.diaryId=?";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, diaryId);
		ResultSet rs=pstmt.executeQuery();
		Diary diary=new Diary();
		if(rs.next()){
			diary.setDiaryId(rs.getInt("diaryId"));
			diary.setTitle(rs.getString("title"));
			diary.setContent(rs.getString("content"));
			diary.setTypeId(rs.getInt("typeId"));
			diary.setTypeName(rs.getString("typeName"));
			diary.setReleaseDate(DateUtil.formatString(rs.getString("releaseDate"),"yyyy-MM-dd HH:mm:ss"));
		}
		return diary;
	}
    /**
     * 日志添加
     * @param con
     * @param diary
     * @return int 
     * @throws Exception
     */
	public int diaryAdd(Connection con,Diary diary)throws Exception{
		String sql="insert into t_diary values(null,?,?,?,now())";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, diary.getTitle());
		pstmt.setString(2, diary.getContent());
		pstmt.setInt(3, diary.getTypeId());
		return pstmt.executeUpdate();
	}
    /**
     * 删除日志
     * @param con
     * @param diaryId
     * @return int 
     * @throws Exception
     */
	public int diaryDelete(Connection con,String diaryId)throws Exception{
		String sql="delete from t_diary where diaryId=?";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, diaryId);
		return pstmt.executeUpdate();
	}
    /**
     * 日志更新
     * @param con
     * @param diary
     * @return int 
     * @throws Exception
     */
	public int diaryUpdate(Connection con,Diary diary)throws Exception{
		String sql="update t_diary set title=?,content=?,typeId=? where diaryId=?";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, diary.getTitle());
		pstmt.setString(2, diary.getContent());
		pstmt.setInt(3, diary.getTypeId());
		pstmt.setInt(4, diary.getDiaryId());
		return pstmt.executeUpdate();
	}
    /**
     * 判断某一个日志类下是否存在日志，如果存在 返回true
     * @param con
     * @param typeId
     * @return
     * @throws Exception
     */
	public boolean existDiaryWithTypeId(Connection con,String typeId)throws Exception{
		String sql="select * from t_diary where typeId=?";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, typeId);
		ResultSet rs=pstmt.executeQuery();
		if(rs.next()){
			return true;
		}else{
			return false;
		}
	}
}

