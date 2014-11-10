package com.szy.web.dao;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.szy.web.model.News;


/**
 *@author coolszy
 *@date Feb 19, 2012
 *@blog http://blog.92coding.com
 */
public class NewsDAO
{
	SqlManager manager;
	String sql = "";
	ResultSet rs;

	public NewsDAO() throws IOException, ClassNotFoundException
	{
		manager = SqlManager.createInstance();
	}

	/**
	 * 获取新闻内容
	 * @param nid 新闻编号
	 * @return
	 * @throws SQLException
	 */
	public News getNews(int nid) throws SQLException
	{
		sql = "SELECT nid,cid,title,body,source,ptime,imgsrc FROM t_news WHERE nid=? and deleted=false";
		Object[] params = new Object[]{ nid };
		manager.connectDB();
		rs = manager.executeQuery(sql, params);
		News news = new News();
		if (rs.next())
		{
			news.setNid(rs.getInt("nid"));
			news.setCid(rs.getInt("cid"));
			news.setTitle(rs.getString("title"));
			news.setBody(rs.getString("body"));
			news.setSource(rs.getString("source"));
			news.setPtime(rs.getString("ptime"));
			news.setImgSrc(rs.getString("imgsrc"));
		}
		manager.closeDB();
		return news;
	}

	/**
	 * 获取指定类别的新闻列表
	 * @param tid 新闻类型
	 * @param startNid 起始编号
	 * @param count 返回数量
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<News> getSpecifyCategoryNews(int tid, int startNid, int count)
			throws SQLException
	{
		ArrayList<News> list = new ArrayList<News>();
		sql = "SELECT nid,cid,title,digest,source,ptime,imgsrc FROM t_news WHERE cid=? AND deleted=false ORDER BY ptime desc LIMIT ?,?";
		Object[] params = new Object[]{ tid, startNid, count };
		manager.connectDB();
		rs = manager.executeQuery(sql, params);
		while (rs.next())
		{
			News news = new News();
			news.setNid(rs.getInt("nid"));
			news.setCid(rs.getInt("cid"));
			news.setTitle(rs.getString("title"));
			news.setDigest(rs.getString("digest"));
			news.setSource(rs.getString("source"));
			news.setPtime(rs.getString("ptime"));
			news.setImgSrc(rs.getString("imgsrc"));
			list.add(news);
		}
		manager.closeDB();
		return list;
	}
}
