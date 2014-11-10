package com.xiaowu.news;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SubscribeActivity extends Activity {

	private Button beginSubscribeBtn = null;
	private Button addSubscription = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subscription_layout);
		
		beginSubscribeBtn = (Button) findViewById(R.id.begin_subscription);
		addSubscription = (Button) findViewById(R.id.addSubscription);
		
		beginSubscribeBtn.setOnClickListener(beginSubscriptionListener);
		addSubscription.setOnClickListener(beginSubscriptionListener);
	}
	
	private OnClickListener beginSubscriptionListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(SubscribeActivity.this, AddSubscriptionActivity.class);
			// TODO Auto-generated method stub
			switch(v.getId()) {
			case R.id.addSubscription:
				startActivity(intent);
				break;
			case R.id.begin_subscription:
				startActivity(intent);
				break;
			}
		}
	};
}
