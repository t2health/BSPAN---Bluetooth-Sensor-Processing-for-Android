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
* NodeEmulator SPINE 'OneShot' Data packet payload into a platform independent one.
* This class is invoked only by the SpineData class, thru the dynamic class loading.
* 
* Note that this class is only used internally at the framework.
*
* @author Raffaele Gravina
* @author Alessia Salmeri
*
* @version 1.3
*/

package spine.payload.codec.emu;

import jade.util.Logger;
import spine.SPINEFunctionConstants;
import spine.SPINEManager;
import spine.SPINESensorConstants;

import spine.datamodel.Feature;
import spine.datamodel.functions.*;

import spine.exceptions.*;

import spine.datamodel.*;

public class OneShotSpineData extends SpineCodec {
	
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
		
		// sensorCode = payload[1];
		byte sensorCode = payload[pldIndex++];
		dataTmp[dtIndex++] = sensorCode;
		
		// bitmask = payload[2];
		byte bitmask = payload[pldIndex++];
		dataTmp[dtIndex++] = bitmask;				
		
		
		for (int j = 0; j<SPINESensorConstants.MAX_VALUE_TYPES; j++) {							
			if (SPINESensorConstants.chPresent(j, bitmask)) {						
					dataTmp[dtIndex++] = payload[pldIndex++]; 
					dataTmp[dtIndex++] = payload[pldIndex++]; 
			}
			else {
				dataTmp[dtIndex++] = 0; 
				dataTmp[dtIndex++] = 0;
			}
		}
		
		OneShotData data = new OneShotData();
		
		try {
			
			// set data.node, data.functionCode e data.timestamp
			data.baseInit(node, payload);
		
			int currCh1Value = Data.convertTwoBytesToInt(dataTmp, 3);
			int currCh2Value = Data.convertTwoBytesToInt(dataTmp, 5);
			int currCh3Value = Data.convertTwoBytesToInt(dataTmp, 7);
			int currCh4Value = Data.convertTwoBytesToInt(dataTmp, 9);
					
			data.setOneShot(new Feature(node, SPINEFunctionConstants.ONE_SHOT, SPINEFunctionConstants.RAW_DATA, sensorCode, bitmask, currCh1Value, currCh2Value, currCh3Value, currCh4Value));
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
