package com.example.asynctaskdemo;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class MyIntentService extends IntentService{

	private static final String TAG = "MyIntentService";
	
	public MyIntentService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String action = intent.getStringExtra("task_action");
		SystemClock.sleep(5000);
		if("xxx.yyy.task".equals(action)) {
			Log.d(TAG, "handle task: " + action);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
