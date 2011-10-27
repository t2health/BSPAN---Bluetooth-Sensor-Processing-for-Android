package com.t2.biofeedback;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.t2.biofeedback.BioFeedbackService.BroadcastMessage;
import com.t2.biofeedback.device.BioFeedbackDevice;
import com.t2.biofeedback.device.SerialBTDevice;
import com.t2.biofeedback.device.Spine.SpineDevice;
import com.t2.biofeedback.device.shimmer.ShimmerDevice;

//import t2.spine.communication.android.AndroidMessage;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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
			// In the event that bluetooth isn't on, tell the service to start when bluetooth does start.
			
			Log.d(TAG, this.getClass().getSimpleName() + ".onReceive(ACTION_SERVICE_START)"); 			
			mStartServiceOnBluetoothStarted = true;
			// Note that we DON'T want to explicitly start the service here.
			// IT will be started on bind()
			// In general, when using bind(autocreate) we NEVER want to use StartService
//			this.startService(context);
			
		} else if(action.equals(BioFeedbackService.ACTION_SERVICE_STOP)) {
			Log.d(TAG, this.getClass().getSimpleName() + ".onReceive(ACTION_SERVICE_STOP)"); 			
			mStartServiceOnBluetoothStarted = false;
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
			// SPINE Command message
			DeviceManager deviceManger = DeviceManager.getInstanceNoCreate();
			if (deviceManger != null) {
				
				short pktType = intent.getShortExtra(EXTRA_MESSAGE_TYPE, (short)-1);

				if (pktType == SPINEPacketsConstants.SERVICE_DISCOVERY) {
					Log.i(TAG, "*** Received a discovery msg  *** message type " + pktType);
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
				} // End if (pktType == xxx
				else if (pktType == SPINEPacketsConstants.SETUP_SENSOR) {
					Log.i(TAG, "*** Received a SETUP_SENSOR msg  *** message type " + pktType);
					byte[] payload =  intent.getByteArrayExtra(EXTRA_MESSAGE_PAYLOAD);
					byte sensor;
					byte command;
					byte[] btAddress = new byte[6];
					String btAddressString;
					
					if (payload.length == 8) {
						// See ShimmerNonSpineSetupSensor_codec for coding format
						sensor = payload[0];
						command = payload[1];

						try {
							for (int i = 0; i < 6; i++) {
								btAddress[i] = payload[i+2];
							}
							btAddressString = Util.getBtStringAddress(btAddress);
						} catch (IndexOutOfBoundsException e) {
							Log.e(TAG, e.toString());
							btAddressString = "";
						}						
						
						
						BioFeedbackDevice[] enabledDevices =  deviceManger.getEnabledDevices();
						for(BioFeedbackDevice d: enabledDevices) {
							if(d.isBonded() && d.isConencted() ) {
								
								if (d instanceof ShimmerDevice)
								{
									
									String s = d.getAddress();
									Log.i(TAG, "Address = " + s);
									
									if (d.getAddress().equalsIgnoreCase(btAddressString)) {
										ShimmerDevice dev = (ShimmerDevice)d;
										dev.setup(sensor, command);
										
									}
									
								}
							}
						}					
						
					}
				} // End if (pktType == xxx
				else if (pktType == SPINEPacketsConstants.POLL_BLUETOOTH_DEVICES) {
					sendDeviceList(context);
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
	
	/**
	 * Sends a JSON encoded string containing the status of the bluetooth system and devices
	 * 	name: name of paired bluetooth device
	 *  address: BT address of paired bluetooth device
	 *  enabled: whether or not the device is enabled by the user
	 *  
	 *  Note that a special name (system) is reserved for the bluetooth system in general
	 *  (to tell whether or not bluetooth is enabled by the user
	 * 
	 * 
	 * @param context
	 */
	private void sendDeviceList(Context context) {

		boolean bluetoothEnabled = false;
		// First see if bluetooth is enabled
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Device does not support Bluetooth");
			return;
		} 
		
	    if (mBluetoothAdapter.isEnabled()) {
	    	bluetoothEnabled = true;
	    }
		
		JSONArray jsonArray = new JSONArray();
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", "system");
			jsonObject.put("address", "");
			jsonObject.put("enabled", bluetoothEnabled);
			
			jsonArray.put(jsonObject);			
			
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}		
		
		DeviceManager deviceManger = DeviceManager.getInstanceNoCreate();
		if (deviceManger != null) {

			BioFeedbackDevice[] bondedDevices =  deviceManger.getBondedDevices();
			BioFeedbackDevice[] enabledDevices =  deviceManger.getEnabledDevices();
			for(BioFeedbackDevice d: bondedDevices) {
				// See if it's enabled
				boolean enabled = false;
				for(BioFeedbackDevice dEnabled: enabledDevices) {
					if (d.getAddress().equalsIgnoreCase(dEnabled.getAddress()))
						enabled = true;
				}
				
				try {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("name", d.getName());
					jsonObject.put("address", d.getAddress());
					jsonObject.put("enabled", enabled);
					
					jsonArray.put(jsonObject);						
				} catch (JSONException e) {
					Log.e(TAG, e.toString());
				}		
			}			
		}
		
		Intent i = new Intent();
		i.setAction("com.t2.biofeedback.service.status.BROADCAST");
		i.putExtra(EXTRA_MESSAGE_TYPE, BroadcastMessage.Type.STATUS);
		i.putExtra(EXTRA_MESSAGE_ID, "STATUS_PAIRED_DEVICES");
		i.putExtra(EXTRA_TIMESTAMP, System.currentTimeMillis());
		i.putExtra(EXTRA_ADDRESS, jsonArray.toString());
		context.sendBroadcast(i);
		
	}
	
	
}
