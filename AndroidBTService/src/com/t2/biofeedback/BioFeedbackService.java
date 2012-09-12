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

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.t2.biofeedback.device.BioFeedbackDevice;
import com.t2.biofeedback.device.SerialBTDevice;

import com.t2.biofeedback.device.SerialBTDevice.DeviceConnectionListener;
import com.t2.biofeedback.device.Spine.SpineDevice;
import com.t2.biofeedback.device.shimmer.ShimmerDevice;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * Background service for use with AndroidSpineServer.
 * Maintains list of Bluetooth devices which communicate with the main Spine server
 * Provides communication between individual Bluetooth devices
 * and the Spine Server.
 * 
 * @see AndroidSpineServer
 * @author scott.coleman
 *
 */
public class BioFeedbackService extends Service implements DeviceConnectionListener {
	private static final String TAG = Constants.TAG;
	
	/**
	 * The interval between succesive scans of Bluetooth devices
	 *  Normal operation is 10000 (10 seconds)
	 */
//	static final int DEVICE_SCAN_INTERVAL = 2000;
	static final int DEVICE_SCAN_INTERVAL = 10000;
	
	public static final byte COMMAND_ENABLED = 2;
	public static final byte COMMAND_DISABLED = 3;	

	public static final int CONN_ERROR = -1;
	public static final int CONN_IDLE = 0;
	public static final int CONN_PAIRED = 1;
	public static final int CONN_CONNECTING = 2;
	public static final int CONN_CONNECTED = 3;	
	
	
	/**
	 * Broadcast message used send status messages to the Spine server
	 * @author scott.coleman
	 *
	 */
	public static final class BroadcastMessage {
		public static final class Type {
			public static final String STATUS = "STATUS";
			public static final String DATA = "DATA";
		}
		
		public static final class Id {
			public static final String CONN_CONNECTING = "CONN_CONNECTING";
			public static final String CONN_CONNECTED = "CONN_CONNECTED";
			public static final String CONN_ANY_CONNECTED = "CONN_ANY_CONNECTED";
			public static final String CONN_CLOSE = "CONN_CLOSE";
			public static final String CONN_ALL_CLOSED = "CONN_ALL_CLOSED";
			public static final String CONN_BEFORE_CLOSE = "CONN_BEFORE_CLOSE";
			public static final String CONN_CONNECTION_LOST = "CONN_CONNECTION_LOST";
			
			public static final String DATA_SKIN_TEMPERATURE = "DATA_SKIN_TEMPERATURE";
			public static final String DATA_RESPIRATION_RATE = "DATA_RESPIRATION_RATE";
			public static final String DATA_HEART_RATE = "DATA_HEART_RATE";
			public static final String BATTERY_LEVEL = "BATTERY_LEVEL";
			public static final String SPINE_MESSAGE = "SPINE_MESSAGE";
			public static final String DATA_MESSAGE = "DATA_MESSAGE";
		}
	}
	
	public static final String ACTION_SPINE_DATA_BROADCAST =  "com.t2.biofeedback.service.spinedata.BROADCAST";
	public static final String ACTION_ZEPHYR_DATA_BROADCAST = "com.t2.biofeedback.service.zephyrdata.BROADCAST";
	public static final String ACTION_DATA_BROADCAST = "com.t2.biofeedback.service.data.BROADCAST";
	public static final String ACTION_STATUS_BROADCAST = "com.t2.biofeedback.service.status.BROADCAST";
	public static final String ACTION_SERVER_DATA_BROADCAST = "com.t2.biofeedback.server.data.BROADCAST";
	public static final String ACTION_SERVICE_START = "com.t2.biofeedback.service.START";
	public static final String ACTION_SERVICE_STOP = "com.t2.biofeedback.service.STOP";
	public static final String ACTION_SERVICE_BTNAMES = "com.t2.biofeedback.service.BTNAMES";
	
	public static final String EXTRA_ADDRESS = "address";
	public static final String EXTRA_NAME = "name";
	public static final String EXTRA_MESSAGE_TYPE = "messageType";
	public static final String EXTRA_MESSAGE_ID = "messageId";
	public static final String EXTRA_MESSAGE_VALUE = "messageValue";
	public static final String EXTRA_TIMESTAMP = "timestamp";
	public static final String EXTRA_MSG_BYTES = "msgBytes";


	
	/**
	 * Interfaces with OS to control individual connections to Bluetooth devices
	 */
	private DeviceManager deviceManager;

	/**
	 * Thread used to periodically update Bluetooth device connections 
	 */
	private ManageDeviceThread manageDeviceThread;
	
