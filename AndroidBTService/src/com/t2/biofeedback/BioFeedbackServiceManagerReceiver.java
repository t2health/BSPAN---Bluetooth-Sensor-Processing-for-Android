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

package com.t2.biofeedback;



//import t2.spine.communication.android.AndroidMessage;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Receiver of broadcast intent messages from 3 different sources:
 * 	1. BTServiceManager (Activity) - Startup and shutdown
 *  2. Android BluetoothAdapter - Bluetooth hardware adapter turned on/off
 *  3. AndroidSpineServer (Activity) - Messages to send to sensor devices (i.e "Discover")
 *  
 * @author scott.coleman
 *
 */
public class BioFeedbackServiceManagerReceiver extends BroadcastReceiver {
	private static final String TAG = Constants.TAG;

	private static boolean mStartServiceOnBluetoothStarted = false;
	
	public static final String EXTRA_ADDRESS = "address";
	public static final String EXTRA_NAME = "name";
	public static final String EXTRA_MESSAGE_TYPE = "messageType";
	public static final String EXTRA_MESSAGE_ID = "messageId";
	public static final String EXTRA_MESSAGE_VALUE = "messageValue";
	public static final String EXTRA_TIMESTAMP = "timestamp";	
	public static final String EXTRA_MESSAGE_PAYLOAD = "messagePayload";	

	private Intent mServiceIntent;
	String mVersion = "";	
	
	@Override
	// Receiving command messages from the SPINE server
	public void onReceive(Context context, Intent intent) {
		
		String action = intent.getAction();
		
		if(mServiceIntent == null) {
			mServiceIntent = new Intent(context.getApplicationContext(), BioFeedbackService.class);
		}
		
		// Start the service.
		if(action.equals(BioFeedbackService.ACTION_SERVICE_START)) {
//			// In the event that bluetooth isn't on, tell the service to start when bluetooth does start.
//			
//			Log.d(TAG, this.getClass().getSimpleName() + ".onReceive(ACTION_SERVICE_START)"); 			
//			mStartServiceOnBluetoothStarted = true;
//			// Note that we DON'T want to explicitly start the service here.
//			// IT will be started on bind()
//			// In general, when using bind(autocreate) we NEVER want to use StartService
//			this.startService(context);
			
		} else if(action.equals(BioFeedbackService.ACTION_SERVICE_STOP)) {
//			Log.d(TAG, this.getClass().getSimpleName() + ".onReceive(ACTION_SERVICE_STOP)"); 			
//			mStartServiceOnBluetoothStarted = false;
//			this.stopService(context);
			
		} else if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
			int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
			
			switch(state) {
				case BluetoothAdapter.STATE_ON:
					Log.d(TAG, this.getClass().getSimpleName() + ".onReceive(ACTION_STATE_CHANGED) state = Bluetooth enabled"); 			
					// Turn on the service because bluetooth has been enabled.
					if(mStartServiceOnBluetoothStarted) {
						// It's ok to use start service here because we know bluetooth just turned on 
						this.startService(context);
					}
					break;
				
				// Turn off the service if bluetooth turns off.
				case BluetoothAdapter.STATE_OFF:
				case BluetoothAdapter.STATE_TURNING_OFF:
					Log.d(TAG, this.getClass().getSimpleName() + ".onReceive(ACTION_STATE_CHANGED) state = Bluetooth disabled"); 			
					
					this.stopService(context);
					break;
			}
		} 
		else if (action.equals(BioFeedbackService.ACTION_SERVER_DATA_BROADCAST)) {
			// We no longer receive commands via broadcasts
			// See BiofeedbackService IncomingHandler()
		}
	}
	
	private void startService(Context context) {

		Log.v(TAG, "Starting service, context = " + context);
		context.startService(mServiceIntent);
	}
	
	private void stopService(Context context) {
		Log.v(TAG, "Stopping service, context = " + context);
		context.stopService(mServiceIntent);
	}
	

	
}
