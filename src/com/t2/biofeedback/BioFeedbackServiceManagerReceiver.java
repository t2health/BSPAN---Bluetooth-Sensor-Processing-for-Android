package com.t2.biofeedback;


import com.t2.biofeedback.device.BioFeedbackDevice;

//import t2.spine.communication.android.AndroidMessage;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BioFeedbackServiceManagerReceiver extends BroadcastReceiver {
	private static final String TAG = Constants.TAG;
	private static boolean startServiceOnBluetoothStarted = false;
	
	public static final String EXTRA_ADDRESS = "address";
	public static final String EXTRA_NAME = "name";
	public static final String EXTRA_MESSAGE_TYPE = "messageType";
	public static final String EXTRA_MESSAGE_ID = "messageId";
	public static final String EXTRA_MESSAGE_VALUE = "messageValue";
	public static final String EXTRA_TIMESTAMP = "timestamp";	

//	public static class BioFeedbackSpineData extends BioFeedbackMessage {
//		public byte[] msgBytes;
//		public long currentTimestamp;
//		
//		public static BioFeedbackSpineData factory(Intent i) {
//			BioFeedbackSpineData m = new BioFeedbackSpineData();
//			m.address = i.getStringExtra("address");
//			m.name = i.getStringExtra("name");
//			m.messageType = i.getStringExtra("messageType");
//			m.messageId = i.getStringExtra("messageId");
//			m.msgBytes = i.getByteArrayExtra("msgBytes");
//			m.currentTimestamp = i.getLongExtra("currentTimestamp", 0);
//			
//			return m;
//		}
//	}	
	
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
		else if (action.equals(BioFeedbackService.ACTION_SERVER_DATA_BROADCAST)) {
			// SPINE Command message
			DeviceManager deviceManger = DeviceManager.getInstanceNoCreate();
			if (deviceManger != null) {
				
			short ClusterId = intent.getShortExtra(EXTRA_MESSAGE_TYPE, (short)-1);
			
			//				AndroidMessage msg = new AndroidMessage();
//								
//				msg.setClusterId(; 
//
//				// Real dumb here
				if (ClusterId == 1) {
//					
					Log.v(TAG, "*** Received a discovery msg  ***");
					BioFeedbackDevice[] enabledDevices =  deviceManger.getEnabledDevices();
					for(BioFeedbackDevice d: enabledDevices) {
						if(d.isBonded() && d.isConencted() ) {
							String str = new String("Reset");
							byte[] strBytes = str.getBytes();
							
	
							d.write(strBytes);
							
						}
					}					
					
				}
//
//				
//				
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
