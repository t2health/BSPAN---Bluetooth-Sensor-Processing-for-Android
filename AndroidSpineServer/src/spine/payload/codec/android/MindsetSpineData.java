package spine.payload.codec.android;

import spine.datamodel.MindsetData;
import spine.datamodel.Node;
import spine.datamodel.functions.SpineCodec;
import spine.datamodel.functions.SpineObject;
import spine.exceptions.MethodNotSupportedException;

/**
 * Message format:
 * Byte				Contents
 * -----------------------------
 * 0 - 8			Spine Header
 * 9				MINDSET_FUNCT_CODE (0x0A)         <-- Payload
 * 10				MINDSET_SENSOR_CODE (0x0D)
 * 11				Exe Code 
 * 12				Signal Quality                    	<--- EXECODE_POOR_SIG_QUALITY_POS
 * 13				Attention							<--- EXECODE_ATTENTION_POS
 * 14				Meditation							<--- EXECODE_MEDITATION_POS
 * 15				Blink Strength						<--- EXECODE_BLINK_STRENGTH_POS
 * 16 - 17			Raw Data							<--- EXECODE_RAW_POS
 * 16 - 40			Spectral Data						<--- EXECODE_SPECTRAL_POS (8 * 3 bytes each big endian)
 */



public class MindsetSpineData extends SpineCodec {
	
	// Note that each Spine mindset message has all of the mindset attribuites
	// Define the hard positions in the Mindset message of each of the attributes
	static final int MINDSET_PREMSG_SIZE 				    = 3;   // 	3 bytes in front of every payload, MINDSET_FUNCT_CODE, MINDSET_SENSOR_CODE, EXECode)	
	
	static final byte PAYLOAD_POS = 0; 		// Note that the Spine header has already been stripped off here
	static final byte EXECODE_POOR_SIG_QUALITY_POS = PAYLOAD_POS + MINDSET_PREMSG_SIZE + 0; // 3
	static final byte EXECODE_ATTENTION_POS = EXECODE_POOR_SIG_QUALITY_POS + 1; 			// 4
	static final byte EXECODE_MEDITATION_POS = EXECODE_ATTENTION_POS + 1; 					// 5
	static final byte EXECODE_BLINK_STRENGTH_POS = EXECODE_MEDITATION_POS + 1; 				// 6
	static final byte EXECODE_RAW_POS = EXECODE_BLINK_STRENGTH_POS + 1; 					// 7
	static final byte EXECODE_SPECTRAL_POS = EXECODE_RAW_POS + 2; 							// 9

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
		data.delta = convertThreeBytesToInt(payload, EXECODE_SPECTRAL_POS + (i++ * 3));
		data.theta = convertThreeBytesToInt(payload, EXECODE_SPECTRAL_POS + (i++ * 3));
		data.lowAlpha = convertThreeBytesToInt(payload, EXECODE_SPECTRAL_POS + (i++ * 3));
		data.highAlpha = convertThreeBytesToInt(payload, EXECODE_SPECTRAL_POS + (i++ * 3));
		data.lowBeta = convertThreeBytesToInt(payload, EXECODE_SPECTRAL_POS + (i++ * 3));
		data.highBeta = convertThreeBytesToInt(payload, EXECODE_SPECTRAL_POS + (i++ * 3));
		data.lowGamma = convertThreeBytesToInt(payload, EXECODE_SPECTRAL_POS + (i++ * 3));
		data.midGamma = convertThreeBytesToInt(payload, EXECODE_SPECTRAL_POS + (i++ * 3));

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
