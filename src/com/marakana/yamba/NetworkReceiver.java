package com.marakana.yamba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class NetworkReceiver extends BroadcastReceiver {
	public static final String TAG = "NetworkReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		boolean isNetworkDown = intent.getBooleanExtra(
				ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
		if (isNetworkDown) {
			Log.d(TAG, "onReceive: Not connected, stopping service");
			context.stopService(new Intent(context, UpdateService.class));
		} else {
			Log.d(TAG, "onReceive: connected, starting UpdateService");
			context.startService(new Intent(context, UpdateService.class));
		}
	}

}
