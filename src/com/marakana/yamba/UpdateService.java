package com.marakana.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter.Status;
import winterwell.jtwitter.TwitterException;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdateService extends Service {
	private static final String TAG = "UpdateService";
	
	static final int DELAY = 60000;
	private boolean runFlag = false;
	private Updater updater;
	private YambaApplication yambaApplication;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		yambaApplication = (YambaApplication) getApplication();
		this.updater = new Updater();
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
		super.onStartCommand(intent, flags, startId);
		
		this.runFlag = true;
		this.updater.start();
		this.yambaApplication.setServiceRunnig(true);
		
		Log.d(TAG, "onStarted");
		return START_STICKY;
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
					// get timeline form the cloud
					try {
						timeline = yambaApplication.getTwitter().getFriendsTimeline();
					} catch (TwitterException e) {
						Log.e(TAG, "Failed to connect to twitter service", e);
					}
					
					// loop over the timeline and print it out
					for (Status status : timeline) {
						Log.d(TAG, String.format("%s: %s", status.user.name, status.text));
					}
					
					Log.d(TAG, "Updater ran");
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					updateService.runFlag = false;
				}
			}
			super.run();
		}
	}
}
