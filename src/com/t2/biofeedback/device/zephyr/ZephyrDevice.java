package com.t2.biofeedback.device.zephyr;

import java.util.ArrayList;
import java.util.BitSet;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.t2.biofeedback.Constants;
import com.t2.biofeedback.device.BioFeedbackDevice;

/**
 * Encapsulates methods necessary to communicate with a Bluetooth Zephyr device
 * 
 * @author scott.coleman
 *
 */
public abstract class ZephyrDevice extends BioFeedbackDevice {
	private static final String TAG = Constants.TAG;
	
	ZephyrDevice(ArrayList<Messenger> serverListeners)
	{
		this.mServerListeners = serverListeners;
	}
	
	/* (non-Javadoc)
	 * @see com.t2.biofeedback.device.BioFeedbackDevice#onSetLinkTimeout(long)
	 */
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

	/* (non-Javadoc)
	 * @see com.t2.biofeedback.device.BioFeedbackDevice#onDeviceConnected()
	 */
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

	/* (non-Javadoc)
	 * @see com.t2.biofeedback.device.SerialBTDevice#onBeforeConnectionClosed()
	 */
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
	
	/* (non-Javadoc)
	 * @see com.t2.biofeedback.device.SerialBTDevice#onBytesReceived(byte[])
	 * 
	 * Parses the received bytes into a Zephyr message and forwards them
	 */
	@Override
	protected void onBytesReceived(byte[] bytes) {
		
		if (bytes[1]== 0x20)
		{
			this.onMessageReceived(ZephyrMessage.parse(bytes));
		}
	}
	
	/**
	 * Receives parsed message from Zephyr device. Formats it to look like
	 * a Spine message then sends it to the Spine server.
	 *  
	 * @param msg	Message to send to Spine server
	 */
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
			
			final int SPINE_HEADER_SIZE = 9;
			final int ZEPHYR_MSG_SIZE = 22;
				
			byte[] zepherMessage = new byte[ZEPHYR_MSG_SIZE + SPINE_HEADER_SIZE];

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
			
//			this.onSpineMessage(zepherMessage);		// Old method of sending data to server
			if (mServerListeners != null)
			{			
		        for (i = mServerListeners.size()-1; i >= 0; i--) {
			        try {
						Bundle b = new Bundle();
						b.putByteArray("message", zepherMessage);
			
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