package com.xiaowu.news;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SearchNewsActiity extends Activity {
	private Button searchButton = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_news_layout);
		
		
		
		searchButton = (Button) findViewById(R.id.search_button);
		
		searchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setData(Uri.parse("http://news.baidu.com/"));
				intent.setAction(Intent.ACTION_VIEW);
				startActivity(intent);
			}
		});
	}
}
