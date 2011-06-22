package spine.payload.codec.android;

import jade.util.Logger;

import java.util.Vector;

import spine.SPINEFunctionConstants;
import spine.SPINEManager;
import spine.SPINESensorConstants;
import spine.datamodel.Data;
import spine.datamodel.Feature;
import spine.datamodel.FeatureData;
import spine.datamodel.MindsetData;
import spine.datamodel.Node;
import spine.datamodel.functions.SpineCodec;
import spine.datamodel.functions.SpineObject;
import spine.exceptions.MethodNotSupportedException;

public class MindsetSpineData extends SpineCodec {

	public byte[] encode(SpineObject payload) throws MethodNotSupportedException {
		throw new MethodNotSupportedException("encode");
	};

	private int MAX_MSG_LENGHT = 42;

	public SpineObject decode(Node node, byte[] payload) {

		if (node == null)
			return null;		
		
		byte[] dataTmp = new byte[MAX_MSG_LENGHT];
		short dtIndex = 0;
		short pldIndex = 0;

		byte functionCode = payload[pldIndex++];
		byte sensorCode = payload[pldIndex++];
		byte exeCode = payload[pldIndex++];
		byte poorSignalStrength = payload[pldIndex++];
		byte attention = payload[pldIndex++];
		byte meditation = payload[pldIndex++];
		byte blinkStrength = payload[pldIndex++];

		MindsetData data = new MindsetData(functionCode, sensorCode, exeCode, poorSignalStrength, attention, meditation, blinkStrength);

		// set data.node, data.functionCode and data.timestamp
		data.baseInit(node, payload);

		int i = 0;
		data.delta = convertThreeBytesToInt(payload, 5 + (i++ * 3));
		data.theta = convertThreeBytesToInt(payload, 5 + (i++ * 3));
		data.lowAlpha = convertThreeBytesToInt(payload, 5 + (i++ * 3));
		data.highAlpha = convertThreeBytesToInt(payload, 5 + (i++ * 3));
		data.lowBeta = convertThreeBytesToInt(payload, 5 + (i++ * 3));
		data.highBeta = convertThreeBytesToInt(payload, 5 + (i++ * 3));
		data.lowGamma = convertThreeBytesToInt(payload, 5 + (i++ * 3));
		data.midGamma = convertThreeBytesToInt(payload, 5 + (i++ * 3));

		return data;
	}

	/**
	 * Converts the three following bytes in the array 'bytes' starting from the index 'index' 
	 * into the corresponding integer
	 * 
	 * @param bytes the byte array from where to take the 3 bytes to be converted to an integer 
	 * @param index the starting index on the interested portion to convert
	 * 
	 * @return the converted integer
	 */  
	public static int convertThreeBytesToInt(byte[] bytes, int index) {    
		if(bytes.length < 3) return 0;
		
		return ( bytes[index + 2] & 0xFF) 		 |
	           ((bytes[index + 1] & 0xFF) << 8)  |
	           ((bytes[index] & 0xFF) << 16);
	}
	
	
}
