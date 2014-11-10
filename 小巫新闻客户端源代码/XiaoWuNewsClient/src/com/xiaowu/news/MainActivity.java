package com.xiaowu.news;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaowu.news.custom.ConstomSimpleAdapter;
import com.xiaowu.news.model.Category;
import com.xiaowu.news.service.SyncHttp;
import com.xiaowu.news.update.UpdateManager;
import com.xiaowu.news.util.DensityUtil;
import com.xiaowu.news.util.StringUtil;

/**
 * 
 * @author wwj
 * 
 */
public class MainActivity extends Activity {

	private final int COLUMNWIDTH_PX = 56; 				// GridView每个单元格的宽度(像素)
	private final int FLINGVELOCITY_PX = 800; 			// ViewFilper滑动的距离(像素)
	private final int NEWSCOUNT = 5; 					// 显示新闻的条数
	private final int SUCCESS = 0;						// 加载新闻成功
	private final int NONEWS = 1;						// 没有新闻
	private final int NOMORENEWS = 2;					// 没有更多新闻
	private final int LOADERROR = 3;					// 加载失败

	private long exitTime;								//按返回键退出的时间
	private int mColumnWidth_dip;						
	private int mFlingVelocity_dip;
	private int mCid; 									// 新闻编号
	private String mCategoryTitle;						// 新闻分类标题
	private ListView mNewslist; 						// 新闻列表
	private SimpleAdapter mNewslistAdapter; 			// 为新闻内容提供需要显示的列表
	private ArrayList<HashMap<String, Object>> mNewsData; // 存储新闻信息的数据集合
	private LayoutInflater mInflater; 					// 用来动态载入没有loadmore_layout界面
	
	private Button category_Button = null;				// 新闻分类标题栏的向右查看的按钮
	
	private HorizontalScrollView categoryScrollView = null;// 水平滚动图

	private Button mTitleBarRefresh;					// 标题栏的刷新按钮
	private ProgressBar mTitleBarProgress;				// 进度条
	private Button mLoadmoreButton;						// 加载更多按钮

