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

import com.t2.Constants;

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
 * 18 - 41			Spectral Data						<--- EXECODE_SPECTRAL_POS (8 * 3 bytes each big endian)
 * 42 - 			512 samples of raw data 
 * 
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
	static final byte EXECODE_ACCUM_MSG_POS = EXECODE_SPECTRAL_POS + 24; 
	

	public byte[] encode(SpineObject payload) throws MethodNotSupportedException {
		throw new MethodNotSupportedException("encode");
	};


	public SpineObject decode(Node node, byte[] payload) {

		if (node == null) {
			return null;
		}
		
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
		if (exeCode == Constants.EXECODE_SPECTRAL || exeCode == Constants.EXECODE_RAW_ACCUM) {
			int totalPower = 0;
			int maxBandPower = 0;
			for (int i = 0; i < MindsetData.NUM_BANDS; i++)	{
				int bandPower = convertThreeBytesToInt(payload, EXECODE_SPECTRAL_POS + (i * 3));
				totalPower += bandPower;
				if (bandPower > maxBandPower) {
					maxBandPower = bandPower;
				}
				data.rawSpectralData[i] = bandPower;
			}

//			if (maxBandPower > 0) {
//				// Now set up the ratio spectral band
//				for (int i = 0; i < MindsetData.NUM_BANDS; i++)	{
//					double power = (double) data.rawSpectralData[i] / (double) maxBandPower;
//					data.ratioSpectralData[i] = (int) (power * 100);
//				}		
//			}
			if (totalPower > 0) {
				// Now set up the ratio spectral band
				for (int i = 0; i < MindsetData.NUM_BANDS; i++)	{
					double band = (double) data.rawSpectralData[i] / (double) totalPower;
					data.ratioSpectralData[i] = (int) (band * 100);
				}		
			}
			
			if (exeCode == Constants.EXECODE_RAW_ACCUM) {
				// Now save the raw data
				int s = payload.length;
				int s1 = data.rawWaveData.length;
				int j = 0;
				for (int i = 0; i < Constants.RAW_ACCUM_SIZE; i++) {
					byte hi = payload[EXECODE_ACCUM_MSG_POS + j++];
					byte lo = payload[EXECODE_ACCUM_MSG_POS + j++];
					int value = ((hi << 8) & 0xff00) | (lo & 0xff);
					if( value >= 32768 ) value = value - 65536;					
					data.rawWaveData[i] = value;
				}
			}			
			
		}
		
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
