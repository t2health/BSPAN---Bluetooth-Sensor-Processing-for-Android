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

package com.t2.biofeedback.device.Spine;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.t2.biofeedback.Constants;
import com.t2.biofeedback.SPINEPacketsConstants;
import com.t2.biofeedback.Util;
import com.t2.biofeedback.device.BioFeedbackDevice;

/**
 * Encapsulates methods necessary to communicate with a Bluetooth Spine device
 * 
 * @author scott.coleman
 *
 */
public abstract class SpineDevice extends BioFeedbackDevice {
	private static final String TAG = Constants.TAG;
	boolean mDebug = true;	
	

	protected static final int MAX_FIFO = 255;
	
	protected byte[] mFifo = new byte[MAX_FIFO];
	protected byte[] mNewHeader = new byte[SPINEPacketsConstants.SPINE_HEADER_SIZE];	

	protected int mFifoHeader1 = 0; 
	protected int mFifoHeader2 = 0; 
	protected int mFifoMsg1 = 0; 
	protected int mFifoTail = 0; 
	
	protected static final int STATE_BUILDING_HEADER = 1;
	protected static final int STATE_BUILDING_MESSAGE = 2;
	protected int state = STATE_BUILDING_HEADER;
	
	protected int currentMsgSeq = 0;
	protected int numMessagesOutOfSequence = 0;
	protected int numMessagesFrameErrors = 0;
	
	SpineDevice(ArrayList<Messenger> serverListeners)
	{
		this.mServerListeners = serverListeners;
		resetFifo();		
	}

	public class SpineHeader {
		int version;		// Byte 0 bits 7:6
		int extension;      // Byte 0 bits 5
		int type;           // Byte 0 bits 4:0
		int group;			// Byte 1
		int sourceNode;     // Bytes 2 - 3 
		int destNode;       // Bytes 4 - 5
		int seq;			// Byte 6
		int totalFragments;	// Byte 7
		int fragment;		// Byte 9

		SpineHeader(byte[] bytes) throws  BadHeaderException 
		{
			int b1 = bytes[0];
			version = (b1 >> 6) & 0x07;
			extension = (b1 >> 5) & 0x01;
			type = b1 & 0x1f;
			
			if ((version != 3) || (type != 4) || extension != 0)
			{
				throw new BadHeaderException("");
			}
		}
	}

   	public class BadHeaderException extends Exception 
   	{
		private static final long serialVersionUID = 4070660360479320363L;

		public BadHeaderException(String msg) 
		{
			super(msg + " invalid header");
		}
	}
	
	/* (non-Javadoc)
	 * @see com.t2.biofeedback.device.BioFeedbackDevice#onDeviceConnected()
	 */
	protected void onDeviceConnected() 
	{
	}

	/* (non-Javadoc)
	 * @see com.t2.biofeedback.device.SerialBTDevice#onBeforeConnectionClosed()
	 */
	protected void onBeforeConnectionClosed() 
	{
	}
	
	/** 
	 * @see com.t2.biofeedback.device.SerialBTDevice#onBytesReceived(byte[])
	 * 
	 * 	Note that the received bytes by come in any length. The parser seeds to 
	 *  Frame the data and detect complete messages. When this happens the
	 *  parser will call dataValueReceived with the complete message.	 * 
	 */
	protected void onBytesReceived(byte[] bytes) 
	{
//		Util.logHexByteString(TAG, bytes);		
//		Log.i(TAG, "Found message: PARTIAL");
		

		// Transfer bytes to fifo one by one
		// Each time updating the state machine
		for (int i=0; i< bytes.length; i++) 
		{
			parseByte(bytes[i]);		
		}		
	}
	
	/**
	 * 
	 */
	protected void resetFifo()
	{
		mFifo = new byte[MAX_FIFO];
		for (int i = 0; i < mFifo.length; i++)
			mFifo[i] = (byte) 0xff;
		mFifoHeader1 = 0; 
		mFifoHeader2 = 0; 
		mFifoMsg1 = 0; 
		mFifoTail = 0; 		
	}
	
