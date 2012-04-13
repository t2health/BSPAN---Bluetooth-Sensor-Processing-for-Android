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

package com.t2.biofeedback.device.shimmer;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.t2.biofeedback.Constants;
import com.t2.biofeedback.SPINESensorConstants;
import com.t2.biofeedback.device.BioFeedbackDevice;


/**
 * Encapsulates methods necessary to communicate with a Bluetooth Shimmer device
 *  Note that this is for devices that do NOT use the SPINE protocol
 *  This is for shimmer devices that have been programmed with the BoilerPlate firmware
 *  
 *  	
 * Message format:

 *  SPINE HEADER
 *  desc: | Vers:Ext:Type | GroupId | SourceId | DestId | Seq#    | TotalFrag   | Frag #|
 *  size: | 2:1:5         | 8       | 16       | 16     | 8       | 8           | 8     |
 *  value:| C4            | 0xAB    | 0xfff3   | 0      | 0       | 1           | 1     | *  
 *  
 * 
 *  SPINE MESSAGE
 *  Byte				Contents
 *  -----------------------------
 *  0 - 8			Spine Header (See Above
 *  9				SHIMMER_FUNCT_CODE (0x0B)         <-- Payload Starts Here
 *  10				shimmerSensorCode 				sensor type is set by start command
 *  11			    Packet Type		
 *  12 -13		    timestamp		
 *  14 -15		    Accel X		
 *  16 -17		    Accel Y		
 *  18 -19		    Accel Z		
 *  20 -21		    GSR (or EMG, or ECG_LALL) depending on which daughter board is installed/configured
 *  22 - 23			ECG_RALL (Only if ecg is configured
 *  		
 * 
 * @author scott.coleman
 *
 */
public abstract class ShimmerDevice extends BioFeedbackDevice{
	private static final String TAG = Constants.TAG;
	
	public static final byte SHIMMER_COMMAND_STOPPED = 0;
	public static final byte SHIMMER_COMMAND_RUNNING = 1;	// Defaults to 4Hz autoranging
	
	// NOTE IF ANY OF THESE CONSTANTS CHANGE THEN THE TABLE AT mCommandTableTranslation MUST CHANGE AS WELL!
	public static final byte SHIMMER_COMMAND_RUNNING_1000HZ_40K = 0 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_500HZ_40K = 1 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_250HZ_40K = 2 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_200HZ_40K = 3 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_166HZ_40K = 4 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_125HZ_40K = 5 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_100HZ_40K = 6 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_50HZ_40K = 7 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_10HZ_40K = 8 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_4HZ_40K = 9 + 2 ;	

	public static final byte SHIMMER_COMMAND_RUNNING_1000HZ_287K = 10 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_500HZ_287K = 11 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_250HZ_287K = 12 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_200HZ_287K = 13 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_166HZ_287K = 14 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_125HZ_287K = 15 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_100HZ_287K = 16 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_50HZ_287K = 17 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_10HZ_287K = 18 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_4HZ_287K = 19 + 2;	

	public static final byte SHIMMER_COMMAND_RUNNING_1000HZ_1M = 20 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_500HZ_1M = 21 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_250HZ_1M = 22 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_200HZ_1M = 23 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_166HZ_1M = 24 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_125HZ_1M = 25 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_100HZ_1M = 26 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_50HZ_1M = 27 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_10HZ_1M = 28 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_4HZ_1M = 29 + 2;	

	public static final byte SHIMMER_COMMAND_RUNNING_1000HZ_3M3 = 30 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_500HZ_3M3 = 31 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_250HZ_3M3 = 32 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_200HZ_3M3 = 33 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_166HZ_3M3 = 34 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_125HZ_3M3 = 35 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_100HZ_3M3 = 36 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_50HZ_3M3 = 37 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_10HZ_3M3 = 38 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_4HZ_3M3 = 39 + 2;	

	public static final byte SHIMMER_COMMAND_RUNNING_1000HZ_AUTORANGE = 40 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_500HZ_AUTORANGE = 41 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_250HZ_AUTORANGE = 42 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_200HZ_AUTORANGE = 43 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_166HZ_AUTORANGE = 44 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_125HZ_AUTORANGE = 45 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_100HZ_AUTORANGE = 46 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_50HZ_AUTORANGE = 47 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_10HZ_AUTORANGE = 48 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_4HZ_AUTORANGE = 49 + 2;	
	
	
	// NOTE - IF ANY OF THESE TABLE ENTRIES CHANGE THEN THE CONSTANTS AT SHIMMER_COMMAND_RUNNING
	// MUST CHANGE AS WELL!
	int[] mCommandTableTranslation = {

			0,
			0xf004, 
			
			0x0100,
			0x0200,
			0x0400,
			0x0500,
			0x0600,
			0x0800,
			0x0a00,
			0x1400,
			0x6400,
			0xf000,
			
			0x0101,
			0x0201,
			0x0401,
			0x0501,
			0x0601,
			0x0801,
			0x0a01,
			0x1401,
			0x6401,
			0xf001,
			
			0x0102,
			0x0202,
			0x0402,
			0x0502,
			0x0602,
			0x0802,
			0x0a02,
			0x1402,
			0x6402,
			0xf002,
			
			0x0103,
			0x0203,
			0x0403,
			0x0503,
			0x0603,
			0x0803,
			0x0a03,
			0x1403,
			0x6403,
			0xf003,
			
			0x0104,
			0x0204,
			0x0404,
			0x0504,
			0x0604,
			0x0804,
			0x0a04,
			0x1404,
			0x6404,
			0xf004,
			
	};
	
	
	
	
	
