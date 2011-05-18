package com.t2.biofeedback;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BioFeedbackServiceManagerReceiver extends BroadcastReceiver {
	private static final String TAG = Constants.TAG;
	private static boolean startServiceOnBluetoothStarted = false;
	
	private Intent serviceIntent;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		if(serviceIntent == null) {
			serviceIntent = new Intent(context.getApplicationContext(), BioFeedbackService.class);
		}
		
		// Start the service.
		if(action.equals(BioFeedbackService.ACTION_SERVICE_START)) {
			// In the event that bluetooth isn't on, tell the service to start when bluetooth does start.
			startServiceOnBluetoothStarted = true;
			this.startService(context);
			
		} else if(action.equals(BioFeedbackService.ACTION_SERVICE_STOP)) {
			startServiceOnBluetoothStarted = false;
			this.stopService(context);
			
		} else if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
			int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
			
			switch(state) {
				case BluetoothAdapter.STATE_ON:
					Log.v(TAG, "Bluetooth enabled.");
					// Turn on the service because bluetooth has been enabled.
					if(startServiceOnBluetoothStarted) {
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
	}
	
	private void startService(Context context) {
		Log.v(TAG, "Starting service");
		context.startService(serviceIntent);
	}
	
	private void stopService(Context context) {
		Log.v(TAG, "Stopping service");
		context.stopService(serviceIntent);
	}
}
