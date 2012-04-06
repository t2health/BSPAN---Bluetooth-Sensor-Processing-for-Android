/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
allows dynamic on node configuration for feature extraction and a 
OtA protocol for the management for WSN

Copyright (C) 2007 Telecom Italia S.p.A. 
 
GNU Lesser General Public License
 
This library is free software; you can redistribute
modify it under the terms of the sub-license (below).

*****************************************************************/

/*****************************************************************
BSPAN - BlueTooth Sensor Processing for Android is a framework 
that extends the SPINE framework to work on Android and the 
Android Bluetooth communication services.

Copyright (C) 2011 The National Center for Telehealth and 
Technology

Eclipse Public License 1.0 (EPL-1.0)

This library is free software; you can redistribute it and/or
modify it under the terms of the Eclipse Public License as
published by the Free Software Foundation, version 1.0 of the 
License.

The Eclipse Public License is a reciprocal license, under 
Section 3. REQUIREMENTS iv) states that source code for the 
Program is available from such Contributor, and informs licensees 
how to obtain it in a reasonable manner on or through a medium 
customarily used for software exchange.

Post your updates and modifications to our GitHub or email to 
t2@tee2.org.

This library is distributed WITHOUT ANY WARRANTY; without 
the implied warranty of MERCHANTABILITY or FITNESS FOR A 
PARTICULAR PURPOSE.  See the Eclipse Public License 1.0 (EPL-1.0)
for more details.
 
You should have received a copy of the Eclipse Public License
along with this library; if not, 
visit http://www.opensource.org/licenses/EPL-1.0

*****************************************************************/

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
        
		// Tell the AndroidBTService to start up
		// No longer necessary because service is started automatically on binding
		// In fact if you start it here then it won't stop at app termination
//		this.sendBroadcast(new Intent("com.t2.biofeedback.service.START"));
		
        
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

		Log.i(TAG, "MainActivity onDestroy");
	    	

    	try {
			SPINEFactory.killSPINEManager();
		} catch (InstantiationException e) {
			Log.e(TAG, "Exception killing SPINE manager: " + e.toString());
			e.printStackTrace();
		}     	

		doUnbindService();   
//		this.sendBroadcast(new Intent("com.t2.biofeedback.service.STOP"));
    	super.onDestroy();
    	
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "OnStart, FirstTime = " + firstTime);
		
		
		if (firstTime) 
		{

			Log.i(TAG, "--------------------Starting Compassion Activity -----------------------");
			
//			Intent i = new Intent(this, BioMapActivity.class);
//			Intent i = new Intent(this, CompassionActivity.class);
//			Intent i = new Intent(this, MeditationActivity.class);
//			Intent i = new Intent(this, BioDetailActivity.class);
//			Intent i = new Intent(this, BioSoundMainActivity.class);
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
//			bindService(intent2, mConnection, 0);
			
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
	    			//msg.replyTo = mMessenger; 
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