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
*
* This class contains the static method to parse (decompress) a 
* TinyOS SPINE 'Feature' Data packet payload into a platform independent one.
* This class is invoked only by the SpineData class, thru the dynamic class loading.
* 
* Note that this class is only used internally at the framework.
*
* @author Raffaele Gravina
* @author Alessia Salmeri
*
* @version 1.3
*/

package spine.payload.codec.tinyos;

import jade.util.Logger;

import java.util.Vector;

import spine.SPINEFunctionConstants;
import spine.SPINEManager;
import spine.SPINESensorConstants;

import spine.datamodel.Data;
import spine.datamodel.Feature;
import spine.datamodel.functions.*;
import spine.exceptions.*;

import spine.datamodel.*;

public class FeatureSpineData extends SpineCodec {
	
	public byte[] encode(SpineObject payload) throws MethodNotSupportedException{
		throw new MethodNotSupportedException("encode");
	};
	
	public SpineObject decode(Node node, byte[] payload) {
		byte[] dataTmp = new byte[579]; 
		short dtIndex = 0;
		short pldIndex = 0;
		
		// functionCode = payload[0];
		byte functionCode = payload[pldIndex++];
		dataTmp[dtIndex++] = functionCode;
		
		pldIndex++;
		
		// sensorCode = payload[2];
		byte sensorCode = payload[pldIndex++];
		dataTmp[dtIndex++] = sensorCode;
		
		//featuresCount = payload[3];
		byte featuresCount = payload[pldIndex++];
		dataTmp[dtIndex++] = featuresCount;
		
		byte currFeatCode, currSensBitmask;
		for (int i = 0; i<featuresCount; i++) {
			currFeatCode = payload[pldIndex++];
			dataTmp[dtIndex++] = currFeatCode;
			
			currSensBitmask = (byte)( (payload[pldIndex]>>4) & 0x0F );
			dataTmp[dtIndex++] = currSensBitmask;
			
			byte resultLen = (byte)(payload[pldIndex++] & 0x0F);					
			for (int j = 0; j<SPINESensorConstants.MAX_VALUE_TYPES; j++) {							
				if (SPINESensorConstants.chPresent(j, currSensBitmask)) {						
					if (resultLen == 1) {
						dataTmp[dtIndex++] = 0;
						dataTmp[dtIndex++] = 0; 
						dataTmp[dtIndex++] = 0; 
						dataTmp[dtIndex++] = payload[pldIndex++]; 
					}
					else if (resultLen == 2) {
						dataTmp[dtIndex++] = 0;
						dataTmp[dtIndex++] = 0; 
						dataTmp[dtIndex++] = payload[pldIndex++]; 
						dataTmp[dtIndex++] = payload[pldIndex++]; 
					}
					else if (resultLen == 3) {
						dataTmp[dtIndex++] = 0;
						dataTmp[dtIndex++] = payload[pldIndex++]; 
						dataTmp[dtIndex++] = payload[pldIndex++]; 
						dataTmp[dtIndex++] = payload[pldIndex++]; 
					}
					else if (resultLen == 4) {
						dataTmp[dtIndex++] = payload[pldIndex++];
						dataTmp[dtIndex++] = payload[pldIndex++]; 
						dataTmp[dtIndex++] = payload[pldIndex++]; 
						dataTmp[dtIndex++] = payload[pldIndex++]; 
					}	
				}
				else {
					dataTmp[dtIndex++] = 0;
					dataTmp[dtIndex++] = 0; 
					dataTmp[dtIndex++] = 0; 
					dataTmp[dtIndex++] = 0;
				}
			}
		}
				
		FeatureData data =  new FeatureData();
		
				
		try {
			
			// set data.nodeID, data.functionCode e data.timestamp
			data.baseInit(node, payload);
			
			Vector feats = new Vector();

			byte currBitmask;	
			int currCh1Value, currCh2Value, currCh3Value, currCh4Value;
			
			for (int i = 0; i<featuresCount; i++) {
				currFeatCode = dataTmp[3+i*18];
				currBitmask = dataTmp[(3+i*18) + 1];
			
				currCh1Value = Data.convertFourBytesToInt(dataTmp, (3+i*18) + 2);
				currCh2Value = Data.convertFourBytesToInt(dataTmp, (3+i*18) + 6);
				currCh3Value = Data.convertFourBytesToInt(dataTmp, (3+i*18) + 10);
				currCh4Value = Data.convertFourBytesToInt(dataTmp, (3+i*18) + 14);
							
				feats.addElement(new Feature(node, SPINEFunctionConstants.FEATURE, currFeatCode, sensorCode, currBitmask, currCh1Value, currCh2Value, currCh3Value, currCh4Value));			
			}
			
			data.setFeatures((Feature[]) feats.toArray(new Feature[0]));
			
		}
		catch (Exception e) {
			e.printStackTrace();
			if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) 
				SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
			data = null;
		}	
				
		return data;
	}
}
