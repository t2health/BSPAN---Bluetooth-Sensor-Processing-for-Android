package com.t2.biofeedback;

import java.util.ArrayList;

import com.t2.biofeedback.device.AverageDeviceValue;
import com.t2.biofeedback.device.BioFeedbackDevice;
import com.t2.biofeedback.device.BioFeedbackDevice.OnDeviceDataMessageListener;
import com.t2.biofeedback.device.SerialBTDevice;

import com.t2.biofeedback.device.BioFeedbackDevice.UnsupportedCapabilityException;
import com.t2.biofeedback.device.SerialBTDevice.DeviceConnectionListener;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class BioFeedbackService extends Service implements DeviceConnectionListener, OnDeviceDataMessageListener {
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
	private static final int MSG_SET_ARRAY_VALUE = 5;
	
	private DeviceManager deviceManager;
	private ManageDeviceThread manageDeviceThread;
	
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
		Log.i(TAG, "Binding BiofeedbackService");
		
        return mMessenger.getBinder();
		
//		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		this.startService();
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		this.stopService();
		mNM.cancelAll();
//		mNM.cancel("started");
		
	}
	
	
	private void startService() {
		Log.i(TAG,"Starting Service");
		this.deviceManager = DeviceManager.getInstance(this.getBaseContext(), this);
		this.manageDeviceThread = new ManageDeviceThread();
		this.manageDeviceThread.start();
	}
	
	private void stopService() {
		Log.i(TAG,"Stopping Service");
		this.manageDeviceThread.setRun(false);
		this.deviceManager.closeAll();
	}
	
	
	private void setListeners(BioFeedbackDevice device) {
		try {
			device.setOnDeviceDataMessageListener(this, device);
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
	
	private Intent getSpineBroadcastIntent(BioFeedbackDevice d, String messageType, String messageId,
			byte[] msgBytes) {
		
		Intent i = getStatusBroadcastIntent(d, messageType, messageId, null);
		i.setAction(ACTION_SPINE_DATA_BROADCAST);
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
	public void onSpineMessage(BioFeedbackDevice d, byte[] message) {
		
		Intent i = getStatusBroadcastIntent(d, BroadcastMessage.Type.DATA,  BroadcastMessage.Id.SPINE_MESSAGE, null);
		i.setAction(ACTION_SPINE_DATA_BROADCAST);
		i.putExtra(EXTRA_MSG_BYTES, message);
		i.putExtra(EXTRA_TIMESTAMP, System.currentTimeMillis());
		
		
		this.sendBroadcast(i);

		
		// Send a test message to test the server listener		
//		if (mServerListener != null)
//		{
//			try {
//				mServerListener.send(Message.obtain(null, 10, 25, 0));
//			} catch (RemoteException e) {
//				Log.e(TAG, "the server listener is dead");
//				mServerListener = null;
//				e.printStackTrace();
//			}
//		}
		
	}

	public void sendRawMessage(byte[] message)
	{
		Log.i(TAG, "Weeeee");
        for (int i = mServerListeners.size()-1; i >= 0; i--) {
	        try {
				Bundle b = new Bundle();
				b.putByteArray("message", message);
	
	            Message msg = Message.obtain(null, MSG_SET_ARRAY_VALUE);
	            msg.setData(b);
	            mServerListeners.get(i).send(msg);
	
	        } catch (RemoteException e) {
	            // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
	        	mServerListeners.remove(i);
	        }
        }						
		
		
	}
	
	@Override
	public void onDeviceMessage(BioFeedbackDevice bioFeedbackDevice,
			byte[] message) {

		Intent i = getStatusBroadcastIntent(bioFeedbackDevice, BroadcastMessage.Type.DATA,  BroadcastMessage.Id.DATA_MESSAGE, null);
		i.setAction(ACTION_ZEPHYR_DATA_BROADCAST);
		i.putExtra(EXTRA_MSG_BYTES, message);
		i.putExtra(EXTRA_TIMESTAMP, System.currentTimeMillis());
		
		this.sendBroadcast(i);

	}
	
    /** For showing and hiding our notification. */
    NotificationManager mNM;
    /** Keeps track of all current registered clients. */
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
     * Command to the service to unregister a client, ot stop receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client as previously given with MSG_REGISTER_CLIENT.
     */
    static final int MSG_UNREGISTER_CLIENT = 2;

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
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                	mServerListeners.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                	mServerListeners.remove(msg.replyTo);
                    break;
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
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());





	
}
