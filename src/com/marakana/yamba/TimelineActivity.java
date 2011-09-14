package com.marakana.yamba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class TimelineActivity extends BaseActivity {
	Cursor cursor;
	ListView listTimeline;
	SimpleCursorAdapter adapter;
	IntentFilter filter;
	TimelineReceiver receiver;
	static final String[] FROM = { DbHelper.C_CREATED_AT, DbHelper.C_USER,
			DbHelper.C_TEXT };
	static final int[] TO = { R.id.textCreatedAt, R.id.textUser, R.id.textText };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		filter = new IntentFilter("com.marakana.yamba.NEW_STATUS");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline);

		// check whether preferences have been set
		if (yamba.getPrefs().getString("username", "") == null) {
			startActivity(new Intent(this, PrefsActivity.class));
			Toast.makeText(this, R.string.msgSetupPrefs, Toast.LENGTH_LONG)
					.show();
		}

		// find your views
		listTimeline = (ListView) findViewById(R.id.listTimeline);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Get the data from the database
		cursor = yamba.getStatusData().getStatusUpdates();
		startManagingCursor(cursor);

		// create the adapter
		adapter = new TimelineAdapter(this, cursor);
		listTimeline.setAdapter(adapter);

		// register the receiver
		registerReceiver(receiver, filter);

		this.setupList();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// UNregister the receiver
		unregisterReceiver(receiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// close the database
		yamba.getStatusData().close();
	}

	// Responsible for fetching data and setting up the list and the adapter
	private void setupList() {
		// get data
		cursor = yamba.getStatusData().getStatusUpdates();
		startManagingCursor(cursor);

		// Setup adapter
		adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, FROM, TO);
		adapter.setViewBinder(VIEW_BINDER);
		listTimeline.setAdapter(adapter);
	}

	// view binder constant to inject business logic for timestamp to relative
	// time conversion
	static final ViewBinder VIEW_BINDER = new ViewBinder() {

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if (view.getId() != R.id.textCreatedAt) {
				return false;
			}
			// update the created at text to relative time
			long timestamp = cursor.getLong(columnIndex);
			CharSequence relTime = DateUtils
					.getRelativeTimeSpanString(timestamp);
			((TextView) view).setText(relTime);
			return true;
		}

	};

	class TimelineReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			cursor = yamba.getStatusData().getStatusUpdates();
			adapter.changeCursor(cursor);
			adapter.notifyDataSetChanged();
			Log.d("TimelineReceiver", "onReceiver");
		}
	}
}
