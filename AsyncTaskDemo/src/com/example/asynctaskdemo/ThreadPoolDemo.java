package com.example.asynctaskdemo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.os.SystemClock;

public class ThreadPoolDemo {
	Runnable command = new Runnable() {
		
		@Override
		public void run() {
			SystemClock.sleep(2000);
		}
	};
	
	private void demo() {
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(4);
		fixedThreadPool.execute(command);
		
		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
		cachedThreadPool.execute(command);
		
		ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(4);
		//2000ms后执行command
		scheduledThreadPool.schedule(command, 2000, TimeUnit.MICROSECONDS);
		//延迟10ms后， 每隔1000ms执行一次command
		scheduledThreadPool.scheduleAtFixedRate(command, 10, 1000, TimeUnit.MICROSECONDS);
		
		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
		singleThreadExecutor.execute(command);
		
		
	}
	
	
	
}
