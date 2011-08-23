package com.t2.compassionMeditation;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.t2.R;
import com.t2.biomap.SharedPref;
import com.t2.compassionDB.BioSession;
import com.t2.compassionDB.BioUser;
import com.t2.compassionDB.DatabaseHelper;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

public class ViewSessionsActivity extends OrmLiteBaseActivity<DatabaseHelper> implements View.OnTouchListener {
	private static final String TAG = "ViewSessionsActivity";
	private static final String mActivityVersion = "1.0";

	String mSelectedUserName;
	Dao<BioUser, Integer> mBioUserDao;
	Dao<BioSession, Integer> mBioSessionDao;
	BioUser mCurrentBioUser = null;
	BioSession mCurrentBioSession = null;
	List<BioUser> currentUsers;	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);		
        setContentView(R.layout.view_sessions_layout);        

//		final RelativeLayout parent = (RelativeLayout) findViewById(R.id.instructionsLayout	);
//		parent.setOnTouchListener (this);
        
        mSelectedUserName = SharedPref.getString(this, "SelectedUser", 	"");
		try {
			mBioUserDao = getHelper().getBioUserDao();
			mBioSessionDao = getHelper().getBioSessionDao();
			
			QueryBuilder<BioUser, Integer> builder = mBioUserDao.queryBuilder();
			builder.where().eq(BioUser.NAME_FIELD_NAME, mSelectedUserName);
			builder.limit(1);
//			builder.orderBy(ClickCount.DATE_FIELD_NAME, false).limit(30);
			List<BioUser> list = mBioUserDao.query(builder.prepare());	
			
			if (list.size() >= 1) {
				mCurrentBioUser = list.get(0);
			}
			else {
				Log.e(TAG, "General Database error" + mSelectedUserName);
			}
			
		} catch (SQLException e) {
			Log.e(TAG, "Can't find user: " + mSelectedUserName , e);

		}        
        
		int i = 0;
		i++;
		
        
	}
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		
		
	}
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		finish();
		return false;
	}
	

}
