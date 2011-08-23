package com.t2.compassionMeditation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;


import com.t2.biomap.SharedPref;

import com.t2.Constants;

import spine.SPINEFunctionConstants;
import spine.datamodel.Node;
import spine.datamodel.Data;
import spine.datamodel.MindsetData;
import spine.datamodel.ZephyrData;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


//Need the following import to get access to the app resources, since this
//class is in a sub-package.
import com.t2.R;


public class ViewHistoryActivity extends Activity implements OnSeekBarChangeListener
{
	private static final String TAG = "BFDemo";
	private static final String mActivityVersion = "1.0";

	private BufferedReader mLogReader = null;

	private Vector mSessionData;
	private int mCursor = 0;
	
	/**
	 * Session name which is used for file creation (based on selected user) 
	 */
	private String mSessionName = "";
	
	
	/**
	 * Application version info determined by the package manager
	 */
	private String mApplicationVersion = "";

	/**
	 * Static instance of this activity
	 */
	private static ViewHistoryActivity instance;
	
	/**
	 * Timer for updating the UI
	 */
	private static Timer mDataUpdateTimer;	
	
	// Charting stuff
	private final static int SPINE_CHART_SIZE = 20;
	
	private GraphicalView mDeviceChartView;
	private int mSpineChartX = 0;
	
	
	private boolean mPaused = false;
	
	// UI Elements
    private Button mAddMeasureButton;
    private Button mPauseButton;
    private Button mToggleLogButton;
    private Button mLlogMarkerButton;
    private TextView mTextInfoView;
    private TextView mTextViewComment;
    private TextView mMeasuresDisplayText;
    private SeekBar mSeekBar;    
    
	protected SharedPreferences sharedPref;
	private static final String KEY_NAME = "results_visible_ids_";	
	private ArrayList<GraphKeyItem> keyItems = new ArrayList<GraphKeyItem>();
	private MindsetData currentMindsetData;
	
	private int bandOfInterest = MindsetData.THETA_ID; // Default to theta
	private int numSecsWithoutData = 0;
	