	private LoadNewsAsyncTack mLoadNewsAsyncTack;		// 声明LoadNewsAsyncTack引用
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_home_layout);
		
		//通过id来获取按钮的引用
		mTitleBarRefresh = (Button) findViewById(R.id.titlebar_refresh);
		mTitleBarProgress = (ProgressBar) findViewById(R.id.titlebar_progress);
		
		mTitleBarRefresh.setOnClickListener(loadmoreListener);
		// 将px转换为dip
		mColumnWidth_dip = DensityUtil.px2dip(this, COLUMNWIDTH_PX);
		mFlingVelocity_dip = DensityUtil.px2dip(this, FLINGVELOCITY_PX);
		//初始化新闻分类的编号
		mCid = 1;
		mCategoryTitle = "焦点";
		mInflater = getLayoutInflater();
		//存储新闻信息的数据集合
		mNewsData = new ArrayList<HashMap<String, Object>>();
		// 获取数组资源
		String[] categoryArray = getResources().getStringArray(
				R.array.categories);

		// 定义一个List数组，用来存放HashMap对象
		final List<HashMap<String, Category>> categories = new ArrayList<HashMap<String, Category>>();
		// 分割新闻字符串
		for (int i = 0; i < categoryArray.length; i++) {
			String temp[] = categoryArray[i].split("[|]");
			if (temp.length == 2) {
				int cid = StringUtil.string2Int(temp[0]);
				String title = temp[1];
				Category type = new Category(cid, title);
				// 定义一个HashMap对象，用来存放键值对
				HashMap<String, Category> hashMap = new HashMap<String, Category>();
				hashMap.put("category_title", type);
				categories.add(hashMap);
			}
		}
		ConstomSimpleAdapter categoryAdapter = new ConstomSimpleAdapter(this,
				categories, R.layout.category_item_layout,
				new String[] { "category_title" },
				new int[] { R.id.category_title });
		// 创建一个网格视图, 用于实现新闻标题的布局
		GridView category = new GridView(this);
		// 设置单元格的背景色为透明，这样选择分类时就不会显示黄色背景了
		category.setSelector(new ColorDrawable(Color.TRANSPARENT));
		// 设置每一个新闻标题的宽度
		category.setColumnWidth(mColumnWidth_dip);
		// 设置网格视图的列数
		category.setNumColumns(GridView.AUTO_FIT);
		// 设置对齐方式
		category.setGravity(Gravity.CENTER);
		// 根据单元格的宽度和数目计算网格视图的宽度
		int width = mColumnWidth_dip * categories.size();
		// 获取布局参数
		LayoutParams params = new LayoutParams(width, LayoutParams.MATCH_PARENT);
		// 设置参数
		category.setLayoutParams(params);
		// 设置Adapter
		category.setAdapter(categoryAdapter);
		// 通过ID获取LinearLayout布局对象
		LinearLayout categoryLayout = (LinearLayout) findViewById(R.id.category_layout);
		// 将网格视图组件添加到LinearLayout布局当中
		categoryLayout.addView(category);

		// 添加单元格点击事件
		category.setOnItemClickListener(new OnItemClickListener() {
			TextView categoryTitle;

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				for (int i = 0; i < parent.getCount(); i++) {
					categoryTitle = (TextView) parent.getChildAt(i);
					categoryTitle.setTextColor(0XFFADB2AD);
					categoryTitle.setBackgroundDrawable(null);

				}
				categoryTitle = (TextView) view;
				categoryTitle.setTextColor(0xFFFFFFFF);
				categoryTitle
						.setBackgroundResource(R.drawable.image_categorybar_item_selected_background);
				Toast.makeText(MainActivity.this, categoryTitle.getText(),
						Toast.LENGTH_SHORT).show();
				//获取新闻分类编号
				mCid = categories.get(position).get("category_title").getCid();
				mCategoryTitle = categories.get(position).get("category_title").getTitle();
				mLoadNewsAsyncTack = new LoadNewsAsyncTack();
				mLoadNewsAsyncTack.execute(0, true);
			}

		});
		
		//第一次获取新闻列表
		getSpecCatNews(mCid, mNewsData, 0, true);
		// 箭头
		categoryScrollView = (HorizontalScrollView) findViewById(R.id.categorybar_scrollView);
		category_Button = (Button) findViewById(R.id.category_arrow_right);
		category_Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				categoryScrollView.fling(mFlingVelocity_dip);
			}
		});

		mNewslistAdapter = new SimpleAdapter(this, mNewsData,
				R.layout.newslist_item_layout, new String[] {
						"newslist_item_title", "newslist_item_digest",
						"newslist_item_source", "newslist_item_ptime" },
				new int[] { R.id.newslist_item_title,
						R.id.newslist_item_digest, R.id.newslist_item_source,
						R.id.newslist_item_ptime });
		mNewslist = (ListView) findViewById(R.id.news_list);
		
		View footerView = mInflater.inflate(R.layout.loadmore_layout, null);
		//在LiseView下面添加“加载更多”
		mNewslist.addFooterView(footerView);
		//显示列表
		mNewslist.setAdapter(mNewslistAdapter);
		
		mNewslist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						NewsDetailActivity.class);
				intent.putExtra("categoryTitle", mCategoryTitle);
				intent.putExtra("newsData", mNewsData);
				intent.putExtra("position", position);
				startActivity(intent);
			}

		});

		mLoadmoreButton = (Button) findViewById(R.id.loadmore_btn);
		mLoadmoreButton.setOnClickListener(loadmoreListener);
		
	}

	/**
	 * 获取指定类型的新闻列表
	 * 
	 * @param cid
	 * @return
	 */
	private int getSpecCatNews(int cid, List<HashMap<String, Object>> newsList,
			int startnid, boolean firstTime) {

		// 如果是第一次加载的话
		if (firstTime) {
			newsList.clear();
		}
		//本机:http://10.0.2.2:8080/web/getSpecifyCategoryNews
		//wifi局域网：192.168.220.1
		String url = "http://10.0.2.2:8080/web/getSpecifyCategoryNews";
		String params = "startnid=" + startnid + "&count=" + NEWSCOUNT
				+ "&cid=" + cid;
		SyncHttp syncHttp = new SyncHttp();

		try {
			// 通过Http协议发送Get请求，返回字符串
			String retStr = syncHttp.httpGet(url, params);
			JSONObject jsonObject = new JSONObject(retStr);
			int retCode = jsonObject.getInt("ret");
			if (retCode == 0) {
				JSONObject dataObj = jsonObject.getJSONObject("data");
				// 获取返回数目
				int totalNum = dataObj.getInt("totalnum");
				if (totalNum > 0) {
					// 获取返回新闻集合
					JSONArray newslistArray = dataObj.getJSONArray("newslist");
					// 将用JSON格式解析的数据添加到数据集合当中
					for (int i = 0; i < newslistArray.length(); i++) {
						JSONObject newsObject = (JSONObject) newslistArray
								.opt(i);
						HashMap<String, Object> hashMap = new HashMap<String, Object>();
						hashMap.put("nid", newsObject.getInt("nid"));
						hashMap.put("newslist_item_title",
								newsObject.getString("title"));
						hashMap.put("newslist_item_digest",
								newsObject.getString("digest"));
						hashMap.put("newslist_item_source",
								newsObject.getString("source"));
						hashMap.put("newslist_item_ptime",
								newsObject.getString("ptime"));
						hashMap.put("newslist_item_comments",
								newsObject.getInt("commentcount"));
						newsList.add(hashMap);
					}
					return SUCCESS;
				} else {
					//第一次加载新闻列表
					if (firstTime) {
						return NONEWS;			//没有新闻
					} else {
						return NOMORENEWS;		//没有更多新闻
					}
				}
			} else {
				return LOADERROR;			//加载新闻失败
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return LOADERROR;			//加载新闻失败
		}
	}

	/**
	 * 为“加载更多”按钮定义匿名内部类
	 */
	private OnClickListener loadmoreListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mLoadNewsAsyncTack = new LoadNewsAsyncTack();
			switch (v.getId()) {
			//点击加载更多
			case R.id.loadmore_btn:
				mLoadNewsAsyncTack.execute(mNewsData.size(), false);	//不是第一次加载新闻里列表
				break;
			//点击刷新按钮
			case R.id.titlebar_refresh:
				mLoadNewsAsyncTack.execute(0, true);
				break;
			}
		}
	};

	/**
	 * 异步更新UI
	 * @author wwj
	 *
	 */
	private class LoadNewsAsyncTack extends AsyncTask<Object, Integer, Integer> {

		//准备运行
		@Override
		protected void onPreExecute() {
			mTitleBarRefresh.setVisibility(View.GONE);
			mTitleBarProgress.setVisibility(View.VISIBLE);
			mLoadmoreButton.setText(R.string.loadmore_text);
		}
		//在后台运行
		@Override
		protected Integer doInBackground(Object... params) {
			return getSpecCatNews(mCid, mNewsData, (Integer) params[0],
					(Boolean) params[1]);
		}
		//完成后台任务
		@Override
		protected void onPostExecute(Integer result) {
			switch (result) {
			//该栏目没有新闻
			case NONEWS:
				Toast.makeText(MainActivity.this, R.string.nonews, Toast.LENGTH_SHORT)
						.show();
				break;
			//该栏目没有更多新闻
			case NOMORENEWS:
				Toast.makeText(MainActivity.this, R.string.nomorenews,
						Toast.LENGTH_SHORT).show();
				break;
			//加载失败
			case LOADERROR:
				Toast.makeText(MainActivity.this, R.string.loadnewserror, Toast.LENGTH_SHORT)
						.show();
				break;
			}
			mTitleBarRefresh.setVisibility(View.VISIBLE);			//刷新按钮设置为可见
			mTitleBarProgress.setVisibility(View.GONE);				//进度条设置为不可见
			mLoadmoreButton.setText(R.string.loadmore_btn);			//按钮信息替换为“加载更多”
			mNewslistAdapter.notifyDataSetChanged();				//通知ListView更新数据
		}
	}
	
	/**
	 * 添加菜单
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(1, 1, 1, "更新");
		menu.add(1, 2, 2, "退出");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case 1:
			UpdateManager updateManager = new UpdateManager(MainActivity.this);
			//检测更新
			updateManager.checkUpdate();
			break;
		case 2:
			finish();
			break;
		}
		return true;
	}
	
	/**
	 * 按键触发的事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK
    			&& event.getAction() == KeyEvent.ACTION_DOWN){
    		if((System.currentTimeMillis() - exitTime > 2000)){
    			Toast.makeText(getApplicationContext(), R.string.backcancel
    					, Toast.LENGTH_LONG).show();
    			exitTime = System.currentTimeMillis();
    		}
    		else{
    			finish();
    			System.exit(0);
    		}
    		return true;
    	}
		return super.onKeyDown(keyCode, event);
	}
}
