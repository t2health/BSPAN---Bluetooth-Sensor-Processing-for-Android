package com.t2.compassionMeditation;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.t2.compassionUtils.MathExtra;





public class ViewSessionsActivity extends OrmLiteBaseActivity<DatabaseHelper> 
				implements OnItemLongClickListener, OnClickListener {
	private static final String TAG = "BFDemo";
	private static final String mActivityVersion = "1.0";
	public static final String EXTRA_TIME_START = "timeStart";
	public static final String EXTRA_CALENDAR_FIELD = "calendarField";
	public static final String EXTRA_REVERSE_DATA = "reverseData";
	
	
	private static ViewSessionsActivity instance;
	private DisplayMetrics displayMetrics = new DisplayMetrics();
	
	private View mDeviceChartView;
	
	

	private static final int DIRECTION_PREVIOUS = -1;
	private static final int DIRECTION_NONE = 0;
	private static final int DIRECTION_NEXT = 1;
	
	
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
	
	protected Calendar startCal;
	protected Calendar endCal;
	protected int calendarField;				// index of calandar parameter (Defaults to day of month)
	private TextView monthNameTextView;
	SimpleDateFormat monthNameFormatter = new SimpleDateFormat("MMMM, yyyy");
	
	
	
	
	
	
	
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
		monthNameTextView = (TextView) this.findViewById(R.id.monthName);
        
        
        mCurrentBioUserName = SharedPref.getString(this, "SelectedUser", 	"");
		sessionKeysList = (ListView) this.findViewById(R.id.listViewSessionKeys);
		sessionKeysList.setOnItemLongClickListener(this);

		long startTime = this.getIntent().getLongExtra(EXTRA_TIME_START, 0);
		if(startTime == 0) {
			startTime = Calendar.getInstance().getTimeInMillis();
		}
		Intent intent = this.getIntent();
		calendarField = intent.getIntExtra(EXTRA_CALENDAR_FIELD, Calendar.DAY_OF_MONTH);
		
		// Set the time ranges.
		// By default this is today:midnight - one month from today: midnight
		startCal = Calendar.getInstance();
		startCal.setTimeInMillis(MathExtra.roundTime(startTime, calendarField));
		startCal.set(calendarField, startCal.getMinimum(calendarField));
		
		endCal = Calendar.getInstance();
		endCal.setTimeInMillis(startCal.getTimeInMillis());
		endCal.add(Calendar.MONTH, 1);
		
		monthNameTextView.setText(monthNameFormatter.format(startCal.getTime()));
		
        updateListView();
		this.findViewById(R.id.monthMinusButton).setOnClickListener(this);
		this.findViewById(R.id.monthPlusButton).setOnClickListener(this);
        
		generateChart(DIRECTION_NEXT);        
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
			final int buttonposition = position;			

			final SessionsKeyItem item = this.getItem(position);
			TextView tv1 = (TextView)convertView.findViewById(R.id.text1);
			TextView tv2 = (TextView)convertView.findViewById(R.id.text2);
			Button button = (Button) convertView.findViewById(R.id.buttonViewDetails);
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
						mSelectedId = buttonposition;
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

		sessionDetails += "Band:   " + session.keyItemNames[session.mindsetBandOfInterestIndex] + "\n";
		sessionDetails += "   Min: " + session.minFilteredValue[session.mindsetBandOfInterestIndex] + "\n";
		sessionDetails += "   Max: " + session.maxFilteredValue[session.mindsetBandOfInterestIndex] + "\n";
		sessionDetails += "   Avg: " + session.avgFilteredValue[session.mindsetBandOfInterestIndex] + "\n";

		
		sessionDetails += "Band: " + session.keyItemNames[session.bioHarnessParameterOfInterestIndex] + "\n";;
		sessionDetails += "   Min: " + session.minFilteredValue[session.bioHarnessParameterOfInterestIndex] + "\n";
		sessionDetails += "   Max: " + session.maxFilteredValue[session.bioHarnessParameterOfInterestIndex] + "\n";
		sessionDetails += "   Avg: " + session.avgFilteredValue[session.bioHarnessParameterOfInterestIndex] + "\n";

		
		
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

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.monthMinusButton:
			monthMinusButtonPressed();
			break;
			
		case R.id.monthPlusButton:
			monthPlusButtonPressed();
			break;
		}
	}

	protected void monthMinusButtonPressed() {
		startCal.add(Calendar.MONTH, -1);
		endCal.add(Calendar.MONTH, -1);
		this.monthNameTextView.setText(monthNameFormatter.format(startCal.getTime()));
		generateChart(DIRECTION_PREVIOUS);
		
//		notesList.setSelection(notesAdapter.getPositionForTimestamp(endCal.getTimeInMillis()));
	}
	
	protected void monthPlusButtonPressed() {
		startCal.add(Calendar.MONTH, 1);
		endCal.add(Calendar.MONTH, 1);
		this.monthNameTextView.setText(monthNameFormatter.format(startCal.getTime()));
		generateChart(DIRECTION_NEXT);
		
//		notesList.setSelection(notesAdapter.getPositionForTimestamp(endCal.getTimeInMillis()));
	}

	private void generateChart(int direction) {
		XYMultipleSeriesDataset dataSet = new XYMultipleSeriesDataset();
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		LineChart chart = new LineChart(dataSet, renderer);
		
		XYSeries series = new XYSeries("test");
		Calendar cal = Calendar.getInstance();
		
        LinearLayout layout = (LinearLayout) findViewById(R.id.deviceChart);    	
    	if (mDeviceChartView != null) {
    		layout.removeView(mDeviceChartView);
    	}

//    	mDeviceChartView = new OffsetGraphicalChartView(this, chart);
     	
     	mDeviceChartView = ChartFactory.getLineChartView(this, dataSet, renderer);
     	layout.addView(mDeviceChartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
         	
		ArrayList<DataPoint> dataPoints = null;
		long startTime = startCal.getTimeInMillis();
		long endTime = endCal.getTimeInMillis();	
		
		double maxChartValue = 0;
		// Get the data points
	//	dataPoints = loadData(keyItems.get(i), startTime, endTime, calendarField);       	

		for (BioSession session : sessionItems) {
			if (session.time >= startTime && session.time <= endTime ) {
				cal.setTimeInMillis(session.time);
				double chartValue = session.avgFilteredValue[0];
				if (chartValue > maxChartValue) maxChartValue = chartValue;
				
				int i;
				i = cal.get(calendarField);
				i = cal.get(Calendar.DAY_OF_WEEK);
				i = cal.get(Calendar.HOUR_OF_DAY);
				i = cal.get(Calendar.MINUTE);
				i++;
				series.add(cal.get(calendarField), chartValue );
				
			}
		}
		
		
		
//		// Put some test points in
//		for (int i = 0; i < 10; i++ ) {
//			DataPoint dp = new DataPoint(System.currentTimeMillis() + i * 1000,0);
//			cal.setTimeInMillis(dp.time);
//			series.add(cal.get(calendarField), i*3);
//			
//		}
		
		
		XYSeriesRenderer seriesRenderer = new XYSeriesRenderer();
		seriesRenderer.setColor(Color.RED);
		seriesRenderer.setPointStyle(PointStyle.CIRCLE);
		seriesRenderer.setFillPoints(true);
		seriesRenderer.setLineWidth(2 * displayMetrics.density);
		
		renderer.addSeriesRenderer(seriesRenderer);
		dataSet.addSeries(series);		
		
		// only contine making the chart if there is data in the series.
		if(dataSet.getSeriesCount() > 0) {
			
			// Make the renderer for the weekend blocks
			Calendar weekendCal = Calendar.getInstance();
			weekendCal.setTimeInMillis(System.currentTimeMillis());
			
//			Calendar weekCal = Calendar.getInstance();
//			weekCal.setTimeInMillis(startCal.getTimeInMillis());
//			int dow = weekCal.get(Calendar.DAY_OF_WEEK);
//			weekCal.add(Calendar.DAY_OF_MONTH, 7 - dow + 2);
			
			int lastDayOfMonth = weekendCal.getActualMaximum(Calendar.DAY_OF_MONTH);
//			int firstMondayOfMonth = weekCal.get(Calendar.DAY_OF_MONTH);
			
			renderer.setShowGrid(false);
			renderer.setAxesColor(Color.WHITE);
			renderer.setLabelsColor(Color.WHITE);
			renderer.setAntialiasing(true);
			renderer.setShowLegend(false);
			renderer.setYLabels(0);
			renderer.setXLabels(15);
			renderer.setYAxisMax(maxChartValue);
			
			renderer.setYAxisMin(0.00);
			renderer.setXAxisMin(1.00);
			renderer.setXAxisMax(lastDayOfMonth);
			
			renderer.setZoomEnabled(true, false);
			renderer.setPanEnabled(true, false);
			renderer.setLegendHeight(10);
		}		
		
		
		
	} // End generateChart
	
	
	private ArrayList<Long> getDataPoints(long startTime, long endTime, int calendarGroupByField) {
		Calendar startCal = Calendar.getInstance();
		Calendar endCal = Calendar.getInstance();
		startCal.setTimeInMillis(startTime);
		endCal.setTimeInMillis(endTime);
		
		startCal.setTimeInMillis(MathExtra.roundTime(startCal.getTimeInMillis(), calendarGroupByField));
		endCal.setTimeInMillis(MathExtra.roundTime(endCal.getTimeInMillis(), calendarGroupByField));
		
		ArrayList<Long> dataPoints = new ArrayList<Long>();
		Calendar runningCal = Calendar.getInstance();
		runningCal.setTimeInMillis(startCal.getTimeInMillis());
		while(true) {
			if(runningCal.getTimeInMillis() >= endTime) {
				break;
			}
			
			switch(calendarGroupByField) {
			case Calendar.MONTH:
				dataPoints.add(runningCal.getTimeInMillis());
				runningCal.add(Calendar.MONTH, 1);
				break;
			case Calendar.DAY_OF_MONTH:
				dataPoints.add(runningCal.getTimeInMillis());
				runningCal.add(Calendar.DAY_OF_MONTH, 1);
				break;
			}
		}
		
		return dataPoints;
	}	// End getDataPoints
	
	
//	public Cursor getResults(long startTime, long endTime) {
//		return this.getDBAdapter().getDatabase().query(
//				quote(Result.TABLE_NAME),
//				new String[]{
//						quote(Result.FIELD_TIMESTAMP),
//						quote(Result.FIELD_VALUE),
//				},
//				quote(Result.FIELD_GROUP_ID)+"=? AND "+ quote(Result.FIELD_TIMESTAMP)+" >= ? AND "+ quote(Result.FIELD_TIMESTAMP)+" < ?",
//				new String[]{
//						this._id+"",
//						startTime+"",
//						endTime+""
//				},
//				null,
//				null,
//				null,
//				null
//		);
//	}	
//	
	
	
}
