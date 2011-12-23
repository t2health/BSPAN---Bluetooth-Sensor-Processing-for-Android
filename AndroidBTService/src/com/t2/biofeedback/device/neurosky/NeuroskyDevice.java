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

package com.t2.biofeedback.device.neurosky;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.t2.biofeedback.Constants;
import com.t2.biofeedback.device.BioFeedbackDevice;

/**
 * Encapsulates methods necessary to communicate with a Bluetooth Neurosky device
 * 
 * @author scott.coleman
 * 
 * Message format:
 * 
 *  SPINE HEADER
 *  desc: | Vers:Ext:Type | GroupId | SourceId | DestId | Seq#    | TotalFrag   | Frag #|
 *  size: | 2:1:5         | 8       | 16       | 16     | 8       | 8           | 8     |
 *  value:| C4            | 0xAB    | 0xfff2   | 0      | 0       | 1           | 1     |
 *
 *  SPINE MESSAGE
 *  Byte				Contents
 *  -----------------------------
 *  0 - 8			Spine Header (see above)
 *  9				MINDSET_FUNCT_CODE (0x0A)		<-- Payload
 *  10				MINDSET_SENSOR_CODE (0x0D)
 *  11				Exe Code 
 *  12				Signal Quality                    	<--- EXECODE_POOR_SIG_QUALITY_POS
 *  13				Attention							<--- EXECODE_ATTENTION_POS
 *  14				Meditation							<--- EXECODE_MEDITATION_POS
 *  15				Blink Strength						<--- EXECODE_BLINK_STRENGTH_POS
 *  16 - 17			Raw Data							<--- EXECODE_RAW_POS
 *  18 - 41			Spectral Data						<--- EXECODE_SPECTRAL_POS  (8 * 3 bytes each big endian)
 * 
 *  42 - 			512 samples of raw data 
 */
public abstract class NeuroskyDevice extends BioFeedbackDevice implements DataListener{
	private static final String TAG = Constants.TAG;
	private static final int NUM_RAW = 512;

	// Change this to reflect the source ID of this sensor
	// This is the is that the server will use to recognize this sensor
	private static final byte SOURCE_ID_HIGH = (byte) 0xff;
	private static final byte SOURCE_ID_LOW = (byte) 0xf2;

	// For testing purposes
	boolean mTestData = false;
	int mTestValue = 0;
	byte[] mTestDataBytes = {
			0x01, 0x02, 0x03, 
			0x04, 0x05, 0x06, 
			0x07, 0x08, 0x09, 
			0x0a, 0x0b, 0x0c, 
			0x11, 0x12, 0x13, 
			0x14, 0x15, 0x16, 
			0x17, 0x18, 0x19, 
			0x1a, 0x1b, 0x1c, 
			};	
	
	
	
	byte t1,t2;
	byte[] mRawAccumData = new byte[NUM_RAW * 2];
	int mRawAccumDataIndex = 0;
	boolean mSendRawWave = true;
	
	
	/**
	 * Parses byte stream coming from Neurosky device into complete messages
	 */
	StreamParser mStreamParser;

	/**
	 * Message formatted according to the MindsetProtocol specification
	 */
	byte[] mMindsetMessage;	
	
	private int mMessageIndex = 0;
	
	
	static final int EXECODE_POOR_SIG_QUALITY = 2;
	static final int EXECODE_ATTENTION = 4;
	static final int EXECODE_MEDITATION = 5;
	static final int EXECODE_BLINK_STRENGTH = 0x16;
	static final int EXECODE_RAW_WAVE = 0x80;
	static final int EXECODE_SPECTRAL = 0x83;
	static final int EXECODE_RAW_ACCUM = 0x90;				// Special T2 code for gaterhing 1 second of raw data and sending it all together

