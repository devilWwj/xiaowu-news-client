package com.xiaowu.news;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.xiaowu.news.service.SyncHttp;
import com.xiaowu.news.thread.PostCommentsThread;

public class NewsDetailActivity extends Activity {

	private final int FINISH = 0;				//代表线程的状态的结束
	private LayoutInflater mNewsbodyLayoutInflater;
	private ViewFlipper mNewsBodyFlipper;		//屏幕切换控件
	private ArrayList<HashMap<String, Object>> mNewsData;
	private float mStartX;						//手指按下的开始位置
	private int mPosition = 0;					//点击新闻位置		
	private int mCursor = 0;					//用来标记新闻点击的位置
	private int mNid;							//新闻编号
	private Button mNewsDetailTitleBarComm;		//显示评论条数的按钮
	private ConstomTextView mNewsBodyDetail;	//新闻详细内容
	private LinearLayout mNewsReplyEditLayout;	//新闻回复的布局
	private LinearLayout mNewsReplyImgLayout;	//新闻图片回复的布局
	private EditText mNewsReplyEditText;		//新闻回复的文本框
	private ImageButton mShareNewsButton;		//分享新闻的按钮
	private ImageButton mFavoritesButton;		//收藏新闻的按钮
	private boolean keyboardShow;				//键盘是否显示
	private Handler mHandler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.arg1) {
			case FINISH:
				//把获取到的新闻显示到界面上
				ArrayList<HashMap<String, Object>> bodyList = (ArrayList<HashMap<String, Object>>) msg.obj;
				mNewsBodyDetail.setText(bodyList);
				break;
			}
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newsdetails_layout);

		mNewsReplyEditLayout = (LinearLayout) findViewById(R.id.news_reply_edit_layout);
		mNewsReplyImgLayout = (LinearLayout) findViewById(R.id.news_reply_img_layout);
		
		Button newsDetailPrev = (Button) findViewById(R.id.newsdetail_titlebar_previous);
		Button newsDetailNext = (Button) findViewById(R.id.newsdetail_titlebar_next);
		mNewsDetailTitleBarComm = (Button) findViewById(R.id.newsdetail_titlebar_comments);
		mNewsReplyEditText = (EditText) findViewById(R.id.news_reply_edittext);
		mShareNewsButton = (ImageButton) findViewById(R.id.news_share_btn);
		mFavoritesButton = (ImageButton) findViewById(R.id.news_favorites_btn);
		
		
		NewsDetailOnClickListener newsDetailOnClickListener = new NewsDetailOnClickListener();
		
		newsDetailPrev.setOnClickListener(newsDetailOnClickListener);
		newsDetailNext.setOnClickListener(newsDetailOnClickListener);
		mNewsDetailTitleBarComm.setOnClickListener(newsDetailOnClickListener);
		mShareNewsButton.setOnClickListener(newsDetailOnClickListener);
		mFavoritesButton.setOnClickListener(newsDetailOnClickListener);
		
		Button newsReplyPost = (Button) findViewById(R.id.news_reply_post);
		newsReplyPost.setOnClickListener(newsDetailOnClickListener);
		ImageButton newsReplyImgBtn = (ImageButton) findViewById(R.id.news_reply_img_btn);
		newsReplyImgBtn.setOnClickListener(newsDetailOnClickListener);
		
		
		//获取传送的数据
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		String categoryName = bundle.getString("categoryTitle");
		TextView titleBarTitle = (TextView) findViewById(R.id.newsdetail_titlebar_title);
		//设置标题栏的标题
		titleBarTitle.setText(categoryName);
		//获取新闻集合
		Serializable serializable = bundle.getSerializable("newsData");
		mNewsData = (ArrayList<HashMap<String, Object>>) serializable;

		//获取点击位置
		mCursor = mPosition = bundle.getInt("position");
		
		mNewsBodyFlipper = (ViewFlipper) findViewById(R.id.news_body_flipper);
		// 获取LayoutInflater对象
		mNewsbodyLayoutInflater = getLayoutInflater();
		
		inflateView(0);
		//启动线程
		new UpdateNewsThread().start();
	}

	/**
	 * 显示上一条新闻
	 */
	private void showPrevious() {
		if(mPosition > 0) {
			mPosition--;
			//记录当前新闻编号
			HashMap<String, Object> hashMap = mNewsData.get(mPosition);
			mNid = (Integer) hashMap.get("nid");
			if(mCursor > mPosition){
				mCursor = mPosition;
				inflateView(0);
				mNewsBodyFlipper.showNext();
			}
			mNewsBodyFlipper.setInAnimation(this, R.anim.push_right_in);	//设置下一页进来时的动画
			mNewsBodyFlipper.setOutAnimation(this, R.anim.push_right_out);	//设置当前页出去的动画
			mNewsBodyFlipper.showPrevious();
		}
		else {
			Toast.makeText(NewsDetailActivity.this, "没有上一篇新闻", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 显示下一条新闻
	 */
	private void showNext() {
		if(mPosition < mNewsData.size() - 1){
			// 设置下一屏动画
			mNewsBodyFlipper.setInAnimation(this, R.anim.push_left_in);
			mNewsBodyFlipper.setOutAnimation(this, R.anim.push_left_out);
			mPosition++;
			//记录当前新闻编号
			HashMap<String, Object> hashMap = mNewsData.get(mPosition);
			mNid = (Integer) hashMap.get("nid");
			if(mPosition >= mNewsBodyFlipper.getChildCount()){
				inflateView(mNewsBodyFlipper.getChildCount());
			}
			mNewsBodyFlipper.showNext();
		} else {
			Toast.makeText(NewsDetailActivity.this, "没有下篇新闻", Toast.LENGTH_SHORT).show();
		}
	}

	private void inflateView(int index) {
		//获取点击新闻信息
		HashMap<String, Object> hashMap = mNewsData.get(mPosition);
		mNid = (Integer) hashMap.get("nid");

		View mNewsBodyView = mNewsbodyLayoutInflater.inflate(
				R.layout.newsbody_layout, null);
		mNewsDetailTitleBarComm.setText(hashMap.get("newslist_item_comments").toString() + "跟帖");
		//新闻标题
		TextView newsTitle = (TextView) mNewsBodyView
				.findViewById(R.id.news_body_title);
		newsTitle.setText(hashMap.get("newslist_item_title").toString());
		//新闻的出处和发布时间
		TextView newsPtimeAndSource = (TextView) mNewsBodyView
				.findViewById(R.id.news_body_ptime_source);
		newsPtimeAndSource.setText(hashMap.get("newslist_item_source").toString() 
				+ "		" + hashMap.get("newslist_item_ptime").toString());
		mNewsBodyDetail = (ConstomTextView) mNewsBodyView
				.findViewById(R.id.news_body_details);
		mNewsBodyDetail.setText(getNewsBody());
		mNewsBodyFlipper.addView(mNewsBodyView, index);
		mNewsBodyDetail.setOnTouchListener(new NewsBodyOntouchListener());
	}

	// 定义内部类--用于处理标题栏的按钮的触发事件
	private class NewsDetailOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			InputMethodManager m = (InputMethodManager) mNewsReplyEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			// TODO Auto-generated method stub
			switch (v.getId()) {
			//上一篇
			case R.id.newsdetail_titlebar_previous:
				showPrevious();
				break;
			//下一篇
			case R.id.newsdetail_titlebar_next:
				showNext();
				break;
			//跟帖
			case R.id.newsdetail_titlebar_comments:
				Intent intent = new Intent(NewsDetailActivity.this,
						CommentsActivity.class);
				intent.putExtra("nid", mNid);
				startActivity(intent);
				break;
			//“写跟帖”图片
			case R.id.news_reply_img_btn:
				mNewsReplyEditLayout.setVisibility(View.VISIBLE);
				mNewsReplyImgLayout.setVisibility(View.GONE);
				mNewsReplyEditText.requestFocus();
				//显示输入法
				m.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
				keyboardShow = true;
				break;
			//分享按钮
			case R.id.news_share_btn:
				Intent shareIntent = new Intent(Intent.ACTION_SEND);
				//纯文本
				shareIntent.setType("text/plain");
				shareIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
				shareIntent.putExtra(Intent.EXTRA_TEXT, "我想将这个分享给你...."+ getTitle());
				startActivity(Intent.createChooser(shareIntent, getTitle()));
				break;
			//收藏按钮
			case R.id.news_favorites_btn:
				Toast.makeText(NewsDetailActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
				break;
			//发表按钮
			case R.id.news_reply_post:
				//隐藏输入法
				m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				String str = mNewsReplyEditText.getText().toString();
				if(str.equals("")){
					Toast.makeText(NewsDetailActivity.this, "不能为空",
							Toast.LENGTH_SHORT).show();
				}
				else {
					mNewsReplyEditLayout.post(new PostCommentsThread(mNid, "广州市",
							str + "",
							new NewsDetailActivity()));
					mNewsReplyEditLayout.setVisibility(View.GONE);
					mNewsReplyImgLayout.setVisibility(View.VISIBLE);
				}
				break;
			}
		}
	}

	private class NewsBodyOntouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getAction()) {
				//手指按下
			case MotionEvent.ACTION_DOWN:
				if(keyboardShow){
					mNewsReplyEditLayout.setVisibility(View.GONE);
					mNewsReplyImgLayout.setVisibility(View.VISIBLE);
					//隐藏输入法
					InputMethodManager m = (InputMethodManager) mNewsReplyEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					keyboardShow = false;
				}
				//得到按下的横坐标的位置
				mStartX = event.getX();
				break;
			case MotionEvent.ACTION_UP:
				// 往左滑动
				if (event.getX() < mStartX) {
					showNext();
				}
				// 往右滑动
				else if (event.getX() > mStartX) {
					showPrevious();
				}
				break;
			}
			return true;
		}
	}
	
	/**
	 * 定义一个线程类，用来更新获取到新闻的信息
	 * @author Administrator
	 *
	 */
	private class UpdateNewsThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			ArrayList<HashMap<String, Object>> newsStr = getNewsBody();
			Message msg = mHandler.obtainMessage();	//获取msg
			msg.arg1 = FINISH;			
			msg.obj = newsStr;
			mHandler.sendMessage(msg);	//给Handler发送信息
		}
	}
	
	
	/**
	 * 获取新闻详细信息
	 * @return
	 */
	private ArrayList<HashMap<String, Object>> getNewsBody(){
		//String retStr = "网络连接失败,请稍后再试";
		ArrayList<HashMap<String, Object>> bodylist = new ArrayList<HashMap<String,Object>>();
		
		SyncHttp syncHttp = new SyncHttp();
		//模拟器:url = "http://10.0.2.2:8080/web/getNews";
		//本机:http://127.0.0.1:8080
		//wifi局域网:http://192.168.220.1:8080
		String url = "http://10.0.2.2:8080/web/getNews";
		String params = "nid=" + mNid;
		try {
			String retString = syncHttp.httpGet(url, params);
			JSONObject  jsonObject = new JSONObject(retString);
			//获取返回码，0表示成功
			int retCode = jsonObject.getInt("ret");
			if(retCode == 0) {
				JSONObject dataObject = jsonObject.getJSONObject("data");
				JSONObject newsObject = dataObject.getJSONObject("news");
				//retStr = newsObject.getString("body");
				JSONArray bodyArray = newsObject.getJSONArray("body");
				for(int i = 0; i < bodyArray.length(); i++) {
					JSONObject object = (JSONObject) bodyArray.opt(i);
					HashMap<String, Object> hashMap = new HashMap<String, Object>();
					hashMap.put("index", object.get("index"));
					hashMap.put("type", object.get("type"));
					hashMap.put("value", object.get("value"));
					bodylist.add(hashMap);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return bodylist;
	}
	
	
	@Override
	public boolean onCreatePanelMenu(int featureId, Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 0, 0, "分享");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()) {
		case 0:
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			//纯文本
			shareIntent.setType("text/plain");
			shareIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
			shareIntent.putExtra(Intent.EXTRA_TEXT, "我想把这个分享给你:" + getTitle());
			shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(Intent.createChooser(shareIntent, getTitle()));
			System.out.println(getTitle());
			break;
			
		}
		return super.onOptionsItemSelected(item);
	}
}
