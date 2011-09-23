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

public class ShimmerNonSpineSetupSensor extends SpineCodec {
	
	// Note that each Spine mindset message has all of the Shimmer attribuites
	// Define the hard positions in the Shimmer message of each of the attributes
	static final int SHIMMER_PREMSG_SIZE = 3;   // 	3 bytes in front of every payload, 
												// SHIMMER_FUNCT_CODE, SHIMMER_SENSOR_CODE, Pkt Type)	
	
	static final byte PAYLOAD_POS = 0; 		// Note that the Spine header has already been stripped off here
	static final byte GSR_POS = PAYLOAD_POS + SHIMMER_PREMSG_SIZE + 0; // 3
	

	public byte[] encode(SpineObject payload) throws MethodNotSupportedException {
//		throw new MethodNotSupportedException("encode");
		return new byte[] {1,2,3,4};
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
