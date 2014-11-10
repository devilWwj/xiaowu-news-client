package com.xiaowu.news.custom;

import java.util.List;
import java.util.Map;

import com.xiaowu.news.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public class ConstomSimpleAdapter extends SimpleAdapter {

	public ConstomSimpleAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = super.getView(position, convertView, parent);
		// 更新第一个TextView的背景
		if(position == 0) {
			TextView categoryTitle = (TextView) v;
			categoryTitle.setBackgroundResource(R.drawable.image_categorybar_item_selected_background);
			categoryTitle.setTextColor(0xFFFFFFFF);
		}
		return v;
	}
	

}
