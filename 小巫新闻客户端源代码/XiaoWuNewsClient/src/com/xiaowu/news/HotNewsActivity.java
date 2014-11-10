package com.xiaowu.news;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaowu.news.custom.ConstomSimpleAdapter;
import com.xiaowu.news.model.HotNewsCategory;
import com.xiaowu.news.util.DensityUtil;
import com.xiaowu.news.util.StringUtil;

public class HotNewsActivity extends Activity {
	private final int COLUMNWIDTH_PX = 75; // GridView每个单元格之间的宽度(像素)
	private int mColumnWidth_dip;
	private GridView hotNewsCategory = null;
	private ListView hotNewsList;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hot_news_layout);

		// 将单元格之间像素距离转换为dip
		mColumnWidth_dip = DensityUtil.px2dip(this, COLUMNWIDTH_PX);

		// 创建一个网格布局实现热榜新闻的栏目布局
		hotNewsCategory = new GridView(this);
		//添加GridView到布局当中
		addGridView();
		
		hotNewsCategory.setOnItemClickListener(new OnItemClickListener() {
			TextView hotNewsTitle;
		
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				for(int i = 0; i < parent.getCount(); i++) {
					hotNewsTitle = (TextView) parent.getChildAt(i);
					hotNewsTitle.setTextColor(0XFFADB2AD);
					hotNewsTitle.setBackgroundDrawable(null);
				}
				
				hotNewsTitle = (TextView) view;
				hotNewsTitle.setTextColor(0xFFFFFFFF);
				hotNewsTitle
				.setBackgroundResource(R.drawable.image_categorybar_item_selected_background);
				Toast.makeText(HotNewsActivity.this, hotNewsTitle.getText(),
						Toast.LENGTH_SHORT).show();
				
				
			}
		});
		List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("hotNewsTitle", "笑脸");
		hashMap.put("hotNewsImage", R.drawable.image_prompt_smiley);
		data.add(hashMap);
		SimpleAdapter adapter = new SimpleAdapter(this, data
				, R.layout.hot_news_list_item_layout
				, new String[]{"hotNewsTitle", "hotNewsImage"}, new int[]{R.id.hotNews_title, R.id.hotnews_image});
		hotNewsList = (ListView) findViewById(R.id.hot_news_list);
		hotNewsList.setAdapter(adapter);
		
		
		
	}
	
	/**
	 *	实现往热榜分类栏添加条目
	 */
	private void addGridView() {
		// 获取数组资源
		String[] hotNewsCagoryArray = getResources().getStringArray(
				R.array.howNewsCategories);

		// 定义一个List数组，用来存放HashMap对象
		List<HashMap<String, HotNewsCategory>> data = new ArrayList<HashMap<String, HotNewsCategory>>();
		// 分割新闻字符串
		for (int i = 0; i < hotNewsCagoryArray.length; i++) {
			String temp[] = hotNewsCagoryArray[i].split("[|]");
			if (temp.length == 2) {
				int cid = StringUtil.string2Int(temp[0]);
				String title = temp[1];
				HotNewsCategory type = new HotNewsCategory(cid, title);
				// 定义一个HashMap对象，用来存放键值对
				HashMap<String, HotNewsCategory> hashMap = new HashMap<String, HotNewsCategory>();
				hashMap.put("hotNews_title", type);
				data.add(hashMap);
			}
		}

		ConstomSimpleAdapter simpleAdapter = new ConstomSimpleAdapter(this,
				data, R.layout.hot_news_category_item_layout,
				new String[] { "hotNews_title" },
				new int[] { R.id.hotNews_title });


		// 设置单元格的背景色为透明，这样选择分类时就不会显示黄色背景了
		hotNewsCategory.setSelector(new ColorDrawable(Color.TRANSPARENT));
		// 设置每一个单元格宽度
		hotNewsCategory.setColumnWidth(mColumnWidth_dip);
		// 设置网格视图的列数
		hotNewsCategory.setNumColumns(GridView.AUTO_FIT);
		// 设置对齐方式
		hotNewsCategory.setGravity(Gravity.CENTER);
		// 根据单元格的宽度和数目计算网格视图的宽度
		int width = mColumnWidth_dip * data.size();
		// 获取布局参数
		LayoutParams params = new LayoutParams(width, LayoutParams.WRAP_CONTENT);
		// 设置参数
		hotNewsCategory.setLayoutParams(params);
		// 设置Adapter
		hotNewsCategory.setAdapter(simpleAdapter);
		// 通过ID获取LinearLayout布局对象
		LinearLayout categoryLayout = (LinearLayout) findViewById(R.id.category_layout);
		// 将网格视图组件添加到LinearLayout布局当中
		categoryLayout.addView(hotNewsCategory);
	}
}
