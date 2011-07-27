package com.t2.biofeedback.device.Mobi;

import com.t2.biofeedback.Constants;

/**
 * Message structure used to send/receive messages to the Zephyr device.
 * 	Describes the internal format Zephyr devices use to pack data
 * @author scott.coleman
 *
 */
public class MobiMessage {
	private static final String TAG = Constants.TAG;
	
	private static final byte STX = (byte) 0xaa;
	
	public byte stx = STX;
	public byte stx1 = STX;
	public int msgId;
	public int length = 4; // min size = 4 bytes - stx, stx1, size, pketype
	public int dlc;
	public byte[] payload = new byte[0];
	public int checksum = 0x0000;
	public boolean validPayload = false;
	
	public byte[] raw;
	
	public MobiMessage() {
		
	}
	
	public MobiMessage(int msgId, byte[] payload) {
		this.msgId = msgId;
		this.setPayload(payload);
		this.setChecksum();
	}
	
	/**
	 * Parses Zephyr message from input byte stream from device
	 * 
	 * @param stream	Byte array containing message bytes
	 * @return			Parsed message
	 */
	public static MobiMessage parse(byte[] stream) {
		int streamIndex = 0;
		MobiMessage m = new MobiMessage();
		
		m.stx = stream[streamIndex++];
		m.msgId = stream[streamIndex++];
		m.dlc = stream[streamIndex++];
		
		byte[] payload = new byte[m.dlc];
		for(int i = 0; i < m.dlc; i++) {
			payload[i] = stream[streamIndex++];
		}
		m.payload = payload;
		
		m.checksum = stream[streamIndex++];
		
	//	m.validPayload = getCRC(m.payload) == m.checksum;
		m.raw = stream;
		
		return m;
	}
	
	public void setPayload(byte[] bytes) {
		this.dlc = bytes.length;
		this.payload = bytes;
		
		if(this.payload.length > 0) {
			this.checksum = (byte) getChecksum(this.payload);
			this.validPayload = true;
		}
	}

	public static int getChecksum(byte[] packet) {
		int checksum = 0;
		for (int i = 0; i < packet.length; i++) {
			checksum = ChecksumPushByte(checksum, readUnsignedByte(packet[i]));
		}
		
		return checksum;
	}

	
	public void setChecksum()
	{
		int checksum = 0;
		checksum += stx;
		checksum += stx1;
		checksum += msgId;
		checksum += length;
		for (int i = 0; i < this.payload.length; i++)
		{
			checksum += this.payload[i];
		}
		
	}
	public static int ChecksumPushByte(int currentChecksum, int newByte) {

		currentChecksum = currentChecksum + newByte;
		return currentChecksum;
	}
	
	/**
	 * @param b		is the byte to convert
	 * @return a 	integer from the given byte
	 */
	static int readUnsignedByte(byte b) {
		return (b & 0xff);
	}
	
	/**
	 * Retrieves payload bytes from message
	 * 
	 * @return	Payload bytes from message
	 */
	public byte[] getBytes() {
		byte[] outBytes = new byte[this.payload.length + 5];
		int i = 0;
		
		outBytes[i++] = stx;
		outBytes[i++] = stx1;
		outBytes[i++] = (byte) length;
		for(int j = 0; j < this.payload.length; ++j) {
			outBytes[i++] = this.payload[j];
		}
		outBytes[i++] = (byte) (this.checksum & 0xff);
		outBytes[i++] = (byte) (this.checksum >> 8 & 0xff);

		
		/*Log.v(TAG, "OutBytes:");
		for(int j = 0; j < outBytes.length; ++j) {
			Log.v(TAG, "\t"+outBytes[j]);
		}*/
		
		return outBytes;
	}
}
