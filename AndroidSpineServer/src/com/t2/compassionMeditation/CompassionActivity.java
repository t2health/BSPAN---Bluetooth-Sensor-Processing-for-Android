package com.t2.compassionMeditation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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


import com.t2.AndroidSpineConnector;
import com.t2.SpineReceiver;
import com.t2.SpineReceiver.BioFeedbackStatus;
import com.t2.SpineReceiver.OnBioFeedbackMessageRecievedListener;
import com.t2.biomap.BioLocation;
import com.t2.biomap.BioMapActivity;
import com.t2.biomap.LogNoteActivity;
import com.t2.biomap.SharedPref;



//import com.t2.vas.activity.ABSResultsActivity.KeyItem;
//import com.t2.vas.activity.ABSResultsActivity.KeyItem;
import com.t2.Constants;

import spine.datamodel.Node;
import spine.SPINEFactory;
import spine.SPINEFunctionConstants;
import spine.SPINEListener;
import spine.SPINEManager;
import spine.datamodel.Address;
import spine.datamodel.Data;
import spine.datamodel.MindsetData;
import spine.datamodel.ServiceMessage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
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
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;


//Need the following import to get access to the app resources, since this
//class is in a sub-package.

import com.t2.R;

/**
 * Main (test) activity for Spine server.
 * This application sets up and initializes the Spine server to talk to a couple of sample devices
 * and displays their data streams.
 * 
 * Note that the AndroidBTService is an Android background service mandatory as a front end 
 * for the Bluetooth devices to work with the Spine server.
 * 
 * This activity uses two mechanisms to communicate with the AndroidBTService
 *  1. Broadcast intents are used to communicate low bandwidth messages: status messages and connection information
 *  2. A service connection is used to communicate potentially high bandwidth messages (Sensor data messages)
 * 
 * 
 * @author scott.coleman
 *
 */
public class CompassionActivity extends Activity implements OnBioFeedbackMessageRecievedListener, SPINEListener {
	private static final String TAG = "CompassionActivity";

    private static AndroidSpineConnector spineConnector;
    
    

	/**
     * The Spine manager contains the bulk of the Spine server. 
     */
    private static SPINEManager mManager;

    /**
	 * This is a broadcast receiver. Note that this is used ONLY for command/status messages from the AndroidBTService
	 * All data from the service goes through the mail SPINE mechanism (received(Data data)).
	 */
	private SpineReceiver mCommandReceiver;
	
	/**
	 * Static instance of this activity
	 */
	private static CompassionActivity instance;

	/**
	 * Service connection used to communicate data messages with the AndroidBTService
	 */
	ServiceConnection mConnection;
	
	/**
	 * This is the MEssenger service which is used to communicate sensor data messages between the main activity 
	 * and the AndroidBTService
	 */
	private Messenger mService = null;	
	
	/**
	 * Whether or not the AndroidBTService is bound to this activity
	 */
	boolean mIsBound = false;
	
	
	
	private static Timer mDataUpdateTimer;	
	
	
	// Charting stuff
	private final static int SPINE_CHART_SIZE = 20;
	
	private GraphicalView mDeviceChartView;

	  
    static final int MSG_UNREGISTER_CLIENT = 2;	
	
	
	int mSpineChartX = 0;
	
	String mPackageName = "";
	int mVersionCode;
	String mVersionName = "";
	Vector<String>  mDeviceDetailContent = new Vector<String>();
	
	
	BufferedWriter mLogWriter = null;
	boolean mLoggingEnabled = false;
	boolean mPaused = false;
	
	public static final int ANDROID_SPINE_SERVER_ACTIVITY = 0;
	public static final String ANDROID_SPINE_SERVER_ACTIVITY_RESULT = "AndroidSpineServerActivityResult";
	
    private Button mAddMeasureButton;
    private Button mPauseButton;
    private Button mToggleLogButton;
    private Button mLlogMarkerButton;
    private TextView mTextInfoView;
    private TextView mMeasuresDisplayText;
    private SeekBar mMeditationBar;    
    
    
    private String mLogMarkerNote = null;
    

	protected SharedPreferences sharedPref;
	private static final String KEY_NAME = "results_visible_ids_";	
	private ArrayList<KeyItem> keyItems = new ArrayList<KeyItem>();
	MindsetData currentMindsetData = new MindsetData();
	
	
	private int bandOfInterest = MindsetData.THETA_ID; // Default to theta
	private int numSecsWithoutData = 0;
	
