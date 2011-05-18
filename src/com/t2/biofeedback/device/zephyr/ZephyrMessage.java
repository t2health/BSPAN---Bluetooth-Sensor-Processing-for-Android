package com.t2.biofeedback.device.zephyr;

import com.t2.biofeedback.Constants;

public class ZephyrMessage {
	private static final String TAG = Constants.TAG;
	
	private static final byte STX = 0x02;
	
	public static final byte ETX = 0x03;
	public static final byte ACK = 0x06;
	public static final byte NAK = 0x15;
	
	public byte stx = STX;
	public int msgId;
	public int dlc;
	public byte[] payload = new byte[0];
	public byte crc = 0x00;
	public byte end;
	public boolean validPayload = false;
	
	public byte[] raw;
	
	public ZephyrMessage() {
		
	}
	
	public ZephyrMessage(int msgId, byte[] payload, byte end) {
		this.msgId = msgId;
		this.setPayload(payload);
		this.end = end;
	}
	
	public static ZephyrMessage parse(byte[] stream) {
		int streamIndex = 0;
		ZephyrMessage m = new ZephyrMessage();
		
		m.stx = stream[streamIndex++];
		m.msgId = stream[streamIndex++];
		m.dlc = stream[streamIndex++];
		
		byte[] payload = new byte[m.dlc];
		for(int i = 0; i < m.dlc; i++) {
			payload[i] = stream[streamIndex++];
		}
		m.payload = payload;
		
		m.crc = stream[streamIndex++];
		m.end = stream[streamIndex++];
		
		m.validPayload = getCRC(m.payload) == m.crc;
		m.raw = stream;
		
		return m;
	}
	
	public void setPayload(byte[] bytes) {
		this.dlc = bytes.length;
		this.payload = bytes;
		
		if(this.payload.length > 0) {
			this.crc = (byte) getCRC(this.payload);
			this.validPayload = true;
		}
	}

	public static int getCRC(byte[] packet) {
		int crc = 0;
		for (int i = 0; i < packet.length; i++) {
			crc = ChecksumPushByte(crc, readUnsignedByte(packet[i]));
		}
		
		return crc;
	}

	/** CRC check taken from Zephyr PDF's */
	public static int ChecksumPushByte(int currentChecksum, int newByte) {

		currentChecksum = (currentChecksum ^ newByte);

		for (int bit = 0; bit < 8; bit++) {

			if ((currentChecksum & 1) == 1)
				currentChecksum = ((currentChecksum >> 1) ^ 0x8C);

			else
				currentChecksum = (currentChecksum >> 1);
		}

		return currentChecksum;
	}
	
	/**
	 * @param b
	 *            is the byte to convert
	 * @return a integer from the given byte
	 */
	static int readUnsignedByte(byte b) {
		return (b & 0xff);
	}
	
	
	public byte[] getBytes() {
		byte[] outBytes = new byte[this.payload.length + 5];
		int i = 0;
		
		outBytes[i++] = stx;
		outBytes[i++] = (byte) this.msgId;
		outBytes[i++] = (byte) this.dlc;
		for(int j = 0; j < this.payload.length; ++j) {
			outBytes[i++] = this.payload[j];
		}
		outBytes[i++] = this.crc;
		outBytes[i++] = this.end;
		
		/*Log.v(TAG, "OutBytes:");
		for(int j = 0; j < outBytes.length; ++j) {
			Log.v(TAG, "\t"+outBytes[j]);
		}*/
		
		return outBytes;
	}
}
