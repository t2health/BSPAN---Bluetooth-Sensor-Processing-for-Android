package com.t2.biofeedback.device.shimmer;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.t2.biofeedback.Constants;
import com.t2.biofeedback.Util;
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
 *  9				SHIMMER_FUNCT_CODE (0x0B)         <-- Payload
 *  10				SHIMMER_SENSOR_CODE (0x0E)
 *  11			    Packet Type		
 *  12 -13		    timestamp		
 *  14 -15		    Accel X		
 *  16 -17		    Accel Y		
 *  18 -19		    Accel Z		
 *  20 -21		    GSR		
 * 
 * @author scott.coleman
 *
 */
public abstract class ShimmerDevice extends BioFeedbackDevice{
	private static final String TAG = Constants.TAG;
	
	// Change this to reflect the source ID of this sensor
	// This is the is that the server will use to recognize this sensor
	private static final byte SOURCE_ID_HIGH = (byte) 0xff;
	private static final byte SOURCE_ID_LOW = (byte) 0xf3;
	
	static final int SHIMMER_FUNCT_CODE						= 0x0B;
	static final int SHIMMER_SENSOR_CODE 					= 0x0E;
	static final int SPINE_HEADER_SIZE 						= 9;
	

	// These numbers all add up for the shimmer being programmed
	// to send only Accel and GSR data (plus timestamp)
	// as configured in the state machine below
	private static final int SHIMMER_PREMSG_SIZE  = 2;   	// 	2 bytes in front of every payload, 
															// SHIMMER_FUNCT_CODE, 
															// SHIMMER_SENSOR_CODE, 
	private static final int SENSOR_MSG_SIZE = 11;		
	private static final int SHIMMER_MSG_SIZE = SENSOR_MSG_SIZE + SHIMMER_PREMSG_SIZE;		
	
	private int state = STATE_OFF;
	
	private static final int STATE_OFF = 0;
	private static final int STATE_SET_SENSORS = 1;
	private static final int STATE_SET_SAMPLERATE = 2;
	private static final int STATE_SET_GSRRANGE = 3;
	private static final int STATE_STREAMING = 4;
	
	// Commands for configuring the Shimmer hardware
	private static final byte[] setSensorsCommand = new byte[] {
			ShimmerMessage.SETSENSORSCOMMAND,
			(byte) (ShimmerMessage.SENSOR0_SensorAccel | ShimmerMessage.SENSOR0_SensorGSR),
			0
	};
	private static final byte[] setSampleRateCommand = new byte[] {
			ShimmerMessage.SETSAMPLINGRATECOMMAND,
			ShimmerMessage.SAMPLING4HZ
	};
	private static final byte[] setGsrRangeCommand = new byte[] {
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
		
		state = STATE_SET_SENSORS;
		this.write(setSensorsCommand);
	}

	/* (non-Javadoc)
	 * @see com.t2.biofeedback.device.SerialBTDevice#onBeforeConnectionClosed()
	 */
	protected void onBeforeConnectionClosed() 
	{
		Log.i(TAG, "Telling Shimer to stop streaming");
		this.write(new byte[] {ShimmerMessage.STOPSTREAMINGCOMMAND});
	}
	
	@Override
	public ModelInfo getModelInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void finalize() throws Throwable {
		Log.i(TAG, "Telling Shimer to stop streaming");
		this.write(new byte[] {ShimmerMessage.STOPSTREAMINGCOMMAND});
		super.finalize();
	}

	@Override
	public String getDeviceAddress() {
		// TODO Auto-generated method stub
		return null;
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
				Log.i(TAG, "Telling Shimmer to start streaming");
				this.write(new byte[] {ShimmerMessage.STARTSTREAMINGCOMMAND});
				state = STATE_STREAMING;
			}

		case STATE_STREAMING:
			if (code == ShimmerMessage.DATAPACKET) {
				Util.logHexByteString(TAG, "Found message:", bytes);
			}
			break;
		}
		
		if (code == 0x00 && bytes.length == SENSOR_MSG_SIZE)
		{
			startMessage(SHIMMER_MSG_SIZE + SPINE_HEADER_SIZE);
			if (!mTestData) {
				for (int i = 0; i < bytes.length; i++) {
					mShimmerMessage[mMessageIndex++] = bytes[i];
				}
			}
			else {
				for (int i = 0; i < mTestDataBytes.length; i++) {
					mShimmerMessage[mMessageIndex++] = mTestDataBytes[i];
				}
				
								
			}
			
			// Now we have a message we need to send it to the server via the server listener(s)
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
		mShimmerMessage[mMessageIndex++] = SHIMMER_FUNCT_CODE;			
		mShimmerMessage[mMessageIndex++] = SHIMMER_SENSOR_CODE;			
	}
	
}