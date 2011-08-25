package com.t2.compassionMeditation;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import com.t2.R;

import com.t2.biomap.SharedPref;
import com.t2.compassionDB.BioSession;
import com.t2.compassionDB.BioUser;
import com.t2.compassionDB.DatabaseHelper;

public class ViewSessionsActivity extends OrmLiteBaseActivity<DatabaseHelper> implements OnItemLongClickListener{
	private static final String TAG = "BFDemo";
	private static final String mActivityVersion = "1.0";
	private static ViewSessionsActivity instance;

	/**
	 * Currently selected user name (as selected at the start of the session)
	 */
	private String mCurrentBioUserName;
	
	/**
	 * BioUser associated with currently selected user name (as selected at the start of the session)
	 */
	private BioUser mCurrentBioUser = null;

	private Dao<BioUser, Integer> mBioUserDao;
	private Dao<BioSession, Integer> mBioSessionDao;
	
	/**
	 * UI ListView for sessions list
	 */
	private ListView sessionKeysList;

	/**
	 * Ordered list of session keys associated with the currently selected user
	 * 
	 */
	private ArrayList<SessionsKeyItem> sessionKeyItems = new ArrayList<SessionsKeyItem>();
	
	/**
	 * Ordered list of BioSessions associated with the currently selected user
	 * 
	 * note that we keep this list only so we can reference the currently selected session for deletion
	 */
	private ArrayList<BioSession> sessionItems = new ArrayList<BioSession>();;

	/**
	 * Index of currently selected session
	 * @see sessionItems
	 */
	private int mSelectedId;		
	
