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

package com.t2.biofeedback.device.zephyr;

import java.util.ArrayList;
import java.util.BitSet;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.t2.biofeedback.Constants;
import com.t2.biofeedback.Util;
import com.t2.biofeedback.device.BioFeedbackDevice;

/**
 * Encapsulates methods necessary to communicate with a Bluetooth Zephyr device
 * 
 * @author scott.coleman
 * 
 * Message format:
 * 
 *  SPINE HEADER
 *  desc: | Vers:Ext:Type | GroupId | SourceId | DestId | Seq#    | TotalFrag   | Frag #|
 *  size: | 2:1:5         | 8       | 16       | 16     | 8       | 8           | 8     |
 *  value:| C4            | 0xAB    | 0xfff1   | 0      | 0       | 1           | 1     |
 * 
 * SPINE MESSAGE
 *  desc: | Func | Sensor | Feat| Feat | Feat     | Bat Level | Heart Rate | Resp Rate | Skin Temp | Label  |
 *  desc: | Code | Code   | Cnt | Code | Bitmask  | Value     | Value      | Value     | Value     | Length |
 *  size: |  8   |  8     |  8  | 8    | 8        | 32        | 32         | 32        | 32        | 8      |
 *  value:|  9   |  C     |  4  | 9    | 0x0f     | xxxxxxxx  | xxxxxxxx   | xxxxxxxx  | xxxxxxxx  | 0      |
 * 
 *
 */
public abstract class ZephyrDevice extends BioFeedbackDevice {
	private static final String TAG = Constants.TAG;
	// Change this to reflect the source ID of this sensor
	// This is the is that the server will use to recognize this sensor
	private static final byte SOURCE_ID_HIGH = (byte) 0xff;
	private static final byte SOURCE_ID_LOW = (byte) 0xf1;	
	
	boolean mDebug = true;	
	long timout;
	static ZephyrDevice instance;
	
	ZephyrDevice(ArrayList<Messenger> serverListeners)
	{
		instance = this;
		this.mServerListeners = serverListeners;
	}
	

	/* (non-Javadoc)
	 * @see com.t2.biofeedback.device.BioFeedbackDevice#onDeviceConnected()
	 */
	@Override
	protected void onDeviceConnected() {
		super.onDeviceConnected();
	    Handler handler = new Handler(); 
	    handler.postDelayed(new Runnable() { 
	         public void run() { 
					Log.v(TAG, "Tell the device to start sending periodic data.");
					ZephyrMessage m = new ZephyrMessage(
							0xA4,
							new byte[] {
								(byte) 0,			// Link timeout LSB
								(byte) 0,			// Link Timeout MSB
								(byte) 0x10,		// LifeSign LSB 10 sec			
								(byte) 0x27,		// Lifesign MSB 
//								(byte) 0x38,		// 1 sec			
//								(byte) 0x03,
							},
							ZephyrMessage.ETX
					);
					instance.write(m);		

				    Handler handler1 = new Handler(); 
				    handler1.postDelayed(new Runnable() { 
				         public void run() { 
								
								
								// Tell the device to return periodic data.
				        	 ZephyrMessage m = new ZephyrMessage(
										0x14,
										new byte[] {
											0x01		// 1 = send at 1Hz, 0 = don't send
										},
										ZephyrMessage.ETX
								);
						    	instance.write(m);				
				        	 
				        	 
				         } 
				    }, 1000);				
					

	        	 
	         } 
	    }, 1000);				
		
		
		
//		AsyncTask<Integer, Void, Void> asyncTask = new AsyncTask<Integer, Void, Void>() {
//
//			@Override
//			protected Void doInBackground(Integer... integers) {
//				
//				
//				return null;
//			}
//
//			@Override
//			protected void onPostExecute(Void aVoid) {
//			}
//		};
//
//		asyncTask.execute(0, 0);
//		
		

	}

	/* (non-Javadoc)
	 * @see com.t2.biofeedback.device.SerialBTDevice#onBeforeConnectionClosed()
	 */
	@Override
	protected void onBeforeConnectionClosed() {
		Log.v(TAG, "Tell the device to stop sending periodic data.");
		ZephyrMessage m = new ZephyrMessage(
				0x14,
				new byte[] {
					0x00
				},
				ZephyrMessage.ETX
		);
    	this.write(m);
	}
	
