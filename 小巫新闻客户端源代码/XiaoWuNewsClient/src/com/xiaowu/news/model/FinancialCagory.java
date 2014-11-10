package com.xiaowu.news.model;

public class FinancialCagory {
	//财经指数分类
	private int financialCid;
	//财经分类标题
	private String finncialTitle;
	
	public FinancialCagory() {
		super();
	}

	public FinancialCagory(int financialCid, String finncialTitle) {
		super();
		this.financialCid = financialCid;
		this.finncialTitle = finncialTitle;
	}

	public int getFinancialCid() {
		return financialCid;
	}

	public void setFinancialCid(int financialCid) {
		this.financialCid = financialCid;
	}

	public String getFinncialTitle() {
		return finncialTitle;
	}

	public void setFinncialTitle(String finncialTitle) {
		this.finncialTitle = finncialTitle;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return finncialTitle;
	}
}