	/**
	 * Sets up messenger service which is used to communicate to the AndroidBTService
	 * @param mService
	 */
	public void setmService(Messenger mService) {
		this.mService = mService;
	}

	/**
	 * @return Static instance of this activity
	 */
	public static CompassionActivity getInstance() 
	{
	   return instance;
	}
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compassion);
        instance = this;
    
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());   
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);        
        
        
        
        
        
        
        Resources resources = this.getResources();
        AssetManager assetManager = resources.getAssets();
        
        // Set up member variables to UI Elements
        mPauseButton = (Button) findViewById(R.id.buttonPause);
        mAddMeasureButton = (Button) findViewById(R.id.buttonAddMeasure);
        mToggleLogButton = (Button) findViewById(R.id.buttonLogging);
        mLlogMarkerButton = (Button) findViewById(R.id.LogMarkerButton);
        mTextInfoView = (TextView) findViewById(R.id.textViewInfo);
        mMeasuresDisplayText = (TextView) findViewById(R.id.measuresDisplayText);
        
        ImageView image = (ImageView) findViewById(R.id.imageView1);
        image.setColorFilter(Color.HSVToColor(255, new float[]{ 120,1.0f,1.0f}), PorterDuff.Mode.MULTIPLY);
        image.setImageResource(R.drawable.headphones);  
        
        

        mMeditationBar = (SeekBar)findViewById(R.id.seekBar1);    
        
        mMeditationBar.setProgress(50);
