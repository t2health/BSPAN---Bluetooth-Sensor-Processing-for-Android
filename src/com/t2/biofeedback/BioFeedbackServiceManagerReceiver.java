package com.t2.biofeedback;


import com.t2.biofeedback.device.BioFeedbackDevice;
import com.t2.biofeedback.device.Spine.SpineDevice;

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

	private Intent mServiceIntent;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		String action = intent.getAction();
		
		if(mServiceIntent == null) {
			mServiceIntent = new Intent(context.getApplicationContext(), BioFeedbackService.class);
		}
		
		// Start the service.
		if(action.equals(BioFeedbackService.ACTION_SERVICE_START)) {
			// In the event that bluetooth isn't on, tell the service to start when bluetooth does start.
			mStartServiceOnBluetoothStarted = true;
			this.startService(context);
			
		} else if(action.equals(BioFeedbackService.ACTION_SERVICE_STOP)) {
			mStartServiceOnBluetoothStarted = false;
			this.stopService(context);
			
		} else if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
			int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
			
			switch(state) {
				case BluetoothAdapter.STATE_ON:
					Log.v(TAG, "Bluetooth enabled.");
					// Turn on the service because bluetooth has been enabled.
					if(mStartServiceOnBluetoothStarted) {
						this.startService(context);
					}
					break;
				
				// Turn off the service if bluetooth turns off.
				case BluetoothAdapter.STATE_OFF:
				case BluetoothAdapter.STATE_TURNING_OFF:
					this.stopService(context);
					break;
			}
		} 
		else if (action.equals(BioFeedbackService.ACTION_SERVER_DATA_BROADCAST)) {
			// SPINE Command message
			DeviceManager deviceManger = DeviceManager.getInstanceNoCreate();
			if (deviceManger != null) {
				
				short pktType = intent.getShortExtra(EXTRA_MESSAGE_TYPE, (short)-1);
				Log.i(TAG, "*** Received message type " + pktType + "  ***");

				if (pktType == SPINEPacketsConstants.SERVICE_DISCOVERY) {
//					Log.i(TAG, "*** Received a discovery msg  ***");
					BioFeedbackDevice[] enabledDevices =  deviceManger.getEnabledDevices();
					for(BioFeedbackDevice d: enabledDevices) {
						if(d.isBonded() && d.isConencted() ) {
							
							if (d instanceof SpineDevice)
							{
								// Special case for Arduino node, need to send "reset" command
								String str = new String("Reset");
								byte[] strBytes = str.getBytes();
								d.write(strBytes);
							}
						}
					}					
				}
			}
		}
	}
	
	private void startService(Context context) {
		Log.v(TAG, "Starting service");
		context.startService(mServiceIntent);
	}
	
	private void stopService(Context context) {
		Log.v(TAG, "Stopping service");
		context.stopService(mServiceIntent);
	}
}
