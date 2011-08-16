package com.marakana.yamba;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends Activity implements OnClickListener, TextWatcher, OnSharedPreferenceChangeListener {
	private static final String TAG = "StatusActivity";
	EditText editText;
	Button updateButton;
	Twitter twitter;
	TextView textCount;
	SharedPreferences prefs;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status);

		// Find views
		editText = (EditText) findViewById(R.id.editText);
		updateButton = (Button) findViewById(R.id.buttonUpdate);
		updateButton.setOnClickListener(this);
		
		textCount = (TextView) findViewById(R.id.textCount);
		textCount.setText(Integer.toString(140));
		textCount.setTextColor(Color.GREEN);
		editText.addTextChangedListener(this);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
	}
	
	class PostToTwitter extends AsyncTask<String, Integer, String> {
		// Called to initiate the background activity
		@Override
		protected String doInBackground(String... statuses) {
			Log.d(TAG, "doInBackground");
			try {
				YambaApplication yambaApplication = (YambaApplication) getApplication();
				Twitter.Status status = yambaApplication.getTwitter().updateStatus(statuses[0]);
				return status.text;
			} catch (TwitterException e) {
				Log.e(TAG, e.toString());
				e.printStackTrace();
				return "Failed to Post";
			}
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			Log.d(TAG, "onProgressUpdate");
			super.onProgressUpdate(values);
		}
		
		@Override
		protected void onPostExecute(String result) {
			Log.d(TAG, "onPostExecute");
			Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG).show();
		}
	}

	public void onClick(View v) {
		try {
			new PostToTwitter().execute(editText.getText().toString());
			Log.d(TAG, "onClick");
		} catch (TwitterException e) {
			Log.d(TAG, "Twitter setStatus failed: " + e);
		}
	}

	@Override
	public void afterTextChanged(Editable statusText) {
		Log.d(TAG, "afterTextChanged");
		int count = 140 - statusText.length();
		textCount.setText(Integer.toString(count));
		textCount.setTextColor(Color.GREEN);
		if (count < 10) {
			textCount.setTextColor(Color.YELLOW);
		}
		if (count < 0) {
			textCount.setTextColor(Color.RED);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		Log.d(TAG, "beforeTextChanged");
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		Log.d(TAG, "onTextChanged");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected");
		switch (item.getItemId()) {
		case R.id.itemPrefs:
			startActivity(new Intent(this, PrefsActivity.class));
			break;
		case R.id.itemServiceStart:
			startService(new Intent(this, UpdateService.class));
			break;
		case R.id.itemServiceStop:
			stopService(new Intent(this, UpdateService.class));
			break;
		}
		
		return true;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Log.d(TAG, "onSharedPreferenceChanged");
		twitter = null;
	}
}