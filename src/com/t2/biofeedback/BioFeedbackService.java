package com.t2.biofeedback;

import com.t2.biofeedback.device.AverageDeviceValue;
import com.t2.biofeedback.device.BioFeedbackDevice;
import com.t2.biofeedback.device.BioFeedbackDevice.OnSpineMessageListener;
import com.t2.biofeedback.device.SerialBTDevice;

import com.t2.biofeedback.device.BioFeedbackDevice.UnsupportedCapabilityException;
import com.t2.biofeedback.device.SerialBTDevice.DeviceConnectionListener;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class BioFeedbackService extends Service implements DeviceConnectionListener, OnSpineMessageListener {
	private static final String TAG = Constants.TAG;
	
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
		}
	}
	
	public static final String ACTION_DATA_BROADCAST = "com.t2.biofeedback.service.data.BROADCAST";
	public static final String ACTION_STATUS_BROADCAST = "com.t2.biofeedback.service.status.BROADCAST";
	public static final String ACTION_SERVICE_START = "com.t2.biofeedback.service.START";
	public static final String ACTION_SERVICE_STOP = "com.t2.biofeedback.service.STOP";
	
	public static final String EXTRA_ADDRESS = "address";
	public static final String EXTRA_NAME = "name";
	public static final String EXTRA_MESSAGE_TYPE = "messageType";
	public static final String EXTRA_MESSAGE_ID = "messageId";
	public static final String EXTRA_MESSAGE_VALUE = "messageValue";
	public static final String EXTRA_TIMESTAMP = "timestamp";
	
	public static final String EXTRA_MSG_BYTES = "msgBytes";

	
	public static final String EXTRA_AVERAGE_VALUE = "avgValue";
	public static final String EXTRA_CURRENT_VALUE = "currentValue";
	public static final String EXTRA_SAMPLE_VALUES = "sampleValue";
	
	public static final String EXTRA_CURRENT_TIMESTAMP = "currentTimestamp";
	public static final String EXTRA_SAMPLE_TIMESTAMPS = "sampleTimestamps";
	
	private DeviceManager deviceManager;
	private ManageDeviceThread manageDeviceThread;
	
	private Handler manageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mangeDevices();
		}
	};

	private void mangeDevices() {
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
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		this.startService();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		this.stopService();
	}
	
	
	private void startService() {
		this.deviceManager = DeviceManager.getInstance(this.getBaseContext());
		this.manageDeviceThread = new ManageDeviceThread();
		this.manageDeviceThread.start();
	}
	
	private void stopService() {
		this.manageDeviceThread.setRun(false);
		this.deviceManager.closeAll();
	}
	
	
	private void setListeners(BioFeedbackDevice device) {
		try {
			device.setOnSpineMessageListener(this);
		} catch (UnsupportedCapabilityException e) {}		
		
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
	
	private Intent getDeviceBroadcastIntent(BioFeedbackDevice d, String messageType, String messageId,
			AverageDeviceValue dv) {
		
		Intent i = getStatusBroadcastIntent(d, messageType, messageId, null);
		i.setAction(ACTION_DATA_BROADCAST);
		i.putExtra(EXTRA_AVERAGE_VALUE, dv.avgValue);
		i.putExtra(EXTRA_CURRENT_VALUE, dv.currentValue);
		i.putExtra(EXTRA_SAMPLE_VALUES, dv.sampleValues);
		
		i.putExtra(EXTRA_CURRENT_TIMESTAMP, dv.currentTimetamp);
		i.putExtra(EXTRA_SAMPLE_TIMESTAMPS, dv.sampleTimestamps);
		
		return i;
	}

	private Intent getDeviceBroadcastIntent(BioFeedbackDevice d, String messageType, String messageId,
			byte[] msgBytes) {
		
		Intent i = getStatusBroadcastIntent(d, messageType, messageId, null);
		i.setAction(ACTION_DATA_BROADCAST);
		i.putExtra(EXTRA_MSG_BYTES, msgBytes);
		
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
		this.sendBroadcast(
				getStatusBroadcastIntent(d, BroadcastMessage.Type.STATUS, "CONN_CONNECTED", null)
		);
		
		if(this.deviceManager.isAnyDeviceConnected()) {
			this.sendBroadcast(
				getStatusBroadcastIntent(null, BroadcastMessage.Type.STATUS, "CONN_ANY_CONNECTED", null)
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


	
	
	private class ManageDeviceThread extends Thread {
		boolean run = true;
		
		@Override
		public void run() {
			this.run = true;
			while(this.run) {
//				Log.v(TAG, "Managing supported devices.");
				manageHandler.sendEmptyMessage(1);
				
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
				}
			}
		}
		
		public void setRun(boolean b) {
			this.run = false;
		}
	}




	@Override
	public void onSpineMessage(byte[] message) {
		// TODO Auto-generated method stub
		// Send message here
		
	}
}
