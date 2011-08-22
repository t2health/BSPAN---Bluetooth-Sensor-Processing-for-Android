package com.t2.biomap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.t2.AndroidSpineConnector;
import com.t2.SpineReceiver;
import com.t2.SpineReceiver.BioFeedbackStatus;
import com.t2.SpineReceiver.OnBioFeedbackMessageRecievedListener;
import com.t2.Util;
import com.t2.biomap.BioLocation;
import com.t2.biomap.BioMapActivity;
import com.t2.biomap.LogNoteActivity;
import com.t2.biomap.SharedPref;
import com.t2.compassionMeditation.GraphsActivity;
import com.t2.Constants;

import spine.datamodel.Node;
import spine.SPINEFactory;
import spine.SPINEFunctionConstants;
import spine.SPINEListener;
import spine.SPINEManager;
import spine.datamodel.Address;
import spine.datamodel.Data;
import spine.datamodel.Feature;
import spine.datamodel.FeatureData;
import spine.datamodel.MindsetData;
import spine.datamodel.ServiceMessage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
public class BioDetailActivity extends Activity implements OnBioFeedbackMessageRecievedListener, SPINEListener {
	private static final String TAG = Constants.TAG;

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
	private static BioDetailActivity instance;

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
	
	String mTargetName = "";
	String mLastMeditation = "";
	String mLastAttention = "";
	String mLastSignalStrength = "";
	
	Vector<BioLocation> currentUsers;
	
	private static Timer mDataUpdateTimer;	
	
	
	// Charting stuff
	private final static int SPINE_CHART_SIZE = 20;
	
	private GraphicalView mDeviceChartView;
	private XYMultipleSeriesDataset mDeviceDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mDeviceRenderer = new XYMultipleSeriesRenderer();
	private XYSeries mMindsetAttentionSeries;
	private XYSeries mMindsetMeditationSeries;
	private XYSeries mCurrentSpineSeries;
	private XYSeries mZephyrHeartRateSeries;
	private XYSeries mZephyrRespRateSeries;
	private XYSeries mZephyrSkinTempSeries;
	  
    static final int MSG_UNREGISTER_CLIENT = 2;	
	
	private EditText mDetailLog;
	
	int mSpineChartX = 0;
	int mAttentionChartX = 0;
	int mMeditationChartX = 0;
	
	String mPackageName = "";
	int mVersionCode;
	String mVersionName = "";
	Vector<String>  mDeviceDetailContent = new Vector<String>();
	
	
	BufferedWriter mLogWriter = null;
	boolean mLoggingEnabled = false;
	boolean mPaused = false;
	
	public static final int ANDROID_SPINE_SERVER_ACTIVITY = 0;
	public static final String ANDROID_SPINE_SERVER_ACTIVITY_RESULT = "AndroidSpineServerActivityResult";
	
    private Button mPauseButton;
    private Button mToggleLogButton;
    private Button mLlogMarkerButton;
    
    private String mLogMarkerNote = null;
	
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
	public static BioDetailActivity getInstance() 
	{
	   return instance;
	}
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        instance = this;
    
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);        
        
        // If we were called from the Biomap activity then it will have
        // sent us a target to focus on
        try {
			// Get target name if one was supplied
			Bundle bundle = getIntent().getExtras();
			mTargetName = bundle.getString("TARGET_NAME");
		} catch (Exception e1) {
			mTargetName = "";
		}
        
        if (mTargetName == null)
        	mTargetName = "";
        
        ImageView image = (ImageView) findViewById(R.id.targetImage);
        
        if (mTargetName.equalsIgnoreCase("Scott"))
            image.setImageResource(R.drawable.scott);        
        if (mTargetName.equalsIgnoreCase("dave"))
            image.setImageResource(R.drawable.dave);        
        if (mTargetName.equalsIgnoreCase("bob"))
            image.setImageResource(R.drawable.bob);        
        
