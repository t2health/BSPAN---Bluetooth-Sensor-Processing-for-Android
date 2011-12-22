/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
allows dynamic configuration of feature extraction capabilities 
of WSN nodes via an OtA protocol

Copyright (C) 2007 Telecom Italia S.p.A. 
 
GNU Lesser General Public License
 
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 
 
This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.
 
You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

/**
* This class contains the static method to parse (decompress) a 
* TinyOS SPINE 'Buffered Raw-Data' Data packet payload into a platform independent one.
* This class is invoked only by the SpineData class, thru the dynamic class loading.
* 
* Note that this class is used only internally at the framework.
* 
* @author Raffaele Gravina
* @author Alessia Salmeri
*
* @version 1.3
*/

package spine.payload.codec.tinyos;

import spine.SPINESensorConstants;
import spine.datamodel.BufferedRawData;
import spine.datamodel.Data;
import spine.datamodel.Node;
import spine.datamodel.functions.SpineCodec;
import spine.datamodel.functions.SpineObject;
import spine.exceptions.MethodNotSupportedException;
import spine.exceptions.PacketDecodingException;

public class BufferedRawDataSpineData extends SpineCodec {
	
	public byte[] encode(SpineObject payload) throws MethodNotSupportedException {
		throw new MethodNotSupportedException("encode");
	};
	
	public SpineObject decode(Node node, byte[] payload) throws PacketDecodingException {
				
		BufferedRawData data =  new BufferedRawData();
		
		data.baseInit(node, payload);
		//data.setFunctionCode(SPINEFunctionConstants.BUFFERED_RAW_DATA);
		
		short pldIndex = 2;
		
		byte sensorCode = (byte)(payload[pldIndex]>>4);
		data.setSensorCode(sensorCode);
		
		byte channelBitmask = (byte)(payload[pldIndex++] & 0x0F);
		data.setChannelBitmask(channelBitmask);
		
		byte dataWordLength = payload[pldIndex++];
		data.setDataWordLength(dataWordLength);
		
		pldIndex++; // skip MSB of the bufferSize because it will be always 0 
		int bufferSize = payload[pldIndex++];
		
		int[][] values = new int[SPINESensorConstants.MAX_VALUE_TYPES][];
		
		// if the actual sensor readings data ((payload.length - pldIndex) bytes) 
		// in the payload is less than what is declared (by channelBitmask, bufferSize, and dataWordLength) 
		// then this message is somehow malformed or corrupted.
		if(((payload.length - pldIndex) < 
				(SPINESensorConstants.countChannelsInBitmask(channelBitmask) * bufferSize * dataWordLength)) ) 
			throw new PacketDecodingException("Malformed or corrupted BufferedRawData message received " +
											  "[from node: " + node.getPhysicalID()+"]");
		
		byte[] dataTmp = new byte[4];		
		for (int i = 0; i<SPINESensorConstants.MAX_VALUE_TYPES; i++) {							
			if (SPINESensorConstants.chPresent(i, channelBitmask)) {				
				values[i] = new int[bufferSize];
				for (int j = bufferSize-1; j >= 0; j--) {
					if (dataWordLength == 1) {
						dataTmp[3] = payload[pldIndex++];
						dataTmp[2] = 0;
						dataTmp[1] = 0;
						dataTmp[0] = 0;
						
					} else if (dataWordLength == 2) {
						dataTmp[3] = payload[pldIndex++];
						dataTmp[2] = payload[pldIndex++];
						dataTmp[1] = 0;
						dataTmp[0] = 0;						
					} else if (dataWordLength == 3) {
						dataTmp[3] = payload[pldIndex++];
						dataTmp[2] = payload[pldIndex++];
						dataTmp[1] = payload[pldIndex++];
						dataTmp[0] = 0;
					} else if (dataWordLength == 4) {
						dataTmp[3] = payload[pldIndex++];
						dataTmp[2] = payload[pldIndex++];
						dataTmp[1] = payload[pldIndex++];
						dataTmp[0] = payload[pldIndex++];
					}
					
					values[i][j] = Data.convertFourBytesToInt(dataTmp, 0);
				}
			}
		}
		
		data.setValues(values);
		
		return data;
	}
}
