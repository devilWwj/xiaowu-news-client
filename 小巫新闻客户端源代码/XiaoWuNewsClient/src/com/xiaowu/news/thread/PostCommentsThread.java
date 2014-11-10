package com.xiaowu.news.thread;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.xiaowu.news.model.Parameter;
import com.xiaowu.news.service.SyncHttp;

/**
 * 异步更新新闻回复信息
 * @author wwj
 *
 */
public class PostCommentsThread extends Thread {
	private int nid;
	private String region;
	private String newsReplyEditText;
	private Context context;
	

	public PostCommentsThread(int nid, String region, String newsReplyEditText,
			Context context) {
		super();
		this.nid = nid;
		this.region = region;
		this.newsReplyEditText = newsReplyEditText;
		this.context = context;
	}



	@Override
	public void run() {
		//url = "http://10.0.2.2:8080/web/postComment";
		String url = "http://192.168.220.1:8080/web/postComment";
		List<Parameter> params = new ArrayList<Parameter>();
		params.add(new Parameter("nid", nid + ""));
		params.add(new Parameter("region", region));
		params.add(new Parameter("content", newsReplyEditText));
		SyncHttp syncHttp = new SyncHttp();
		try {
			String retStr = syncHttp.httpPost(url, params);
			JSONObject object = new JSONObject(retStr);
			int retCode = object.getInt("ret");
			if(retCode == 0) {
				Toast.makeText(context, "发表成功", Toast.LENGTH_SHORT).show();
				return;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}