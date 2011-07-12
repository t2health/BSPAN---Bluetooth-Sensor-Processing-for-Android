package com.t2.vas.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter extends SQLiteOpenHelper {
	private Context context;
	private SQLiteDatabase database;
	private OnDatabaseCreatedListener createListener;
	private OnDatabaseUpdatedListener onUpgradeListner;
	
	public DBAdapter(Context c, String dbName, int dbVersion) {
		super(c, dbName, null, dbVersion);
		this.context = c;
		this.init();
	}
	
	private void init() {
		
	}
	
	public Context getContext() {
		return this.context;
	}
	
	public SQLiteDatabase getDatabase() {
		return this.database;
	}
	
	public DBAdapter open() {
		this.database = this.getWritableDatabase();
		return this;
	}

	public boolean isOpen() {
		if(this.database == null) {
			return false;
		}
		return this.database.isOpen();
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		this.database = db;
		
		if(this.createListener != null) {
			this.createListener.onDatabaseCreated(db);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(this.onUpgradeListner != null) {
			this.onUpgradeListner.onUpgrade(db, oldVersion, newVersion);
		}
	}
	
	public static ContentValues buildContentValues(String[] keys, String[] values) {
		ContentValues v = new ContentValues();
		for(int i = 0; i < keys.length; i++) {
			v.put(keys[i], values[i]);
		}
		return v;
	}
	
	public void setOnCreateListener(OnDatabaseCreatedListener l) {
		this.createListener = l;
	}
	
	public void setOnUpdatedListener(OnDatabaseUpdatedListener l) {
		this.onUpgradeListner = l;
	}
	
	public interface OnDatabaseCreatedListener {
		public void onDatabaseCreated(SQLiteDatabase db);
	}
	
	public interface OnDatabaseUpdatedListener {
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
	}
}
