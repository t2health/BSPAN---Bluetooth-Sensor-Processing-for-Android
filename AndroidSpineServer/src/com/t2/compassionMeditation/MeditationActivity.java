package com.t2.compassionMeditation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.achartengine.model.XYSeries;




import com.t2.AndroidSpineConnector;
import com.t2.SpineReceiver;
import com.t2.SpineReceiver.BioFeedbackStatus;
import com.t2.SpineReceiver.OnBioFeedbackMessageRecievedListener;
import com.t2.biomap.BioLocation;
import com.t2.biomap.BioMapActivity;
import com.t2.biomap.LogNoteActivity;
import com.t2.biomap.SharedPref;
import com.t2.compassionMeditation.CompassionActivity.KeyItem;



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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
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
public class MeditationActivity extends Activity 
		implements 	OnBioFeedbackMessageRecievedListener, SPINEListener, 
					View.OnTouchListener, SeekBar.OnSeekBarChangeListener {
	private static final String TAG = "MeditationActivity";

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
	 * Service connection used to communicate data messages with the AndroidBTService
	 */
	ServiceConnection mConnection;
	
	/**
	 * This is the MEssenger service which is used to communicate sensor data messages between the main activity 
	 * and the AndroidBTService
	 */
	private Messenger mService = null;	
	
	/**
	 * Static instance of this activity
	 */
	private static MeditationActivity instance;
	
	
    private boolean mShowingControls = false; 
	
	
	
	private static Timer mDataUpdateTimer;	
	
	
    static final int MSG_UNREGISTER_CLIENT = 2;	
	
	
	
	String mPackageName = "";
	int mVersionCode;
	String mVersionName = "";
	Vector<String>  mDeviceDetailContent = new Vector<String>();
	
	
	BufferedWriter mLogWriter = null;
	boolean mLoggingEnabled = false;
	boolean mPaused = false;
	
	public static final int ANDROID_SPINE_SERVER_ACTIVITY = 0;
	public static final String ANDROID_SPINE_SERVER_ACTIVITY_RESULT = "AndroidSpineServerActivityResult";
	
    private Button mToggleLogButton;
    private Button mLlogMarkerButton;
    private Button mPauseButton;
    private Button mBackButton;
    private TextView mTextInfoView;
    private ImageView mBuddahImage; 
    private SeekBar mSeekBar;
    
    
    private MovingAverage mMovingAverage;
    private double mGain = 1;
    
    
    private String mLogMarkerNote = null;
    

	protected SharedPreferences sharedPref;
	private static final String KEY_NAME = "results_visible_ids_";	
	private ArrayList<KeyItem> keyItems = new ArrayList<KeyItem>();
	MindsetData currentMindsetData = new MindsetData();
	
	
	private int bandOfInterest = MindsetData.THETA_ID; // Default to theta
	private int numSecsWithoutData = 0;
	private ArrayList<String> mCurrentUsers;
	private String mSelectedUser = null;
	private int mSelection = 0;
	
	/**
	 * Sets up messenger service which is used to communicate to the AndroidBTService
	 * @param mService
	 */
	public void setmService(Messenger mService) {
		this.mService = mService;
	}

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.meditation);
        
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());   
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);        
        
        mMovingAverage = new MovingAverage(30);
        
        View v1 = findViewById (R.id.buddahView); 
        v1.setOnTouchListener (this);        
        
        Resources resources = this.getResources();
        AssetManager assetManager = resources.getAssets();
        
        // Set up member variables to UI Elements
        mToggleLogButton = (Button) findViewById(R.id.buttonLogging);
        mLlogMarkerButton = (Button) findViewById(R.id.LogMarkerButton);
        mTextInfoView = (TextView) findViewById(R.id.textViewInfo);
        mPauseButton = (Button) findViewById(R.id.buttonPause);
        mBackButton = (Button) findViewById(R.id.buttonBack);
		mSeekBar = (SeekBar)findViewById(R.id.seekBar1);

		mSeekBar.setOnSeekBarChangeListener(this);
		
		
        // Controls start as invisible, need to touch screen to activate them
        mTextInfoView.setVisibility(View.GONE);
		mPauseButton.setVisibility(View.GONE);
		mBackButton.setVisibility(View.GONE);
		mSeekBar.setVisibility(View.GONE);
		
        
        
        ImageView image = (ImageView) findViewById(R.id.imageView1);
        image.setImageResource(R.drawable.signal_bars0);  
        
        mBuddahImage = (ImageView) findViewById(R.id.buddahView);
        mBuddahImage.setImageResource(R.drawable.buddha);
        
        
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
    	try {
        	if (mLogWriter != null)
        		mLogWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        	
    	
    	saveState();
    	
    	this.unregisterReceiver(this.mCommandReceiver);
		Log.i(TAG, "TAG +  onDestroy");
	    	
	}

    
	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "TAG +  OnStart");
		
		mCurrentUsers = getUsers();		
		
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	alert.setTitle(R.string.select_users_text);
    	alert.setSingleChoiceItems(ArraysExtra.toStringArray(mCurrentUsers.toArray()),0,
    			new DialogInterface.OnClickListener() {

	    			public void onClick(DialogInterface dialog, int whichButton) {
	
	    				mSelection = whichButton;
	        			
	                }
                });
    	alert.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
    			mSelectedUser = mCurrentUsers.get(mSelection);
    			Toast.makeText(instance, "Selected User: " + mSelectedUser, Toast.LENGTH_LONG).show();
    			openLogFile();

            }
        });
    	alert.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
    	// Add new user
    	alert.setNeutralButton(R.string.new_user_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

        		getNewUserName("Enter new user name");
            }
        });

		alert.show();		
		
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
							
							int value = mindsetData.getRatioFeature(bandOfInterest);
							mMovingAverage.pushValue(value);	
							int filteredValue = (int) (mMovingAverage.getValue() * mGain);
							mBuddahImage.setAlpha((int) filteredValue);
							mTextInfoView.setText("Raw= " + value + ", Filtered= " + filteredValue);							
							
							
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


	
	public void onButtonClick(View v)
	{
		 final int id = v.getId();
		    switch (id) {
		    case R.id.buttonBack:
				Intent i = new Intent(this, CompassionActivity.class);
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
			if (mLoggingEnabled == true)
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
				
				String currentDateTimeString = DateFormat.getDateInstance().format(new Date());				
				currentDateTimeString = sdf.format(new Date());
				
				String logData = currentDateTimeString + ", " + currentMindsetData.getLogDataLine() + "\n";
				
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
		Log.i(TAG, "TAG +  onPause");
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
		Log.i(TAG, "TAG +  onStop");
		super.onStop();
	}	

	
	@Override
	protected void onRestart() {
		Log.i(TAG, "TAG +  onRestart");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "TAG +  onResume");
		
		restoreState();
		
		
		
		super.onResume();
	}


	void saveState()
	{
		 SharedPref.putBoolean(this, "LoggingEnabled", 	mLoggingEnabled);
	}
	void restoreState()
	{
		openLogFile();
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
	
	
	
	private ArrayList<String> getUsers() {
		String[] usersStrArr = SharedPref.getValues(
				sharedPref, 
				"CurrentUsers", 
				",",
				new String[0]
//				new String[] {""}
		);
		
		return new ArrayList<String>(
				Arrays.asList(
						ArraysExtra.toStringArray(usersStrArr)
				)
		);		
	}	

	private void setUsers(ArrayList<String> users) {
		SharedPref.setValues(
				sharedPref, 
				"CurrentUsers", 
				",", 
				ArraysExtra.toStringArray(users.toArray(new String[users.size()]))
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
	public boolean onTouch(View arg0, MotionEvent arg1) {
		if (mShowingControls) {
			mShowingControls = false;
			mTextInfoView.setVisibility(View.GONE);
			mPauseButton.setVisibility(View.GONE);
			mBackButton.setVisibility(View.GONE);
			mSeekBar.setVisibility(View.GONE);
			
		}
		else {
			mShowingControls = true;
			mTextInfoView.setVisibility(View.VISIBLE);
			mPauseButton.setVisibility(View.VISIBLE);
			mBackButton.setVisibility(View.VISIBLE);
			mSeekBar.setVisibility(View.VISIBLE);
			
		}
		
		
		return false;
	}


	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		double  alpha = arg1 * 2.5;
		mBuddahImage.setAlpha((int) alpha);
		mTextInfoView.setText(Integer.toString((int)alpha));
		
	}


	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}
		
	public void getNewUserName(String message) {
		AlertDialog.Builder alert1 = new AlertDialog.Builder(this);

		alert1.setMessage(message);

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert1.setView(input);

		alert1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			mSelectedUser = input.getText().toString();

			
      		mCurrentUsers.add(mSelectedUser);
      		setUsers(mCurrentUsers);

		  
		  }
		});

		alert1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
			  mSelectedUser = null;
		  }
		});

		alert1.show();
	}

	void closeLogFile() {
	}
	
	void openLogFile() {
		mLoggingEnabled = true;	
//		mLoggingEnabled = SharedPref.getBoolean(this, "LoggingEnabled", false);	
		
		
		if (mLoggingEnabled && this.mSelectedUser != null)
		{
			// Create a log file name from the seledcted user and date/time
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
			String currentDateTimeString = sdf.format(new Date());
			
			String logFileName = mSelectedUser + "_" + currentDateTimeString + ".log";

			// Open a file for saving data
    		try {
    		    File root = Environment.getExternalStorageDirectory();
    		    if (root.canWrite()){
    		        File gpxfile = new File(root, logFileName);
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


