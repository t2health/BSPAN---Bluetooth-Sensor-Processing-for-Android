package com.t2.biofeedback.device.neurosky;

import java.util.ArrayList;
import java.util.BitSet;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.t2.biofeedback.Constants;
import com.t2.biofeedback.Util;
import com.t2.biofeedback.device.BioFeedbackDevice;




//import t2.spine.communication.android.AndroidMessage;


//import spine.communication.android.AndroidMessage;



import java.util.ArrayList;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;


import com.t2.biofeedback.BioFeedbackService;
import com.t2.biofeedback.Constants;
import com.t2.biofeedback.Util;
import com.t2.biofeedback.device.BioFeedbackDevice;

public abstract class NeuroskyDevice extends BioFeedbackDevice implements DataListener{
	private static final String TAG = Constants.TAG;

	StreamParser mStreamParser;
	
	NeuroskyDevice(ArrayList<Messenger> serverListeners)
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
	
	
	protected void onSetLinkTimeout(long linkTimeout) {
	}

	protected void onDeviceConnected() 
	{
		mStreamParser = new StreamParser(StreamParser.PARSER_TYPE_PACKETS, this, null);
		
	}

	protected void onBeforeConnectionClosed() 
	{
	}
	
	protected void onBytesReceived(byte[] bytes) 
	{
	//	Util.logHexByteString(TAG, "Partial Neurosky: ", bytes);		
//		Log.i(TAG, "Found message: PARTIAL");
		

		// Transfer bytes to parser one by one
		// Each time updating the state machine
		for (int i=0; i< bytes.length; i++) 
		{
			mStreamParser.parseByte(bytes[i]);		
			
		}		
	}
	
	protected void resetFifo()
	{
	}
	


	private void onMessageReceived(byte[] message) 
	{
		this.onSpineMessage(message);
	}			
	
	public void write(byte[] bytes) {

		super.write(bytes);
	}
	
	public void dataValueReceived( int extendedCodeLevel, int code, int numBytes,
			   byte[] valueBytes, Object customData )
			   {
		
				if (code != 0x80)
				{
					Util.logHexByteString(TAG, "Code: " + code + " " , valueBytes);
					
				}
					
			   }
}