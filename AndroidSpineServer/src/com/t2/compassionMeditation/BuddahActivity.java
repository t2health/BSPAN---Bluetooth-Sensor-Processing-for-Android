package com.t2.compassionMeditation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;



import bz.org.t2health.lib.activity.BaseActivity;
import bz.org.t2health.lib.analytics.Analytics;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;


import com.t2.SpineReceiver;
import com.t2.SpineReceiver.BioFeedbackStatus;
import com.t2.SpineReceiver.OnBioFeedbackMessageRecievedListener;
import com.t2.compassionDB.BioSession;
import com.t2.compassionDB.BioUser;
import com.t2.compassionUtils.MathExtra;
import com.t2.compassionUtils.MovingAverage;
import com.t2.compassionUtils.RateOfChange;


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
import spine.datamodel.ZephyrData;
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
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
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
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


//Need the following import to get access to the app resources, since this
//class is in a sub-package.
import com.t2.R;


//public class BuddahActivity extends OrmLiteBaseActivity<DatabaseHelper>
public class BuddahActivity extends BaseActivity
		implements 	OnBioFeedbackMessageRecievedListener, SPINEListener, 
					View.OnTouchListener, SeekBar.OnSeekBarChangeListener {
	private static final String TAG = "MeditationActivity";
	private static final String mActivityVersion = "2.3";

	private int mIntroFade = 255;
	private int mSubTimerClick = 100;
	
	Dao<BioUser, Integer> mBioUserDao;
	Dao<BioSession, Integer> mBioSessionDao;

	BioUser mCurrentBioUser = null;
	BioSession mCurrentBioSession = null;
	List<BioUser> currentUsers;	
	
	File mLogFile;

	/**
	 * Number of seconds remaining in the session
	 *   This is set initially from SharedPref.PREF_SESSION_LENGTH
	 */
	private int mSecondsRemaining = 0;
	private int mSecondsTotal = 0;
	

	
	
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
	private static BuddahActivity instance;
	
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
	
	
	// UI Elements
	private Button mToggleLogButton;
    private Button mLlogMarkerButton;
    private Button mPauseButton;
    private TextView mTextInfoView;
    private TextView mTextBioHarnessView;
    private TextView mCountdownTextView;
    private ImageView mBuddahImage; 
    private ImageView mLotusImage; 
    private SeekBar mSeekBar;
    private ImageView mSignalImage;    
    /**
     * Moving average used to smooth the display of the band of interest
     */
    private MovingAverage mMovingAverage;
    private int mMovingAverageSize = 10;

    private MovingAverage mMovingAverageROC;
    private int mMovingAverageSizeROC = 6;
    
    float maxMindsetValue = 0;
    float minMindsetValue = 0;
    float AverageMindsetValue = 0;

    /**
     * Gain used to determine how band of interest affects the buddah image 
     */
    private double mAlphaGain = 1;
    
	protected SharedPreferences sharedPref;
	
	private ArrayList<KeyItem> keyItems = new ArrayList<KeyItem>();
	private int heartRatePos;
	private int respRatePos;
	private int skinTempPos;
	
	MindsetData currentMindsetData;
	ZephyrData currentZephyrData = new ZephyrData();
	
	
	private int mMindsetBandOfInterest = MindsetData.THETA_ID; // Default to theta
	private int mBioHarnessParameterOfInterest = MindsetData.THETA_ID; // Default to theta
	private int numSecsWithoutData = 0;
	
	private String mAudioTrack;
	
	private static Object mKeysLock = new Object();
    private RateOfChange mRateOfChange;
    private int mRateOfChangeSize = 6;

	int mLotusRawValue = 0;;     
	double mLotusScaledValue = 0;;     
	int mLotusFilteredValue = 0;;     
	
	
    private MediaPlayer mMediaPlayer;
    private ToneGenerator mToneGenerator; 
    
    private boolean mShowLotus;
    LinearInterpolator interpolator = new LinearInterpolator();
    
	
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
	private String mLogCatName = "";
	
	boolean mSaveRawWave;
	boolean mAllowComments;
	boolean mShowAGain;
	String[] mBioHarnessParameters;	
	String mLogFileName = "";
	
//	private AudioToneThread audioToneThread;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        
                
		// Clear the logcat
        try {
		    String cmd = "logcat -c ";
		    Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			Log.e(TAG, "Error clearing logcat" + e.toString());
			e.printStackTrace();
		}			
        
		
		Log.i(TAG, TAG +  " onCreate");
		instance = this;
        mRateOfChange = new RateOfChange(mRateOfChangeSize);
		
		mIntroFade = 255;
        
        // We don't want the screen to timeout in this activity
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);		// This needs to happen BEFORE setContentView
        
        
        setContentView(R.layout.buddah_activity_layout);
        
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());   

    	currentMindsetData = new MindsetData(this);
		mSaveRawWave = SharedPref.getBoolean(this, 
				BioZenConstants.PREF_SAVE_RAW_WAVE, 
				BioZenConstants.PREF_SAVE_RAW_WAVE_DEFAULT);
		
		mShowAGain = SharedPref.getBoolean(this, 
				BioZenConstants.PREF_SHOW_A_GAIN, 
				BioZenConstants.PREF_SHOW_A_GAIN_DEFAULT);

		mAllowComments = SharedPref.getBoolean(this, 
				BioZenConstants.PREF_COMMENTS, 
				BioZenConstants.PREF_COMMENTS_DEFAULT);
		
		mShowLotus = SharedPref.getBoolean(this,"show_lotus", true);
		
				
		mAudioTrack =SharedPref.getString(this, "audio_track" ,"NONE");

		mBioHarnessParameters = getResources().getStringArray(R.array.bioharness_parameters_array);
		
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);       
        
        String s = SharedPref.getString(this, BioZenConstants.PREF_SESSION_LENGTH, 	"10");  
        mSecondsRemaining = Integer.parseInt(s) * 60;
