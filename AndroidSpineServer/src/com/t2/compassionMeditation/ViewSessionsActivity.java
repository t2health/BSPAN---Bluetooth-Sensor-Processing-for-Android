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
import org.achartengine.chart.BarChart;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.RangeBarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import spine.datamodel.MindsetData;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;


import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import com.t2.R;

import com.t2.biomap.SharedPref;
import com.t2.compassionDB.BioSession;
import com.t2.compassionDB.BioUser;
import com.t2.compassionDB.DatabaseHelper;
import com.t2.compassionMeditation.GraphsActivity.GraphKeyItem;
import com.t2.compassionUtils.MathExtra;





public class ViewSessionsActivity extends OrmLiteBaseActivity<DatabaseHelper> 
				implements OnItemLongClickListener, OnClickListener{
	private static final String TAG = "BFDemo";
	private static final String mActivityVersion = "1.0";
	public static final String EXTRA_TIME_START = "timeStart";
	public static final String EXTRA_CALENDAR_FIELD = "calendarField";
	public static final String EXTRA_CALENDAR_FIELD_INCREMENT = "calendarFieldIncrement";
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
	private ArrayList<BioSession> sessionItems = new ArrayList<BioSession>();
	
	

	/**
	 * Index of currently selected session
	 * @see sessionItems
	 */
	private int mSelectedId;		
	
	protected Calendar startCal;
	protected Calendar endCal;
	protected int calendarField;				// index of calandar parameter (Defaults to day of month)
	protected int calendarFieldIncrement;				
	
	
	private TextView monthNameTextView;
	SimpleDateFormat monthNameFormatter;
	
	Spinner mBandOfInterestSpinner;
	
	private ArrayList<GraphKeyItem> keyItems = new ArrayList<GraphKeyItem>();
	protected SharedPreferences sharedPref;	
	protected int mBandOfInterest = Constants.PREF_BAND_OF_INTEREST_DEFAULT;
	
	public class MyOnItemSelectedListener implements OnItemSelectedListener {

	    public void onItemSelected(AdapterView<?> parent,
	        View view, int pos, long id) {
			SharedPref.putInt(instance, Constants.PREF_BAND_OF_INTEREST_REVIEW , pos);	   
			mBandOfInterest = pos;
    		generateChart(DIRECTION_NEXT); 
			
			
	    }

	    public void onNothingSelected(AdapterView parent) {
	      // Do nothing.
	    }
	}	
	
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

		Intent intent = this.getIntent();
//		calendarField = intent.getIntExtra(EXTRA_CALENDAR_FIELD, Calendar.HOUR_OF_DAY);
		calendarField = intent.getIntExtra(EXTRA_CALENDAR_FIELD, Calendar.DAY_OF_MONTH);
		
		setCalendarResolution();
    	setupCalendars();            	
        updateListView();

        this.findViewById(R.id.monthMinusButton).setOnClickListener(this);
		this.findViewById(R.id.monthPlusButton).setOnClickListener(this);
        this.monthNameTextView.setOnClickListener(this);
        
        // Get the list of band names from the first session (All of the session key names will be the same)
		if (sessionItems.size() >= 1) {
			BioSession session = sessionItems.get(0);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, session.keyItemNames);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mBandOfInterestSpinner = (Spinner) findViewById(R.id.spinnerBandOfInterest);		
			mBandOfInterestSpinner.setAdapter(adapter)	;	

			mBandOfInterestSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener());    
			mBandOfInterestSpinner.setSelection(SharedPref.getInt(this, Constants.PREF_BAND_OF_INTEREST_REVIEW , 	
					mBandOfInterest));
		}
		
        
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
	                	setupCalendars();            	
	            		updateListView();
	            		generateChart(DIRECTION_NEXT); 
						
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
		long startTime = startCal.getTimeInMillis();
		long endTime = endCal.getTimeInMillis();	
		
		
		if (mCurrentBioUser != null) {
		
			for (BioSession session: mCurrentBioUser.getSessions()) {
				
				if (session.time >= startTime && session.time <= endTime ) {
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

		case R.id.monthName:
			calendarResolutionButtonPressed();
			break;
		}
		
		
		
	}

	double getXValueBasedOnResolution(long sessionTime) {
		int i;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(sessionTime);
		
		i = cal.get(calendarField);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int dayofWeek = cal.get(Calendar.DAY_OF_WEEK);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		
		float value = 0;;

		switch (calendarField) {
		case Calendar.DAY_OF_MONTH:
			value = day + (float) hour/24 + (float) minute/60;
			break;

		case Calendar.HOUR_OF_DAY:
			value = (float) hour + (float) minute/60;
			break;

		default:
			break;
		}

		
		return (double) value;
		
	}
	
	
	private void generateChart(int direction) {
		XYMultipleSeriesDataset dataSet = new XYMultipleSeriesDataset();
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		
//		XYSeries minSeries = new XYSeries("minSeries");
		XYSeries avgSeries = new XYSeries("avgSeries");
//		XYSeries maxSeries = new XYSeries("maxSeries");
		
        LinearLayout layout = (LinearLayout) findViewById(R.id.deviceChart);    	
    	if (mDeviceChartView != null) {
    		layout.removeView(mDeviceChartView);
    	}

//		LineChart chart = new LineChart(dataSet, renderer);
//		RangeBarChart chart = new RangeBarChart(dataSet, renderer, BarChart.Type.DEFAULT);

    	
  //  	mDeviceChartView = new OffsetGraphicalChartView(this, chart);
     	
//     	mDeviceChartView = ChartFactory.getRangeBarChartView(this, dataSet, renderer, BarChart.Type.DEFAULT );
     	mDeviceChartView = ChartFactory.getLineChartView(this, dataSet, renderer);
     	layout.addView(mDeviceChartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
         	
		ArrayList<DataPoint> dataPoints = null;
		long startTime = startCal.getTimeInMillis();
		long endTime = endCal.getTimeInMillis();	
		
		double maxChartValue = 0;


		// Get the data points
		for (BioSession session : sessionItems) {
			if (session.time >= startTime && session.time <= endTime ) {
				double chartYMin = session.minFilteredValue[mBandOfInterest];
				double chartYAvg = session.avgFilteredValue[mBandOfInterest];
				double chartYMax = session.maxFilteredValue[mBandOfInterest];
				if (chartYMin > maxChartValue)  maxChartValue = chartYMin;
				if (chartYAvg > maxChartValue)  maxChartValue = chartYAvg;
				if (chartYMax > maxChartValue)  maxChartValue = chartYMax;
				
				double chartXValue = getXValueBasedOnResolution(session.time);
				
//				minSeries.add(chartXValue, chartYMin );
				avgSeries.add(chartXValue, chartYAvg );
//				maxSeries.add(chartXValue, chartYMax );
				
			}
		}
		
		
		
//		// Put some test points in
//		for (int i = 0; i < 10; i++ ) {
//			DataPoint dp = new DataPoint(System.currentTimeMillis() + i * 1000,0);
//			cal.setTimeInMillis(dp.time);
//			series.add(cal.get(calendarField), i*3);
//			
//		}
		
		
//		XYSeriesRenderer minSeriesRenderer = new XYSeriesRenderer();
//		minSeriesRenderer.setColor(Color.YELLOW);
//		minSeriesRenderer.setPointStyle(PointStyle.CIRCLE);
//		minSeriesRenderer.setFillPoints(false);
//		minSeriesRenderer.setLineWidth(0 * displayMetrics.density);
//		renderer.addSeriesRenderer(minSeriesRenderer);
//		dataSet.addSeries(minSeries);		
//		
		XYSeriesRenderer avgSeriesRenderer = new XYSeriesRenderer();
		avgSeriesRenderer.setColor(Color.RED);
		avgSeriesRenderer.setPointStyle(PointStyle.CIRCLE);
		avgSeriesRenderer.setFillPoints(true);
		avgSeriesRenderer.setLineWidth(2 * displayMetrics.density);
		renderer.addSeriesRenderer(avgSeriesRenderer);
		dataSet.addSeries(avgSeries);		

//		XYSeriesRenderer maxSeriesRenderer = new XYSeriesRenderer();
//		maxSeriesRenderer.setColor(Color.YELLOW);
//		maxSeriesRenderer.setPointStyle(PointStyle.CIRCLE);
//		maxSeriesRenderer.setFillPoints(false);
//		maxSeriesRenderer.setLineWidth(0 * displayMetrics.density);
//		renderer.addSeriesRenderer(maxSeriesRenderer);
//		dataSet.addSeries(maxSeries);		
		
		
		
		// only contine making the chart if there is data in the series.
		if(dataSet.getSeriesCount() > 0) {
			
			// Make the renderer for the weekend blocks
			Calendar weekendCal = Calendar.getInstance();
			weekendCal.setTimeInMillis(System.currentTimeMillis());
			
			
			int lastDayOfMonth = weekendCal.getActualMaximum(calendarField);
//			int lastDayOfMonth = weekendCal.getActualMaximum(Calendar.DAY_OF_MONTH);
			
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

	protected void monthMinusButtonPressed() {
		startCal.add(calendarFieldIncrement, -1);
		endCal.add(calendarFieldIncrement, -1);
		this.monthNameTextView.setText(monthNameFormatter.format(startCal.getTime()));
        updateListView();
		generateChart(DIRECTION_PREVIOUS);
	}
	
	protected void monthPlusButtonPressed() {
		startCal.add(calendarFieldIncrement, 1);
		endCal.add(calendarFieldIncrement, 1);
		this.monthNameTextView.setText(monthNameFormatter.format(startCal.getTime()));
        updateListView();
		generateChart(DIRECTION_NEXT);
	}

	void setupCalendars() {
    	long startTime = Calendar.getInstance().getTimeInMillis();
		
		startCal = Calendar.getInstance();
		startCal.setTimeInMillis(MathExtra.roundTime(startTime, calendarField));
		startCal.set(calendarField, startCal.getMinimum(calendarField));
		
		endCal = Calendar.getInstance();
		endCal.setTimeInMillis(startCal.getTimeInMillis());
		endCal.add(calendarFieldIncrement, 1);
    	
		monthNameTextView.setText(monthNameFormatter.format(startCal.getTime()));
		
	}
	
	void calendarResolutionButtonPressed() {
	    String[] items = {"Day of Month", "Hour of Day" };
		AlertDialog.Builder alert = new AlertDialog.Builder(instance);
		alert.setTitle("Set Calandar Resolution");
		alert.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            	switch (which) {
            	case 0:
            		calendarField = Calendar.DAY_OF_MONTH;
            		break;
            	case 1:
            		calendarField = Calendar.HOUR_OF_DAY;
            		break;
            	}
        		
            	setCalendarResolution();
        		// We must reset the start and end calendars
            	setupCalendars();           
                updateListView();
            	
        		generateChart(DIRECTION_NEXT);        
            }		
        });				
		
		alert.show();		
	}
	
	void setCalendarResolution() {

		switch (calendarField) {
		case Calendar.DAY_OF_MONTH:
			calendarFieldIncrement = Calendar.MONTH; 
			monthNameFormatter = new SimpleDateFormat("MMMM, yyyy");
			
			break;
	
		case Calendar.HOUR_OF_DAY:
			monthNameFormatter = new SimpleDateFormat("dd-MMMM-yyyy");
			calendarFieldIncrement = Calendar.DATE; 
			break;
	
		default:
			break;
		}
		
		
	}
	

	
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
