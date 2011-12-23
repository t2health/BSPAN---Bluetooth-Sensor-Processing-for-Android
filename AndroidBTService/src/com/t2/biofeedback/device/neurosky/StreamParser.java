/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
allows dynamic on node configuration for feature extraction and a 
OtA protocol for the management for WSN

Copyright (C) 2007 Telecom Italia S.p.A. 
 
GNU Lesser General Public License
 
This library is free software; you can redistribute
modify it under the terms of the sub-license (below).

*****************************************************************/

/*****************************************************************
BSPAN - BlueTooth Sensor Processing for Android is a framework 
that extends the SPINE framework to work on Android and the 
Android Bluetooth communication services.

Copyright (C) 2011 The National Center for Telehealth and 
Technology

Eclipse Public License 1.0 (EPL-1.0)

This library is free software; you can redistribute it and/or
modify it under the terms of the Eclipse Public License as
published by the Free Software Foundation, version 1.0 of the 
License.

The Eclipse Public License is a reciprocal license, under 
Section 3. REQUIREMENTS iv) states that source code for the 
Program is available from such Contributor, and informs licensees 
how to obtain it in a reasonable manner on or through a medium 
customarily used for software exchange.

Post your updates and modifications to our GitHub or email to 
t2@tee2.org.

This library is distributed WITHOUT ANY WARRANTY; without 
the implied warranty of MERCHANTABILITY or FITNESS FOR A 
PARTICULAR PURPOSE.  See the Eclipse Public License 1.0 (EPL-1.0)
for more details.
 
You should have received a copy of the Eclipse Public License
along with this library; if not, 
visit http://www.opensource.org/licenses/EPL-1.0

*****************************************************************/

package com.t2.biofeedback.device.neurosky;



/*
 * @(#)StreamParser.java    0.9    Jun 04, 2008
 *
 * Copyright (c) 2008 NeuroSky, Inc. All Rights Reserved
 * NEUROSKY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/**
 * @file StreamParser.java
 *
 * The ThinkGear Stream Parser is used to parse bytes of ThinkGear data
 * streamed from any source.  Each arriving byte is fed into the Parser
 * using the parseByte() method, and a user supplied callback object
 * (a ThinkGearDataListener) is called whenever data values are available
 * from complete Packets.  Each Parser can be initialized to parse
 * either: streams of ThinkGear Packets, or streams of 2-byte raw values
 * (the old 5V ThinkGear stream format).
 *
 * @author Kelvin Soo
 * @version 0.9 Jun 04, 2008 Kelvin Soo
 *   - Initial version (ported from the version 2.0 Mar 04, 2008 
 *     C implementation of StreamParser).
 */
public class StreamParser {
	
	/* Parser types (for constructor) */
	/* Almost all implementations should use PARSER_TYPE_PACKETS as
	 * the argument to the constructor.  The PARSER_TYPE_2BYTERAW
	 * should only be used for special legacy (5V ThinkGear) support.
	 */
	public static final int PARSER_TYPE_PACKETS  = 0x01;
	public static final int PARSER_TYPE_2BYTERAW = 0x02;
	
	/* Member (state) variables */
	int type;
	int state;
	int lastByte;
	int payloadLength;
	int payloadBytesReceived;
	byte[] payload = new byte[256];
	int payloadSum;
	int chksum;
	DataListener listener = null;
	Object listenerData = null;

	/* Parser CODE definitions */
	/* Not actually used by this class.  Included for reference purposes
	 * only.
	 */
	public static final byte PARSER_CODE_BATTERY     = (byte) 0x01;
	public static final byte PARSER_CODE_POOR_SIGNAL = (byte) 0x02;
	public static final byte PARSER_CODE_ATTENTION   = (byte) 0x04;
	public static final byte PARSER_CODE_MEDITATION  = (byte) 0x05;
	public static final byte PARSER_CODE_8BIT_RAW    = (byte) 0x06;
	public static final byte PARSER_CODE_RAW_MARKER  = (byte) 0x07;
	public static final byte PARSER_CODE_RAW         = (byte) 0x80;
	public static final byte PARSER_CODE_EEG_POWERS  = (byte) 0x81;
	
	/* Internal parser states for PARSER_TYPE_PACKETS type (private) */
	private static final byte PARSER_STATE_SYNC           = (byte) 0x01;
	private static final byte PARSER_STATE_SYNC_CHECK     = (byte) 0x02;
	private static final byte PARSER_STATE_PAYLOAD_LENGTH = (byte) 0x03;
	private static final byte PARSER_STATE_PAYLOAD        = (byte) 0x04;
	private static final byte PARSER_STATE_CHKSUM         = (byte) 0x05;
    
	/* Internal parser states for PARSER_TYPE_2BYTERAW type (private) */
	private static final byte PARSER_STATE_WAIT_HIGH      = (byte) 0x06;
	private static final byte PARSER_STATE_WAIT_LOW       = (byte) 0x07;
	
	/* Other internal constants (private) */
	private static final byte PARSER_SYNC_BYTE   = (byte) 0xAA;
	private static final byte PARSER_EXCODE_BYTE = (byte) 0x55;
	
