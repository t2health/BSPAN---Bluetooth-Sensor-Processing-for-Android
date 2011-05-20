package com.t2.biofeedback.device.Spine;

import java.util.BitSet;

import android.util.Log;

import com.t2.biofeedback.Constants;
import com.t2.biofeedback.device.BioFeedbackDevice;

public abstract class SpineDevice extends BioFeedbackDevice {
	private static final String TAG = Constants.TAG;
	
	@Override
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

	@Override
	protected void onDeviceConnected() {
		super.onDeviceConnected();
		
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

	@Override
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
	
	@Override
	protected void onBytesReceived(byte[] bytes) {
//		Log.i(TAG, "hi there");
//		Log.i(TAG, bytes.toString());
		
		    
		StringBuffer hexString = new StringBuffer();
		for (int i=0;i<bytes.length;i++) {
		    hexString.append(Integer.toHexString(0xFF & bytes[i]));
		    }		
		Log.i(TAG, new String(hexString));
	}
	
	
//	private void write(ZephyrMessage msg) {
//		this.write(msg.getBytes());
//	}
}
