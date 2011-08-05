package com.t2.compassionMeditation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;


import com.t2.SpineReceiver;
import com.t2.SpineReceiver.BioFeedbackStatus;
import com.t2.SpineReceiver.OnBioFeedbackMessageRecievedListener;
import com.t2.biomap.BioMapActivity;
import com.t2.biomap.LogNoteActivity;
import com.t2.biomap.SharedPref;

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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


//Need the following import to get access to the app resources, since this
//class is in a sub-package.
import com.t2.R;


public class MeditationActivity extends Activity 
		implements 	OnBioFeedbackMessageRecievedListener, SPINEListener, 
					View.OnTouchListener, SeekBar.OnSeekBarChangeListener {
	private static final String TAG = "MeditationActivity";
	private static final String mActivityVersion = "1.0";

	/**
	 * Number of seconds remaining in the session
	 *   This is set initially from SharedPref.PREF_SESSION_LENGTH
	 */
	private int mSecondsRemaining = 0;
	
	/**
	 * Application version info determined by the package manager
	 */
	private String mApplicationVersion = "";

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
	private static MeditationActivity instance;
	
    /**
     * Toggled by screen press, indicates whether or not to show buttons/tools on screen
     */
    private boolean mShowingControls = false; 
	
	/**
	 * Timer for updating the UI
	 */
	private static Timer mDataUpdateTimer;	
	
	
	private BufferedWriter mLogWriter = null;
	private boolean mLoggingEnabled = false;
	private boolean mPaused = false;
	
	public static final int ANDROID_SPINE_SERVER_ACTIVITY = 0;
	public static final String ANDROID_SPINE_SERVER_ACTIVITY_RESULT = "AndroidSpineServerActivityResult";
	
	// UI Elements
	private Button mToggleLogButton;
    private Button mLlogMarkerButton;
    private Button mPauseButton;
    private Button mBackButton;
    private TextView mTextInfoView;
    private TextView mCountdownTextView;
    private ImageView mBuddahImage; 
    private SeekBar mSeekBar;
    
    /**
     * Moving average used to smooth the display of the band of interest
     */
    private MovingAverage mMovingAverage;
    
    private int mMovingAverageSize = 30;

    /**
     * Gain used to determine how band of interest affects the buddah image 
     */
    private double mAlphaGain = 1;
    
	protected SharedPreferences sharedPref;
	MindsetData currentMindsetData = new MindsetData();
	
	
	private int bandOfInterest = MindsetData.THETA_ID; // Default to theta
	private int numSecsWithoutData = 0;
	/**
	 * Non-volatile list of users that the system knows about
	 *   Note that this is a list of names only which
	 *   is used to determine file names.
	 *   Deleting a user from this list DOES NOT delete the
	 *   data associated with the name/user
	 */
	private ArrayList<String> mCurrentUsers;

	/**
	 * This is the user that the operator selected when the activity first started
	 *  All logging for this session will be done for this user/name
	 */
	private String mSelectedUser = null;

	
	/**
	 * Temp variable used in SelectUser() to indicate which user was selected
	 *  Note that this needed to be a member variable because of error: 
	 *  	"Cannot refer to a non-final variable mSelection inside an inner 
	 *      class defined in a different method" 
	 */
	private int mSelection = 0;

	/**
	 * Session name which is used for file creation (based on selected user) 
	 */
	private String mSessionName = "";
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        
        // We don't want the screen to timeout in this activity
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);		// This needs to happen BEFORE setContentView
        setContentView(R.layout.meditation);
        
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());   
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);       
        
        mSecondsRemaining = SharedPref.getInt(this, com.t2.compassionMeditation.Constants.PREF_SESSION_LENGTH, 	10);        
        
        mMovingAverage = new MovingAverage(mMovingAverageSize);
        
        View v1 = findViewById (R.id.buddahView); 
        v1.setOnTouchListener (this);        
        
        Resources resources = this.getResources();
        AssetManager assetManager = resources.getAssets();
        
        // Set up member variables to UI Elements
        mToggleLogButton = (Button) findViewById(R.id.buttonLogging);
        mLlogMarkerButton = (Button) findViewById(R.id.LogMarkerButton);
        mTextInfoView = (TextView) findViewById(R.id.textViewInfo);
        mCountdownTextView = (TextView) findViewById(R.id.countdownTextView);
        mPauseButton = (Button) findViewById(R.id.buttonPause);
        mBackButton = (Button) findViewById(R.id.buttonBack);

        // Note that the seek bar is a debug thing - used only to set the
        // alpha of the buddah image manually for visual testing
        mSeekBar = (SeekBar)findViewById(R.id.seekBar1);
		mSeekBar.setOnSeekBarChangeListener(this);
		
        // Controls start as invisible, need to touch screen to activate them
		mCountdownTextView.setVisibility(View.GONE);
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
        
		try {
			PackageManager packageManager = this.getPackageManager();
			PackageInfo info = packageManager.getPackageInfo(this.getPackageName(), 0);			
			mApplicationVersion = info.versionName;
			Log.i(TAG, "Compassion Meditation Application Version: " + mApplicationVersion + ", Activity Version: " + mActivityVersion);
		} 
		catch (NameNotFoundException e) {
			   	Log.e(TAG, e.toString());
		}
		
		mManager.discoveryWsn();
		
		mAlphaGain = SharedPref.getFloat(this, 
				com.t2.compassionMeditation.Constants.PREF_ALPHA_GAIN, 	
				com.t2.compassionMeditation.Constants.PREF_ALPHA_GAIN_DEFAULT);
		
    } // End onCreate(Bundle savedInstanceState)
    
    @Override
	protected void onDestroy() {
    	super.onDestroy();
    	
    	mLoggingEnabled = false;
    	try {
        	if (mLogWriter != null)
        		mLogWriter.close();
		} catch (IOException e) {
			Log.e(TAG, "Exeption closing file " + e.toString());
			e.printStackTrace();
		}        	
    	
    	saveState();
    	
    	this.unregisterReceiver(this.mCommandReceiver);
		Log.i(TAG, TAG +  " onDestroy");
	}
    
	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, TAG +  " OnStart");
		
		SelectUser();
		
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
    
	/**
	 * Convert seconds to string display of hours:minutes:seconds 
	 * @param time Total number of seconds to display
	 * @return String formated to hours:minutes:seconds
	 */
	String secsToHMS(long time) {
		
		long secs = time;
		long hours = secs / 3600;
		secs = secs % 3600;
		long mins = secs / 60;
		secs = secs % 60;
		
		return "Time remaining: " + hours + ":" + mins + ":" + secs;
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.menu_compassion_meditation, menu);
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
			switch (data.getFunctionCode()) {

			case SPINEFunctionConstants.MINDSET: {
					Node source = data.getNode();
				
					MindsetData mindsetData = (MindsetData) data;
					//Log.i("BFDemo", "" + mindsetData.exeCode);
					if (mindsetData.exeCode == Constants.EXECODE_RAW_ACCUM) {
						//Log.i("BFDemo", "" + mindsetData.rawSignal);
					}
					
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
					
					if (mindsetData.exeCode == Constants.EXECODE_SPECTRAL && mPaused == false) {
						currentMindsetData.updateSpectral(mindsetData);
						Log.i(TAG, "Spectral Data");
						numSecsWithoutData = 0;				
						
						int value = mindsetData.getRatioFeature(bandOfInterest);
						mMovingAverage.pushValue(value);	
						int filteredValue = (int) (mMovingAverage.getValue() * mAlphaGain);
						mBuddahImage.setAlpha((int) filteredValue);
						mTextInfoView.setText("Theta: " + value + ", " + filteredValue);							
						
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
		    	finish();
		    	break;
		    		    
		    case R.id.buttonPause:
		    	handlePause(mSessionName + " Paused");
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
		    			Log.e(TAG, "Exeption closing file " + e.toString());
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
	
	/**
	 * This method is called directly by the timer and runs in the same thread as the timer
	 * From here We call the method that will work with the UI through the runOnUiThread method.
	 */
	private void TimerMethod()
	{
		this.runOnUiThread(Timer_Tick);
	}

	/**
	 * This method runs in the same thread as the UI.
	 */
	private Runnable Timer_Tick = new Runnable() {
		public void run() {

			numSecsWithoutData++;
			if (mPaused == true || currentMindsetData == null || numSecsWithoutData > 2) {
				return;
			}
			Log.i("SensorData", ", " + currentMindsetData.getLogDataLine());

			if (mSecondsRemaining-- > 0) {
				mCountdownTextView.setText(secsToHMS(mSecondsRemaining));	
			}
			else {
		    	handlePause("Session Complete"); // Allow opportinuty for a note
			}
			
			if (mLoggingEnabled == true) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
				
				String currentDateTimeString = DateFormat.getDateInstance().format(new Date());				
				currentDateTimeString = sdf.format(new Date());
				
				String logData = currentDateTimeString + ",, " + currentMindsetData.getLogDataLine() + "\n";
				
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
		Log.i(TAG, TAG +  " onPause");
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
		Log.i(TAG, TAG +  " onStop");
		if (!mSessionName.equalsIgnoreCase("")) {
			Toast.makeText(instance, "Saving: " + mSessionName, Toast.LENGTH_LONG).show();
		}
		mSelectedUser = "";
		mSessionName = "";
    	saveState();
		
		
		super.onStop();
	}	
	
	@Override
	protected void onRestart() {
		Log.i(TAG, TAG +  " onRestart");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, TAG +  " onResume");
		restoreState();
		super.onResume();
	}


	void saveState()
	{
		 SharedPref.putBoolean(this, "LoggingEnabled", 	mLoggingEnabled);
		 SharedPref.putString(this, "SessionName", 	mSessionName);
		 SharedPref.putString(this, "SelectedUser", 	mSelectedUser);
	}
	
	void restoreState()
	{
		mCurrentUsers = getUsers();	
		mSelectedUser = SharedPref.getString(this, "SelectedUser", 	"");

		setNewSessionName();
		mSessionName = SharedPref.getString(this, "SessionName", 	mSessionName);
		if (!mSessionName.equalsIgnoreCase("")) {
			openLogFile();
		}
	}

	
	/**
	 * @return List of current users from shared preference
	 */
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

	/**
	 * Saves list of current users to shared preferences
	 * @param users List of users to save
	 */
	private void setUsers(ArrayList<String> users) {
		SharedPref.setValues(
				sharedPref, 
				"CurrentUsers", 
				",", 
				ArraysExtra.toStringArray(users.toArray(new String[users.size()]))
		);
	}	
	
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		
		// Toggle showing screen buttons/controls
		if (mShowingControls) {
			mShowingControls = false;
			mCountdownTextView.setVisibility(View.GONE);
			mTextInfoView.setVisibility(View.GONE);
			mPauseButton.setVisibility(View.GONE);
			mBackButton.setVisibility(View.GONE);
			mSeekBar.setVisibility(View.GONE);
			
		}
		else {
			mShowingControls = true;
			mCountdownTextView.setVisibility(View.VISIBLE);
			mTextInfoView.setVisibility(View.VISIBLE);
			mPauseButton.setVisibility(View.VISIBLE);
			mBackButton.setVisibility(View.VISIBLE);
			mSeekBar.setVisibility(View.VISIBLE);
		}
		return false;
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		// *** For test/debug only
		double  alpha = arg1 * 2.5;
		mBuddahImage.setAlpha((int) alpha);
		mTextInfoView.setText(Integer.toString((int)alpha));
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
	}


	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
	}
		
	/**
	 * Presents a dialog allowing the operator to choose/add/delete specific users for session
	 */
	public void SelectUser() {
		mCurrentUsers = getUsers();	
		mSelection = 0;

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
    			setNewSessionName();    			
    			openLogFile();
		    	handlePause(mSessionName + " Paused"); // Allow opportinuty for a note

            }
        });
    	alert.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
          		String s = mCurrentUsers.get(mSelection);
            	mCurrentUsers.remove(s);
          		setUsers(mCurrentUsers);
          		SelectUser(); // Go back to main selection dialog
            }
        });
    	// Add new user
    	alert.setNeutralButton(R.string.new_user_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

        		getNewUserName("Enter new user name");
            }
        });

		alert.show();		
		
	}
	
	/**
	 * Presents a dialog to the operator to enter the name of a new user
	 * @param message Prompt message for dialog
	 */
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
      		SelectUser(); // Go back to main selection dialog
		  
		  }
		});

		alert1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
			  mSelectedUser = null;
		  }
		});

		alert1.show();
	}

	/**
	 * Handles the pause button press
	 *   Brings up a dialog that allows the user to either restart, or quit
	 *   Note that in any case the text entered by the user is saved to the log file
	 */
	public void handlePause(String message) {
		AlertDialog.Builder alert1 = new AlertDialog.Builder(this);

		alert1.setTitle(message);
		alert1.setMessage("Notes:");
		mPaused = true;

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert1.setView(input);

		alert1.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			
			addNoteToLog(input.getText().toString());
			Toast.makeText(instance, "Saving: " + mSessionName, Toast.LENGTH_LONG).show();
			
			mSelectedUser = "";
			mSessionName = "";
			
			finish();
		  
		  }
		});

		alert1.setNegativeButton("Start", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
				mPaused = false;
				addNoteToLog(input.getText().toString());
		  }
		});

		alert1.show();
	}

	/**
	 * Writes a specific note to the log - adding a time stamp
	 * @param note Note to save to log
	 */
	void addNoteToLog(String note) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		
		String currentDateTimeString = DateFormat.getDateInstance().format(new Date());				
		currentDateTimeString = sdf.format(new Date());
		
		String logData = currentDateTimeString + ", " + note + "\n";
		
        try {
        	if (mLogWriter != null)
        		mLogWriter.write(logData);
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}		
	}
	
	void closeLogFile() {
	}
	
	/**
	 * Sets the session name (file name to be saved) based on current time/date
	 */
	private void setNewSessionName() {
		// Create a log file name from the seledcted user and date/time
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
		String currentDateTimeString = sdf.format(new Date());
		
		mSessionName = mSelectedUser + "_" + currentDateTimeString + ".log";	
	}
	
	void openLogFile() {
		mLoggingEnabled = true;	
		
		if (mLoggingEnabled && this.mSelectedUser != null) {

			Toast.makeText(instance, "Starting: " + mSessionName, Toast.LENGTH_LONG).show();

			// Open a file for saving data
    		try {
    		    File root = Environment.getExternalStorageDirectory();
    		    if (root.canWrite()){
    		        File gpxfile = new File(root, mSessionName);
    		        FileWriter gpxwriter = new FileWriter(gpxfile, true); // open for append
    		        mLogWriter = new BufferedWriter(gpxwriter);
    		    } 
    		    else {
        		    Log.e(TAG, "Could not write file " );
        			AlertDialog.Builder alert = new AlertDialog.Builder(this);
        			
        			alert.setTitle("ERROR");
        			alert.setMessage("Cannot write to file");	
        			alert.show();			
    		    	
    		    }
    		} catch (IOException e) {
    		    Log.e(TAG, "Could not write file " + e.getMessage());
    			AlertDialog.Builder alert = new AlertDialog.Builder(this);
    			
    			alert.setTitle("ERROR");
    			alert.setMessage("Cannot write to file");	
    			alert.show();			
    		    
    		}
		}
	}
}