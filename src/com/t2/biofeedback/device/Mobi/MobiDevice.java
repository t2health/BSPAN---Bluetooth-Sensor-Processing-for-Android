package com.t2.biofeedback.device.Mobi;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.t2.biofeedback.Constants;
import com.t2.biofeedback.Util;
import com.t2.biofeedback.device.BioFeedbackDevice;
import com.t2.biofeedback.device.Mobi.Structures.FrontEndInfo;
import com.t2.biofeedback.device.zephyr.ZephyrMessage;

/**
 * Encapsulates methods necessary to communicate with a Bluetooth Neurosky device
 * 
 * @author scott.coleman
 *
 */
public abstract class MobiDevice extends BioFeedbackDevice{
	private static final String TAG = Constants.TAG;

	
	boolean mDebug = true;	
	
	
	/**
	 * @param serverListeners 	List of server listeners (used to transmit messages to the Spine server) 
	 */
	MobiDevice(ArrayList<Messenger> serverListeners)
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
		Log.i(TAG, "Mobi device Connected");
		super.onDeviceConnected();
		MobiMessage m = new MobiMessage(
				FrontEndInfo.TMSFRONTENDINFOREQ,
				new byte[] {
					
				}
		);
    	this.write(m.getBytes());		
		

//		mStreamParser = new StreamParser(StreamParser.PARSER_TYPE_PACKETS, this, null);
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
//			mStreamParser.parseByte(bytes[i]);		
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

	//	if (mDebug)
	//		Util.logHexByteString(TAG, "Found message:", "");
	}
}