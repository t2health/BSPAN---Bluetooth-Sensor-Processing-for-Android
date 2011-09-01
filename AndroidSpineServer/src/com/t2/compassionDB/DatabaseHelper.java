package com.t2.compassionDB;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.t2.compassionMeditation.PreferenceData;

/**
 * Database helper which creates and upgrades the database and provides the DAOs for the app.
 * 
 * @author kevingalligan
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	/************************************************
	 * Suggested Copy/Paste code. Everything from here to the done block.
	 ************************************************/

	private static final String DATABASE_NAME = "helloAndroid42.db";	
	private static final int DATABASE_VERSION = 6;
//	private static final int DATABASE_VERSION = 7;

	private Dao<BioUser, Integer> bioUserDao = null;
	private Dao<BioSession, Integer> bioSessionDao = null;
	private Dao<PreferenceData, Integer> preferenceDao = null;
	

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/************************************************
	 * Suggested Copy/Paste Done
	 ************************************************/

	@Override
	public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, BioUser.class);
			TableUtils.createTable(connectionSource, BioSession.class);
			TableUtils.createTable(connectionSource, PreferenceData.class);
			
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Unable to create datbases", e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVer, int newVer) {
		try {
			TableUtils.dropTable(connectionSource, BioUser.class, true);
			TableUtils.dropTable(connectionSource, BioSession.class, true);
			TableUtils.dropTable(connectionSource, PreferenceData.class, true);
			onCreate(sqliteDatabase, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Unable to upgrade database from version " + oldVer + " to new "
					+ newVer, e);
		}
	}

	public Dao<BioUser, Integer> getBioUserDao() throws SQLException {
		if (bioUserDao == null) {
			bioUserDao = getDao(BioUser.class);
		}
		return bioUserDao;
	}
	public Dao<BioSession, Integer> getBioSessionDao() throws SQLException {
		if (bioSessionDao == null) {
			bioSessionDao = getDao(BioSession.class);
		}
		return bioSessionDao;
	}
	public Dao<PreferenceData, Integer> getPreferenceDao() throws SQLException {
		if (preferenceDao == null) {
			preferenceDao = getDao(PreferenceData.class);
		}
		return preferenceDao;
	}
	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		bioUserDao = null;
		bioSessionDao = null;
		preferenceDao = null;
	}	
	
}