	/**
	 * Constructs a ThinkGear parser object of the given @c parserType,
	 * and registers the given @c listener.
	 * 
	 * @param parserType   One of the PARSER_TYPE_* constants, either:
	 *                       PARSER_TYPE_PACKETS (ThinkGear-EM)
	 *                       PARSER_TYPE_2BYTERAW (legacy 5V support only)
	 * @param listener     An object that implements the ThinkGearListener
	 *                     interface, which implements the dataValueReceived()
	 *                     event handler (callback method).
	 * @param listenerData Any arbitrary object that should be passed to the
	 *                     dataValueReceived() method as well.  This parameter
	 *                     is ot really necessary in Java, but is included 
	 *                     here for consistency with the C implementation of 
	 *                     this StreamParser class.
	 */
	public StreamParser( int parserType, DataListener listener, 
						   Object listenerData ) {
		this.type = parserType;
		switch( parserType ) {
			case( PARSER_TYPE_PACKETS ):
				this.state = PARSER_STATE_SYNC;
			    break;
			case( PARSER_TYPE_2BYTERAW ):
				this.state = PARSER_STATE_WAIT_HIGH;
			    break;
			default:
				throw new IllegalArgumentException( "Invalid parserType" );
		}
		this.listener = listener;
		this.listenerData = listenerData;
	}
	
	/**
	 * Parses the byte @b as part of a ThinkGear data stream.
	 * 
	 * @param b The byte from the ThinkGear data stream to parse.
	 * 
	 * @return -2 if a complete Packet was received, but the checksum failed.
	 * @return 0 if the @c byte did not yet complete a Packet.
	 * @return 1 if a Packet was received and parsed successfully. 
	 */
	public int parseByte( byte b ) {

		int returnValue = 0;
		
		switch( state ) {

			case( PARSER_STATE_SYNC ):
		    	if( b == PARSER_SYNC_BYTE ) 
		    		state = PARSER_STATE_SYNC_CHECK;
		    	break;

			case( PARSER_STATE_SYNC_CHECK ):
				if( b == PARSER_SYNC_BYTE ) 
					state = PARSER_STATE_PAYLOAD_LENGTH;
				else                        
					state = PARSER_STATE_SYNC;
				break;

			case( PARSER_STATE_PAYLOAD_LENGTH ):
				payloadLength = b;
				payloadBytesReceived = 0;
				payloadSum = 0;
				state = PARSER_STATE_PAYLOAD;
				break;

			case( PARSER_STATE_PAYLOAD ):
				payload[payloadBytesReceived++] = (byte)b;
				payloadSum += b;
				if( payloadBytesReceived >= payloadLength ) {
					state = PARSER_STATE_CHKSUM;
				}
				break;

			case( PARSER_STATE_CHKSUM ):
				chksum = b;
				state = PARSER_STATE_SYNC;
				int i = (~payloadSum & 0xFF);
				
				if( (chksum & 0xFF) != (~payloadSum & 0xFF) )
				{
					returnValue = -2;
				}
				else 
				{
					returnValue = 1;
					parsePacketPayload();
				}
				break;

			case( PARSER_STATE_WAIT_HIGH ):
				break;

			case( PARSER_STATE_WAIT_LOW ):
				break;
			
			default:
				throw new RuntimeException( "Illegal state: "+state );
		}
		
		/* Save current byte */
		lastByte = b;
		
		return( returnValue );
	}
	
	/**
	 * Parses a Packet's array of payload[] bytes into DataRows.  The 
	 * parser's registered listener's @c dataValueReceived() method is 
	 * invoked to handle (the components of) each DataRow.
	 */
	private void parsePacketPayload() {
		
		int i = 0;
		int extendedCodeLevel = 0;
		int code = 0;
		int numBytes = 0;
		
		/* Parse all bytes from payload[] */
		while( i < payloadLength ) {
			
			/* Parse possible EXtended CODE bytes */
			while( payload[i] == PARSER_EXCODE_BYTE ) {
				extendedCodeLevel++;
				i++;
			}
			
			/* Parse CODE */
			code = payload[i++] & 0xFF;
			
			/* Parse value length */
			if( code >= 0x80 ) 
				numBytes = payload[i++] & 0xFF;
			else               
				numBytes = 1;
			
			/* Call the callback function to handle the DataRow value */
			byte[] valueBytes = new byte[numBytes];
			
			for(int j = 0; j < numBytes; j++)
				valueBytes[j] = (byte)(payload[i + j] & 0xFF);
			
			if( listener != null ) {
				listener.dataValueReceived( extendedCodeLevel, code, numBytes,
											valueBytes, listenerData );
			}
			i += numBytes;
		}
	}

	/**
	 * Assuming the @c bytes array contains the 4 bytes of an IEEE 754
	 * floating point number in network byte order (big-endian), this
	 * function returns the corresponding Java floating point number.
	 * 
	 * @param bytes An array of 4 bytes corresponding to the 4 bytes
	 *              of an IEEE 754 floating point number in network
	 *              byte order (big-endian).
	 *              
	 * @return The Java floating point number corresponding to the
	 *         IEEE 754 floating poing number in @c bytes.
	 */
	public static float bigEndianBytesToFloat( byte[] bytes ) {

		int bits = (bytes[0] & 0xFF) << 0  |
				   (bytes[1] & 0xFF) << 8  |
				   (bytes[2] & 0xFF) << 16 |
				   (bytes[3] & 0xFF) << 24;
		
		return( Float.intBitsToFloat(bits) );
	}
}