	/**
	 * Adds byte to FIFO and parses the FIFO for valie message headers
	 * 
	 * @param aByte		Byte to add to FIFO
	 */
	protected void parseByte(byte aByte) {
		//AndroidMessage	msg = new AndroidMessage();
		int ix = 0;
		int jx = 0;
		int r;
		
		if (aByte == -60)
		{
			ix++;
		}
			
		if (aByte == -62)
			jx++;
			
		r = ix + jx;
		
		switch (state)
		{
		case STATE_BUILDING_HEADER:
			// Looking for valid header
			mFifo[mFifoTail++] = aByte;
			if (mFifoTail - mFifoHeader1 < SPINEPacketsConstants.SPINE_HEADER_SIZE)
				break;

			if (isHeader(mFifoHeader1))
			{
				state = STATE_BUILDING_MESSAGE;
				mFifoHeader2 = mFifoTail;
			}
			else
			{
				mFifoHeader1++;
			}
			break;

			
		case STATE_BUILDING_MESSAGE:
			// At least one header found. Now fill up FIFO message bytes.
			// Continue until another header is encountered. At that
			// time save the previous message and use the newly
			// found header as a start for the next message
			mFifo[mFifoTail++] = aByte;
			if (mFifoTail - mFifoHeader2 < SPINEPacketsConstants.SPINE_HEADER_SIZE)
				break;

			if (isHeader(mFifoHeader2))
			{
				// Found message
				int messageSize = mFifoTail - SPINEPacketsConstants.SPINE_HEADER_SIZE;
				
				byte[] messageArray = new byte[messageSize];
				
				int j = 0;
				for (int i = mFifoHeader1; i < mFifoTail - SPINEPacketsConstants.SPINE_HEADER_SIZE; i++)
				{
					byte b = mFifo[i];
					messageArray[j++] = b;
				}    				
				
				int seq = messageArray[6];
				if (currentMsgSeq != 0 && seq != currentMsgSeq + 1)
				{
					numMessagesOutOfSequence++;
//					Log.i(TAG, "Message out of sequence! Expected seq=" + (currentMsgSeq + 1) +  ", Found " 
//							+ seq + ", Total out of seq = " + numMessagesOutOfSequence);    	
					
				}
				currentMsgSeq = seq;
				
//				Log.i(TAG, "Found message: FULL");

				if (mDebug)
					Util.logHexByteString(TAG, "Found message:", messageArray);
				
				// this.onSpineMessage(message); // Old method of sending data to server
				
				if (mServerListeners != null)
				{
					for (int i = mServerListeners.size()-1; i >= 0; i--) {
				        try {
							Bundle b = new Bundle();
							b.putByteArray("message", messageArray);
				
				            Message msg = Message.obtain(null, MSG_SET_ARRAY_VALUE);
				            msg.setData(b);
				            mServerListeners.get(i).send(msg);
				
				        } catch (RemoteException e) {
				            // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
				        	mServerListeners.remove(i);
				        }
				    }				
					
				}
				
				// Now start over
				resetFifo();
				for (mFifoTail = 0; mFifoTail < SPINEPacketsConstants.SPINE_HEADER_SIZE; mFifoTail++)
				{
					mFifo[mFifoTail] = mNewHeader[mFifoTail];
				}
   				mFifoHeader2 = mFifoTail;    				
			}
			else
			{
				mFifoHeader2++;
			}
			break;
		}
			
			if (mFifoTail >= MAX_FIFO)
			{
				state = STATE_BUILDING_HEADER;
				numMessagesFrameErrors++;
				Log.e(TAG, "Spine message Framing error, numErrors = " + numMessagesFrameErrors);
				
				mFifoHeader1 = 0; 
				mFifoHeader2 = 0; 
				mFifoMsg1 = 0; 
				mFifoTail = 0;				
			}
	}
			
	/**
	 * Search for valid header by comparing bytes in the fifo to a reference
	 * header string. The reference has wildcards for places where the
	 * header might change.
	 *		Ex header: Data packet from node 1 to base (0), seq # 1, one frag
	 * 			C4 00 01 00 00 00 01 01 01
     *		Ex reference string
	 * 			C4 00 xx xx 00 00 xx xx xx	 * 
	 * 
	 * 
	 * @param index		Index into FIFO to start looking for header
	 * @return
	 */
	protected boolean isHeader(int index)
	{
		boolean result = true;
		int[] headerTemplate = {0xC4, 0xab, -1, -1, 0x00, 0x00, -1, -1, -1,0,0,0,0,0,0,0,0,0,0,0}; // Don't cares are -1
		int[] headerTemplate1 = {0xC2, 0xab, -1, -1, 0x00, 0x00, -1, -1, -1,0,0,0,0,0,0,0,0,0,0,0}; // Don't cares are -1
		
		// Check for a data packet
		for (int i = 0 ; i < SPINEPacketsConstants.SPINE_HEADER_SIZE; i++)
		{
			mNewHeader[i] = mFifo[index + i];		
			if (headerTemplate[i] != -1)
			{
				if(mFifo[index + i] != (byte) headerTemplate[i])
				{
					result = false;
					break;
				}
			}
		}

		if (result == true)
			return result;
		
		result = true;
		
		// Check for a service advertisement packet
		for (int i = 0 ; i < SPINEPacketsConstants.SPINE_HEADER_SIZE; i++)
		{
			mNewHeader[i] = mFifo[index + i];		
			if (headerTemplate1[i] != -1)
			{
				if(mFifo[index + i] != (byte) headerTemplate1[i])
				{
					result = false;
					break;
				}
			}
		}
		if (result == true)
			return result;

		return result;
	}

	
	/* (non-Javadoc)
	 * @see com.t2.biofeedback.device.BioFeedbackDevice#write(byte[])
	 */
	public void write(byte[] bytes) {

		super.write(bytes);
	}

}