package com.t2;

import com.t2.Constants;

import spine.SPINEFactory;
import spine.SPINEManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * Main (test) activity for Spine server.
 * This activity's only purpose is to:
 * 1. establish the main spine manager
 * 2. Start the BT Service
 * 3. Bind the service to this activity to the androidMessageServer can get messages from the service
 * 4. Stick around until all of the activities using spine are destroyed.
 * 5. Shutdown the binding and service after everything is done.
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
public class AndroidSpineServerMainActivity extends Activity{
	private static final String TAG = Constants.TAG;

    private static AndroidSpineConnector spineConnector;
    private static boolean firstTime = true;

	/**
     * The Spine manager contains the bulk of the Spine server. 
     */
    private static SPINEManager mManager;
	
	/**
	 * Static instance of this activity
	 */
	private static AndroidSpineServerMainActivity instance;

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
	private boolean mIsBound = false;
	
	private static final int MSG_UNREGISTER_CLIENT = 2;	
	
	private String mPackageName = "";
	private int mVersionCode;
	private String mVersionName = "";
	private String mTargetName = "";
	
	public static final int ANDROID_SPINE_SERVER_ACTIVITY = 0;
	public static final String ANDROID_SPINE_SERVER_ACTIVITY_RESULT = "AndroidSpineServerActivityResult";
	
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
	public static AndroidSpineServerMainActivity getInstance() 
	{
	   return instance;
	}
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.i(TAG, "OnCreate");
		firstTime = true;
        instance = this;

        // If we were called from the Biomap activity then it will have
        // sent us a target to focus on
        try {
			// Get target name if one was supplied
			Bundle bundle = getIntent().getExtras();
			mTargetName = bundle.getString("TARGET_NAME");
		} catch (Exception e1) {
			mTargetName = "";
		}
        
        
        AndroidSpineConnector.setMainActivityInstance(instance);
        
        Resources resources = this.getResources();
        AssetManager assetManager = resources.getAssets();
        
		// Initialize SPINE by passing the fileName with the configuration properties
		try {
			mManager = SPINEFactory.createSPINEManager("SPINETestApp.properties", resources);
		} catch (InstantiationException e) {
			Log.e(TAG, "Exception creating SPINE manager: " + e.toString());
			e.printStackTrace();
		}        
        
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

    } // End onCreate(Bundle savedInstanceState)
    
    @Override
	protected void onDestroy() {
    	super.onDestroy();

    	this.sendBroadcast(new Intent("com.t2.biofeedback.service.STOP"));
		Log.i(TAG, "MainActivity onDestroy");
	    	
    	doUnbindService();   

    	try {
			SPINEFactory.killSPINEManager();
		} catch (InstantiationException e) {
			Log.e(TAG, "Exception killing SPINE manager: " + e.toString());
			e.printStackTrace();
		}     	
    	
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "OnStart, FirstTime = " + firstTime);
		
		// Tell the AndroidBTService to start up
		this.sendBroadcast(new Intent("com.t2.biofeedback.service.START"));
		
		if (firstTime) 
		{

			Log.i(TAG, "--------------------Starting Compassion Activity -----------------------");
			
//			Intent i = new Intent(this, BioMapActivity.class);
//			Intent i = new Intent(this, CompassionActivity.class);
//			Intent i = new Intent(this, MeditationActivity.class);
//			Intent i = new Intent(this, BioDetailActivity.class);
//			Intent i = new Intent(this, MainChooserActivity.class);
//			Intent i = new Intent(this, SplashScreenActivity.class);
			Intent i = new Intent(mTargetName);
			this.startActivity(i);
		}
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

	@Override
	protected void onPause() {
		Log.i(TAG, "MainActivity onPause");
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
		finish();
		firstTime = false;
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "MainActivity onResume");
		super.onResume();
	}
}