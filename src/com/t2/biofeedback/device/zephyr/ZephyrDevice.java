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
		this.onMessageReceived(ZephyrMessage.parse(bytes));
	}
	
	private void onMessageReceived(ZephyrMessage msg) {
		if(!msg.validPayload) {
			return;
		}
		
		if(msg.msgId == 0x20) {
			BitSet bs = byteArrayToBitSet(new byte[] {msg.raw[53], msg.raw[54]});
			
			this.onBatteryLevel(
					System.currentTimeMillis(),
					bitSetToByteArray(bs.get(0, 7))[0]
			);
			
			this.onHeartRate(
					System.currentTimeMillis(), 
					byteArrayToInt(new byte[] {msg.payload[9], msg.payload[10]})
			);
			
			this.onRespirationRate(
					System.currentTimeMillis(), 
					byteArrayToInt(new byte[] {msg.payload[11], msg.payload[12]}) / 10.00
			);
			
			this.onSkinTemperature(
					System.currentTimeMillis(), 
					byteArrayToInt(new byte[] {msg.payload[13], msg.payload[14]}) / 10.00
			);
		}
	}
	
	private void write(ZephyrMessage msg) {
		this.write(msg.getBytes());
	}
}