//        AndroidSpineConnector.setMainActivityInstance(instance);
        
        Resources resources = this.getResources();
        AssetManager assetManager = resources.getAssets();
        
        // Set up member variables to UI Elements
        mDetailLog = (EditText) findViewById(R.id.detailLog);
        mPauseButton = (Button) findViewById(R.id.buttonPause);
        mToggleLogButton = (Button) findViewById(R.id.buttonLogging);
        mLlogMarkerButton = (Button) findViewById(R.id.LogMarkerButton);

		// Initialize SPINE by passing the fileName with the configuration properties
		try {
			mManager = SPINEFactory.createSPINEManager("SPINETestApp.properties", resources);
		} catch (InstantiationException e) {
			Log.e(TAG, "Exception creating SPINE manager: " + e.toString());
			e.printStackTrace();
		}        
		
		// Since zepher is a static node we have to manually put it in the active node list
		// Note that the sensor id 0xfff1 (-15) is a reserved id for this particular sensor
		Node zepherNode = null;
		zepherNode = new Node(new Address("" + Constants.RESERVED_ADDRESS_ZEPHYR));
		mManager.getActiveNodes().add(zepherNode);
		
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
        
        // Set up Device data chart
        if (mDeviceChartView == null) 
        {
          LinearLayout layout = (LinearLayout) findViewById(R.id.deviceChart);
          mDeviceChartView = ChartFactory.getLineChartView(this, mDeviceDataset, mDeviceRenderer);
          layout.addView(mDeviceChartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        }    
        mDeviceRenderer.setShowLabels(false);
        mDeviceRenderer.setMargins(new int[] {0,0,0,0});
        mDeviceRenderer.setShowAxes(true);
        mDeviceRenderer.setShowLegend(true);
        
        mDeviceRenderer.setZoomEnabled(false, false);
        mDeviceRenderer.setPanEnabled(false, false);
        mDeviceRenderer.setYAxisMin(0);
        mDeviceRenderer.setYAxisMax(255);
        
        mMindsetAttentionSeries = new XYSeries("Attention");
        mMindsetMeditationSeries = new XYSeries("Meditation");
        mCurrentSpineSeries = new XYSeries("Test Data");
        mZephyrHeartRateSeries = new XYSeries("HeartRate");
        mZephyrRespRateSeries = new XYSeries("RespRate");
        mZephyrSkinTempSeries = new XYSeries("SkinTemp");
        
        
        mDeviceDataset.addSeries(mMindsetAttentionSeries);
        mDeviceDataset.addSeries(mMindsetMeditationSeries);
        mDeviceDataset.addSeries(mCurrentSpineSeries);

        mDeviceDataset.addSeries(mZephyrHeartRateSeries);
        mDeviceDataset.addSeries(mZephyrRespRateSeries);
        mDeviceDataset.addSeries(mZephyrSkinTempSeries);

        XYSeriesRenderer renderer = new XYSeriesRenderer();
        
        renderer = new XYSeriesRenderer();
        renderer.setColor(Color.GREEN); // White
        mDeviceRenderer.addSeriesRenderer(renderer);
        
        renderer = new XYSeriesRenderer();
        renderer.setColor(Color.YELLOW); // White
        mDeviceRenderer.addSeriesRenderer(renderer);
        
        renderer = new XYSeriesRenderer();
        renderer.setColor(Color.WHITE); // White
        mDeviceRenderer.addSeriesRenderer(renderer);
        
        renderer = new XYSeriesRenderer();
        renderer.setColor(Color.BLUE); 
        mDeviceRenderer.addSeriesRenderer(renderer);
        
        renderer = new XYSeriesRenderer();
        renderer.setColor(Color.MAGENTA); 
        mDeviceRenderer.addSeriesRenderer(renderer);
        
        renderer = new XYSeriesRenderer();
        renderer.setColor(Color.CYAN); 
        mDeviceRenderer.addSeriesRenderer(renderer);
        
		try {
			PackageManager packageManager = this.getPackageManager();
			PackageInfo info = packageManager.getPackageInfo(this.getPackageName(), 0);			
			mPackageName = info.packageName;
			mVersionCode = info.versionCode;
			mVersionName = info.versionName;
			Log.i(TAG, "Spine server Test Application Version " + mVersionName);
		} 
		catch (NameNotFoundException e) {
			   	Log.e(TAG, e.toString());
		}
		
		// Load users from the database
		currentUsers = Util.setupUsers();
		
		mManager.discoveryWsn();
    } // End onCreate(Bundle savedInstanceState)
    
    @Override
	protected void onDestroy() {
    	super.onDestroy();
    	
    	mLoggingEnabled = false;
    	saveState();
    	
//    	this.sendBroadcast(new Intent("com.t2.biofeedback.service.STOP"));
    	this.unregisterReceiver(this.mCommandReceiver);
		Log.i(TAG, "BioDetailActivity onDestroy");
	    	
  //  	doUnbindService();    	
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "OnStart");
		
		
		// Tell the AndroidBTService to start up
