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

package spine.payload.codec.android;

import spine.datamodel.Node;
import spine.datamodel.ShimmerData;
import spine.datamodel.functions.SpineCodec;
import spine.datamodel.functions.SpineObject;
import spine.exceptions.MethodNotSupportedException;

/**
 * Message format:
 * Byte				Contents
 * -----------------------------
 * 0 - 8			Spine Header
 * 9				SHIMMER_FUNCT_CODE (0x0A)         <-- Payload
 * 10				SHIMMER_SENSOR_CODE (0x0D)
 * 11			    Packet Type		
 * 12 -13		    gsr		
 */

public class SpineSetupSensor extends SpineCodec {
	
	// Note that each Spine mindset message has all of the Shimmer attribuites
	// Define the hard positions in the Shimmer message of each of the attributes
	static final int SHIMMER_PREMSG_SIZE = 3;   // 	3 bytes in front of every payload, 
												// SHIMMER_FUNCT_CODE, SHIMMER_SENSOR_CODE, Pkt Type)	
	
	static final byte PAYLOAD_POS = 0; 		// Note that the Spine header has already been stripped off here
	static final byte GSR_POS = PAYLOAD_POS + SHIMMER_PREMSG_SIZE + 0; // 3
	

	public byte[] encode(SpineObject payload) throws MethodNotSupportedException {
		throw new MethodNotSupportedException("encode");
	};

	public SpineObject decode(Node node, byte[] payload) {

		if (node == null) {
			return null;
		}
		
		short pldIndex = 0;

		byte functionCode = payload[pldIndex++];
		byte sensorCode = payload[pldIndex++];
		byte packetType = payload[pldIndex++];

		ShimmerData data = new ShimmerData(functionCode, sensorCode, packetType);
		
		
		data.timestamp = convertTwoBytesToInt(payload, pldIndex);
		pldIndex += 2;
		data.accel[ShimmerData.AXIS_X] = convertTwoBytesToInt(payload, pldIndex);
		pldIndex += 2;
		data.accel[ShimmerData.AXIS_Y] = convertTwoBytesToInt(payload, pldIndex);
		pldIndex += 2;
		data.accel[ShimmerData.AXIS_Z] = convertTwoBytesToInt(payload, pldIndex);
		pldIndex += 2;

		
		// We need to treat gsr special since in it's top 2 bits it has
		// the gsr range coded into it (For Auto range only)
		data.gsr = ( payload[pldIndex] & 0xFF)| ((payload[pldIndex + 1] & 0x3F) << 8);
		data.gsrRange = ((payload[pldIndex + 1] & 0xC0) >> 6);
//		data.gsr = convertTwoBytesToInt(payload, pldIndex);

		// set data.node, data.functionCode and data.timestamp
		data.baseInit(node, payload);

		return data;
	}
	
	
	public static int convertTwoBytesToInt(byte[] bytes, int index) {    
		if(bytes.length < 2) return 0;
		
		return ( bytes[index] & 0xFF) 		 |
        ((bytes[index + 1] & 0xFF) << 8);
	}
//	byte hi = payload[EXECODE_ACCUM_MSG_POS + j++];
//	byte lo = payload[EXECODE_ACCUM_MSG_POS + j++];
//	int value = ((hi << 8) & 0xff00) | (lo & 0xff);
//	if( value >= 32768 ) value = value - 65536;		
	
}
