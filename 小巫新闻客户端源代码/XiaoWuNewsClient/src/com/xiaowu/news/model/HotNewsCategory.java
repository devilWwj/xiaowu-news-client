package com.xiaowu.news.model;

public class HotNewsCategory {
	//热榜类型编号
	private int hotNewsCid;
	//热榜标题
	private String hotNewsTitle;
	
	public HotNewsCategory() {
		super();
	}

	public HotNewsCategory(int hotNewsCid, String hotNewsTitle) {
		super();
		this.hotNewsCid = hotNewsCid;
		this.hotNewsTitle = hotNewsTitle;
	}

	public int getHotNewsCid() {
		return hotNewsCid;
	}

	public void setHotNewsCid(int hotNewsCid) {
		this.hotNewsCid = hotNewsCid;
	}

	public String getHotNewsTitle() {
		return hotNewsTitle;
	}

	public void setHotNewsTitle(String hotNewsTitle) {
		this.hotNewsTitle = hotNewsTitle;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return hotNewsTitle;
	}
}
