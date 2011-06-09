package com.t2.biofeedback.device.zephyr;

import java.util.BitSet;

import android.util.Log;

import com.t2.biofeedback.Constants;
import com.t2.biofeedback.device.BioFeedbackDevice;

public abstract class ZephyrDevice extends BioFeedbackDevice {
	private static final String TAG = Constants.TAG;
	
	@Override
	protected void onSetLinkTimeout(long linkTimeout) {
		ZephyrMessage m = new ZephyrMessage(
				0xA4,
				new byte[] {
					(byte) linkTimeout,
					(byte) linkTimeout,
					0x1,
					0x1,
				},
				ZephyrMessage.ETX
		);
		this.write(m);
	}

	@Override
	protected void onDeviceConnected() {
		super.onDeviceConnected();
		
		Log.v(TAG, "Tell the device to start sending periodic data.");
		// Tell the device to return periodic data.
		ZephyrMessage m = new ZephyrMessage(
				0x14,
				new byte[] {
					0x01
				},
				ZephyrMessage.ETX
		);
    	this.write(m);
	}

	@Override
	protected void onBeforeConnectionClosed() {
		Log.v(TAG, "Tell the device to stop sending periodic data.");
		ZephyrMessage m = new ZephyrMessage(
				0x14,
				new byte[] {
					0x00
				},
				ZephyrMessage.ETX
		);
    	this.write(m);
	}
	
	@Override
	protected void onBytesReceived(byte[] bytes) {
		StringBuffer hexString = new StringBuffer();
		for (int i=0;i<bytes.length;i++) {
		    hexString.append(Integer.toHexString(0xFF & bytes[i]));
		    }		
		Log.i(TAG, new String(hexString));
		ZephyrMessage msg = ZephyrMessage.parse(bytes);		
		
// TODO: See if we want to send Zephyr heartbeat messages
		// we might want to send all messages, but for now, since there are a lot of heartbeat
		// messages for now we'll only send data messages
		if(msg.msgId == 0x20) {
			this.onDeviceMessage(bytes);			
		}
		
	}
	
	
	private void write(ZephyrMessage msg) {
		this.write(msg.getBytes());
	}
}
