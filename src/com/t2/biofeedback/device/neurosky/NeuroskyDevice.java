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
 */
public abstract class NeuroskyDevice extends BioFeedbackDevice implements DataListener{
	private static final String TAG = Constants.TAG;
	private static final int NUM_RAW = 512;
	boolean mTestData = false;
	
	
	
	byte t1,t2;
	byte[] mRawAccumData = new byte[NUM_RAW * 2];
	int mRawAccumDataIndex = 0;
	boolean mSendRawWave = true;
	
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
	

	/**
	 * Message format:
	 * Byte				Contents
	 * -----------------------------
	 * 0 - 8			Spine Header
	 * 9				MINDSET_FUNCT_CODE (0x0A)		<-- Payload
	 * 10				MINDSET_SENSOR_CODE (0x0D)
	 * 11				Exe Code 
	 * 12				Signal Quality                    	<--- EXECODE_POOR_SIG_QUALITY_POS
	 * 13				Attention							<--- EXECODE_ATTENTION_POS
	 * 14				Meditation							<--- EXECODE_MEDITATION_POS
	 * 15				Blink Strength						<--- EXECODE_BLINK_STRENGTH_POS
	 * 16 - 17			Raw Data							<--- EXECODE_RAW_POS
	 * 16 - 40			Spectral Data						<--- EXECODE_SPECTRAL_POS  (8 * 3 bytes each big endian)
	 * 41 - 			512 samples of raw data 
	 */
	
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

	static final byte EXECODE_ACCUM_MSG_POS = 41; 
	
	/**
	 * @param serverListeners 	List of server listeners (used to transmit messages to the Spine server) 
	 */
	NeuroskyDevice(ArrayList<Messenger> serverListeners)
	{
		this.mServerListeners = serverListeners;
	}
	
	/* (non-Javadoc)
	 * @see com.t2.biofeedback.device.BioFeedbackDevice#onSetLinkTimeout(long)
	 */
	protected void onSetLinkTimeout(long linkTimeout) {
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
		mMindsetMessage[mMessageIndex++] = (byte) 0xff;
		mMindsetMessage[mMessageIndex++] = (byte) 0xf2;
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
		// We need to build a SPINE-style message
		
		//  SPINE HEADER
		//  desc: | Vers:Ext:Type | GroupId | SourceId | DestId | Seq#    | TotalFrag   | Frag #|
		//  size: | 2:1:5         | 8       | 16       | 16     | 8       | 8           | 8     |
		//  value:| C4            | 0xAB    | 0xfff2   | 0      | 0       | 1           | 1     |

		// 		Note: Pkt Type: 4 = data, Function code: 1 = Raw Data, Sensor code: D = Mindset data 

		// bits    	parameter
		// 8		function code
		// 8		sensor code
		// 8		function type
		// 8		poor signal bytes
		// 8		Attention
		// 8		Meditation
		// 8		Blink Strength
		// 16		RAW wave value
		// 24*8		spectral data
		
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
				if (mRawAccumDataIndex >= NUM_RAW * 2) {

					// We should never get here
					Log.e(TAG, "mRawAccumDataIndex overflow");
				}
				else {
					mRawAccumData[mRawAccumDataIndex++] = valueBytes[0] ;
					mRawAccumData[mRawAccumDataIndex++] = valueBytes[1];
				}

			}
			return;
		
		case EXECODE_SPECTRAL:
			
//			Log.i(TAG, "Spectral, mRawAccumDataIndex = " + mRawAccumDataIndex);

			if (mSendRawWave) {
				startMessage(MINDSET_ACCUM_MSG_SIZE + SPINE_HEADER_SIZE);
				
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
	}
}