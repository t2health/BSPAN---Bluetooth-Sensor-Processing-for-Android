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