	// Change this to reflect the source ID of this sensor
	// This is the is that the server will use to recognize this sensor
	private static final byte SOURCE_ID_HIGH = (byte) 0xff;
	private static final byte SOURCE_ID_LOW = (byte) 0xf3;
	
	static final int DEFAULT_SHIMMER_FUNCT_CODE				= 0x0B;
	static final int DEFAULT_SHIMMER_SENSOR_CODE 			= 0x0E;
	static final int SPINE_HEADER_SIZE 						= 9;
	

	// These numbers all add up for the shimmer being programmed
	// to send only Accel and GSR data (plus timestamp)
	// as configured in the state machine below
	private static final int SHIMMER_PREMSG_SIZE  = 2;   	// 	2 bytes in front of every payload, 
															// SHIMMER_FUNCT_CODE, 
															// SHIMMER_SENSOR_CODE, 

	private static final int ECG_SENSOR_MSG_SIZE = 13;		
	private static final int SENSOR_MSG_SIZE = 11;		
	
	private static final int MAX_SENSOR_MSG_SIZE = 13;		
	private static final int MAX_SHIMMER_MSG_SIZE = MAX_SENSOR_MSG_SIZE + SHIMMER_PREMSG_SIZE;		
	
	private int mSensorMsgSize = SENSOR_MSG_SIZE;		// default to 11 bytes but may change to 11 if ecg
	
	private int state = STATE_OFF;
	
	private static final int STATE_OFF = 0;
	private static final int STATE_SET_SENSORS = 1;
	private static final int STATE_SET_SAMPLERATE = 2;
	private static final int STATE_SET_GSRRANGE = 3;
	private static final int STATE_STREAMING = 4;
	private static final int STATE_SENDING_PACKET = 5;
	
    public static final byte GSR_RANGE_HW_RES_40K    = 0;	//   10k– 56k
    public static final byte GSR_RANGE_HW_RES_287K   = 1;	//   56k-220k
    public static final byte GSR_RANGE_HW_RES_1M     = 2;	//	220k-680k
    public static final byte GSR_RANGE_HW_RES_3M3    = 3;	//	680k–4.7M		
    public static final byte GSR_RANGE_AUTORANGE     = 4;    
	
	
	private byte shimmerSensorCode = DEFAULT_SHIMMER_SENSOR_CODE;
	
	// Commands for configuring the Shimmer hardware
	private static final byte[] setSensorsCommand_GSR = new byte[] {
		ShimmerMessage.SETSENSORSCOMMAND,
		(byte) (ShimmerMessage.SENSOR0_SensorAccel | ShimmerMessage.SENSOR0_SensorGSR),
		0
	};
	private static final byte[] setSensorsCommand_EMG = new byte[] {
		ShimmerMessage.SETSENSORSCOMMAND,
		(byte) (ShimmerMessage.SENSOR0_SensorAccel | ShimmerMessage.SENSOR0_SensorEMG),
		0
	};

	private static final byte[] setSensorsCommand_ECG = new byte[] {
		ShimmerMessage.SETSENSORSCOMMAND,
		(byte) (ShimmerMessage.SENSOR0_SensorAccel | ShimmerMessage.SENSOR0_SensorECG),
		0
	};

	private static final byte[] setSensorsCommand_MAG = new byte[] {
		ShimmerMessage.SETSENSORSCOMMAND,
		(byte) (ShimmerMessage.SENSOR0_SensorAccel | ShimmerMessage.SENSOR0_SensorMag),
		0
	};

	private static final byte[] setSensorsCommand_STRAIN = new byte[] {
		ShimmerMessage.SETSENSORSCOMMAND,
		(byte) (ShimmerMessage.SENSOR0_SensorAccel),
		(byte) (ShimmerMessage.SENSOR1_SensorStrain)
	};

