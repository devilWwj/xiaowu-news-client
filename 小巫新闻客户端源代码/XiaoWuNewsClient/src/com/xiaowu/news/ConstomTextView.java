package com.xiaowu.news;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Html;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ConstomTextView extends LinearLayout{

	//上下文对象
	private Context mContext;
	//声明TypedArray的引用
	private TypedArray mTypedArray;
	//布局参数
	private LayoutParams params;
	
	public ConstomTextView(Context context) {
		super(context);
	}
	
	public ConstomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		this.setOrientation(LinearLayout.VERTICAL);
		//从attrs.xml文件中那个获取自定义属性
		mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.constomTextView);
	}
	
	public void setText(ArrayList<HashMap<String, Object>> datas) {
		//遍历ArrayList
		for(HashMap<String, Object> hashMap : datas) {
			//获取key为"type"的值
			String type = (String) hashMap.get("type");
			//如果value=imaeg
			if(type.equals("image")){
				//获取自定义属性属性
				int imagewidth = mTypedArray.getDimensionPixelOffset(R.styleable.constomTextView_image_width, 100);
				int imageheight = mTypedArray.getDimensionPixelOffset(R.styleable.constomTextView_image_height, 100);
				ImageView imageView = new ImageView(mContext);
				params = new LayoutParams(imagewidth, imageheight);
				params.gravity = Gravity.CENTER_HORIZONTAL;	//居中
				imageView.setLayoutParams(params);
				//显示图片
				imageView.setImageResource(R.drawable.ic_constom);
				imageView.setScaleType(ScaleType.CENTER_INSIDE);
				//将imageView添加到LinearLayout当中
				addView(imageView);
				//启动异步线程更新异步显示图片信息
				new DownloadPicThread(imageView, hashMap.get("value").toString()).start();
			}
			else {
				float textSize = mTypedArray.getDimension(R.styleable.constomTextView_textSize, 16);
				int textColor = mTypedArray.getColor(R.styleable.constomTextView_textColor, 0xFF0000FF);
				TextView textView = new TextView(mContext);
				textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
				textView.setText(Html.fromHtml(hashMap.get("value").toString()));
				textView.setTextSize(textSize);		//设置字体大小
				textView.setTextColor(textColor);	//设置字体颜色
				addView(textView);
			}
		}
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> hashMap = (HashMap<String, Object>) msg.obj;
			ImageView imageView = (ImageView) hashMap.get("imageView");
			LayoutParams params = new LayoutParams(msg.arg1, msg.arg2);
			params.gravity = Gravity.CENTER_HORIZONTAL;	//居中
			imageView.setLayoutParams(params);
			Drawable drawable = (Drawable) hashMap.get("drawable");
			imageView.setImageDrawable(drawable);		//显示图片
		};
	};
	
	/**
	 * 定义一个线程类，异步加载图片
	 * @author Administrator
	 *
	 */
	private class DownloadPicThread extends Thread {
		private ImageView imageView;
		private String mUrl;
		
		
		public DownloadPicThread(ImageView imageView, String mUrl) {
			super();
			this.imageView = imageView;
			this.mUrl = mUrl;
		}


		@Override
		public void run() {
			// TODO Auto-generated method stub
			Drawable drawable = null;
			int newImgWidth = 0;
			int newImgHeight = 0;
			try {
				drawable = Drawable.createFromStream(new URL(mUrl).openStream(), "image");
				//对图片进行缩放
				newImgWidth = drawable.getIntrinsicWidth() / 3;
				newImgHeight = drawable.getIntrinsicHeight() / 3;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			//让线程休眠2秒
			SystemClock.sleep(2000);
			//使用Handler更新UI
			Message msg = handler.obtainMessage();
			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put("imageView", imageView);
			hashMap.put("drawable", drawable);
			msg.obj = hashMap;
			msg.arg1 = newImgWidth;
			msg.arg2 = newImgHeight;
			handler.sendMessage(msg);
		}
	}

}
