package com.marakana.yamba;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
	static final String TAG = "DbHelper";
	static final String DB_NAME = "timeline.db";
	static final int DB_VERSION = 1;
	static final String TABLE = "timeline";
	static final String C_ID = BaseColumns._ID;
	static final String C_CREATE_AT = "create_at";
	static final String C_SOURCE = "source";
	static final String C_TEXT = "txt";
	static final String C_USER = "user";
	Context context;

	// Constructor
	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table " + TABLE + " (" + C_ID + " int primary key, " + C_CREATE_AT + " int, " + C_SOURCE + " text, " 
				+ C_USER + " text, " + C_TEXT + " text)";
		
		db.execSQL(sql);
		
		Log.d(TAG, "onCreate sql: " + sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int odVersion, int newVersion) {
		// Typically do ALTER TABLE statements, but...we're just in development,
		// so:
		db.execSQL("drop table if exists " + TABLE); // drop the old database
		Log.d(TAG, "onUpdate");
		onCreate(db);
	}

}
