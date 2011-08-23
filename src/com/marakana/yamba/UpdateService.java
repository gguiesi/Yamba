package com.marakana.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter.Status;
import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

public class UpdateService extends Service {
	private static final String TAG = "UpdateService";
	
	static final int DELAY = 60000; // wait a minute
	boolean runFlag = false;
	Updater updater;
	YambaApplication yambaApplication;
	
	DbHelper dbHelper;
	SQLiteDatabase db;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		yambaApplication = (YambaApplication) getApplication();
		this.updater = new Updater();
		
		dbHelper = new DbHelper(this);
		
		Log.d(TAG, "onCreate");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		this.runFlag = false;
		this.updater.interrupt();
		this.updater = null;
		this.yambaApplication.setServiceRunnig(false);
		Log.d(TAG, "onDestroy");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (!runFlag) {
			this.runFlag = true;
			this.updater.start();
			((YambaApplication) super.getApplication()).setServiceRunnig(true);
			
			Log.d(TAG, "onStarted");
		}
		
		return Service.START_STICKY;
	}
	
	class Updater extends Thread {
		private List<Status> timeline;

		public Updater() {
			super("UpdaterService-Update");
		}
		
		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			UpdateService updateService = UpdateService.this;
			while (updateService.runFlag) {
				Log.d(TAG, "Updater Running");
				try {
					YambaApplication yamba = (YambaApplication) updateService.getApplication();
					int newUpdates = yamba.fetchStatusUpdates();
					if (newUpdates > 0) {
						Log.d(TAG, "We have a new Status");
					}
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					updateService.runFlag = false;
				}
			}
		}
	}
}
