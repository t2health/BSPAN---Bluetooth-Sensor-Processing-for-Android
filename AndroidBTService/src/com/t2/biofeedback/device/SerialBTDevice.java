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

package com.t2.biofeedback.device;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Set;
import java.util.UUID;

import com.t2.biofeedback.Constants;
import com.t2.biofeedback.Util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;


/**
 * Base class of all Bluetooth based sensor devices
 * 	Each SerialBTDevice has two threads:
 * 		A connect thread (ConnectThread) used to establish connection to a device.	
 * 		A connected thread (ConnectedThread) used to send/receive data to/from the connected device
 * 
 * @author scott.coleman
 *
 */
/**
 * @author scott.coleman
 *
 */
public abstract class SerialBTDevice {
	private static final String TAG = Constants.TAG;
	
	private static final int MSG_CONNECTED = 1;
	private static final int MSG_CONNECTION_LOST = 2;
	private static final int MSG_MANAGE_SOCKET = 3;
	private static final int MSG_BYTES_RECEIVED = 4;
	protected static final int MSG_SET_ARRAY_VALUE = 5;
	private static final int MSG_SET_SPINE_ARRAY_VALUE = 6;
	private static final int MSG_SET_ZEPHYR_ARRAY_VALUE = 7;
	
	public static final UUID UUID_RFCOMM_GENERIC = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private final BluetoothAdapter adapter;
	private BluetoothDevice device;
	private BluetoothSocket socket;

	/**
	 * Thread used to communicate messages to/from device
	 */
	private ConnectedThread connectedThread;

	/**
	 * Thread used to establish connection to device
	 */
	private ConnectThread connectThread;

	/**
	 * Listener to send connect/disconnect messages to
	 */
	private DeviceConnectionListener connectionListener;
	
	/**
	 * Whether or not to reconnect automatically when a connection is lost
	 */
	private boolean reconnectOnConnectionLost = true;
	
	/**
	 * Queue of bytes for ConnectedThread to write to device
	 */
	private ArrayList<Byte[]> writeQueue = new ArrayList<Byte[]>();
	
	/**
	 * Handler used to send messages from the ConnectedThread to the main class
	 */
	private Handler threadHandler;

	/**
	 * List of devices to notify when a message is received from a device
	 */
	protected ArrayList<Messenger> mServerListeners;	

	/**
	 * Used to discern what type of class this is
	 */
	private SerialBTDevice me;
	
	/**
	 * Sets this SerialBTDevice to the corresponding OS Bluetooth device which
	 * matches the given address
	 * 
	 * @param BH_ADDRESS	Address of device to set this class to
	 */
	public void setDevice(String BH_ADDRESS)
	{
		if(!BluetoothAdapter.checkBluetoothAddress(BH_ADDRESS)) {
			this.device = null;
			throw new InvalidBluetoothAddressException("\""+ this.getDeviceAddress() +"\" is an invalid bluetooth device address.");
		}
		
		// Get an Android device based on the sub-class address
		this.device = this.adapter.getRemoteDevice(BH_ADDRESS);
		
	}
	
	/**
	 * Sets server listeners which listen for data from this device
	 * @param serverListeners	Array of listeners to send any received messages to
	 */
	public void setServerListeners(ArrayList<Messenger> serverListeners)
	{
		mServerListeners = serverListeners;
	}
	