	private int heartRatePos;
	private int respRatePos;
	private int skinTempPos;
	
	
	/**
	 * @return Static instance of this activity
	 */
	public static ViewHistoryActivity getInstance() {
	   return instance;
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // This needs to happen BEFORE setContentView
        setContentView(R.layout.view_history);
        instance = this;
    	
        currentMindsetData = new MindsetData(this);
    
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());   
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);      
        
        bandOfInterest = SharedPref.getInt(this, 
				com.t2.compassionMeditation.Constants.PREF_BAND_OF_INTEREST ,
				com.t2.compassionMeditation.Constants.PREF_BAND_OF_INTEREST_DEFAULT);
        
        
        Resources resources = this.getResources();
        AssetManager assetManager = resources.getAssets();
        
        // Set up member variables to UI Elements
        mPauseButton = (Button) findViewById(R.id.buttonPause);
        mAddMeasureButton = (Button) findViewById(R.id.buttonAddMeasure);
        mToggleLogButton = (Button) findViewById(R.id.buttonLogging);
        mLlogMarkerButton = (Button) findViewById(R.id.LogMarkerButton);
        mTextInfoView = (TextView) findViewById(R.id.textViewInfo);
        mTextViewComment = (TextView) findViewById(R.id.textViewComment);
        
        mMeasuresDisplayText = (TextView) findViewById(R.id.measuresDisplayText);
        
        ImageView image = (ImageView) findViewById(R.id.imageView1);
        image.setImageResource(R.drawable.signal_bars0);  

        mSeekBar = (SeekBar)findViewById(R.id.seekBar1);    
        mSeekBar.setOnSeekBarChangeListener(this);        
        

     
        int i;
        for (i = 0; i < MindsetData.NUM_BANDS + 2; i++) {		// 2 extra, for attention and meditation
        	GraphKeyItem key = new GraphKeyItem(i, MindsetData.spectralNames[i], "");
            keyItems.add(key);
        }
        heartRatePos = i;
    	GraphKeyItem key = new GraphKeyItem(i++, "HeartRate", "");
        keyItems.add(key);
        
        respRatePos = i;
        key = new GraphKeyItem(i++, "RespRate", "");
        keyItems.add(key);
        
        skinTempPos = i;
    	key = new GraphKeyItem(i, "SkinTemp", "");
        keyItems.add(key);
                

        // Set up Device data chart
        generateChart();
        
		try {
			PackageManager packageManager = this.getPackageManager();
			PackageInfo info = packageManager.getPackageInfo(this.getPackageName(), 0);			
			mApplicationVersion = info.versionName;
			Log.i(TAG, "Compassion Meditation Application Version: " + mApplicationVersion + ", Activity Version: " + mActivityVersion);
		} 
		catch (NameNotFoundException e) {
			   	Log.e(TAG, e.toString());
		}
		
        // Get the session(file) name
        try {
			// Get target name if one was supplied
			Bundle bundle = getIntent().getExtras();
			mSessionName = bundle.getString(com.t2.compassionMeditation.Constants.EXTRA_SESSION_NAME);

			mSessionData = new Vector();
			loadSessionData();
			mSeekBar.setMax(mSessionData.size() - SPINE_CHART_SIZE);
			updateChart();
			
			
			
			
			
		} catch (Exception e1) {
			mSessionName = "";
		}
    } // End onCreate(Bundle savedInstanceState)
    
    class MindsetPoint extends MindsetData {
    	/**
		 * 
		 */
		private static final long serialVersionUID = 5647398490731023479L;
		public String dateTime;
    	public String comment;
    	
    	public ZephyrData zephyrData = new ZephyrData();;
    	

    	
    	
    	MindsetPoint() {
    		super(context);
    	}
    	
    }
    
    void updateChart() {
		for (int j = 0; j < SPINE_CHART_SIZE; j++) {

			if (j + mCursor >= mSessionData.size()) {
				if (mDeviceChartView != null) {
		            mDeviceChartView.repaint();
		        }  
				return;

			}
			MindsetPoint p = (MindsetPoint) mSessionData.get(j + mCursor);
			if (p.comment.equalsIgnoreCase("")) {
				MindsetPoint data = (MindsetPoint) mSessionData.get(j + mCursor);
				currentMindsetData = (MindsetPoint) mSessionData.get(j + mCursor);

				for (int i = 0; i < MindsetData.NUM_BANDS + 2; i++) {		// 2 extra, for attention and meditation
		        	keyItems.get(i).rawValue = currentMindsetData.getFeatureValue(i);
		        }				
				
				keyItems.get(heartRatePos).rawValue = data.zephyrData.heartRate / 3;
				keyItems.get(respRatePos).rawValue = data.zephyrData.respRate * 5;
				keyItems.get(skinTempPos).rawValue = data.zephyrData.skinTemp;				
				
				int keyCount = keyItems.size();
				for(int i = 0; i < keyItems.size(); ++i) {
					GraphKeyItem item = keyItems.get(i);
					
					if(!item.visible) {
						continue;
					}
						int v = currentMindsetData.getFeatureValue((int) item.id);
//						item.series.add(mSpineChartX, currentMindsetData.getFeatureValue((int) item.id));
						item.series.add(mSpineChartX, item.rawValue);						
						if (item.series.getItemCount() > SPINE_CHART_SIZE) {
							item.series.remove(0);
						}
						
						mSpineChartX++;
				} 						
			}
			else {
				// It's a comment
			}
	
			
		}
		
		if (mDeviceChartView != null) {
            mDeviceChartView.repaint();
        }  

    }
    
    MindsetPoint parseLine(String line) {
    	MindsetPoint data = new MindsetPoint();
    	
    	String[] tokens = line.split(",");
    	
    	if (tokens.length == 2) {
    		data.dateTime = tokens[0];
    		data.comment = tokens[1];
    	}
    	else if (tokens.length == 23) {
    		int i = 0;
    		data.dateTime = tokens[i++];
    		data.comment = tokens[i++];
    		data.poorSignalStrength = Integer.parseInt(tokens[i++].trim());
    		data.attention = Integer.parseInt(tokens[i++].trim());
    		data.meditation = Integer.parseInt(tokens[i++].trim());
    		for (int j = 0; j < MindsetData.NUM_BANDS; j++)	{
    			data.scaledSpectralData[j] = Integer.parseInt(tokens[i++].trim());
    		}    		
    		i++;
    		for (int j = 0; j < MindsetData.NUM_BANDS; j++)	{
    			data.rawSpectralData[j] = Integer.parseInt(tokens[i++].trim());
    		}    		
    	}
    	else if (tokens.length == 25) {
    		int i = 0;
    		
    		data.dateTime = tokens[i++];
    		data.zephyrData.heartRate = Integer.parseInt(tokens[i++].trim());
    		data.zephyrData.respRate = Integer.parseInt(tokens[i++].trim());
    		data.zephyrData.skinTemp = Integer.parseInt(tokens[i++].trim());
    		
    		data.comment = "";
    		data.poorSignalStrength = Integer.parseInt(tokens[i++].trim());
    		data.attention = Integer.parseInt(tokens[i++].trim());
    		data.meditation = Integer.parseInt(tokens[i++].trim());
    		for (int j = 0; j < MindsetData.NUM_BANDS; j++)	{
    			data.ratioSpectralData[j] = Integer.parseInt(tokens[i++].trim());
    		}    		
    		i++;
    		for (int j = 0; j < MindsetData.NUM_BANDS; j++)	{
    			data.rawSpectralData[j] = Integer.parseInt(tokens[i++].trim());
    		}    		
    	}
    	return data;
    }
    
    
	boolean loadSessionData()
	{
		// Open a file for saving data
		try {
		    File root = Environment.getExternalStorageDirectory();
		    if (root.canWrite()){
		        File gpxfile = new File(root, mSessionName);
		        FileReader gpxreader = new FileReader(gpxfile); // open for append
		        mLogReader = new BufferedReader(gpxreader);
		        
		        String lineToParse;
		        while ((lineToParse = mLogReader.readLine()) != null) {
		        	try {
						MindsetPoint point = parseLine(lineToParse);
						mSessionData.add(point);
						Log.i(TAG,lineToParse);
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
		        TextView textViewSessionName = (TextView) findViewById(R.id.textViewSessionName);
		        textViewSessionName.setText("Session: " + mSessionName);
		        
		    } 
		    else {
    		    Log.e(TAG, "Could not open file " );
    			AlertDialog.Builder alert = new AlertDialog.Builder(this);
    			
    			alert.setTitle("ERROR");
    			alert.setMessage("Cannot open to file");	
    			alert.show();			
		    	
		    }
		} catch (IOException e) {
		    Log.e(TAG, "Could not write file " + e.getMessage());
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			
			alert.setTitle("ERROR");
			alert.setMessage("Cannot write to file");	
			alert.show();			
		    
		}
		
		
		return true;
	}
    
    
    
    @Override
	protected void onDestroy() {
    	super.onDestroy();
    	
    	saveState();
    	
		Log.i(TAG, TAG + " onDestroy");
	}

	private void generateChart() {
        // Set up chart
    	XYMultipleSeriesDataset deviceDataset = new XYMultipleSeriesDataset();
    	XYMultipleSeriesRenderer deviceRenderer = new XYMultipleSeriesRenderer();        

        LinearLayout layout = (LinearLayout) findViewById(R.id.deviceChart);    	
    	if (mDeviceChartView != null) {
    		layout.removeView(mDeviceChartView);
    	}
       	if (true) {
          mDeviceChartView = ChartFactory.getLineChartView(this, deviceDataset, deviceRenderer);
          mDeviceChartView.setBackgroundColor(Color.BLACK);
//          mDeviceChartView.setBackgroundColor(Color.WHITE);
          layout.addView(mDeviceChartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        }    
    	
        deviceRenderer.setShowLabels(false);
        deviceRenderer.setMargins(new int[] {0,5,5,0});
        deviceRenderer.setShowAxes(true);
        deviceRenderer.setShowLegend(false);
        
        deviceRenderer.setZoomEnabled(false, false);
        deviceRenderer.setPanEnabled(false, false);
        deviceRenderer.setYAxisMin(0);
        deviceRenderer.setYAxisMax(150);
//        deviceRenderer.setYAxisMax(255);

        SpannableStringBuilder sMeasuresText = new SpannableStringBuilder("Displaying: ");
        
		ArrayList<Long> visibleIds = getVisibleIds("measure");
		int keyCount = keyItems.size();
        keyCount = keyItems.size();
        
		int lineNum = 0;
		for(int i = 0; i < keyItems.size(); ++i) {
			GraphKeyItem item = keyItems.get(i);
			
			item.visible = visibleIds.contains(item.id);
			if(!item.visible) {
				continue;
			}
			
			deviceDataset.addSeries(item.series);
			item.color = getKeyColor(i, keyCount);
			
			// Add name of the measure to the displayed text field
			ForegroundColorSpan fcs = new ForegroundColorSpan(item.color);
			int start = sMeasuresText.length();
			sMeasuresText.append(keyItems.get(i).title1 + ", ");			
			int end = sMeasuresText.length();
			sMeasuresText.setSpan(fcs, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			if (sMeasuresText.length() > 40 && lineNum == 0) {
				lineNum++;
			}
			
			XYSeriesRenderer seriesRenderer = new XYSeriesRenderer();
			seriesRenderer.setColor(item.color);
			
			seriesRenderer.setPointStyle(PointStyle.CIRCLE);
//			seriesRenderer.setFillPoints(true);
//			seriesRenderer.setLineWidth(2 * displayMetrics.density);			
			
			
			deviceRenderer.addSeriesRenderer(seriesRenderer);
			
		}     
		mMeasuresDisplayText.setText(sMeasuresText) ;       
		
	}
    
	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, TAG + " OnStart");
		
		
		// Set up a timer to do graphical updates
		mDataUpdateTimer = new Timer();
		mDataUpdateTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				TimerMethod();
			}

		}, 0, 1000);		
		
		setCursor(0);		
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.menu_compassion_meditation, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.about:
			String content = "National Center for Telehealth and Technology (T2)\n\n";
			content += "Compassion Meditation Application\n";
			content += "Application Version: " + mApplicationVersion + "\n";
			content += "Activity Version: " + mActivityVersion;
			
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			
			alert.setTitle("About");
			alert.setMessage(content);	
			alert.show();			
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	

	/**
	 * This is where we receive sensor data that comes through the actual
	 * Spine channel. 
	 * @param data		Generic Spine data packet. Should be cast to specifid data type indicated by data.getFunctionCode()
	 *
	 * @see spine.SPINEListener#received(spine.datamodel.Data)
	 */

	public void received(Data data) {
		
		if (data != null) {
			switch (data.getFunctionCode()) {

			case SPINEFunctionConstants.MINDSET: {
					Node source = data.getNode();
				
					MindsetData mindsetData = (MindsetData) data;
					if (mindsetData.exeCode == Constants.EXECODE_POOR_SIG_QUALITY) {
						
						int sigQuality = mindsetData.poorSignalStrength & 0xff;
						ImageView image = (ImageView) findViewById(R.id.imageView1);
						if (sigQuality == 200)
							image.setImageResource(R.drawable.signal_bars0);
						else if (sigQuality > 150)
							image.setImageResource(R.drawable.signal_bars1);
						else if (sigQuality > 100)
							image.setImageResource(R.drawable.signal_bars2);
						else if (sigQuality > 50)
							image.setImageResource(R.drawable.signal_bars3);
						else if (sigQuality > 25)
							image.setImageResource(R.drawable.signal_bars4);
						else 
							image.setImageResource(R.drawable.signal_bars5);
					}
					
					if (mindsetData.exeCode == Constants.EXECODE_SPECTRAL) {
						currentMindsetData.updateSpectral(mindsetData);
						Log.i(TAG, "Spectral Data");
						numSecsWithoutData = 0;							
					}
					
					if (mindsetData.exeCode == Constants.EXECODE_POOR_SIG_QUALITY) {
						currentMindsetData.poorSignalStrength = mindsetData.poorSignalStrength;
					}
					
					if (mindsetData.exeCode == Constants.EXECODE_ATTENTION) {
						currentMindsetData.attention= mindsetData.attention;
					}
					
					if (mindsetData.exeCode == Constants.EXECODE_MEDITATION) {						
						currentMindsetData.meditation= mindsetData.meditation;
					}						
					
					break;
				} // End case SPINEFunctionConstants.MINDSET:
			} // End switch (data.getFunctionCode())
		} // End if (data != null)
	}
	

	/**
	 * Converts a byte array to an integer
	 * @param bytes		Bytes to convert
	 * @return			Integer representaion of byte array
	 */
	public static int byteArrayToInt(byte[] bytes) {
		int val = 0;
		
		for(int i = 0; i < bytes.length; i++) {
			int n = (bytes[i] < 0 ? (int)bytes[i] + 256 : (int)bytes[i]) << (8 * i);
			val += n;
		}
		
		return val;
	}
	
	public void onButtonClick(View v)
	{
		 final int id = v.getId();
		    switch (id) {

		    
		    case R.id.buttonLeft:
		    	if (mCursor > 0) {
		    		mSeekBar.setProgress(mCursor - 1);
		    	}
		    	break;

		    case R.id.buttonRight:
	    		mSeekBar.setProgress(mCursor + 1);

	    		// A HUGE cheat here, if we're as far as we can go then show the comment from the last line
	    		if (mCursor >= mSessionData.size() - SPINE_CHART_SIZE) {
		    		MindsetPoint p = (MindsetPoint) mSessionData.get(mSessionData.size() - 1);
		            if (!p.comment.equalsIgnoreCase("")) {
		            	mTextViewComment.setText("Comment: " + p.comment);
		            }
		            else {
		            	mTextViewComment.setText("");
		            }	    		
	    			
	    		}
	    		
	    		
		    	break;

		    case R.id.buttonBack:
		    	finish();
		    	
		    	break;
		    		    
		    case R.id.buttonAddMeasure:
		    	
		    	boolean toggleArray[] = new boolean[keyItems.size() + 2];
				for(int j = 0; j < keyItems.size(); ++j) {
					KeyItem item = keyItems.get(j);
					if(item.visible)
						toggleArray[j] = true;
					else
						toggleArray[j] = false;
					
				}		    	
		    	
				String[] measureNames = new String[keyItems.size()];
				int i = 0;
				for (GraphKeyItem item: keyItems) {
					measureNames[i++] = item.title1;
				}
								
		    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
		    	alert.setTitle(R.string.alert_dialog_measure_selector);
		    	alert.setMultiChoiceItems(measureNames,
//		    	alert.setMultiChoiceItems(R.array.measure_select_dialog_items,
		    			toggleArray,
	                    new DialogInterface.OnMultiChoiceClickListener() {

		    			public void onClick(DialogInterface dialog, int whichButton,boolean isChecked) {

                			KeyItem item = keyItems.get(whichButton);
                			item.visible = item.visible ? false: true;
	                 		saveVisibleKeyIds();	
	                 		generateChart();	                 		
	                        }
	                    });
		    	alert.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {

                 		generateChart();	                 		

	                }
	            });
	
				alert.show();
		    	
		    	break;
		    case R.id.buttonPause:
				if (mPaused == true) {
					mPaused = false;
					mPauseButton.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
				}
				else {
					mPaused = true;
					mPauseButton.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
				}
		        break;
		        
		    } // End switch		
	}
	
	/**
	 * This method is called directly by the timer and runs in the same thread as the timer
	 * From here We call the method that will work with the UI through the runOnUiThread method.
	 */	
	private void TimerMethod() {
		this.runOnUiThread(Timer_Tick);
	}

	/**
	 * This method runs in the same thread as the UI.
	 */
	private Runnable Timer_Tick = new Runnable() {
		public void run() {

//			numSecsWithoutData++;
//			if (numSecsWithoutData > 2) {
//				return;
//			}
//
//			
//			if (mPaused == true || currentMindsetData == null) {
//				return;
//			}
//			
//
//	        int keyCount = keyItems.size();
//			for(int i = 0; i < keyItems.size(); ++i) {
//				KeyItem item = keyItems.get(i);
//				
//				if(!item.visible) {
//					continue;
//				}
//				
//				item.series.add(mSpineChartX, currentMindsetData.getFeatureValue((int) item.id));
//				if (item.series.getItemCount() > SPINE_CHART_SIZE) {
//					item.series.remove(0);
//				}
//				
//			} 			
//			
//	        mTextInfoView.setText(
//	        		"Theta: " + currentMindsetData.getFeatureValue(bandOfInterest) + "\n" +  
//	        		"Time Remaining: "
//	        		);
//			
//
//			mSpineChartX++;
//			
//			if (mDeviceChartView != null) {
//	            mDeviceChartView.repaint();
//	        }   				
		}
	};

	@Override
	protected void onPause() {
		Log.i(TAG, TAG + " onPause");
		mDataUpdateTimer.purge();
    	mDataUpdateTimer.cancel();

    	saveState();
    	
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, TAG + " onStop");
		super.onStop();
	}	

	
	@Override
	protected void onRestart() {
		Log.i(TAG, TAG + " onRestart");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, TAG + " onResume");
		
		restoreState();
		super.onResume();
	}

	void saveState()
	{
	}
	void restoreState()
	{
	}


	private void saveVisibleKeyIds() {
		String keySuffix = "measure";
		ArrayList<Long> toggledIds = new ArrayList<Long>();
		for(int i = 0; i < keyItems.size(); ++i) {
			KeyItem item = keyItems.get(i);
			if(item.visible) {
				toggledIds.add(item.id);
			}
		}
		setVisibleIds(keySuffix, toggledIds);
	}
	
	
	private ArrayList<Long> getVisibleIds(String keySuffix) {
		String[] idsStrArr = SharedPref.getValues(
				sharedPref, 
				KEY_NAME+keySuffix, 
				",",
				new String[0]
		);
		
		return new ArrayList<Long>(
				Arrays.asList(
						ArraysExtra.toLongArray(idsStrArr)
				)
		);
	}	

	private void setVisibleIds(String keySuffix, ArrayList<Long> ids) {
		SharedPref.setValues(
				sharedPref, 
				KEY_NAME+keySuffix, 
				",", 
				ArraysExtra.toStringArray(ids.toArray(new Long[ids.size()]))
		);
	}	
	
	protected int getKeyColor(int currentIndex, int totalCount) {
		float hue = currentIndex / (1.00f * totalCount) * 360.00f;
		
		return Color.HSVToColor(
    			255,
    			new float[]{
    				hue,
    				1.0f,
    				1.0f
    			}
    	);
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		try {
			setCursor(arg1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void setCursor(int start) {
		mCursor = start;
		MindsetPoint p = (MindsetPoint) mSessionData.get(mCursor);

        mTextInfoView.setText(p.dateTime + ":\n " + p.getSpectralName(bandOfInterest) + ":" + p.getFeatureValue(bandOfInterest) + "\n");
        if (!p.comment.equalsIgnoreCase("")) {
        	mTextViewComment.setText("Comment: " + p.comment);
        }
        else {
        	mTextViewComment.setText("");
        }
		
		updateChart();
		
	}
	
	
	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private static class GraphKeyItem extends KeyItem{
		public XYSeries series;		
		
		public GraphKeyItem(long id, String title1, String title2) {
			super(id, title1, title2);
			series = new XYSeries(title1);		
		}
		
	}
	
}