//        mSeekBar.setIndeterminate(true);
        
        
        
		// Initialize SPINE by passing the fileName with the configuration properties
		try {
			mManager = SPINEFactory.createSPINEManager("SPINETestApp.properties", resources);
		} catch (InstantiationException e) {
			Log.e(TAG, "Exception creating SPINE manager: " + e.toString());
			e.printStackTrace();
		}        
		
		// Since Mindset is a static node we have to manually put it in the active node list
		// Note that the sensor id 0xfff1 (-15) is a reserved id for this particular sensor
		Node mindsetNode = null;
		mindsetNode = new Node(new Address("" + Constants.RESERVED_ADDRESS_MINDSET));
		mManager.getActiveNodes().add(mindsetNode);
				
		// ... then we need to register a SPINEListener implementation to the SPINE manager instance
		// to receive sensor node data from the Spine server
		// (I register myself since I'm a SPINEListener implementation!)
		mManager.addListener(this);	        
                
		// Create a broadcast receiver. Note that this is used ONLY for command messages from the service
		// All data from the service goes through the mail SPINE mechanism (received(Data data)).
		// See public void received(Data data)
        this.mCommandReceiver = new SpineReceiver(this);
     
        for (int i = 0; i < MindsetData.NUM_BANDS; i++) {
        	KeyItem key = new KeyItem(i, MindsetData.spectralNames[i], "");
            keyItems.add(key);
        }

        // Set up Device data chart
        generateChart();
        
        
        
		try {
			PackageManager packageManager = this.getPackageManager();
			PackageInfo info = packageManager.getPackageInfo(this.getPackageName(), 0);			
			mPackageName = info.packageName;
			mVersionCode = info.versionCode;
			mVersionName = info.versionName;
			Log.i(TAG, "Compassion Meditation Application Version " + mVersionName);
		} 
		catch (NameNotFoundException e) {
			   	Log.e(TAG, e.toString());
		}
		
		
		mManager.discoveryWsn();
    } // End onCreate(Bundle savedInstanceState)
    
    
    @Override
	protected void onDestroy() {
    	super.onDestroy();
    	
    	mLoggingEnabled = false;
    	saveState();
    	
    	this.sendBroadcast(new Intent("com.t2.biofeedback.service.STOP"));
    	this.unregisterReceiver(this.mCommandReceiver);
		Log.i(TAG, "MainActivity onDestroy");
	    	
  //  	doUnbindService();    	
	}

	private void generateChart() {
        // Set up chart
    	XYMultipleSeriesDataset deviceDataset = new XYMultipleSeriesDataset();
    	XYMultipleSeriesRenderer deviceRenderer = new XYMultipleSeriesRenderer();        

        LinearLayout layout = (LinearLayout) findViewById(R.id.deviceChart);    	
    	if (mDeviceChartView != null)
    	{
    		layout.removeView(mDeviceChartView);
    	}
       	if (true) 
        {
          mDeviceChartView = ChartFactory.getLineChartView(this, deviceDataset, deviceRenderer);
          mDeviceChartView.setBackgroundColor(Color.WHITE);
          layout.addView(mDeviceChartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        }    

    	
        deviceRenderer.setShowLabels(false);
        deviceRenderer.setMargins(new int[] {0,5,5,0});
        deviceRenderer.setShowAxes(true);
        deviceRenderer.setShowLegend(false);
      //  deviceRenderer.setBackgroundColor(Color.WHITE);
        
        deviceRenderer.setZoomEnabled(false, false);
        deviceRenderer.setPanEnabled(false, false);
        deviceRenderer.setYAxisMin(0);
        deviceRenderer.setYAxisMax(255);
        

        SpannableStringBuilder sMeasuresText = new SpannableStringBuilder("Displaying: ");
        
		ArrayList<Long> visibleIds = getVisibleIds("measure");
		int keyCount = keyItems.size();
        keyCount = keyItems.size();
        
		int lineNum = 0;
		for(int i = 0; i < keyItems.size(); ++i) {
			KeyItem item = keyItems.get(i);
			
			item.visible = visibleIds.contains(item.id);
			if(!item.visible) {
				continue;
			}
			
			deviceDataset.addSeries(item.series);
			item.color = getKeyColor(i, keyCount);
			
			// Add name of the measure to the displayed text field
			ForegroundColorSpan fcs = new ForegroundColorSpan(item.color);
			int start = sMeasuresText.length();
			sMeasuresText.append(MindsetData.spectralNames[i] + ", ");
			int end = sMeasuresText.length();
			sMeasuresText.setSpan(fcs, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			if (sMeasuresText.length() > 40 && lineNum == 0)
			{
				lineNum++;
				//sMeasuresText.append("\n");
			}
			
			XYSeriesRenderer seriesRenderer = new XYSeriesRenderer();
			seriesRenderer.setColor(item.color);
//			seriesRenderer.setPointStyle(PointStyle.CIRCLE);
//			seriesRenderer.setFillPoints(true);
//			seriesRenderer.setLineWidth(2 * displayMetrics.density);
			
			
			
			deviceRenderer.addSeriesRenderer(seriesRenderer);
			

			
		}     
		mMeasuresDisplayText.setText(sMeasuresText) ;       
		
	}
    
	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "OnStart");
		
		
		
		// Set up filter intents so we can receive broadcasts
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.t2.biofeedback.service.status.BROADCAST");
		this.registerReceiver(this.mCommandReceiver,filter);
		
		// Set up a timer to do graphical updates
		mDataUpdateTimer = new Timer();
		mDataUpdateTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				TimerMethod();
			}

		}, 0, 1000);		
		
		
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.settings:
			startActivity(new Intent("com.t2.biofeedback.MANAGER"));
			return true;
			
		case R.id.discover:
			mManager.discoveryWsn();

			return true;
			
		case R.id.about:
			String content = "National Center for Telehealth and Technology (T2)\n\n";
			content += "Spine Server Test Application\n";
			content += "Version " + mVersionName;
			
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			
			alert.setTitle("About");
			alert.setMessage(content);	
			alert.show();			
			return true;
			
		case R.id.biomap:

			Intent i = new Intent(this, BioMapActivity.class);
			this.startActivity(i);
			return true;

		default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * This callback is called whenever the AndroidBTService sends us an indication that
	 * it is actively trying to establish a BT connection to one of the nodes.
	 * 
	 * @see com.t2.SpineReceiver.OnBioFeedbackMessageRecievedListener#onStatusReceived(com.t2.SpineReceiver.BioFeedbackStatus)
	 */
	@Override
	public void onStatusReceived(BioFeedbackStatus bfs) {
		if(bfs.messageId.equals("CONN_CONNECTING")) {
			Log.i(TAG, "Received command : CONN_CONNECTING" );
			Toast.makeText (getApplicationContext(), "**** Connecting to Sensor Node ****", Toast.LENGTH_SHORT).show ();
		} 
		else if(bfs.messageId.equals("CONN_ANY_CONNECTED")) {
			Log.i(TAG, "Received command : CONN_ANY_CONNECTED" );
			// Something has connected - discover what it was
			mManager.discoveryWsn();
			Toast.makeText (getApplicationContext(), "**** Sensor Node Connected ****", Toast.LENGTH_SHORT).show ();
		} 
		else if(bfs.messageId.equals("CONN_CONNECTION_LOST")) {
			Log.i(TAG, "Received command : CONN_ANY_CONNECTED" );		
			Toast.makeText (getApplicationContext(), "**** Sensor Node Connection lost ****", Toast.LENGTH_SHORT).show ();
		}
	}

	@Override
	public void newNodeDiscovered(Node newNode) {
	}

	@Override
	public void received(ServiceMessage msg) {
	}

	/**
	 * This is where we receive sensor data that comes through the actual
	 * Spine channel. 
	 * @param data		Generic Spine data packet. Should be cast to specifid data type indicated by data.getFunctionCode()
	 *
	 * @see spine.SPINEListener#received(spine.datamodel.Data)
	 */
	@Override
	public void received(Data data) {
		
		if (data != null) {
//			if (!mPaused == true) {
				switch (data.getFunctionCode()) {

				case SPINEFunctionConstants.MINDSET: {
						Node source = data.getNode();
					
						MindsetData mindsetData = (MindsetData) data;
						if (mindsetData.exeCode == Constants.EXECODE_POOR_SIG_QUALITY) {
							
							int sigQuality = mindsetData.poorSignalStrength & 0xff;
							ImageView image = (ImageView) findViewById(R.id.imageView1);
							if (sigQuality == 200) {
						        image.setColorFilter(Color.HSVToColor(255, new float[]{ 0,1.0f,1.0f}), PorterDuff.Mode.MULTIPLY);
						        image.setImageResource(R.drawable.headphones_bad);  
							}
							else {
						        double f = 120 - (double) sigQuality * 0.6; 
						        image.setColorFilter(Color.HSVToColor(255, new float[]{(float) f,1.0f,1.0f}), PorterDuff.Mode.MULTIPLY);
						        image.setImageResource(R.drawable.headphones);  
								
							}
							
							
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
//			} // End if (!mPaused == true)
//			else
//			{
//		
//			}
		} // End if (data != null)
	}
	
	@Override
	public void discoveryCompleted(Vector activeNodes) {
		Log.i(TAG, "discovery completed" );	
		
		Node curr = null;
		for (Object o: activeNodes)
		{
			curr = (Node)o;
			Log.i(TAG, o.toString());
		}
			
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

	/**
	 * Binds this activity to a service using the service connection specified.
	 * 
	 * Note that it is the responsibility of the calling party (AndroidMessageServer) 
	 * to update this activities member variable, mService, when the connection to 
	 * the service is complete.
	 * 
	 * AndroidMessageServer can't do the bind by itself because it needs to be done 
	 * by an Android activity. Also, we do it here because the AndroidMessageServer
	 * doesn't know when we are destroyed. Here we know and can unbind the service.
	 * 
	 * The reason we don't simply move all of the binding here is that AndroidMessageServer
	 * needs to create it's own Messenger for the service connection.
	 *  
	 * @param mConnection	A previously established service connection
	 */
	public void doBindService(ServiceConnection mConnection ) {
		this.mConnection = mConnection; 
		Log.i(TAG, "*****************binding **************************");

		try {
			Intent intent2 = new Intent("com.t2.biofeedback.IBioFeedbackService");
			bindService(intent2, mConnection, Context.BIND_AUTO_CREATE);
			Log.i(TAG, "*****************binding SUCCESS**************************");
			
			mIsBound = true;
		} catch (Exception e) {
			Log.i(TAG, "*****************binding FAIL**************************");
			Log.e(TAG, e.toString());
		}
	}	

	/**
	 * Unbinds any service connection we may have
	 */
	void doUnbindService() {
	    if (mIsBound) {
			Log.i(TAG, "*****************UN-binding **************************");
	    	
	        // If we have received the service, and hence registered with
	        // it, then now is the time to unregister.
	        if (mService != null) {
	            try {
	                Message msg = Message.obtain(null,MSG_UNREGISTER_CLIENT);
	    			Log.i(TAG, "*****************UN- binding SUCCESS**************************");
	    			// msg.replyTo = mMessenger; We don't care about reply to because we're shutting down
	                mService.send(msg);
	            } catch (RemoteException e) {
	                // There is nothing special we need to do if the service
	                // has crashed.
	            }
	        }

	        // Detach our existing connection.
	        unbindService(mConnection);
	        mIsBound = false;
	    }
	}
	
	
	public void onButtonClick(View v)
	{
		 final int id = v.getId();
		    switch (id) {
		    case R.id.buttonBack:
				Intent i = new Intent(this, BioMapActivity.class);
				this.startActivity(i);
		    	
		    	break;
		    		    
		    case R.id.buttonAddMeasure:
		    	
		    	boolean toggleArray[] = new boolean[keyItems.size()];
				for(int j = 0; j < keyItems.size(); ++j) {
					KeyItem item = keyItems.get(j);
					if(item.visible)
						toggleArray[j] = true;
					else
						toggleArray[j] = false;
					
				}		    	
		    	
		    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
		    	alert.setTitle(R.string.alert_dialog_measure_selector);
		    	alert.setMultiChoiceItems(R.array.measure_select_dialog_items,
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
				if (mPaused == true)
				{
					mPaused = false;
					mPauseButton.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
				}
				else
				{
					mPaused = true;
					mPauseButton.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
				}
		        break;
		        
		    case R.id.buttonLogging:
		        if (mLoggingEnabled == true)
		        {
		        	mLoggingEnabled = false;
		        	mToggleLogButton.setText("Log:\nOFF");
		        	mToggleLogButton.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
		        	mLlogMarkerButton.setVisibility(View.GONE);

		        	try {
		            	if (mLogWriter != null)
		            		mLogWriter.close();
		    		} catch (IOException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}        	
		        }
		        else
		        {
		    		// Open a file for saving data
		    		try {
		    		    File root = Environment.getExternalStorageDirectory();
		    		    if (root.canWrite()){
		    		        File gpxfile = new File(root, "BioData.txt");
		    		        FileWriter gpxwriter = new FileWriter(gpxfile, true); // open for append
		    		        mLogWriter = new BufferedWriter(gpxwriter);
		    		        // Put a visual marker in
		    		        mLogWriter.write("----------------------------------------------\n");

		    		    }
		    		} catch (IOException e) {
		    		    Log.e(TAG, "Could not write file " + e.getMessage());
		    		}		
		        	mLoggingEnabled = true;
		        	mToggleLogButton.setText("Log:\nON");
		        	mToggleLogButton.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
		        	mLlogMarkerButton.setVisibility(View.VISIBLE);
		        }
		    	break;

		    case R.id.LogMarkerButton:
				Intent i1 = new Intent(this, LogNoteActivity.class);
				this.startActivityForResult(i1, ANDROID_SPINE_SERVER_ACTIVITY);
		    	break;
		    } // End switch		
	}
	
	private void TimerMethod()
	{
		//This method is called directly by the timer
		//and runs in the same thread as the timer.

		//We call the method that will work with the UI
		//through the runOnUiThread method.
		this.runOnUiThread(Timer_Tick);
	}

	//This method runs in the same thread as the UI.    	       
	private Runnable Timer_Tick = new Runnable() {
		public void run() {

			numSecsWithoutData++;
			if (numSecsWithoutData > 2) {
				return;
			}

			
			if (mPaused == true || currentMindsetData == null) {
				return;
			}
			
			if (mLoggingEnabled == true) {
			}			
			
			currentMindsetData.logData();

	        int keyCount = keyItems.size();
			for(int i = 0; i < keyItems.size(); ++i) {
				KeyItem item = keyItems.get(i);
				
				if(!item.visible) {
					continue;
				}
				
				item.series.add(mSpineChartX, currentMindsetData.getRatioFeature((int) item.id));
				if (item.series.getItemCount() > SPINE_CHART_SIZE) {
					item.series.remove(0);
				}
				
			} 			
			
	        mTextInfoView.setText(
	        		"Theta: " + currentMindsetData.getRatioFeature(bandOfInterest) + "\n" +  
	        		"Time Remaining: "
	        		);
			

	        
	        // Update the mediation bar
	        int side = currentMindsetData.powerTest(bandOfInterest);
	        final double BAR_ABS_MAXVAL = 100;
	        final double BAR_ABS_CENTERVAL = 50;
	        final double BAR_ABS_MINVAL = 0;
	        final int SIDE_RIGHT = 1;
	        final int SIDE_LEFT = -1;
	        
	        double scaledCenterValue = 100;

	        double gain = scaledCenterValue / BAR_ABS_CENTERVAL;
	        
	        double valueToPlot = currentMindsetData.getRatioFeature(bandOfInterest) * gain;
	        if (valueToPlot > BAR_ABS_CENTERVAL) {
	        	valueToPlot = BAR_ABS_CENTERVAL;
	        }
	        
	        
	        if (side == SIDE_RIGHT) {
	        	valueToPlot = BAR_ABS_MAXVAL - valueToPlot;
	        }
	        mMeditationBar.setProgress((int) valueToPlot);
	        
			
			mSpineChartX++;
			
			if (mDeviceChartView != null) {
	            mDeviceChartView.repaint();
	        }   				
			
			
			
			
		}
	};

	@Override
	protected void onPause() {
		Log.i(TAG, "MainActivity onPause");
		mDataUpdateTimer.purge();
    	mDataUpdateTimer.cancel();

    	saveState();
    	
    	
        try {
        	if (mLogWriter != null)
        		mLogWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "MainActivity onStop");
		super.onStop();
	}	

	
	@Override
	protected void onRestart() {
		Log.i(TAG, "MainActivity onRestart");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "MainActivity onResume");
		
		restoreState();
		
		
		
		super.onResume();
	}


	void saveState()
	{
		 SharedPref.putBoolean(this, "LoggingEnabled", 	mLoggingEnabled);
	}
	void restoreState()
	{
		mLoggingEnabled = SharedPref.getBoolean(this, "LoggingEnabled", false);	
		if (mLoggingEnabled)
		{
    		// Open a file for saving data
    		try {
    		    File root = Environment.getExternalStorageDirectory();
    		    if (root.canWrite()){
    		        File gpxfile = new File(root, "BioData.txt");
    		        FileWriter gpxwriter = new FileWriter(gpxfile, true); // open for append
    		        mLogWriter = new BufferedWriter(gpxwriter);
    		        // Put a visual marker in
    		        mLogWriter.write("----------------------------------------------\n");
    		        if (mLogMarkerNote != null)
    		        {
        		        mLogWriter.write(mLogMarkerNote + "\n");
        		        mLogMarkerNote = null;
    		        }

    		    }
    		} catch (IOException e) {
    		    Log.e(TAG, "Could not write file " + e.getMessage());
    		}
		}
		
	}

	static class KeyItem {
		public long id;
		public String title1;
		public String title2;
		public int color;
		public boolean visible;
		public boolean reverseData = false; 
		public XYSeries series;		
		
		public KeyItem(long id, String title1, String title2) {
			this.id = id;
			this.title1 = title1;
			this.title2 = title2;
			series = new XYSeries(title1);		
			this.visible = true;
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
	
	class KeyItemAdapter extends ArrayAdapter<KeyItem> {
		public static final int VIEW_TYPE_ONE_LINE = 1;
		public static final int VIEW_TYPE_TWO_LINE = 2;
		
		private LayoutInflater layoutInflater;
		private int layoutId;

		public KeyItemAdapter(Context context, int viewType,
				List<KeyItem> objects) {
			super(context, viewType, objects);
			
			layoutInflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
			if(viewType == VIEW_TYPE_TWO_LINE) {
				layoutId = R.layout.list_item_result_key_2;
			} else {
				layoutId = R.layout.list_item_result_key_1;
			}
		}
		
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			if(convertView == null) {
//				convertView = layoutInflater.inflate(layoutId, null);
//			}
//
//			final KeyItem item = this.getItem(position);
//			TextView tv1 = (TextView)convertView.findViewById(R.id.text1);
//			TextView tv2 = (TextView)convertView.findViewById(R.id.text2);
//			ToggleButton tb = (ToggleButton)convertView.findViewById(R.id.showKeyToggleButton);
//			View keyBox = convertView.findViewById(R.id.keyBox);
//			
//			boolean tv1Null = tv1 == null;
//			boolean tv2Null = tv2 == null;
//			if(reverseLabels && !tv1Null && !tv2Null) {
//				if(!tv1Null) {
//					tv1.setText(item.title2);
//				}
//				if(!tv2Null) {
//					tv2.setText(item.title1);
//				}
//			} else {
//				if(!tv1Null) {
//					tv1.setText(item.title1);
//				}
//				if(!tv2Null) {
//					tv2.setText(item.title2);
//				}				
//			}
//			
//			if(tb != null) {
//				if(isKeyItemsClickable()) {
//					tb.setFocusable(false);
//				}
//				tb.setOnCheckedChangeListener(null);
//				tb.setChecked(item.visible);
//				tb.setOnCheckedChangeListener(new OnCheckedChangeListener(){
//					@Override
//					public void onCheckedChanged(
//							CompoundButton buttonView, boolean isChecked) {
//						item.visible = isChecked;
//						onKeyToggleButtonCheckedChanged();
//					}
//				});
//			}
//			
//			if(keyBox != null) {
//				keyBox.setBackgroundColor(item.color);
//			}
//			
//			return convertView;
//		}
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
		
	
	
}


