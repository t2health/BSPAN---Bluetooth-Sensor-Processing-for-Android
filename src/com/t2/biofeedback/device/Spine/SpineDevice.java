package com.t2.biofeedback.device.Spine;

import java.util.BitSet;

import spine.SPINEPacketsConstants;
import spine.SPINEFunctionConstants;
import spine.SPINESensorConstants;
import spine.communication.android.AndroidMessage;



import android.util.Log;

import com.t2.biofeedback.Constants;
import com.t2.biofeedback.device.BioFeedbackDevice;
import com.t2.biofeedback.device.zephyr.ZephyrMessage;

public abstract class SpineDevice extends BioFeedbackDevice {
	private static final String TAG = Constants.TAG;

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
	
	void SpineDevice()
	{
		resetFifo();		
	}
	
	
	
   	public class Header {
		int version;
		int extension;
		int type;
		int group;
		int sourceNode;
		int destNode;
		int seq;
		int totalFragments;
		int fragment;

		Header(byte[] bytes) throws  BadHeaderException {
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
	
	
	
	public class BadHeaderException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4070660360479320363L;

		public BadHeaderException(String msg) {
			super(msg + " invalid header");
		}
	}
	
	
	protected void onSetLinkTimeout(long linkTimeout) {
//		ZephyrMessage m = new ZephyrMessage(
//				0xA4,
//				new byte[] {
//					(byte) linkTimeout,
//					(byte) linkTimeout,
//					0x1,
//					0x1,
//				},
//				ZephyrMessage.ETX
//		);
//		this.write(m);
	}

	protected void onDeviceConnected() {
		
//		Log.v(TAG, "Tell the device to start sending periodic data.");
//		// Tell the device to return periodic data.
//		ZephyrMessage m = new ZephyrMessage(
//				0x14,
//				new byte[] {
//					0x01
//				},
//				ZephyrMessage.ETX
//		);
//    	this.write(m);
	}

	protected void onBeforeConnectionClosed() {
//		Log.v(TAG, "Tell the device to stop sending periodic data.");
//		ZephyrMessage m = new ZephyrMessage(
//				0x14,
//				new byte[] {
//					0x00
//				},
//				ZephyrMessage.ETX
//		);
//    	this.write(m);
	}
	
	protected void onBytesReceived(byte[] bytes) {
//		Log.i(TAG, "hi there");
//		Log.i(TAG, bytes.toString());
		
		    
		StringBuffer hexString = new StringBuffer();
		for (int i=0;i<bytes.length;i++) {
		    hexString.append(Integer.toHexString(0xFF & bytes[i]));
		    }		
		Log.i(TAG, new String(hexString));

		int nuBytes = bytes.length;
		// Transfer bytes to fifo
		for (int i=0; i< bytes.length; i++) {

			addByteCheckMsg(bytes[i]);		

		
		}		
		
		
		
	}
	
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
	
	protected void addByteCheckMsg(byte aByte) {
		//AndroidMessage	msg = new AndroidMessage();
		
		
		switch (state)
		{
		case STATE_BUILDING_HEADER:
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
			mFifo[mFifoTail++] = aByte;
			if (mFifoTail - mFifoHeader2 < SPINEPacketsConstants.SPINE_HEADER_SIZE)
				break;

			if (isHeader(mFifoHeader2))
			{
				// Found message

				int messageSize = mFifoTail - SPINEPacketsConstants.SPINE_HEADER_SIZE;
				
				byte[] messageArray = new byte[messageSize];
				
				StringBuffer hexString = new StringBuffer();
				int j = 0;
				for (int i = mFifoHeader1; i < mFifoTail - SPINEPacketsConstants.SPINE_HEADER_SIZE; i++)
				{
					
					byte b = mFifo[i];
					messageArray[j++] = b;
				    hexString.append(Integer.toHexString(0xFF & b));
					
				}    				
				
				Log.i(TAG, "Found message: " + new String(hexString));    	
				
				this.onMessageReceived(messageArray);
				
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
				mFifoTail = 0;
			
			// Search for valid header
			//Ex header: Data packet from node 1 to base (0), seq # 1, one frag
			// C4 00 01 00 00 00 01 01 01
			// C4 00 xx xx 00 00 xx xx xx
//			try {
//				Header h = new Header(bytes);
//				
//			} catch (BadHeaderException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	
	protected boolean isHeader(int index)
	{
		boolean result = true;
		int[] headerTemplate = {0xC4, 0x00, -1, -1, 0x00, 0x00, -1, -1, -1,0,0,0,0,0,0,0,0,0,0,0}; // Don't cares are -1
		
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
		return result;
	}

	private void onMessageReceived(byte[] message) {
		
		this.onSpineMessage(message);
		
	}			
	
//	private void write(ZephyrMessage msg) {
//		this.write(msg.getBytes());
//	}
}