	private static byte[] setSampleRateCommand = new byte[] {
			ShimmerMessage.SETSAMPLINGRATECOMMAND,
//			ShimmerMessage.SAMPLING125HZ
			ShimmerMessage.SAMPLING4HZ
	};
	private static byte[] setGsrRangeCommand = new byte[] {
			ShimmerMessage.SETGSRRANGECOMMAND,
			ShimmerMessage.GSR_RANGE_AUTORANGE
	};
	
	
	
	// For testing purposes
	boolean mTestData = false;
	int mTestValue = 0xa55a;
	byte[] mTestDataBytes = {
			(byte) 0x00,   	// Packet type - data message
			(byte) 0x41,   	// timestamp low
			(byte) 0x95,   	// timestamp high
			(byte) 0x81,   	// accel x low
			(byte) 0x07,   	// accel x high	
			(byte) 0x69, 	// accel y low
			(byte) 0x04, 	// accel y high
			(byte) 0x62, 	// accel z low
			(byte) 0x07, 	// accel z high
			(byte) 0xd4, 	// gsr low
			(byte) 0xc2		// gsr high
			};	

	/**
	 * Message formatted according to the Shimmer Boilerplate specification
	 */
	byte[] mShimmerMessage;	
	byte[] mSensorBuffer = new byte[MAX_SENSOR_MSG_SIZE];
	int mSensorBufferIndex = 0;
	
	private int mMessageIndex = 0;
	

	/**
	 * @param serverListeners 	List of server listeners (used to transmit messages to the Spine server) 
	 */
	ShimmerDevice(ArrayList<Messenger> serverListeners)
	{
		this.mServerListeners = serverListeners;
	}


	/* (non-Javadoc)
	 * @see com.t2.biofeedback.device.BioFeedbackDevice#onDeviceConnected()
	 */
	protected void onDeviceConnected() 
	{
		Log.i(TAG, "Configurating Shimmer - Setting sensors to monitor");
		
//		state = STATE_SET_SENSORS;
//		this.write(setSensorsCommand);
	}

	/* (non-Javadoc)
	 * @see com.t2.biofeedback.device.SerialBTDevice#onBeforeConnectionClosed()
	 */
	protected void onBeforeConnectionClosed() 
	{
		Log.i(TAG, "Telling Shimer to stop streaming");
		stopStreaming();
	}
	
	protected void stopStreaming() {
		this.write(new byte[] {ShimmerMessage.STOPSTREAMINGCOMMAND});
		state = STATE_OFF;
	}
	
	
	@Override
	public ModelInfo getModelInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void finalize() throws Throwable {
		Log.i(TAG, "Telling Shimer to stop streaming");
		stopStreaming();
		super.finalize();
	}

	@Override
	public String getDeviceAddress() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setup(byte sensor, byte command) {


		
		if (command == SHIMMER_COMMAND_STOPPED) {
			Log.i(TAG, "Received command to STOP streaming sensor" + sensor);
			stopStreaming();
		}
		else  {
			Log.i(TAG, "Received command to START streaming sensor " + sensor);
			state = STATE_SET_SENSORS;
			
			// First we need to adjust the arrays setSampleRateCommand and setGsrRangeCommand
			// to set up parameters indirectly passed in the command byte
			try {
	
				if (command >= SHIMMER_COMMAND_RUNNING && command < mCommandTableTranslation.length) {
					byte range = (byte) (mCommandTableTranslation[command] & 0xff);
					byte rate =  (byte) ((mCommandTableTranslation[command] & 0xff00) >> 8);
					setSampleRateCommand[1] = rate;
					setGsrRangeCommand[1] = range;
					
				}
			} catch (IndexOutOfBoundsException e) {
			}
			

			
			
			
			
			
			switch (sensor) {
			case SPINESensorConstants.SHIMMER_GSR_SENSOR:
				this.write(setSensorsCommand_GSR);
				shimmerSensorCode = sensor;
				break;
			case SPINESensorConstants.SHIMMER_EMG_SENSOR:
				this.write(setSensorsCommand_EMG);
				shimmerSensorCode = sensor;
				break;
			case SPINESensorConstants.SHIMMER_ECG_SENSOR:
				mSensorMsgSize = ECG_SENSOR_MSG_SIZE;			// We only need to set it here, everywhere else it defaults				
				this.write(setSensorsCommand_ECG);
				shimmerSensorCode = sensor;
				break;
			case SPINESensorConstants.SHIMMER_MAG_SENSOR:
				this.write(setSensorsCommand_MAG);
				shimmerSensorCode = sensor;
				break;
			case SPINESensorConstants.SHIMMER_STRAIN_SENSOR:
				this.write(setSensorsCommand_STRAIN);
				shimmerSensorCode = sensor;
				break;
			}
			
			
			
		}
	}
	
