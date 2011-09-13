package spine.payload.codec.android;

import com.t2.Constants;

import spine.datamodel.MindsetData;
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

public class ShimmerSpineData extends SpineCodec {
	
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
		int gsr = payload[pldIndex++];
		gsr += ((int)payload[pldIndex++] << 8) & 0xFF00;

		ShimmerData data = new ShimmerData(functionCode, sensorCode, packetType);
		data.gsr = gsr;

		// set data.node, data.functionCode and data.timestamp
		data.baseInit(node, payload);

		return data;
	}
}