	/**
	 * Handler used to received messages from ManageDeviceThread
	 * Used only to call mangeDevices periodically to look for
	 * Bluetooth devices, add/remove, etc.
	 */
	private Handler manageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mangeDevices();
		}
	};

	/**
	 * We get here once every 10 seconds in order to scan for devices
	 * connect/disconnect, etc.
	 */
	private void mangeDevices() {
		Log.d(TAG, this.getClass().getSimpleName() + ".manageDevices() V2.3.1");		
		deviceManager.manage();
		
		// Set the listeners for all the enabled devices.
		BioFeedbackDevice[] enabledDevices = deviceManager.getEnabledDevices();
		for(int i = 0; i < enabledDevices.length; i++) {
			setListeners(enabledDevices[i]);
		}
		
		deviceManager.connectAll();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, this.getClass().getSimpleName() + ".onBind()");		
        return mMessenger.getBinder();
	}

	
	
	@Override
	public void onRebind(Intent intent) {
		Log.d(TAG, this.getClass().getSimpleName() + ".onReBind()");		
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, this.getClass().getSimpleName() + ".onUnBind()");		
		return super.onUnbind(intent);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		String version = "";
		
		try {
			PackageManager packageManager = this.getPackageManager();
			PackageInfo info = packageManager.getPackageInfo(this.getPackageName(), 0);			
			version = info.versionName;
		} 
		catch (NameNotFoundException e) {
			   	Log.e(TAG, e.toString());
		}		
        Log.d(TAG, this.getClass().getSimpleName() + ".onCreate(), Version " + version); 
		
		this.deviceManager = DeviceManager.getInstance(this.getBaseContext(), mServerListeners);
		this.manageDeviceThread = new ManageDeviceThread();
		this.manageDeviceThread.start();		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		String version = "";
		
		try {
			PackageManager packageManager = this.getPackageManager();
			PackageInfo info = packageManager.getPackageInfo(this.getPackageName(), 0);			
			version = info.versionName;
		} 
		catch (NameNotFoundException e) {
			   	Log.e(TAG, e.toString());
		}			
		
        Log.d(TAG, this.getClass().getSimpleName() + ".onDestroy(), Version " + version); 
		this.manageDeviceThread.setRun(false);
		this.deviceManager.closeAll();
//		this.deviceManager = null;
	}
	
	
	private void setListeners(BioFeedbackDevice device) {
		device.setDeviceConnectionListener(this);
	}
	
	private Intent getStatusBroadcastIntent(SerialBTDevice d, String messageType, String messageId, Double value) {
		Intent i = new Intent();
		i.setAction(ACTION_STATUS_BROADCAST);
		if(d != null) {
				i.putExtra(EXTRA_ADDRESS, d.getAddress());
				i.putExtra(EXTRA_NAME, d.getName());
			}
			i.putExtra(EXTRA_MESSAGE_TYPE, messageType);
			i.putExtra(EXTRA_MESSAGE_ID, messageId);
			i.putExtra(EXTRA_MESSAGE_VALUE, value);
			i.putExtra(EXTRA_TIMESTAMP, System.currentTimeMillis());
		return i;
	}
	
	@Override
	public void onDeviceConnecting(SerialBTDevice d) {
		this.sendBroadcast(
				getStatusBroadcastIntent(d, BroadcastMessage.Type.STATUS, "CONN_CONNECTING", null)
		);
	}

	@Override
	public void onDeviceConnected(SerialBTDevice d) {
		int i = 1;
		i++;
		this.sendBroadcast(
				getStatusBroadcastIntent(d, BroadcastMessage.Type.STATUS, "CONN_CONNECTED", null)
		);
		
		if(this.deviceManager.isAnyDeviceConnected()) {
			this.sendBroadcast(
				getStatusBroadcastIntent(d, BroadcastMessage.Type.STATUS, "CONN_ANY_CONNECTED", null)
			);
		}
	}

	@Override
	public void onDeviceClosed(SerialBTDevice d) {
		this.sendBroadcast(
				getStatusBroadcastIntent(d, BroadcastMessage.Type.STATUS, BroadcastMessage.Id.CONN_CLOSE, null)
		);
		
		if(this.deviceManager.isAllDevicesClosed()) {
			this.sendBroadcast(
					getStatusBroadcastIntent(null, BroadcastMessage.Type.STATUS, BroadcastMessage.Id.CONN_ALL_CLOSED, null)
			);
		}
	}

	@Override
	public void onBeforeDeviceClosed(SerialBTDevice d) {
		this.sendBroadcast(
				getStatusBroadcastIntent(d, BroadcastMessage.Type.STATUS, BroadcastMessage.Id.CONN_BEFORE_CLOSE, null)
		);
	}

	@Override
	public void onDeviceConnectionLost(SerialBTDevice d) {
		this.sendBroadcast(
				getStatusBroadcastIntent(d, BroadcastMessage.Type.STATUS, BroadcastMessage.Id.CONN_CONNECTION_LOST, null)
		);
	}
	
	/**
	 * Main thread for updating supported devices
	 * @author scott.coleman
	 *
	 */
	private class ManageDeviceThread extends Thread {
		boolean run = true;
		
		@Override
		public void run() {
			this.run = true;
			while(this.run) {
//				Log.v(TAG, "Managing supported devices.");
				manageHandler.sendEmptyMessage(1);
				
				try {
					Thread.sleep(DEVICE_SCAN_INTERVAL);
				} catch (InterruptedException e) {
				}
			}
		}
		
		public void setRun(boolean b) {
			this.run = false;
		}
	}

    /**
     * List of server listeners. These devices will be notified
     * any time any device transmits data to the Spine Server
     */
    ArrayList<Messenger> mServerListeners = new ArrayList<Messenger>();

    /** Holds last value set by a client. */
    int mValue = 0;

    /**
     * Command to the service to register a client, receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client where callbacks should be sent.
     */
    static final int MSG_REGISTER_CLIENT = 1;

    /**
     * Command to the service to unregister a client, or stop receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client as previously given with MSG_REGISTER_CLIENT.
     */
    static final int MSG_UNREGISTER_CLIENT = 2;

    /**
     * Command to the service to send a spine command
     */
    static final int MSG_SPINE_COMMAND = 8888;

    
    /**
     * Command to service to set a new value.  This can be sent to the
     * service to supply a new value, and will be sent by the service to
     * any registered clients with the new value.
     */
    static final int MSG_SET_VALUE = 3;

    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
        	
    		Log.d(TAG, this.getClass().getSimpleName() + ".handleMessage(), what = " + msg.what + ", arg1 = " + msg.arg1);		
        	
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                	mServerListeners.add(msg.replyTo);
                	Log.d(TAG, this.getClass().getSimpleName() + " MSG_REGISTER_CLIENT - Adding client data listener: " + msg.replyTo + ", len = " + mServerListeners.size()); 
                    break;
                case MSG_UNREGISTER_CLIENT:
                	mServerListeners.clear();