	/**
	 * 
	 * Receives bytes from Shimmer device Decodes them and sends them to the 
	 * server
	 * @see com.t2.biofeedback.device.SerialBTDevice#onBytesReceived(byte[])
	 */
	protected void onBytesReceived(byte[] bytes) 
	{
	//	Util.logHexByteString(TAG, "Found message:", bytes);
		byte code = bytes[0];
		
//		switch (9999) {
		switch (state) {
		case STATE_SET_SENSORS:
			if (code == ShimmerMessage.ACKCOMMANDPROCESSED) {
				Log.i(TAG, "Configurating Shimmer - Setting sample rate");
				this.write(setSampleRateCommand);
				state = STATE_SET_SAMPLERATE;
			}
			break;
			
		case STATE_SET_SAMPLERATE:
			if (code == ShimmerMessage.ACKCOMMANDPROCESSED) {
				Log.i(TAG, "Configurating Shimmer - Setting gsr Range");
				this.write(setGsrRangeCommand);
				state = STATE_SET_GSRRANGE;
			}
			break;
			
		case STATE_SET_GSRRANGE:
			if (code == ShimmerMessage.ACKCOMMANDPROCESSED) {
				Log.i(TAG, "Telling Shimmer to start streaming V2.4");
				this.write(new byte[] {ShimmerMessage.STARTSTREAMINGCOMMAND});
				mSensorBufferIndex = 0;				
				state = STATE_STREAMING;
			}

		case STATE_STREAMING:
//			Util.logHexByteString(TAG, "Raw Packet:", bytes);

//			if (code == ShimmerMessage.DATAPACKET) {
//				Log.i(TAG, "                 !RESET!");
//				mSensorBufferIndex = 0;
//			}
			
			// If starting a new packet make sure it starts out with a 00 (DATAPACKET)
			// We should really be buffering the shimmer data more robustly for for now we'll live with a few sync errors
			if (mSensorBufferIndex == 0) {
				if (code != ShimmerMessage.DATAPACKET) {
//					Log.e(TAG, "Sync Error");
					break;
				}
			}
			
			try {
				for (int rawIndex = 0; rawIndex < bytes.length; rawIndex++) {
					
					
					mSensorBuffer[mSensorBufferIndex++] = bytes[rawIndex];
					
					if (mSensorBufferIndex >= mSensorMsgSize) {

						startMessage(mSensorMsgSize + SHIMMER_PREMSG_SIZE + SPINE_HEADER_SIZE);				
						for (int i = 0; i < mSensorMsgSize; i++) {
							mShimmerMessage[mMessageIndex++] = mSensorBuffer[i];
						}

						// Now we have a message we need to send it to the server via the server listener(s)
	//					Util.logHexByteString(TAG, "Complete Packet:", mSensorBuffer);
						mSensorBufferIndex = 0;
						sendMessage();			
						
						
						
						
						sendBytesAsMessage(mSensorBuffer);
						mSensorBufferIndex = 0;
					}
				}

			} catch (IndexOutOfBoundsException e) {
			   	Log.e(TAG, e.toString());
			   	mSensorBufferIndex = 0;
			}
			
			break;
		} // End switch (state) 
		

	}
	
	void sendBytesAsMessage(byte[] messageBytes) {
	}
	
	void sendMessage() {
		if (mServerListeners != null) {
	        for (int i = mServerListeners.size()-1; i >= 0; i--) {
		        try {
					Bundle b = new Bundle();
					b.putByteArray("message", mShimmerMessage);
		
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
	
	
	/* (non-Javadoc)
	 * @see com.t2.biofeedback.device.BioFeedbackDevice#write(byte[])
	 * 
	 */
	public void write(byte[] bytes) {
		super.write(bytes);
	}
	
	/**
	 * Begins a Shimmer message, populating common fields
	 */
	private void startMessage(int messageSize)
	{
		mMessageIndex = 0;
		mShimmerMessage = new byte[messageSize];		
		mShimmerMessage[mMessageIndex++] = (byte) 0xc4; 
		mShimmerMessage[mMessageIndex++] = (byte) 0xab;
		mShimmerMessage[mMessageIndex++] = SOURCE_ID_HIGH;
		mShimmerMessage[mMessageIndex++] = SOURCE_ID_LOW;
		mShimmerMessage[mMessageIndex++] = (byte) 0x00;
		mShimmerMessage[mMessageIndex++] = (byte) 0x00;
		mShimmerMessage[mMessageIndex++] = (byte) 0x00;
		mShimmerMessage[mMessageIndex++] = (byte) 0x01;
		mShimmerMessage[mMessageIndex++] = (byte) 0x01;	
		mShimmerMessage[mMessageIndex++] = DEFAULT_SHIMMER_FUNCT_CODE;			
		mShimmerMessage[mMessageIndex++] = shimmerSensorCode;			
	}
	
}