	static final int MINDSET_FUNCT_CODE						= 0x0A;
	static final int MINDSET_SENSOR_CODE 					= 0x0D;
	static final int SPINE_HEADER_SIZE 						= 9;

	
	static final int MINDSET_PREMSG_SIZE 				    = 3;   // 	3 bytes in front of every payload, MINDSET_FUNCT_CODE, MINDSET_SENSOR_CODE, EXECode)	
	static final int MINDSET_MSG_SIZE 						= 33 + MINDSET_PREMSG_SIZE;		
	static final int MINDSET_ACCUM_MSG_SIZE 				= 33 + MINDSET_PREMSG_SIZE + NUM_RAW * 2;		
	
	
	// Note that each Spine mindset message has all of the mindset attribuites
	// Define the hard positions in the Mindset message of each of the attributes
	static final byte PAYLOAD_POS = SPINE_HEADER_SIZE; 
	static final byte EXECODE_POOR_SIG_QUALITY_POS = PAYLOAD_POS + MINDSET_PREMSG_SIZE + 0; 
	static final byte EXECODE_ATTENTION_POS = EXECODE_POOR_SIG_QUALITY_POS + 1; 
	static final byte EXECODE_MEDITATION_POS = EXECODE_ATTENTION_POS + 1; 
	static final byte EXECODE_BLINK_STRENGTH_POS = EXECODE_MEDITATION_POS + 1; 
	static final byte EXECODE_RAW_POS = EXECODE_BLINK_STRENGTH_POS + 1; 
	static final byte EXECODE_SPECTRAL_POS = EXECODE_RAW_POS + 2; 
	static final byte EXECODE_ACCUM_MSG_POS = EXECODE_SPECTRAL_POS + 24; 
	
	/**
	 * @param serverListeners 	List of server listeners (used to transmit messages to the Spine server) 
	 */
	NeuroskyDevice(ArrayList<Messenger> serverListeners)
	{
		this.mServerListeners = serverListeners;
	}
	

	/* (non-Javadoc)
	 * @see com.t2.biofeedback.device.BioFeedbackDevice#onDeviceConnected()
	 */
	protected void onDeviceConnected() 
	{
		mStreamParser = new StreamParser(StreamParser.PARSER_TYPE_PACKETS, this, null);
	}

	/* (non-Javadoc)
	 * @see com.t2.biofeedback.device.SerialBTDevice#onBeforeConnectionClosed()
	 */
	protected void onBeforeConnectionClosed() 
	{
	}
	
	/**
	 * 
	 * Receives bytes from Bluetooth device and sends them to the parser.
	 * 	Note that the received bytes by come in any length. The parser seeds to 
	 *  Frame the data and detect complete messages. When this happens the
	 *  parser will call dataValueReceived with the complete message.
	 * @see com.t2.biofeedback.device.SerialBTDevice#onBytesReceived(byte[])
	 */
	protected void onBytesReceived(byte[] bytes) 
	{
	//	Util.logHexByteString(TAG, "Found message:", bytes);
		// Transfer bytes to parser one by one
		// Each time updating the state machine
		// then checking for a value header
		for (int i=0; i< bytes.length; i++) 
		{
			mStreamParser.parseByte(bytes[i]);		
		}		
	}
	
	/* (non-Javadoc)
	 * @see com.t2.biofeedback.device.BioFeedbackDevice#write(byte[])
	 * 
	 * Not used - Neurosky device is write only.
	 */
	public void write(byte[] bytes) {
		super.write(bytes);
	}
	
	/**
	 * Begins a Mindset message, populating common fields
	 */
	private void startMessage(int messageSize)
	{
		mMessageIndex = 0;
		mMindsetMessage = new byte[messageSize];		
		mMindsetMessage[mMessageIndex++] = (byte) 0xc4; 
		mMindsetMessage[mMessageIndex++] = (byte) 0xab;
		mMindsetMessage[mMessageIndex++] = SOURCE_ID_HIGH;
		mMindsetMessage[mMessageIndex++] = SOURCE_ID_LOW;
		mMindsetMessage[mMessageIndex++] = (byte) 0x00;
		mMindsetMessage[mMessageIndex++] = (byte) 0x00;
		mMindsetMessage[mMessageIndex++] = (byte) 0x00;
		mMindsetMessage[mMessageIndex++] = (byte) 0x01;
		mMindsetMessage[mMessageIndex++] = (byte) 0x01;	
		mMindsetMessage[mMessageIndex++] = MINDSET_FUNCT_CODE;			
		mMindsetMessage[mMessageIndex++] = MINDSET_SENSOR_CODE;			
	}
	
