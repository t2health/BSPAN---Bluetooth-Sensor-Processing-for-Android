package com.t2.biofeedback.device;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Set;
import java.util.UUID;

import com.t2.biofeedback.BioFeedbackService;
import com.t2.biofeedback.Constants;
import com.t2.biofeedback.Util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;


public abstract class SerialBTDevice {
	private static final String TAG = Constants.TAG;
	
	private static final int MSG_CONNECTED = 1;
	private static final int MSG_CONNECTION_LOST = 2;
	private static final int MSG_MANAGE_SOCKET = 3;
	private static final int MSG_BYTES_RECEIVED = 4;
	
	public static final UUID UUID_RFCOMM_GENERIC = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private final BluetoothAdapter adapter;
//	private final BluetoothDevice device;
	private BluetoothDevice device;
	private BluetoothSocket socket;
	private ConnectedThread connectedThread;
	private DeviceConnectionListener connectionListener;
	
	private boolean reconnectOnConnectionLost = true;
	
	private ArrayList<Byte[]> writeQueue = new ArrayList<Byte[]>();
	
	private Handler threadHandler;

	private ConnectThread connectThread;
	protected BioFeedbackService mBiofeedbackService;	
	
	public void setDevice(String BH_ADDRESS)
	{
		if(!BluetoothAdapter.checkBluetoothAddress(BH_ADDRESS)) {
			this.device = null;
			throw new InvalidBluetoothAddressException("\""+ this.getDeviceAddress() +"\" is an invalid bluetooth device address.");
		}
		
		// Get an Android device based on the sub-class address
		this.device = this.adapter.getRemoteDevice(BH_ADDRESS);
		
	}
	
	public void setBioFeedbackService(BioFeedbackService biofeedbackService)
	{
		mBiofeedbackService = biofeedbackService;
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
						onBytesReceived(msg.getData().getByteArray("bytes"));
						break;
				}
			}
		};
	}
	
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
	
	public void close() {
		if(!isInitialized()) {
			return;
		}
		
		this.close(true);
	}
	
	private void close(boolean report) {
		if(!isInitialized()) {
			return;
		}
		
//		Log.v(TAG, "BT close");
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
//		Log.v(TAG, "Device connecting.");
		if(connectionListener != null) {
			connectionListener.onDeviceConnecting(this);
		}
	}
	
	private void deviceConnected() {
//		Log.v(TAG, "Device connected.");
		if(connectionListener != null) {
			connectionListener.onDeviceConnected(this);
		}
		this.onDeviceConnected();
	}
	
	private void beforeDeviceClosed() {
		if(this.isConencted()) {
//			Log.v(TAG, "Run before device disconnected.");
			if(connectionListener != null) {
				connectionListener.onBeforeDeviceClosed(this);
			}
			this.onBeforeConnectionClosed();
		}
	}
	
	private void deviceClosed() {
//		Log.v(TAG, "Device disconnected.");
		if(connectionListener != null) {
			connectionListener.onDeviceClosed(this);
		}
	}
	
	private void deviceConnectionLost() {
//		Log.v(TAG, "Lost the connection to the device.");
		if(connectionListener != null) {
			connectionListener.onDeviceConnectionLost(this);
		}
		
		// Try to reconnect.
		if(this.isBonded() && reconnectOnConnectionLost) {
//			Log.v(TAG, "Trying to reconnect to the device.");
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
					tmpSocket.connect();
					//threadHandler.sendEmptyMessage(MSG_CONNECTED);
					break;
				} catch(IOException connectException) {
					try {
						tmpSocket.close();
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
					bytes = this.inputStream.read(buffer);
					if(!this.isConnected) {
						threadHandler.sendEmptyMessage(MSG_CONNECTED);
						pushQueuedBytes();
					}
					this.isConnected = true;
					
					if(bytes > 0) {
						byte[] newBytes = new byte[bytes];
						for(int i = 0; i < bytes; i++) {
							newBytes[i] = buffer[i];
						}
						
						// Send the message to the server via the more direct route
						// Note this used to send the message to the handler here in this file.
						// Now it sends directly to the server application - much better performance
						if (mBiofeedbackService != null)
						{
							mBiofeedbackService.sendRawMessage(newBytes);
						}
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
				Util.logHexByteString(TAG, "Writing to BT:", bytes);
				
				
				
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
		/**
		 * 
		 */
		private static final long serialVersionUID = -5585726446729463776L;

		public DeviceNotBondedException(String msg) {
			super(msg);
		}
	}
	
	public class InvalidBluetoothAddressException extends RuntimeException {
		/**
		 * 
		 */
		private static final long serialVersionUID = -30604768627158724L;

		public InvalidBluetoothAddressException(String msg) {
			super(msg);
		}
	}
	
	public interface DeviceConnectionListener {
		public void onDeviceConnecting(SerialBTDevice d);
		public void onDeviceConnected(SerialBTDevice d);
		public void onDeviceClosed(SerialBTDevice d);
		public void onBeforeDeviceClosed(SerialBTDevice serialBTDevice);
		public void onDeviceConnectionLost(SerialBTDevice d);
	}
}