	public SerialBTDevice() {
		this.adapter = BluetoothAdapter.getDefaultAdapter();
		
		if(!BluetoothAdapter.checkBluetoothAddress(this.getDeviceAddress())) {
			this.device = null;
			throw new InvalidBluetoothAddressException("\""+ this.getDeviceAddress() +"\" is an invalid bluetooth device address.");
		}
		
		// Get an Android device based on the sub-class address
		this.device = this.adapter.getRemoteDevice(this.getDeviceAddress());
		Set t = this.adapter.getBondedDevices();
		
		me = this;
		
		// TODO: change all messages to use direct connect (via service MEssenger) instead of listeners
		// This is not too important because status messages dont' happen all that much, so the load
		// of sending them via broadcast intents is not too bad.
		//
		threadHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what) {
					case MSG_CONNECTED:
						deviceConnected();
						break;
					case MSG_CONNECTION_LOST:
						deviceConnectionLost();
						break;
					case MSG_MANAGE_SOCKET:
						manageConnection(socket);
						break;
					case MSG_BYTES_RECEIVED:
						// Bytes received from the ConnectedThread (from a sensor device)
						onBytesReceived(msg.getData().getByteArray("bytes"));
						break;
				}
			}
		};
	} // End public SerialBTDevice()
	
	public boolean isConnecting() {
		if(!this.isConencted()) {
			if(this.connectThread != null && 
					this.connectThread.isRunning()) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isConencted() {
		if(this.connectedThread == null) {
			return false;
		}
		return this.connectedThread.isRunning();
	}
	
	public BluetoothDevice getDevice() {
		return this.device;
	}
	
	public String getAddress() {
		return this.device.getAddress();
	}
	
	public String getName() {
		return this.device.getName();
	}
	
	public boolean isInitialized() {
		return this.device != null;
	}
	
	public void setDeviceConnectionListener(DeviceConnectionListener t) {
		this.connectionListener = t;
	}
	
	public boolean isBonded() {
		if(!isInitialized()) {
			return false;
		}
		return this.device.getBondState() == BluetoothDevice.BOND_BONDED;
	}
	
	/**
	 * Begin process of connecting to Bluetooth device
	 */
	public void connect() {
		if(!isInitialized()) {
			return;
		}
		
		if(this.isConencted() || this.isConnecting()) {
			return;
		}
		
		this.close(false);
		
		// See if this device is paired.
		if(!this.isBonded()) {
			throw new DeviceNotBondedException("The bluetooth device "+ this.device.getAddress() +" is not bonded (paired).");
		}
		
		// Notify that we have begun the connecting process.
		this.deviceConnecting();
		
//		Log.v(TAG, "BT connect");
		// Begin connecting.
		this.connectThread = new ConnectThread(this.device);
		this.connectThread.start();
	}
	
	/**
	 * Close connection to device
	 */
	public void close() {
		if(!isInitialized()) {
			return;
		}
		
		this.close(true);
	}
	
	/**
	 * Close connection to device
	 * 
	 * @param report	Whether of not to call beforeDeviceClosed and deviceClosed
	 */
	private void close(boolean report) {
		if(!isInitialized()) {
			return;
		}
		
		Log.v(TAG, "BT close");
		if(report) {
			this.beforeDeviceClosed();
		}
		
		if(this.connectedThread != null) {
			this.connectedThread.cancel();
			this.connectedThread.interrupt();
			this.connectedThread = null;
		}
		
		if(this.connectThread != null) {
			this.connectThread.cancel();
			this.connectThread.interrupt();
			this.connectThread = null;
		}
		
		if(report) {
			this.deviceClosed();
		}
	}
	
	private void deviceConnecting() {

		Log.v(TAG, "Device (" + this.getName() + ") connecting.");
		if(connectionListener != null) {
			connectionListener.onDeviceConnecting(this);
		}
	}
	
	private void deviceConnected() {
		Log.v(TAG, "Device (" + this.getName() + ") connected.");
		if(connectionListener != null) {
			connectionListener.onDeviceConnected(this);
		}
		this.onDeviceConnected();
	}
	
	private void beforeDeviceClosed() {
		Log.v(TAG, "beforeDeviceClosed() - this = " + this);
		if(this.isConencted()) {
			Log.v(TAG, "Run before device disconnected.");
			if(connectionListener != null) {
				connectionListener.onBeforeDeviceClosed(this);
			}
			this.onBeforeConnectionClosed();
		}
	}
	
	private void deviceClosed() {
		Log.v(TAG, "Device (" + this.getName() + ") disconnected.");
		if(connectionListener != null) {
			connectionListener.onDeviceClosed(this);
		}
	}
	
	private void deviceConnectionLost() {
		Log.v(TAG, "Lost the connection to the device (" + this.getName() + ")");
		if(connectionListener != null) {
			connectionListener.onDeviceConnectionLost(this);
		}
		
		// Try to reconnect.
		if(this.isBonded() && reconnectOnConnectionLost) {
			Log.v(TAG, "Trying to reconnect to the device (" + this.getName() + ")");
			this.connect();
		}
	}
	
	public abstract String getDeviceAddress();
	protected abstract void onBytesReceived(byte[] bytes);
	protected abstract void onDeviceConnected();
	protected abstract void onBeforeConnectionClosed();
	protected abstract void onConnectedClosed();
	
	private void manageConnection(BluetoothSocket socket) {
		this.connectedThread = new ConnectedThread(socket);
		this.connectedThread.start();
	}
	
	private void pushQueuedBytes() {
		// if there are queued Byte[], write them.
		while(writeQueue.size() > 0) {
			Byte[] qBytes = writeQueue.get(0);
			byte[] bytes = new byte[qBytes.length];
			
			for(int i = 0; i < qBytes.length; i++) {
				bytes[i] = qBytes[i];
			}
			
			connectedThread.write(bytes);
			writeQueue.remove(0);
		}
	}
	
	/**
	 * Immediately write the bytes to the device
	 * 
	 * @param bytes	Bytes to send to device
	 */
	protected void write(byte[] bytes) {
		// Immediately write the bytes.
		if(this.connectedThread != null && this.connectedThread.isRunning()) {
			this.connectedThread.write(bytes);
			return;
		}
		
		// Queue the bytes and reconnect.
		Byte[] qBytes = new Byte[bytes.length];
		for(int i = 0; i < bytes.length; i++) {
			qBytes[i] = bytes[i];
		}
		this.writeQueue.add(qBytes);
	}
	
	
	
	public static BitSet byteArrayToBitSet(byte[] bytes) {
		BitSet bits = new BitSet(bytes.length * 8);
		for (int i = 0; i < bytes.length * 8; i++) {
			if ((bytes[bytes.length - i / 8 - 1] & (1 << (i % 8))) > 0) {
				bits.set(i);
			}
		}
	    return bits;
	}
	
	public static byte[] bitSetToByteArray(BitSet bs) {
		byte[] bytes = new byte[(int) Math.ceil(bs.size() / 8)];
		for(int i = 0; i < bs.size(); i++) {
			if(bs.get(i) == true) {
				bytes[i / 8] |= 1 << i;
			}
		}
		return bytes;
	}
	
	public static int byteArrayToInt(byte[] bytes) {
		int val = 0;
		
		for(int i = 0; i < bytes.length; i++) {
			int n = (bytes[i] < 0 ? (int)bytes[i] + 256 : (int)bytes[i]) << (8 * i);
			val += n;
		}
		
		return val;
	}
	
	public static byte[] intToByteArray(int value, int byteArrayLength) {
		byte[] b = new byte[byteArrayLength];
		for (int i = 0; i < b.length; i++) {
			int offset = (b.length - 1 - i) * 8;
			b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return b;
    }
	
	private class ConnectThread extends Thread {
		private final BluetoothDevice device;
		private boolean isRunning = false;
		private boolean cancelled = false;
		
		
		public ConnectThread(BluetoothDevice device) {
			this.device = device;
		}

		@Override
		public void run() {
//			adapter.cancelDiscovery();
			isRunning = true;
			cancelled = false;
			
			int loopCount = 0;
			BluetoothSocket tmpSocket = null;
			while(true) {
				tmpSocket = null;
				
				// Break out if this was cancelled.
				if(cancelled) {
					break;
				}
				
				// Get the device socket.
				try {
					tmpSocket = device.createRfcommSocketToServiceRecord(UUID_RFCOMM_GENERIC);
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				// Try to connect to the socket.
				try {
					if (tmpSocket != null) tmpSocket.connect();
					//threadHandler.sendEmptyMessage(MSG_CONNECTED);
					break;
				} catch(IOException connectException) {
					try {
						if (tmpSocket != null) tmpSocket.close();
					} catch(IOException closeException) {
					}
				}
				
				// Wait to try to connect to the device again.
				int sleepSeconds = (int) (((loopCount / 5) + 1) * 1.5);
				sleepSeconds = (sleepSeconds > 10)? 10: sleepSeconds;
//				Log.v(TAG, "\tFailed to connect, will try again in "+ sleepSeconds +" sec.");
				try {
					Thread.sleep(sleepSeconds * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				loopCount++;
			}
			
			isRunning = false;
			
			// Tell the main thread to manage the socket.
			if(tmpSocket != null) {
				socket = tmpSocket;
				threadHandler.sendEmptyMessage(MSG_MANAGE_SOCKET);
			}
		}
		
		public void cancel() {
			this.cancelled  = true;
		}
		
		public boolean isRunning() {
			return this.isRunning;
		}
	}
	
	/**
	 * Main thread for an already connected device.
	 * Handles transmit and receive of data to/from device
	 * 
	 * @author scott.coleman
	 *
	 */
	private class ConnectedThread extends Thread {
		private final BluetoothSocket socket;
		private final InputStream inputStream;
		private final OutputStream outputStream;
		private boolean isRunning = false;
		private boolean isConnected = false;
		private boolean cancelled = false;
		
		public ConnectedThread(BluetoothSocket s) {
    		this.socket = s;
    		
    		InputStream tmpIs = null;
    		OutputStream tmpOs = null;
    		try {
    			tmpIs = socket.getInputStream();
    			tmpOs = socket.getOutputStream();
    		} catch(IOException e) {
    			
    		}
    		
    		inputStream = tmpIs;
    		outputStream = tmpOs;
    	}
		
		@Override
		public void run() {
			isRunning = true;
			byte[] buffer = new byte[1024];
			int bytes;
			
//			Log.v(TAG, "Receiving data from device.");
			while(true) {
				// Break out if this was cancelled.
				if(cancelled) {
					break;
				}
				
				try {
					
					// ** Note - we must send the connected message BEFORE the inputstream.read()
					// because some devices will block on .read() therefore the connected
					// message will never get sent
					
					if(!this.isConnected) {
						threadHandler.sendEmptyMessage(MSG_CONNECTED);
						pushQueuedBytes();
					}
					this.isConnected = true;

					
					bytes = this.inputStream.read(buffer);
					
					if(bytes > 0) {
						byte[] newBytes = new byte[bytes];
						for(int i = 0; i < bytes; i++) {
							newBytes[i] = buffer[i];
						}
						

						// TODO: decide whether to send only raw bytes (to the server) - Method A 
						// here or decode them in a sub-class then send only complete messages - Method B
						// The difference is that if we decode them here then we need to add 
						// one extra level of messaging (framing done in SpineDevice) : 

						// Method B:
						// 	msg(bytes) -> SerialBTDevice -> msg(SpineDevice.onBytesReceived()) -> Server

						// Method A:
						// Otherwise we send partial byte packets directly to the server and it will frame them:
						//  msg(bytes) -> Server.
						//    The drawback to this method is that the class SerialDTDevice needs to have
						//    knowledge  of what type of class it is (SpineDevice) and add that to the message
						//    so the server knows how to frame it
						
						
						// Send the message to the server via the OLD route
						// Call the bytes recieved handler.
						// This call is ran in the main thread.
						//						Bundle data = new Bundle();
						//						data.putByteArray("message", newBytes);
						//						Message msg = new Message();
						//						msg.what = MSG_BYTES_RECEIVED;
						//						msg.setData(data);
						//						threadHandler.sendMessage(msg);
						
						// -----------------
						// Method B:
						// -----------------
//						// Send the message to the server via the more direct route
//						// Note this used to send the message to the handler here in this file.
//						// Now it sends directly to the server application - much better performance
//						Log.i(TAG, "Weeeee");
//				        for (int i = mServerListeners.size()-1; i >= 0; i--) {
//					        try {
//								Bundle b = new Bundle();
//								b.putByteArray("message", newBytes);
//					
//					            Message msg = Message.obtain(null, MSG_SET_ARRAY_VALUE);
//					            msg.setData(b);
//					            mServerListeners.get(i).send(msg);
//					
//					        } catch (RemoteException e) {
//					            // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
//					        	mServerListeners.remove(i);
//					        }
//				        }	
//				        if (me instanceof SpineDevice)
//				        {
//				        	
//				        }

						// -----------------
						// Method A:
						// -----------------
						
				    	Bundle data = new Bundle();
						data.putByteArray("bytes", newBytes);
						Message msg = new Message();
						msg.what = MSG_BYTES_RECEIVED;
						msg.setData(data);
						threadHandler.sendMessage(msg);
					}
					
				// Lost the connection for some reason.
				} catch (IOException e1) {
					if(!this.cancelled) {
						threadHandler.sendEmptyMessage(MSG_CONNECTION_LOST);
					}
					break;
				}
			}
			
//			Log.v(TAG, "Stopped receiving data from device.");
			isRunning = false;
		}
		
		public void write(byte[] bytes) {
			try {
				Util.logHexByteString(TAG, "(" + getAddress() + ")" + "Writing to BT:", bytes);
				
				
				
				this.outputStream.write(bytes);
			} catch (IOException e) {
				Log.e(TAG, "**************Failed to write to device.");
			}
		}
    	
		public void cancel() {
			this.cancelled = true;
			
			try {
				this.socket.close();
			} catch (IOException e) {}
		}
		
		public boolean isRunning() {
			return this.isRunning;
		}
    }
	
	public class DeviceNotBondedException extends RuntimeException {

		private static final long serialVersionUID = -5585726446729463776L;

		public DeviceNotBondedException(String msg) {
			super(msg);
		}
	}
	
	public class InvalidBluetoothAddressException extends RuntimeException {

		private static final long serialVersionUID = -30604768627158724L;

		public InvalidBluetoothAddressException(String msg) {
			super(msg);
		}
	}
	
	public interface DeviceConnectionListener {
		/**
		 * Called when a connection is trying to be established with device
		 * @param d	Device in question
		 */
		public void onDeviceConnecting(SerialBTDevice d);

		/**
		 * Called when a connection has been established with device
		 * @param d	Device in question
		 */
		public void onDeviceConnected(SerialBTDevice d);

		/**
		 * Called when device connection is closed
		 * @param d
		 */
		public void onDeviceClosed(SerialBTDevice d);

		/**
		 * Called just before a device connection is closed
		 * @param serialBTDevice
		 */
		public void onBeforeDeviceClosed(SerialBTDevice serialBTDevice);
		
		/**
		 * Called when an existing connection to a device has been lost
		 * @param d
		 */
		public void onDeviceConnectionLost(SerialBTDevice d);
	}
}