//                	mServerListeners.remove(msg.replyTo);
                	Log.d(TAG, this.getClass().getSimpleName() + " MSG_UNREGISTER_CLIENT - Removing ALL data listenes:  , len = " + mServerListeners.size()); 
                    break;

                // This is not currently used (except by example). 
                case MSG_SET_VALUE:
                    mValue = msg.arg1;
                    for (int i=mServerListeners.size()-1; i>=0; i--) {
                        try {
                        	mServerListeners.get(i).send(Message.obtain(null,
                                    MSG_SET_VALUE, mValue, 0));
                        } catch (RemoteException e) {
                            // The client is dead.  Remove it from the list;
                            // we are going through the list from back to front
                            // so this is safe to do inside the loop.
                        	mServerListeners.remove(i);
                        }
                    }
                    break;
                    
                // Test of sending commands through this route
                case MSG_SPINE_COMMAND:
                	int pktType = msg.arg1;
                	
                	switch (pktType) {
                	case SPINEPacketsConstants.SERVICE_DISCOVERY:
                		Log.d(TAG, "*** NEW Received a SERVICE_DISCOVERY msg  *** message type " + pktType);
                		performDiscoveryTasks();                		
                	break;

                	case SPINEPacketsConstants.SERVICE_COMMAND:
                		Log.d(TAG, "*** NEW Received a SERVICE_COMMAND msg  *** message type " + pktType);
                		performServiceCommand(msg);
                	break;

                	case SPINEPacketsConstants.SETUP_SENSOR:
                		Log.d(TAG, "*** NEW Received a SETUP_SENSOR msg  *** message type " + pktType);
                		performSetupSensor(msg);
                	break;

                	case SPINEPacketsConstants.POLL_BLUETOOTH_DEVICES:
                		Log.d(TAG, "*** NEW Received a POLL_BLUETOOTH_DEVICES msg  *** message type " + pktType);
                		sendDeviceList();
                	break;
                	}
                	break;
                    
                    
                default:
                    super.handleMessage(msg);
            }
        }
    }
    
    void performDiscoveryTasks() {
      	if (deviceManager == null) {
			Log.e(TAG, "NEW no device manager");
			return;
      	}
		BioFeedbackDevice[] enabledDevices =  deviceManager.getEnabledDevices();
		for(BioFeedbackDevice d: enabledDevices) {
			if(d.isBonded() && d.isConencted() ) {
				
				if (d instanceof SpineDevice)	{
					// Special case for Arduino node, need to send "reset" command
					String str = new String("Reset");
					byte[] strBytes = str.getBytes();
					d.write(strBytes);
				}
			}
		}      	
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
	private void sendDeviceList() {

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
			jsonObject.put("connectionStatus", 0); // Don't care for this one
			
			
			jsonArray.put(jsonObject);			
			
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}		
		
		if (deviceManager != null) {

			
			
			BioFeedbackDevice[] bondedDevices =  deviceManager.getBondedDevices();
			BioFeedbackDevice[] enabledDevices =  deviceManager.getEnabledDevices();
			for(BioFeedbackDevice d: bondedDevices) {
				// See if it's enabled
				boolean enabled = false;
				int connectionStatus = CONN_IDLE; // Default
				
				for(BioFeedbackDevice dEnabled: enabledDevices) {
					if (d.getAddress().equalsIgnoreCase(dEnabled.getAddress())) {
						enabled = true;
						if(d.isConencted()) {
							connectionStatus = CONN_CONNECTED;
						} else if(d.isConnecting()) {
							connectionStatus = CONN_CONNECTING;
						} else {
							connectionStatus = CONN_PAIRED;
						}						
					}
				}
				
				try {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("name", d.getName());
					jsonObject.put("address", d.getAddress());
					jsonObject.put("enabled", enabled);
					jsonObject.put("connectionStatus", connectionStatus);
					
					jsonArray.put(jsonObject);						
				} catch (JSONException e) {
					Log.e(TAG, e.toString());
				}		
			}			
			Log.d(TAG, "JSON paired devices = " + jsonArray.toString() );

			
			// TOTO: we should probably change this from broadcast to use the ipc messaging channels
			Intent i = new Intent();
			i.setAction("com.t2.biofeedback.service.status.BROADCAST");
			i.putExtra(EXTRA_MESSAGE_TYPE, BroadcastMessage.Type.STATUS);
			i.putExtra(EXTRA_MESSAGE_ID, "STATUS_PAIRED_DEVICES");
			i.putExtra(EXTRA_TIMESTAMP, System.currentTimeMillis());
			i.putExtra(EXTRA_ADDRESS, jsonArray.toString());
			sendBroadcast(i);		
			}
		else {
			Log.e(TAG, "NEW no device manager");			
		}
	}
    
    void performSetupSensor(Message msg) {
      	byte[] payload = msg.getData().getByteArray("EXTRA_MESSAGE_PAYLOAD");
      	if (payload == null)
      		return;
      	if (deviceManager == null) {
			Log.e(TAG, "NEW no device manager");
			return;
      	}
      	
		byte sensor;
		byte command;
		byte[] btAddress = new byte[6];
		String btAddressString;
		
		if (payload.length == 8 + 255) {
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
			
			BioFeedbackDevice[] enabledDevices =  deviceManager.getEnabledDevices();
			
			for(BioFeedbackDevice d: enabledDevices) {
				if(d.isBonded() && d.isConencted() ) {
					
					if (d instanceof ShimmerDevice)	{
						String s = d.getAddress();
						
						if (d.getAddress().equalsIgnoreCase(btAddressString)) {
							Log.i(TAG, "Address = " + s);
							ShimmerDevice dev = (ShimmerDevice)d;
							dev.setup(sensor, command);
							
						}
					}
				}
			}					
		}    	
    }    
    void performServiceCommand(Message msg) {
      	byte[] payload = msg.getData().getByteArray("EXTRA_MESSAGE_PAYLOAD");
      	if (payload == null)
      		return;
      	if (deviceManager == null) {
			Log.e(TAG, "NEW no device manager");
			return;
      	}
      	
		byte sensor;
		byte command;
		byte[] btAddress = new byte[6];
		String btAddressString;
		
		if (payload.length == 7 + 255) {
			// See ShimmerNonSpineSetupSensor_codec for coding format
			command = payload[0];

			try {
				for (int i = 0; i < 6; i++) {
					btAddress[i] = payload[i+1];
				}
				btAddressString = Util.getBtStringAddress(btAddress);
			} catch (IndexOutOfBoundsException e) {
				Log.e(TAG, e.toString());
				btAddressString = "";
			}						
			
			if (command == COMMAND_ENABLED) {
				this.deviceManager.setDeviceEnabled(btAddressString, true);			
			}
			else {
				if (command == COMMAND_DISABLED) {
					this.deviceManager.setDeviceEnabled(btAddressString, false);			
					
				}
				else {
					Log.e(TAG, "performServiceCommand() Command not recognized");
				}
			}
		}    
		
		// Now send the updated device list to the activity so it can update it's listview
		sendDeviceList();		
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());
	
}