//		this.sendBroadcast(new Intent("com.t2.biofeedback.service.START"));
		
		// Set up filter intents so we can receive broadcasts
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.t2.biofeedback.service.status.BROADCAST");

		// These are only used when the AndroidBTService sends sensor data directly to this 
		// activity.
		// Currently all sensor data comesin throuugh the service connection that is set up
		// to the AndroidBTService.
		
		//filter.addAction("com.t2.biofeedback.service.spinedata.BROADCAST");
		//filter.addAction("com.t2.biofeedback.service.data.BROADCAST");
		//filter.addAction("com.t2.biofeedback.service.zephyrdata.BROADCAST");
		
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
		
		if (data != null)
		{
			if (!mPaused == true)
			{
				switch (data.getFunctionCode()) {
				case SPINEFunctionConstants.FEATURE: {
					Node source = data.getNode();
					Feature[] feats = ((FeatureData)data).getFeatures();
					Feature firsFeat = feats[0];
					byte sensor = firsFeat.getSensorCode();
					byte featCode = firsFeat.getFeatureCode();
					int ch1Value = firsFeat.getCh1Value();
					
					// Look up the id of this view and update the owners data
					// that corresponds to this address
					for (BioLocation user: currentUsers)
					{
						if (user.mAddress == source.getPhysicalID().getAsInt())
						{
							user.mHeartRate = ch1Value;
						}
					}				
			        Log.i("SensorData","ch1Value= " + ch1Value);
			    	
					break;
				} // End case SPINEFunctionConstants.FEATURE:
				
				case SPINEFunctionConstants.ZEPHYR: {
					Node source = data.getNode();
					Feature[] feats = ((FeatureData)data).getFeatures();
					Feature firsFeat = feats[0];
					
					byte sensor = firsFeat.getSensorCode();
					byte featCode = firsFeat.getFeatureCode();
					int batLevel = firsFeat.getCh1Value();
					int heartRate = firsFeat.getCh2Value();
					double respRate = firsFeat.getCh3Value() / 10;
					int skinTemp = firsFeat.getCh4Value() / 10;
					double skinTempF = (skinTemp * 9 / 5) + 32;				
					Log.i("SensorData","heartRate= " + heartRate + ", respRate= " + respRate + ", skinTemp= " + skinTempF);
					
					// Look up the id of this view and update the owners data
					// that corresponds to this address
					for (BioLocation user: currentUsers)
					{
						if (user.mAddress == source.getPhysicalID().getAsInt())
						{
							user.mZBattLevel = batLevel;
							user.mZRespRate = (int)respRate;
							user.mZHeartRate = heartRate;
							user.mZSkinTemp = skinTemp;
						}
					}				
					
					break;
				} // End case SPINEFunctionConstants.ZEPHYR:
				
				case SPINEFunctionConstants.MINDSET: {
					Node source = data.getNode();
					
					MindsetData mData = (MindsetData) data;
					if (mData.exeCode == Constants.EXECODE_POOR_SIG_QUALITY)
					{
//						Log.i(TAG, "poorSignalStrength= "  + mData.poorSignalStrength);
	//					int b = mData.poorSignalStrength &  0xff;
	//					String result = Integer.toHexString(b);					
						// Look up the id of this view and update the owners data
						// that corresponds to this address
						for (BioLocation user: currentUsers)
						{
							if (user.mAddress == source.getPhysicalID().getAsInt())
							{
								user.mSignalStrength = mData.poorSignalStrength;
							}
						}					
					}
					if (mData.exeCode == Constants.EXECODE_ATTENTION)
					{
						//Log.i("SensorData", "attention= "  + mData.attention);
	
						// Look up the id of this view and update the owners data
						// that corresponds to this address
						for (BioLocation user: currentUsers)
						{
							if (user.mAddress == source.getPhysicalID().getAsInt())
							{
								user.mAttention = mData.attention;
							}
						}					
					}
					if (mData.exeCode == Constants.EXECODE_SPECTRAL)
					{
//						Log.i("SensorData", "Spectral = "  + mData.delta);
					}
					if (mData.exeCode == Constants.EXECODE_MEDITATION)
					{
						//Log.i("SensorData", "meditation= "  + mData.meditation);
						
						// Look up the id of this view and update the owners data
						// that corresponds to this address
						for (BioLocation user: currentUsers)
						{
							if (user.mAddress == source.getPhysicalID().getAsInt())
							{
								user.mMeditation = mData.meditation;
							}
						}					
					}
					break;
					} // End case SPINEFunctionConstants.MINDSET:
				
					case SPINEFunctionConstants.ONE_SHOT:
						Log.i(TAG, "SPINEFunctionConstants.ONE_SHOT"  );
						break;
						
					case SPINEFunctionConstants.ALARM:
						Log.i(TAG, "SPINEFunctionConstants.ALARM"  );
						break;
				} // End switch (data.getFunctionCode())
				
				// Now display the current user's data
				String statusLine = "";
				for (BioLocation user: currentUsers)
				{
					if (user.mName.equalsIgnoreCase(mTargetName))
					{
						statusLine = user.buildStatusText();
							mDetailLog.setText(statusLine);					
					}
				}			
			} // End if (!mPaused == true)
			else
			{
				// We're paused, simply display the last data
				String statusLine = "** PAUSED** ";
				for (BioLocation user: currentUsers)
				{
					if (user.mName.equalsIgnoreCase(mTargetName))
					{
						statusLine += user.buildStatusText();
							mDetailLog.setText(statusLine);					
					}
				}			
			}
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


	
	void updateDetailLog(String text, int dataType)
	{
		if (mTargetName.equalsIgnoreCase("scott"))
		{
			if (dataType == Constants.DATA_TYPE_HEARTRATE)
				mDetailLog.setText("Heart Rate: " + text);
				
		}
		else
		{
			if (dataType == Constants.DATA_TYPE_MEDITATION)
				mLastMeditation = text;
			if (dataType == Constants.DATA_TYPE_ATTENTION)
				mLastAttention = text;
			if (dataType == Constants.DATA_SIGNAL_STRENGTH)
				mLastSignalStrength = text;
			
						
			
			mDetailLog.setText(	"Signal Strength: " + mLastSignalStrength + "\n" + 
								"Meditation: " + mLastMeditation + "\n" + 
								"Attention: " + mLastAttention);
			
		}
	}
	
	public void onButtonClick(View v)
	{
		 final int id = v.getId();
		    switch (id) {
		    case R.id.button1:
				Intent i = new Intent(this, BioMapActivity.class);
				this.startActivity(i);
		    	
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

			if (mPaused == true)
				return;
			String logData = "" + mTargetName + ", "; 
			
			//String statusLine = "";
			for (BioLocation user: currentUsers)
			{
				if (user.mName.equalsIgnoreCase(mTargetName))
				{
			    	
			    	
					for (int i = 0; i < user.mSensors.length; i++)
					{
						switch (user.mSensors[i])
						{
						case Constants.DATA_TYPE_ATTENTION:
							mMindsetAttentionSeries.add(mSpineChartX, user.mAttention);
							logData += "ATTN, " + user.mAttention + ",";
							break;
						case Constants.DATA_TYPE_MEDITATION:
							mMindsetMeditationSeries.add(mSpineChartX, user.mMeditation);
							logData += "MED, " + user.mMeditation + ",";
							break;
						case Constants.DATA_TYPE_HEARTRATE:
							mCurrentSpineSeries.add(mSpineChartX, user.mHeartRate);
							logData += "HR, " + user.mHeartRate + ",";
							break;
							
						case Constants.DATA_ZEPHYR_HEARTRATE:
							mZephyrHeartRateSeries.add(mSpineChartX, user.mZHeartRate);
							logData += "ZHR, " + user.mHeartRate + ",";
							break;
							
						case Constants.DATA_ZEPHYR_RESPRATE:
							mZephyrRespRateSeries.add(mSpineChartX, user.mZRespRate);
							logData += "ZRR, " + user.mHeartRate + ",";
							break;
							
						case Constants.DATA_ZEPHYR_SKINTEMP:
							mZephyrSkinTempSeries.add(mSpineChartX, user.mZSkinTemp);
							logData += "ZST, " + user.mHeartRate + ",";
							break;
							
							
							
							
						}
					}			    	
			    	
					mSpineChartX++;
			    	if (mCurrentSpineSeries.getItemCount() > SPINE_CHART_SIZE)
						mCurrentSpineSeries.remove(0);
			    	if (mMindsetAttentionSeries.getItemCount() > SPINE_CHART_SIZE)
			    		mMindsetAttentionSeries.remove(0);
			    	if (mMindsetMeditationSeries.getItemCount() > SPINE_CHART_SIZE)
			    		mMindsetMeditationSeries.remove(0);
			    	

					if (mDeviceChartView != null) {
			            mDeviceChartView.repaint();
			        }   			    	
				}
			}			
			
			if (mLoggingEnabled == true)
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
				
				String currentDateTimeString = DateFormat.getDateInstance().format(new Date());				
				currentDateTimeString = sdf.format(new Date());
				
				logData = currentDateTimeString + ", " + logData + "\n";
				
		        try {
		        	if (mLogWriter != null)
		        		mLogWriter.write(logData);
				} catch (IOException e) {
					Log.e(TAG, e.toString());
				}
			}			
		}
	};

	@Override
	protected void onPause() {
		Log.i(TAG, "BioDetailActivity onPause");
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
		Log.i(TAG, "BioDetailActivity onStop");
		super.onStop();
	}	

	
	@Override
	protected void onRestart() {
		Log.i(TAG, "BioDetailActivity onRestart");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "BioDetailActivity onResume");
		
		restoreState();
		
		
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode) { 
	    case (ANDROID_SPINE_SERVER_ACTIVITY) :  
	      if (resultCode == RESULT_OK) {
	    	  

	    	  // We can't write the note yet because we may not have been re-initialized
	    	  // since the not dialog put us into pause.
	    	  // We'll save the note and write it at restore
	    	  mLogMarkerNote = data.getStringExtra(ANDROID_SPINE_SERVER_ACTIVITY_RESULT);
	    	  
	      } 
	      break; 
	    } 
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

}