//        mSecondsRemaining = SharedPref.getInt(this, com.t2.compassionMeditation.Constants.PREF_SESSION_LENGTH, 	10);  
        mSecondsTotal = mSecondsRemaining; 

		s = SharedPref.getString(this, 
				BioZenConstants.PREF_ALPHA_GAIN, "5");
		mAlphaGain = Float.parseFloat(s);

        
        mMovingAverage = new MovingAverage(mMovingAverageSize);
        mMovingAverageROC = new MovingAverage(mMovingAverageSizeROC);
        
        View v1 = findViewById (R.id.buddahView); 
        v1.setOnTouchListener (this);        
        
        Resources resources = this.getResources();
        AssetManager assetManager = resources.getAssets();
        
        // Set up member variables to UI Elements
        mTextInfoView = (TextView) findViewById(R.id.textViewInfo);
        mTextBioHarnessView = (TextView) findViewById(R.id.textViewBioHarness);
        mCountdownTextView = (TextView) findViewById(R.id.countdownTextView);
        mPauseButton = (Button) findViewById(R.id.buttonPause);
        mSignalImage = (ImageView) findViewById(R.id.imageView1);    
                

        // Note that the seek bar is a debug thing - used only to set the
        // alpha of the buddah image manually for visual testing
        mSeekBar = (SeekBar)findViewById(R.id.seekBar1);
		mSeekBar.setOnSeekBarChangeListener(this);
		mSeekBar.setProgress((int) mAlphaGain * 10);      
		
        // Controls start as invisible, need to touch screen to activate them
		mCountdownTextView.setVisibility(View.INVISIBLE);
		mTextInfoView.setVisibility(View.INVISIBLE);
		mTextBioHarnessView.setVisibility(View.INVISIBLE);		
		mPauseButton.setVisibility(View.INVISIBLE);
		mSeekBar.setVisibility(View.INVISIBLE);
		
        ImageView image = (ImageView) findViewById(R.id.imageView1);
        image.setImageResource(R.drawable.signal_bars0);  
        
        mBuddahImage = (ImageView) findViewById(R.id.buddahView);
        mBuddahImage.setImageResource(R.drawable.buddha);

        mLotusImage = (ImageView) findViewById(R.id.lotusView);
        mLotusImage.setImageResource(R.drawable.lotus_flower);
        

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
			
		
		Node zepherNode = null;
		zepherNode = new Node(new Address("" + Constants.RESERVED_ADDRESS_ZEPHYR));
		mManager.getActiveNodes().add(zepherNode);
		
		
                
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
		
        int i;
        for (i = 0; i < MindsetData.NUM_BANDS + 2; i++) {		// 2 extra, for attention and meditation
        	KeyItem key = new KeyItem(i, MindsetData.spectralNames[i], "");
            keyItems.add(key);
        }
        heartRatePos = i;
    	KeyItem key = new KeyItem(i++, "HeartRate", "");
        keyItems.add(key);
        
        respRatePos = i;
        key = new KeyItem(i++, "RespRate", "");
        keyItems.add(key);
        
        skinTempPos = i;
    	key = new KeyItem(i, "SkinTemp", "");
        keyItems.add(key);
        		
		
		String selectedUserName = SharedPref.getString(this, "SelectedUser", 	"");
		
		// Now get the database object associated with this user
		
		try {
			mBioUserDao = getHelper().getBioUserDao();
			mBioSessionDao = getHelper().getBioSessionDao();
			
			QueryBuilder<BioUser, Integer> builder = mBioUserDao.queryBuilder();
			builder.where().eq(BioUser.NAME_FIELD_NAME, selectedUserName);
			builder.limit(1);
//			builder.orderBy(ClickCount.DATE_FIELD_NAME, false).limit(30);
			List<BioUser> list = mBioUserDao.query(builder.prepare());	
			
			if (list.size() == 1) {
				mCurrentBioUser = list.get(0);
			}
			else if (list.size() == 0)
			{
				try {
					mCurrentBioUser = new BioUser(selectedUserName, System.currentTimeMillis());
					mBioUserDao.create(mCurrentBioUser);
				} catch (SQLException e1) {
					Log.e(TAG, "Error creating user " + selectedUserName , e1);
				}		
			}
			else {
				Log.e(TAG, "General Database error" + selectedUserName);
			}
			
		} catch (SQLException e) {
			Log.e(TAG, "Can't find user: " + selectedUserName , e);

		}
		
		// Create a session data point for this session (to put in data
		mCurrentBioSession = new BioSession(mCurrentBioUser, System.currentTimeMillis());

		// Create a log file name from the seledcted user and date/time
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
		String currentDateTimeString = sdf.format(new Date());
		
		mSessionName = selectedUserName + "_" + currentDateTimeString + ".log";
		mLogCatName = "Logcat" + currentDateTimeString + ".log";		
		
    	saveState();
    } // End onCreate(Bundle savedInstanceState)
    
    
    
    @Override
	public void onBackPressed() {
    	handlePause("Session Complete"); // Allow opportinuty for a note
		
   // 	super.onBackPressed();
	}



	@Override
	protected void onDestroy() {
    	super.onDestroy();
	
    	if (mMediaPlayer != null) {
    		mMediaPlayer.stop();
    		mMediaPlayer.release();
    		mMediaPlayer = null;
    	}
    	

    	Log.i(TAG, TAG +  " onDestroy");
    	
    	mLoggingEnabled = false;
    	try {
        	if (mLogWriter != null)
        		mLogWriter.close();
		} catch (IOException e) {
			Log.e(TAG, "Exeption closing file " + e.toString());
			e.printStackTrace();
		}        	
    	
    	this.unregisterReceiver(this.mCommandReceiver);
	}
    
	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, TAG +  " OnStart");
		
		
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

		}, 0, 10);		
		

		if (mMediaPlayer != null) {
	        mMediaPlayer.stop();
		}

		int resource = 0;
		if (mAudioTrack.equalsIgnoreCase("Meditate Grandpa")) resource = R.raw.meditate_grandpa_full; 
		if (mAudioTrack.equalsIgnoreCase("Pancake Brain")) resource = R.raw.pancake_brain_full; 
		if (mAudioTrack.equalsIgnoreCase("Peace Eggplant")) resource = R.raw.peace_eggplant_full; 
		
		if (resource != 0) {
			mMediaPlayer = MediaPlayer.create(this, resource);
			if (mMediaPlayer != null) {
		        mMediaPlayer.start();
		        mMediaPlayer.setLooping(true);
			}
			
		}
		
