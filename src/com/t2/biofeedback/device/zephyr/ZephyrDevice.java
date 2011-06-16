package com.t2.biofeedback.device.zephyr;

import java.util.ArrayList;
import java.util.BitSet;

import android.os.Messenger;
import android.util.Log;

import com.t2.biofeedback.BioFeedbackService;
import com.t2.biofeedback.Constants;
import com.t2.biofeedback.Util;
import com.t2.biofeedback.device.BioFeedbackDevice;

public abstract class ZephyrDevice extends BioFeedbackDevice {
	private static final String TAG = Constants.TAG;
	
	
	ZephyrDevice(BioFeedbackService biofeedbackService)
	{
		this.mBiofeedbackService = biofeedbackService;
	}
	
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
		
		if (bytes[1]== 0x20)
		{
			this.onMessageReceived(ZephyrMessage.parse(bytes));
		}
		
	}
	
	private void onMessageReceived(ZephyrMessage msg) {
		if(!msg.validPayload) {
			return;
		}
		// TODO: See if we want to send Zephyr heartbeat messages
		// we might want to send all messages, but for now, since there are a lot of heartbeat
		// messages for now we'll only send data messages
		if(msg.msgId == 0x20) {

//			Util.logHexByteString(TAG, msg.payload);
			
			// Use this to send the message directly to the main aplication
			//this.onDeviceMessage(msg.payload);			
			// Use this to send the message directly to the main aplication
			

			// Use this to send the message via the normal SPINE mechanism
			
			// We need to build a SPINE-style message

			//  SPINE HEADER
			//  desc: | Vers:Ext:Type | GroupId | SourceId | DestId | Seq#    | TotalFrag   | Frag #|
			//  size: | 2:1:5         | 8       | 16       | 16     | 8       | 8           | 8     |
			//  value:| C4            | 0xAB    | 0xfff1   | 0      | 0       | 1           | 1     |

			// SPINE MESSAGE
			//  desc: | Func | Sensor | Feat| Feat | Feat     | Bat Level | Heart Rate | Resp Rate | Skin Temp | Label  |
			//  desc: | Code | Code   | Cnt | Code | Bitmask  | Value     | Value      | Value     | Value     | Length |
			//  size: |  8   |  8     |  8  | 8    | 8        | 32        | 32         | 32        | 32        | 8      |
			//  value:|  9   |  C     |  4  | 9    | 0x0f     | xxxxxxxx  | xxxxxxxx   | xxxxxxxx  | xxxxxxxx  | 0      |
			//
			// 		Note: Pkt Type: 4 = data, Function code: 1 = Raw Data, Sensor code: C = Zephyr data 
			
			final int ZEPHYR_FUNCT_CODE							= 0x09;
			final int ZEPHER_FUNCT_TYPE							= 1;   //(Raw data)
			final int ZEPHYR_SENSOR_CODE 						= 0x0C;
//
//			final int ZEPHYR_FEATURE_CODE_BATLEVEL 				= 1;
//			final int ZEPHYR_FEATURE_CODE_BATLEVEL_BITMASK 		= 0x08;
//
//			final int ZEPHYR_FEATURE_CODE_HEARTRATE 			= 2;
//			final int ZEPHYR_FEATURE_CODE_HEARTRATE_BITMASK 	= 0x04;
//
//			final int ZEPHYR_FEATURE_CODE_RESPRATE 				= 3;
//			final int ZEPHYR_FEATURE_CODE_RESPRATE_BITMASK 		= 0x02;
//
//			final int ZEPHYR_FEATURE_CODE_SKINTEMP 				= 4;
//			final int ZEPHYR_FEATURE_CODE_SKINTEMP_BITMASK 		= 0x01;

			
			final int SPINE_HEADER_SIZE = 9;
			final int ZEPHYR_MSG_SIZE = 22;
			
//			final int ZEPHYR_PKT_TYPE_POS = 0;
//			final int ZEPHYR_FUNCT_CODE_POS = 1;
//			final int ZEPHYR_SENSOR_CODE_POS = 2;
//			final int ZEPHYR_FEAT_COUNT_POS = 3;
//			final int ZEPHYR_FEAT_BATLEVEL_POS = 4;
//			final int ZEPHYR_FEAT_HEARTRATE_POS = 10;
//			final int ZEPHYR_FEAT_RESPRATE_POS = 16;
//			final int ZEPHYR_FEAT_SKINTEMP_POS = 22;
				
			byte[] zepherMessage = new byte[ZEPHYR_MSG_SIZE + SPINE_HEADER_SIZE];

//			byte[] spineHeader = new byte[] {(byte) 0xc4, (byte) 0xab, (byte) 0xff, (byte) 0xf1, 0,0,0,1,1}; 
			
			// First add spine header
			int i = 0;
			// Header
			zepherMessage[i++] = (byte) 0xc4; 
			zepherMessage[i++] = (byte) 0xab;
			zepherMessage[i++] = (byte) 0xff;
			zepherMessage[i++] = (byte) 0xf1;
			zepherMessage[i++] = (byte) 0x00;
			zepherMessage[i++] = (byte) 0x00;
			zepherMessage[i++] = (byte) 0x00;
			zepherMessage[i++] = (byte) 0x01;
			zepherMessage[i++] = (byte) 0x01;
			
			// MEssage
			zepherMessage[i++] = ZEPHYR_FUNCT_CODE;
			zepherMessage[i++] = ZEPHYR_SENSOR_CODE;
			zepherMessage[i++] = 1;  //1 feature 

			zepherMessage[i++] = ZEPHER_FUNCT_TYPE;   
			zepherMessage[i++] = 0x0f;   				// Bitmask

			
			zepherMessage[i++] = msg.raw[53];
			zepherMessage[i++] = msg.raw[54];
			zepherMessage[i++] = 0;
			zepherMessage[i++] = 0;
			
			zepherMessage[i++] = 0;
			zepherMessage[i++] = 0;
			zepherMessage[i++] = msg.payload[10];
			zepherMessage[i++] = msg.payload[9];
			
			zepherMessage[i++] = 0;
			zepherMessage[i++] = 0;
			zepherMessage[i++] = msg.payload[12];
			zepherMessage[i++] = msg.payload[11];
			
			zepherMessage[i++] = 0;
			zepherMessage[i++] = 0;
			zepherMessage[i++] = msg.payload[14];
			zepherMessage[i++] = msg.payload[13];

			zepherMessage[i++] = 0; // No label
			
			this.onSpineMessage(zepherMessage);
			

		}		
		
	}
	
	
	private void write(ZephyrMessage msg) {
		this.write(msg.getBytes());
	}
	

	
	public static byte[] bitSetToByteArray(BitSet bs) {
		byte[] bytes = new byte[(int) Math.ceil(bs.size() / 8)];
		for(int i = 0; i < bs.size(); i++) {
			if(bs.get(i) == true) {
				bytes[i / 8] |= 1 << i;
			}
		}
		return bytes;
	}

}