	/**
	 * Adapter used to provide list of views for the sessionKeyItems list
	 * @see sessionKeyItems
	 */
	private SessionsKeyItemAdapter sessionKeysAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        instance = this;
        
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);        
        
        setContentView(R.layout.view_sessions_layout); 
        
        mCurrentBioUserName = SharedPref.getString(this, "SelectedUser", 	"");
		sessionKeysList = (ListView) this.findViewById(R.id.listViewSessionKeys);
		sessionKeysList.setOnItemLongClickListener(this);
        
        updateListView();        
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}

	static class SessionsKeyItem {
		public long id;
		public String title1;
		public String title2;
		public int color;
		public boolean visible;
		public boolean reverseData = false; 
		
		public SessionsKeyItem(long id, String title1, String title2) {
			this.id = id;
			this.title1 = title1;
			this.title2 = title2;
		}
		
		public SessionsKeyItem(long id, String title1, String title2, int color) {
			this.id = id;
			this.title1 = title1;
			this.title2 = title2;
			this.color = color;
		}
		
		public HashMap<String,Object> toHashMap() {
			HashMap<String,Object> data = new HashMap<String,Object>();
			data.put("id", id);
			data.put("title1", title1);
			data.put("title2", title2);
			data.put("color", color);
			data.put("visible", visible);
			return data;
		}
	}	
	
	class SessionsKeyItemAdapter extends ArrayAdapter<SessionsKeyItem> {
		public static final int VIEW_TYPE_ONE_LINE = 1;
		public static final int VIEW_TYPE_TWO_LINE = 2;
		
		private LayoutInflater layoutInflater;
		private int layoutId;

		public SessionsKeyItemAdapter(Context context, int viewType,
				List<SessionsKeyItem> objects) {
			super(context, viewType, objects);
			
			layoutInflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
			if(viewType == VIEW_TYPE_TWO_LINE) {
				layoutId = R.layout.list_item_result_key_2;
			} else {
				layoutId = R.layout.list_item_result_key_1;
			}
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				convertView = layoutInflater.inflate(layoutId, null);
			}

			final SessionsKeyItem item = this.getItem(position);
			TextView tv1 = (TextView)convertView.findViewById(R.id.text1);
			TextView tv2 = (TextView)convertView.findViewById(R.id.text2);
			Button button = (Button) findViewById(R.id.buttonViewDetails);
			View keyBox = convertView.findViewById(R.id.keyBox);
			
			boolean tv1Null = (tv1 == null);
			boolean tv2Null = (tv2 == null);
			if(!tv1Null) {
				tv1.setText(item.title1);
			}
			if(!tv2Null) {
				tv2.setText(item.title2);
			}
			if(button != null) {
				button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
		            	showSessionDetails();
					}
				});				
			}
			
			if(keyBox != null) {
				keyBox.setBackgroundColor(item.color);
			}
			
			return convertView;
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		
		mSelectedId = arg2;		
		
		AlertDialog.Builder alert = new AlertDialog.Builder(instance);
		alert.setTitle("Choose Activity");
    	alert.setPositiveButton("View Details", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	showSessionDetails();
            	
            }
        });				
		
    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });				
		
    	alert.setNeutralButton("Delete Session", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	
				AlertDialog.Builder alert2 = new AlertDialog.Builder(instance);
				alert2.setMessage("Are you sure?");

				alert2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
	            	try {
	            		sessionKeyItems.get(mSelectedId);
	            		mBioSessionDao.delete(sessionItems.get(mSelectedId));	
	            		updateListView();	            		
						
					} catch (SQLException e) {
						Log.e(TAG, "Error deleting user" + e.toString());
					}
				}
				});

				alert2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				  public void onClick(DialogInterface dialog, int whichButton) {
				  }
				});

				alert2.show();
            }
        });				
		
		alert.show();		
		return false;
	}

	/**
	 * Populates sessionItems and sessionKeyItems with session from the currently selected user
	 * then uses the adapter to populate the list view with that data
	 */
	private void updateListView() {

		// Retrieve the BuiUser object associated with object mSelectedUserName
		try {
			mBioUserDao = getHelper().getBioUserDao();
			mBioSessionDao = getHelper().getBioSessionDao();
			
			QueryBuilder<BioUser, Integer> builder = mBioUserDao.queryBuilder();
			builder.where().eq(BioUser.NAME_FIELD_NAME, mCurrentBioUserName);
			builder.limit(1);
			List<BioUser> list = mBioUserDao.query(builder.prepare());	
			
			if (list.size() >= 1) {
				mCurrentBioUser = list.get(0);
			}
			else {
				Log.e(TAG, "General Database error" + mCurrentBioUserName);
			}
			
		} catch (SQLException e) {
			Log.e(TAG, "Can't find user: " + mCurrentBioUserName , e);

		}        
        	
		// Fill the collections sessionItems, and sessionKeyItems with session data from the current user
		sessionItems.clear();
		sessionKeyItems.clear();
		
		if (mCurrentBioUser != null) {
		
			for (BioSession session: mCurrentBioUser.getSessions()) {
				
				SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy HH:mm:ss", Locale.US);
				String title = sdf.format(new Date(session.time));			
				
				int color;
				if (session.precentComplete >= 100) {
					color = Color.GREEN;
				}
				else {
					color = Color.YELLOW;
				}
				
				SessionsKeyItem item = new SessionsKeyItem(1,title, "", color);
				sessionKeyItems.add(item);
				sessionItems.add(session);
				
			}
		}				
	
		sessionKeysAdapter = new SessionsKeyItemAdapter(this, 1, sessionKeyItems);		
		sessionKeysList.setAdapter(sessionKeysAdapter);		
	}
	
	private void showSessionDetails() {
		AlertDialog.Builder alert2 = new AlertDialog.Builder(instance);
		
		BioSession session = sessionItems.get(mSelectedId);
		
		String sessionDetails = "";
		sessionDetails += "Completion: " + session.precentComplete + "%\n";
		sessionDetails += "Length: " + secsToHMS(session.secondsCompleted) + "\n";
		sessionDetails += "Mindset Band of Interest: " + session.mindsetBandOfInterest + "\n";
		sessionDetails += "BioHarness Parameter of Interest: " + session.bioHarnessParameterOfInterest + "\n";
		sessionDetails += "Comments: " + session.comments + "\n";
		
		alert2.setMessage(sessionDetails);
		alert2.show();
		
	}

	private String secsToHMS(int time) {
		long secs = time;
		long hours = secs / 3600;
		secs = secs % 3600;
		long mins = secs / 60;
		secs = secs % 60;
		
		return hours + ":" + mins + ":" + secs;
	}
}
