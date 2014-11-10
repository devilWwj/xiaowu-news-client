package com.xiaowu.news;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.xiaowu.news.model.Category;
import com.xiaowu.news.util.StringUtil;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class AddSubscriptionActivity extends Activity{
	
	private ListView subList = null;			//关注列表
	private SimpleAdapter subListAdapter; 		//用于显示关注列表的Adaper
	private ArrayList<HashMap<String, Category>> subListData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_subscription_layout);
		
		
		//从界面当中获取ListView
		subList = (ListView) findViewById(R.id.subList);
		subListData = new ArrayList<HashMap<String,Category>>();
		// 获取数组资源
		String[] categoryArray = getResources().getStringArray(
				R.array.categories);
		for(int i = 0; i < categoryArray.length; i++) {
			String temp[] = categoryArray[i].split("[|]");
			if(temp.length == 2) {
				int cid = StringUtil.string2Int(temp[0]);
				String title = temp[1];
				Category type = new Category(cid, title);
				// 定义一个HashMap对象，用来存放键值对
				HashMap<String, Category> hashMap = new HashMap<String, Category>();	
				hashMap.put("category_title", type);
				subListData.add(hashMap);
			}
		}
		
		subListAdapter = new SimpleAdapter(this, subListData, R.layout.sublist_item_layout
				, new String[]{"category_title"}, new int[]{R.id.category_title});
		
		
		//显示列表
		subList.setAdapter(subListAdapter);
		
	}
}
