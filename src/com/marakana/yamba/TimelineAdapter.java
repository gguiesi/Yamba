package com.marakana.yamba;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class TimelineAdapter extends SimpleCursorAdapter {
	static final String[] FROM = {DbHelper.C_CREATED_AT, DbHelper.C_USER, DbHelper.C_TEXT};
	static final int[] TO = {};
	
	public TimelineAdapter(Context context, Cursor c) {
		super(context, R.layout.row, c, FROM, TO);
	}

	// this is where the actual binding of a cursor to view happens
	@Override
	public void bindView(View row, Context context, Cursor cursor) {
		super.bindView(row, context, cursor);
		
		// Manually bind created at timestamp to its view
		long timestamp = cursor.getLong(cursor.getColumnIndex(DbHelper.C_CREATED_AT));
		TextView textCreatedAt = (TextView) row.findViewById(R.id.textCreatedAt);
		textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(timestamp));
	}

}