	/* 
	 * Called when the parser has a data row to send to the server
	 *  
	 * (non-Javadoc)
	 * @see com.t2.biofeedback.device.neurosky.DataListener#dataValueReceived(int, int, int, byte[], java.lang.Object)
	 */
	public void dataValueReceived( int extendedCodeLevel, int code, int numBytes,
			   byte[] valueBytes, Object customData )
	{
		// We need to build a SPINE-style message (See Above)
	
		
		// For now we'll ignore all extended codes
		if (extendedCodeLevel != 0)
		{
			Log.i(TAG, "Extended code: -----------------------------" + extendedCodeLevel);
			return;
		}
		
// TODO: re-do coding scheme to only send bytes necessary for the exe code received
		//Log.i(TAG, "code: " + code);
		switch (code)
		{
		case EXECODE_POOR_SIG_QUALITY:
			//Log.i(TAG, "siq Q");
			startMessage(MINDSET_MSG_SIZE + SPINE_HEADER_SIZE);
			mMindsetMessage[mMessageIndex++] = (byte) code;
			mMindsetMessage[EXECODE_POOR_SIG_QUALITY_POS] = valueBytes[0];
			break;
			
		case EXECODE_ATTENTION:
			//Log.i(TAG, "Atten");
			startMessage(MINDSET_MSG_SIZE + SPINE_HEADER_SIZE);
			mMindsetMessage[mMessageIndex++] = (byte) code;
			mMindsetMessage[EXECODE_ATTENTION_POS] = valueBytes[0];
			break;
		case EXECODE_MEDITATION:
			startMessage(MINDSET_MSG_SIZE + SPINE_HEADER_SIZE);
			mMindsetMessage[mMessageIndex++] = (byte) code;
			mMindsetMessage[EXECODE_MEDITATION_POS] = valueBytes[0];
			break;
		case EXECODE_BLINK_STRENGTH:
			startMessage(MINDSET_MSG_SIZE + SPINE_HEADER_SIZE);
			mMindsetMessage[mMessageIndex++] = (byte) code;
			mMindsetMessage[EXECODE_BLINK_STRENGTH_POS] = valueBytes[0];
			break;
		
		case EXECODE_RAW_WAVE:
			// For now we'll NOT ignore raw wave data (comes in every 2 ms)
			if (mSendRawWave) {
				if (mRawAccumDataIndex > (NUM_RAW * 2) - 2) {
					mRawAccumDataIndex = 0;

					// We should never get here
					Log.e(TAG, "mRawAccumDataIndex overflow");
					
				}
				else {
					
					if (mTestData) {
						byte b1 = (byte) ((mTestValue >> 8) & 0xff) ;
//						byte b1 = 0x55;
						byte b2 = (byte) (mTestValue & 0xff);
						if (b1 == 0x1f && b2 == 0x7f) {
							int i = 0;
							i++;
						}
						
						mRawAccumData[mRawAccumDataIndex++] = b1 ;
						mRawAccumData[mRawAccumDataIndex++] = b2;
						if (mTestValue++ >= 0xffff)
							mTestValue = 0;
							
					}
					else {
						mRawAccumData[mRawAccumDataIndex++] = valueBytes[0] ;
						mRawAccumData[mRawAccumDataIndex++] = valueBytes[1];
					}
				}

			}
			return;
		
		case EXECODE_SPECTRAL:
			
			Log.i(TAG, "Spectral, mRawAccumDataIndex = " + mRawAccumDataIndex);

			if (mSendRawWave) {
				startMessage(MINDSET_ACCUM_MSG_SIZE + SPINE_HEADER_SIZE);
				
			//	Log.i(TAG, "fred = " + mRawAccumData[0] + " " + mRawAccumData[1]);
				mMindsetMessage[mMessageIndex++] = (byte) EXECODE_RAW_ACCUM;	

				for (int i = 0; i < NUM_RAW * 2; i++) {
					mMindsetMessage[EXECODE_ACCUM_MSG_POS + i] = mRawAccumData[i];
				}
				mRawAccumDataIndex = 0;

			}
			else {
				mMindsetMessage[mMessageIndex++] = (byte) code;	
				startMessage(MINDSET_MSG_SIZE + SPINE_HEADER_SIZE);

			}
			
			int valueSize = valueBytes.length;
			
			
			// this code should ONLY have 24 bytes length, if not don't do anything
//			if (numBytes == 24 && valueSize == 24)
			if (valueSize == 24)
			{
				for (int i = 0; i < numBytes; i++)
				{
					if (!mTestData) {
						mMindsetMessage[EXECODE_SPECTRAL_POS + i] = valueBytes[i];
					}
					else {
						mMindsetMessage[EXECODE_SPECTRAL_POS + i] = mTestDataBytes[i];
					}
				}
			}
			break;

		default:
			return;
		
		}
		
//		Util.logHexByteString(TAG, "Found message:", mMindsetMessage);
//		Util.logHexByteString(TAG, "Found message:", mMindsetMessage);
		
		// Now we have a message we need to send it to the server via the server listener(s)
		if (mServerListeners != null)
		{
	        for (int i = mServerListeners.size()-1; i >= 0; i--) {
		        try {
					Bundle b = new Bundle();
					b.putByteArray("message", mMindsetMessage);
		
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