	/* (non-Javadoc)
	 * @see com.t2.biofeedback.device.SerialBTDevice#onBytesReceived(byte[])
	 * 
	 * Parses the received bytes into a Zephyr message and forwards them
	 */
	@Override
	protected void onBytesReceived(byte[] bytes) {
		
		if (bytes[1]== 0x20)
		{
			// For now we'll only send data messages
			this.onMessageReceived(ZephyrMessage.parse(bytes));
		}
		if (bytes[1]== 0x14)
		{
			Log.v(TAG, " *********** Received response to start sending");
		}
		if (bytes[1]== 0xa4)
		{
			Log.v(TAG, " *********** Received response to Timeout");
		}
		if (bytes[1]== 0x23)
		{
			Log.v(TAG, "ZephyrHeartbeat ");
		}
	}
	
	/**
	 * Receives parsed message from Zephyr device. Formats it to look like
	 * a Spine message then sends it to the Spine server.
	 *  
	 * @param msg	Message to send to Spine server
	 */
	private void onMessageReceived(ZephyrMessage msg) {
		if(!msg.validPayload) {
			return;
		}

		if (mDebug)	Util.logHexByteString(TAG,   msg.payload);
		
		// Use this to send the message via the normal SPINE mechanism
		
		// We need to build a SPINE-style message

		
		final int ZEPHYR_FUNCT_CODE							= 0x09;
		final int ZEPHER_FUNCT_TYPE							= 1;   //(Raw data)
		final int ZEPHYR_SENSOR_CODE 						= 0x0C;
		
		final int SPINE_HEADER_SIZE = 9;
		final int ZEPHYR_MSG_SIZE = 22;
			
		byte[] zepherMessage = new byte[ZEPHYR_MSG_SIZE + SPINE_HEADER_SIZE];

		// First add spine header
		int i = 0;
		// Header
		zepherMessage[i++] = (byte) 0xc4; 
		zepherMessage[i++] = (byte) 0xab;
		zepherMessage[i++] = SOURCE_ID_HIGH;
		zepherMessage[i++] = SOURCE_ID_LOW;
		zepherMessage[i++] = (byte) 0x00;
		zepherMessage[i++] = (byte) 0x00;
		zepherMessage[i++] = (byte) 0x00;
		zepherMessage[i++] = (byte) 0x01;
		zepherMessage[i++] = (byte) 0x01;
		
		// MEssage
		zepherMessage[i++] = ZEPHYR_FUNCT_CODE;
		zepherMessage[i++] = ZEPHYR_SENSOR_CODE;
		zepherMessage[i++] = 1;  //1 feature 

		zepherMessage[i++] = ZEPHER_FUNCT_TYPE;   
		zepherMessage[i++] = 0x0f;   				// Bitmask
		
		zepherMessage[i++] = msg.raw[53];
		zepherMessage[i++] = msg.raw[54];
		zepherMessage[i++] = 0;
		zepherMessage[i++] = 0;
		
		zepherMessage[i++] = 0;
		zepherMessage[i++] = 0;
		zepherMessage[i++] = msg.payload[10];
		zepherMessage[i++] = msg.payload[9];
		
		zepherMessage[i++] = 0;
		zepherMessage[i++] = 0;
		zepherMessage[i++] = msg.payload[12];
		zepherMessage[i++] = msg.payload[11];
		
		zepherMessage[i++] = 0;
		zepherMessage[i++] = 0;
		zepherMessage[i++] = msg.payload[14];
		zepherMessage[i++] = msg.payload[13];

		zepherMessage[i++] = 0; // No label
		
//			this.onSpineMessage(zepherMessage);		// Old method of sending data to server
		if (mServerListeners != null)
		{		
//				if (mDebug)	Log.i(TAG, "1");

	        for (i = mServerListeners.size()-1; i >= 0; i--) {
//					if (mDebug)	Log.i(TAG, "2");
		        try {
					Bundle b = new Bundle();
					b.putByteArray("message", zepherMessage);
		
		            Message msg1 = Message.obtain(null, MSG_SET_ARRAY_VALUE);
		            msg1.setData(b);
		            mServerListeners.get(i).send(msg1);
		
		        } catch (RemoteException e) {
		            // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
		        	mServerListeners.remove(i);
		        }
	        }			
		}	
		else {
			Log.i(TAG, "** No Listeners ** " );
		}
		
	}
	
	private void write(ZephyrMessage msg) {
		this.write(msg.getBytes());
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
}