//		try {
//			mToneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, 20);
//			mToneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_REORDER);
//		} catch (RuntimeException e) {
//			Log.e(TAG, "Exception playing tone: " + e.toString() );
//			
//			e.printStackTrace();
//		}
		
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
		
		return hours + ":" + mins + ":" + secs;
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	//	this.getMenuInflater().inflate(R.menu.menu_compassion_meditation, menu);
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
				
				currentZephyrData.heartRate = heartRate;
				currentZephyrData.respRate = (int) respRate;
				currentZephyrData.skinTemp = (int) skinTempF;
				
	        	synchronized(mKeysLock) {				
    				float scaled  = MathExtra.scaleData((float)skinTempF, 110F, 70F, 255);
					keyItems.get(skinTempPos).rawValue = (int) skinTempF;
					keyItems.get(skinTempPos).setScaledValue((int) scaled);
					
    				scaled = MathExtra.scaleData((float)heartRate, 250F, 20F, 255);
					keyItems.get(heartRatePos).rawValue = heartRate;
					keyItems.get(heartRatePos).setScaledValue((int) scaled);

    				scaled = MathExtra.scaleData((float)respRate, 120F, 5F, 255);					
					keyItems.get(respRatePos).rawValue = (int) respRate;
					keyItems.get(respRatePos).setScaledValue((int) scaled);
	        	}				
				

				Log.i("SensorData","heartRate= " + heartRate + ", respRate= " + respRate + ", skinTemp= " + skinTempF);
				
				numSecsWithoutData = 0;		

				break;
			} // End case SPINEFunctionConstants.ZEPHYR:			
			

			case SPINEFunctionConstants.MINDSET: {
					Node source = data.getNode();
				
					MindsetData mindsetData = (MindsetData) data;
					//Log.i("BFDemo", "" + mindsetData.exeCode);
					if (mindsetData.exeCode == Constants.EXECODE_RAW_ACCUM) {
					}
					
					if (mindsetData.exeCode == Constants.EXECODE_POOR_SIG_QUALITY) {
						
			        	synchronized(mKeysLock) {	
			        		currentMindsetData.poorSignalStrength = mindsetData.poorSignalStrength;
			        	}

						int sigQuality = mindsetData.poorSignalStrength & 0xff;

						if (mShowingControls || sigQuality == 200)
							mSignalImage.setVisibility(View.VISIBLE);
						else
							mSignalImage.setVisibility(View.INVISIBLE);
						
						if (sigQuality == 200)
							mSignalImage.setImageResource(R.drawable.signal_bars0);
						else if (sigQuality > 150)
							mSignalImage.setImageResource(R.drawable.signal_bars1);
						else if (sigQuality > 100)
							mSignalImage.setImageResource(R.drawable.signal_bars2);
						else if (sigQuality > 50)
							mSignalImage.setImageResource(R.drawable.signal_bars3);
						else if (sigQuality > 25)
							mSignalImage.setImageResource(R.drawable.signal_bars4);
						else 
							mSignalImage.setImageResource(R.drawable.signal_bars5);

					}
					
					if (mindsetData.exeCode == Constants.EXECODE_SPECTRAL || mindsetData.exeCode == Constants.EXECODE_RAW_ACCUM) {
						Log.i(TAG, "Spectral Data");
			        	synchronized(mKeysLock) {	
							if (mPaused == false) {
								if (mindsetData.exeCode == Constants.EXECODE_RAW_ACCUM)
									currentMindsetData.updateRawWave(mindsetData);
	
								currentMindsetData.updateSpectral(mindsetData);
								numSecsWithoutData = 0;				
								Log.i("SensorData", ", " + currentMindsetData.getLogDataLine());
					        	synchronized(mKeysLock) {				
							        for (int i = 0; i < MindsetData.NUM_BANDS + 2; i++) {		// 2 extra, for attention and meditation
							        	float scaled = MathExtra.scaleData((float)currentMindsetData.getFeatureValue(i), 100F, 20F, 255, (float)mAlphaGain);	
							        	keyItems.get(i).rawValue = currentMindsetData.getFeatureValue(i);
										keyItems.get(i).setScaledValue((int) scaled);							        	
							        }
					        	}									
	
//								if (mLoggingEnabled == true) {
//									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
//									
//									String currentDateTimeString = DateFormat.getDateInstance().format(new Date());				
//									currentDateTimeString = sdf.format(new Date());
//									
//									String logData = currentDateTimeString + ", " + currentZephyrData.getLogDataLine();
//									logData += currentMindsetData.getLogDataLine(mindsetData.exeCode, mSaveRawWave) + "\n";
//									
//									
//							        try {
//							        	if (mLogWriter != null)
//							        		mLogWriter.write(logData);
//									} catch (IOException e) {
//										Log.e(TAG, e.toString());
//									}
//								}			
							} // End if (mPaused == false)
			        	}
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
	
	/**
	 * Hansles UI button clicks
	 * @param v
	 */
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
		        
//		    case R.id.buttonLogging:
//		        if (mLoggingEnabled == true)
//		        {
//		        	mLoggingEnabled = false;
//		        	mToggleLogButton.setText("Log:\nOFF");
//		        	mToggleLogButton.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
//		        	mLlogMarkerButton.setVisibility(View.INVISIBLE);
//
//		        	try {
//		            	if (mLogWriter != null)
//		            		mLogWriter.close();
//		    		} catch (IOException e) {
//		    			Log.e(TAG, "Exeption closing file " + e.toString());
//		    			e.printStackTrace();
//		    		}        	
//		        }
//		        else
//		        {
//		        	mLoggingEnabled = true;
//		        	mToggleLogButton.setText("Log:\nON");
//		        	mToggleLogButton.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
//		        	mLlogMarkerButton.setVisibility(View.VISIBLE);
//		        }
//		    	break;

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

			// We get here every .01 second
			if (mPaused == true || currentMindsetData == null || currentZephyrData == null) {
				return;
			}

			if (mSubTimerClick-- > 0) {
				if (mIntroFade > 0) {

					mBuddahImage.setAlpha(mIntroFade--);
					mLotusImage.setAlpha(mIntroFade--);
					
				}
				
				
//				float v = (float) currentZephyrData.skinTemp  / 110F ; 
//				float f = interpolator.getInterpolation(v);
//				audioToneThread.setFrequency(f * 110 * 4);
								
				
				
				
				
				return;
			}
			else {
				mSubTimerClick = 100;
				
			}
			
			// We get here every 1 second
			numSecsWithoutData++;
			
			if (mLoggingEnabled == true && numSecsWithoutData < 2) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
				
				String currentDateTimeString = DateFormat.getDateInstance().format(new Date());				
				currentDateTimeString = sdf.format(new Date());
				
				String logData = currentDateTimeString + ", " + currentZephyrData.getLogDataLine();
				logData += currentMindsetData.getLogDataLine(currentMindsetData.exeCode, mSaveRawWave) + "\n";
				
				
		        try {
		        	if (mLogWriter != null)
		        		mLogWriter.write(logData);
				} catch (IOException e) {
					Log.e(TAG, e.toString());
				}
			}			


			int iBuddahAlphaValue;
			KeyItem keyItem = keyItems.get(mMindsetBandOfInterest);
			
			int rawBuddahValue = keyItem.rawValue;			
			String mindsetBandName = keyItem.title1;
			int filteredBuddahValue = keyItem.getFilteredScaledValue();
			iBuddahAlphaValue = filteredBuddahValue;
			
			mTextInfoView.setText(mindsetBandName + ": " + rawBuddahValue + ", " + filteredBuddahValue +  ", " + iBuddahAlphaValue);		
				

			
			keyItem = keyItems.get(mBioHarnessParameterOfInterest);			
    		mLotusRawValue = keyItem.rawValue;    		

			
    		// We want to update the rate of change once every second
    		keyItem.updateRateOfChange();
			int filteredLotusValue = keyItem.getRateOfChangeScaledValue();
			int iLotusAlphaValue = 255 - filteredLotusValue;
			
			
			
			
			
			String bioHarnessBandName = keyItems.get(mBioHarnessParameterOfInterest).title1; 
			mTextBioHarnessView.setText(bioHarnessBandName + ": " + mLotusRawValue + ", " + (int) filteredLotusValue );		
			
			if (mIntroFade <= 0) {
				mBuddahImage.setAlpha(iBuddahAlphaValue);
				
//				audioToneThread.setFrequency(iBuddahAlphaValue * 6);
				
				
				if (mShowLotus) {
					mLotusImage.setAlpha(iLotusAlphaValue);
				}
				else {
					mLotusImage.setAlpha(0);
				}
			}
			
			if (mSecondsRemaining-- > 0) {
				mCountdownTextView.setText("Time remaining: " + secsToHMS(mSecondsRemaining));	
			}
			else {
				if (mMediaPlayer != null) {
			        mMediaPlayer.stop();
				}
				
				mMediaPlayer = MediaPlayer.create(instance, R.raw.wind_chime_1);
				if (mMediaPlayer != null) {
			        mMediaPlayer.start();
			        mMediaPlayer.setLooping(true);			        
				}

		    	handlePause("Session Complete"); // Allow opportinuty for a note
			}
		}
	};

	@Override
	protected void onPause() {
		Log.i(TAG, TAG +  " onPause");
		
//		if(audioToneThread != null) {
//			audioToneThread.cancel();
//			audioToneThread.interrupt();
//			audioToneThread = null;
//		}		
		
		
		mDataUpdateTimer.purge();
    	mDataUpdateTimer.cancel();
    	currentMindsetData.saveScaleData();	
		mManager.removeListener(this);	

    	saveState();
    	mLoggingEnabled = false;
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
		restoreState(); // Opens log file
		// ... then we need to register a SPINEListener implementation to the SPINE manager instance
		// to receive sensor node data from the Spine server
		// (I register myself since I'm a SPINEListener implementation!)
		mManager.addListener(this);	     
		
//		audioToneThread = new AudioToneThread();
//		audioToneThread.start();		
		
		super.onResume();
	}


	void saveState()
	{
		 SharedPref.putBoolean(this, "LoggingEnabled", 	mLoggingEnabled);
		 SharedPref.putString(this, "SessionName", 	mSessionName);
	}
	
	void restoreState()
	{
		if (mSessionName.equalsIgnoreCase(""))
			mSessionName = SharedPref.getString(this, "SessionName", 	mSessionName);
		
        String s =SharedPref.getString(this, BioZenConstants.PREF_BAND_OF_INTEREST ,"0");
        mMindsetBandOfInterest = Integer.parseInt(s);
		
		
		s = SharedPref.getString(this, 
				BioZenConstants.PREF_BIOHARNESS_PARAMETER_OF_INTEREST ,
				BioZenConstants.PREF_BIOHARNESS_PARAMETER_OF_INTEREST_DEFAULT);

		mBioHarnessParameterOfInterest = Integer.parseInt(s); 
		
		if (!mSessionName.equalsIgnoreCase("")) {
			Analytics.onStartSession(this);						
			openLogFile();
		}
		
	}

	
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		
		// Toggle showing screen buttons/controls
		if (mShowingControls) {
			mShowingControls = false;
			mCountdownTextView.setVisibility(View.INVISIBLE);
			mTextInfoView.setVisibility(View.INVISIBLE);
			mTextBioHarnessView.setVisibility(View.INVISIBLE);
			mPauseButton.setVisibility(View.INVISIBLE);
			mSeekBar.setVisibility(View.INVISIBLE);
			
		}
		else {
			mShowingControls = true;
			mCountdownTextView.setVisibility(View.VISIBLE);
			mTextInfoView.setVisibility(View.VISIBLE);
			mTextBioHarnessView.setVisibility(View.VISIBLE);
			mPauseButton.setVisibility(View.VISIBLE);
			mSeekBar.setVisibility(mShowAGain ? View.VISIBLE :View.INVISIBLE);

		}
		return false;
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		mAlphaGain = arg1/10;
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
	}


	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		SharedPref.putString(this,
				BioZenConstants.PREF_ALPHA_GAIN, new Float(mAlphaGain).toString() );
	}
	


	/**
	 * Handles the pause button press
	 *   Brings up a dialog that allows the user to either restart, or quit
	 *   Note that in any case the text entered by the user is saved to the log file
	 */
	public void handlePause(String message) {
		
		mPaused = true;

//		if (mMediaPlayer != null) {
//			mMediaPlayer.pause();
//		}

		Intent intent1 = new Intent(instance, EndSessionActivity.class);
		instance.startActivityForResult(intent1, BioZenConstants.END_SESSION_ACTIVITY);		
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
	private void setNewSessionName(String userName) {
		
	}
	
	void openLogFile() {
		mLoggingEnabled = true;	
		
		if (mLoggingEnabled) {

			Toast.makeText(instance, "Starting: " + mSessionName, Toast.LENGTH_LONG).show();

			// Open a file for saving data
    		try {
    		    File root = Environment.getExternalStorageDirectory();
    		    if (root.canWrite()){
    		        mLogFile = new File(root, mSessionName);
    		        FileWriter gpxwriter = new FileWriter(mLogFile, true); // open for append
    		        mLogWriter = new BufferedWriter(gpxwriter);

			        try {
			        	if (mLogWriter != null) {
			        		String line = "Time, " + currentZephyrData.getLogDataLineHeader();
			        		line += currentMindsetData.getLogDataLineHeader();
			        		mLogWriter.write(line + "\n");
			        	}
					} catch (IOException e) {
						Log.e(TAG, e.toString());
					}
    		        
    		        
    		    } 
    		    else {
        		    Log.e(TAG, "Could not write file " );
        			AlertDialog.Builder alert = new AlertDialog.Builder(this);
        			
        			alert.setTitle("ERROR");
        			alert.setMessage("Cannot write to file");	
        	    	alert.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
        	            public void onClick(DialogInterface dialog, int whichButton) {
        	            	mIntroFade = 255;
        	            }
        	        });
        			
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode) {
			case BioZenConstants.END_SESSION_ACTIVITY:
				 if (data != null) {
					int action = data.getIntExtra(
							BioZenConstants.END_SESSION_ACTIVITY_RESULT,
							BioZenConstants.END_SESSION_RESTART);
					
					
					switch (action) {
					
					default:
					case BioZenConstants.END_SESSION_RESTART:
						break;

					case BioZenConstants.END_SESSION_SAVE:
						EndAndSaveSession(data);
						break;

					case BioZenConstants.END_SESSION_QUIT:
						mLogFile.delete();
						Analytics.onEndSession(this);						
						finish();					
						break;
					}					 
				 }
				 else {
						mLogFile.delete();
						Analytics.onEndSession(this);						
						finish();					
				 }

				break;
		}
	}
	
	void EndAndSaveSession(Intent data) {

		String notes = "";
		String categoryName = "";
		 if (data != null) {
				notes = data.getStringExtra(
						BioZenConstants.END_SESSION_ACTIVITY_NOTES);

				categoryName = data.getStringExtra(
						BioZenConstants.END_SESSION_ACTIVITY_CATEGORY);
				
				if (categoryName == null) categoryName = "";
				if (notes == null) notes = "";
		 }
		
		
		addNoteToLog(notes);
		Toast.makeText(instance, "Saving: " + mSessionName, Toast.LENGTH_LONG).show();
		
		
		// Save catlog file for possible debugging
		try {
		    File filename = new File(Environment.getExternalStorageDirectory() + "/" + mLogCatName); 
		    filename.createNewFile(); 
		    mLogFileName = filename.getAbsolutePath();
		    String cmd = "logcat -d -f "+filename.getAbsolutePath();
		    Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}			
		
		if (!mSessionName.equalsIgnoreCase("")) {
			Toast.makeText(instance, "Saving: " + mSessionName, Toast.LENGTH_LONG).show();
		}		
		
		// -----------------------------
		// Save stats for session
		// -----------------------------
		if (mCurrentBioSession != null) {
			mCurrentBioSession.comments += notes;
			mCurrentBioSession.category = categoryName;

	        for (int i = 0; i < BioZenConstants.MAX_KEY_ITEMS; i++) {		
	        	mCurrentBioSession.maxFilteredValue[i] = keyItems.get(i).getMaxFilteredValue();
	        	mCurrentBioSession.minFilteredValue[i] = 
	        		keyItems.get(i).getMinFilteredValue() != 9999 ? keyItems.get(i).getMinFilteredValue() : 0;
	        	mCurrentBioSession.avgFilteredValue[i] = keyItems.get(i).getAvgFilteredValue();
	        	mCurrentBioSession.keyItemNames[i] = keyItems.get(i).title1;
	        }
	        
	        int secondsCompleted =  mSecondsTotal -  mSecondsRemaining;
	        float precentComplete = (float) secondsCompleted / (float) mSecondsTotal;
	        mCurrentBioSession.precentComplete = (int) (precentComplete * 100);
	        mCurrentBioSession.secondsCompleted = secondsCompleted;
	        mCurrentBioSession.logFileName = mLogFileName; 
	        
	        mCurrentBioSession.mindsetBandOfInterestIndex = mMindsetBandOfInterest;
	        mCurrentBioSession.bioHarnessParameterOfInterestIndex = mBioHarnessParameterOfInterest;
	        

	        // Udpate the database with the current session
			try {
				mBioSessionDao.create(mCurrentBioSession);
			} catch (SQLException e1) {
				Log.e(TAG, "Error saving current session to database", e1);
			}			
			
		}
		

		
		finish();
		
		
	}
		
	

	
}