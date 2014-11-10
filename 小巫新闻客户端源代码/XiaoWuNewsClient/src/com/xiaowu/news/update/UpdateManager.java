package com.xiaowu.news.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.xiaowu.news.R;

/**
 * 
 * @author wwj
 * @date 2012/11/17
 * 实现软件更新的管理类
 */
public class UpdateManager {
	
	//下载中...
	private static final int DOWNLOAD = 1;
	//下载完成
	private static final int DOWNLOAD_FINISH = 2;
	//保存解析的XML信息
	HashMap<String , String> mHashMap;
	//下载保存路径
	private String mSavePath;
	//记录进度条数量
	private int progress;
	//是否取消更新
	private boolean cancelUpdate = false;
	//上下文对象
	private Context mContext;
	//进度条
	private ProgressBar mProgressBar;
	//更新进度条的对话框
	private Dialog mDownloadDialog;
	
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			//下载中。。。
			case DOWNLOAD:
				//更新进度条
				System.out.println(progress);
				mProgressBar.setProgress(progress);
				break;
			//下载完成
			case DOWNLOAD_FINISH:
				// 安装文件
				installApk();
				break;
			}
		};
	};


	public UpdateManager(Context context) {
		super();
		this.mContext = context;
	}
	
	
	/**
	 * 检测软件更新
	 */
	public void checkUpdate() {
		if (isUpdate()) {
			//显示提示对话框
			showNoticeDialog();
		} else {
			Toast.makeText(mContext, R.string.soft_update_no, Toast.LENGTH_SHORT).show();
		}
		
	}
	
	private void showNoticeDialog() {
		// TODO Auto-generated method stub
		//构造对话框
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_update_title);
		builder.setMessage(R.string.soft_update_info);
		//更新
		builder.setPositiveButton(R.string.soft_update_updatebtn, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				// 显示下载对话框
				showDownloadDialog();
			}
		});
		// 稍后更新
		builder.setNegativeButton(R.string.soft_update_later, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		Dialog noticeDialog = builder.create();
		noticeDialog.show();
	}
	
	private void showDownloadDialog() {
		// 构造软件下载对话框
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_updating);
		// 给下载对话框增加进度条
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.softupdate_progress, null);
		mProgressBar = (ProgressBar) view.findViewById(R.id.update_progress);
		builder.setView(view);
		builder.setNegativeButton(R.string.soft_update_cancel, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				// 设置取消状态
				cancelUpdate = true;
			}
		});
		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		//下载文件
		downloadApk();
	}
	
	/**
	 * 下载APK文件
	 */
	private void downloadApk() {
		// TODO Auto-generated method stub
		// 启动新线程下载软件
		new DownloadApkThread().start();
	}


	/**
	 * 检查软件是否有更新版本
	 * @return
	 */
	public boolean isUpdate() {
		// 获取当前软件版本
		int versionCode = getVersionCode(mContext);
		//把version.xml放到网络上，然后获取文件信息
		InputStream inStream = ParseXmlService.class.getClassLoader().getResourceAsStream("version.xml");
		// 解析XML文件。 由于XML文件比较小，因此使用DOM方式进行解析
		ParseXmlService service = new ParseXmlService();
		try {
			mHashMap = service.parseXml(inStream);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		if(null != mHashMap) {
			int serviceCode = Integer.valueOf(mHashMap.get("version"));
			//版本判断
			if(serviceCode > versionCode) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取软件版本号
	 * @param context
	 * @return
	 */
	private int getVersionCode(Context context) {
		// TODO Auto-generated method stub
		int versionCode = 0;

		// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
		try {
			versionCode = context.getPackageManager().getPackageInfo(
					"com.xiaowu.news", 0).versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return versionCode;
	}
	
	/**
	 * 下载文件线程
	 * @author Administrator
	 *
	 */
	private class DownloadApkThread extends Thread {
		@Override
		public void run() {
			try
			{
				//判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				{
					// 获取SDCard的路径
					String sdpath = Environment.getExternalStorageDirectory() + "/";
					mSavePath = sdpath + "download";
					URL url = new URL(mHashMap.get("url"));
					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.connect();
					// 获取文件大小
					int length = conn.getContentLength();
					// 创建输入流
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// 如果文件不存在，新建目录
					if (!file.exists())
					{
						file.mkdir();
					}
					File apkFile = new File(mSavePath, mHashMap.get("name"));
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					do
					{
						int numread = is.read(buf);
						count += numread;
						// 计算进度条的位置
						progress = (int) (((float) count / length) * 100);
						// 更新进度
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0)
						{
							// 下载完成
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);//点击取消就停止下载
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			// 取消下载对话框显示
			mDownloadDialog.dismiss();
		}
	}
	
	/**
	 * 安装APK文件
	 */
	private void installApk()
	{
		File apkfile = new File(mSavePath, mHashMap.get("name"));
		if (!apkfile.exists())
		{
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
		mContext.startActivity(i);
	}
}
