package com.xiaowu.news;


import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

public class TabHostActivity extends TabActivity implements OnCheckedChangeListener{
	
	private TabHost mTabHost;
	private RadioGroup radioGroup;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maintabs);
		
		// 实例化TabHost
		mTabHost = this.getTabHost();
		
		// 添加选项卡
		mTabHost.addTab(mTabHost.newTabSpec("ONE").setIndicator("ONE")
				.setContent(new Intent(this, MainActivity.class)));
		mTabHost.addTab(mTabHost.newTabSpec("TWO").setIndicator("TWO")
				.setContent(new Intent(this, SubscribeActivity.class)));
		mTabHost.addTab(mTabHost.newTabSpec("THREE").setIndicator("THREE")
				.setContent(new Intent(this, HotNewsActivity.class)));
		mTabHost.addTab(mTabHost.newTabSpec("FOUR").setIndicator("FOUR")
				.setContent(new Intent(this, FinancialActivity.class)));
		mTabHost.addTab(mTabHost.newTabSpec("FIVE").setIndicator("FIVE")
				.setContent(new Intent(this, SearchNewsActiity.class)));
		
		radioGroup = (RadioGroup) findViewById(R.id.main_radio);
		radioGroup.setOnCheckedChangeListener(this);
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		switch(checkedId) {
		case R.id.news_home_tabbar_button:
			mTabHost.setCurrentTabByTag("ONE");
			break;
		case R.id.news_subscription_tabbar_button:
			mTabHost.setCurrentTabByTag("TWO");
			break;
		case R.id.hot_news_tabbar_button:
			mTabHost.setCurrentTabByTag("THREE");
			break;
		case R.id.financial_index_tabbar_button:
			mTabHost.setCurrentTabByTag("FOUR");
			break;
		case R.id.search_news_tabbar_button:
			mTabHost.setCurrentTabByTag("FIVE");
			break;		
	}
	}
}
