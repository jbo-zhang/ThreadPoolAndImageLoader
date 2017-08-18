package com.example.asynctaskdemo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;

public class MainActivity extends ActionBarActivity {

	private GridView mImageGridView;
	private ImageAdapter mImageAdapter;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pic_layout);
		
		mImageGridView = (GridView) findViewById(R.id.gridView1);
		mImageAdapter = new ImageAdapter(this);
		mImageGridView.setAdapter(mImageAdapter);
		
		//优化卡顿
		mImageGridView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					mImageAdapter.setGridViewIdle(true);
					mImageAdapter.notifyDataSetChanged();
				} else {
					mImageAdapter.setGridViewIdle(false);
				}
			}
			
			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				
			}
		});
		
	setContentView(R.layout.activity_main);
		
		
		findViewById(R.id.bt_hello_world).setOnClickListener(new OnClickListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
//				try {
//					new DownloadFilesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,new URL("file://nihao"));
//					new DownloadFilesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,new URL("file://nihao"));
//					new DownloadFilesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,new URL("file://nihao"));
//					new DownloadFilesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,new URL("file://nihao"));
//					new DownloadFilesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,new URL("file://nihao"));
//					new DownloadFilesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,new URL("file://nihao"));
//					new DownloadFilesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,new URL("file://nihao"));
//				} catch (MalformedURLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
				throw new RuntimeException("自定义异常： 这是自己跑出的异常");
			}
		});
		
		
		
		
	}
	
	private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {

		@Override
		protected Long doInBackground(URL... urls) {
			int count = urls.length;
			long totalSize = 0;
			for(int i = 0; i < count; i++) {
				totalSize += Downloader.downloadFile(urls[i]); 
				publishProgress((int) ((i/ (float) count) * 100));
				if(isCancelled()) {
					break;
				}
			}
			SystemClock.sleep(5000);
			return totalSize;
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress) {
			setProgressPercent(progress[0]);
		}

		@Override
		protected void onPostExecute(Long result) {
			showDialog("Downloaded " + result + " bytes");
		}
	}
	
	private void showDialog(String string) {
		Log.d("9095", string);
		
	}
	
	private void setProgressPercent(Integer integer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
