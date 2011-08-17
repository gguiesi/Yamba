package com.marakana.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Status;
import winterwell.jtwitter.TwitterException;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

public class UpdateService extends Service {
	private static final String TAG = "UpdateService";
	
	static final int DELAY = 60000; // wait a minute
	private boolean runFlag = false;
	private Updater updater;
	private YambaApplication yambaApplication;
	
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
		
//		super.onStartCommand(intent, flags, startId);
//		
//		this.yambaApplication.setServiceRunnig(true);
		
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
					// get timeline form the cloud
					try {
						timeline = yambaApplication.getTwitter().getFriendsTimeline();
					} catch (TwitterException e) {
						Log.e(TAG, "Failed to connect to twitter service", e);
					}
					
					// open database for writing
					db = dbHelper.getWritableDatabase();
					
					// loop over the timeline and print it out
					ContentValues contentValues = new ContentValues();
					for (Twitter.Status status : timeline) {
						// insert into database
						contentValues.clear();
						contentValues.put(DbHelper.C_ID, status.id.toString());
						contentValues.put(DbHelper.C_CREATE_AT, status.createdAt.getTime());
						contentValues.put(DbHelper.C_SOURCE, status.source);
						contentValues.put(DbHelper.C_TEXT, status.text);
						contentValues.put(DbHelper.C_USER, status.user.name);
						try {
							db.insertOrThrow(DbHelper.TABLE, null, contentValues);
						} catch (SQLException e) {
							Log.e(TAG, e.getMessage());
						}
						
						Log.d(TAG, String.format("%s: %s", status.user.name, status.text));
					}
					
					// close the database
					db.close();
					
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
