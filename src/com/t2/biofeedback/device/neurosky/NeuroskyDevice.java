package com.t2.biofeedback.device.neurosky;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.t2.biofeedback.Constants;
import com.t2.biofeedback.Util;
import com.t2.biofeedback.device.BioFeedbackDevice;

/**
 * Encapsulates methods necessary to communicate with a Bluetooth Neurosky device
 * 
 * @author scott.coleman
 *
 */
public abstract class NeuroskyDevice extends BioFeedbackDevice implements DataListener{
	private static final String TAG = Constants.TAG;

	/**
	 * Parses byte stream coming from Neurosky device into complete messages
	 */
	StreamParser mStreamParser;

	/**
	 * Message formatted according to the MindsetProtocol specification
	 */
	byte[] mMindsetMessage;	
	
	private int mMessageIndex = 0;
	
	
	static final byte EXECODE_POOR_SIG_QUALITY = 2;
	static final byte EXECODE_ATTENTION = 4;
	static final byte EXECODE_MEDITATION = 5;
	static final byte EXECODE_BLINK_STRENGTH = 0x16;
	static final byte EXECODE_RAW_WAVE = (byte) 0x80;
	static final byte EXECODE_SPECTRAL = (byte) 0x83;

	static final int MINDSET_FUNCT_CODE						= 0x0A;
	static final int MINDSET_SENSOR_CODE 					= 0x0D;
	static final int SPINE_HEADER_SIZE 						= 9;
	static final int MINDSET_MSG_SIZE 						= 33;		
	
	static final byte EXECODE_POOR_SIG_QUALITY_POS = SPINE_HEADER_SIZE + 3 + 0; 
	static final byte EXECODE_ATTENTION_POS = SPINE_HEADER_SIZE + 3  + 1; 
	static final byte EXECODE_MEDITATION_POS = SPINE_HEADER_SIZE + 3  + 2; 
	static final byte EXECODE_BLINK_STRENGTH_POS = SPINE_HEADER_SIZE + 3  + 3; 
	
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
	private void startMessage()
	{
		mMessageIndex = 0;
		mMindsetMessage = new byte[MINDSET_MSG_SIZE + SPINE_HEADER_SIZE];		
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
			return;
		}
		
// TODO: re-do coding scheme to only send bytes necessary for the exe code received
		switch (code)
		{
		case EXECODE_POOR_SIG_QUALITY:
			startMessage();
			mMindsetMessage[mMessageIndex++] = (byte) code;
			mMindsetMessage[EXECODE_POOR_SIG_QUALITY_POS] = valueBytes[0];
			break;
			
		case EXECODE_ATTENTION:
			startMessage();
			mMindsetMessage[mMessageIndex++] = (byte) code;
			mMindsetMessage[EXECODE_ATTENTION_POS] = valueBytes[0];
			break;
		case EXECODE_MEDITATION:
			startMessage();
			mMindsetMessage[mMessageIndex++] = (byte) code;
			mMindsetMessage[EXECODE_MEDITATION_POS] = valueBytes[0];
			break;
		case EXECODE_BLINK_STRENGTH:
			startMessage();
			mMindsetMessage[mMessageIndex++] = (byte) code;
			mMindsetMessage[EXECODE_BLINK_STRENGTH_POS] = valueBytes[0];
			break;
		
		case EXECODE_RAW_WAVE:
			// For now we'll ignore raw wave data (comes in every 2 ms)
			return;
		
		case EXECODE_SPECTRAL:
			startMessage();
			mMindsetMessage[mMessageIndex++] = (byte) code;	
			int j = 0;
			byte messageLength = valueBytes[j++];

			// this code should ONLY have 24 bytes length, if not don't do anything
			if (messageLength == 24)
			{
				for (int i = 0; i < messageLength; i++)
				{
					mMindsetMessage[mMessageIndex++] = valueBytes[j++];
				}
			}
			break;

		default:
			return;
		
		}
		
		Util.logHexByteString(TAG, "Found message:", mMindsetMessage);
		
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