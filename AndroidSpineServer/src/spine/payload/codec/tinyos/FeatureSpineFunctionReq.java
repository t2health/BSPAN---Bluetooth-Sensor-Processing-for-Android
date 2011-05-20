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
* Objects of this class are used for expressing at high level function requests 
* (both activation and deactivation) of type 'Feature'.
* An application that needs to do a Feature request, must create a new FeatureSpineFunctionReq
* object, set on it the sensor involved and use the addFeature one or more times 
* (currently, up to 7 add are supported per each request) 
* for features activation, or the removeFeature (currently, up to 7 remove are supported per each request) 
* for features deactivation.
* 
* This class also implements the encode method of the abstract class SpineFunctionReq that is used internally
* to convert the high level request into an actual SPINE Ota message. 
* 
* Note that this class is used only internally at the framework.
*
* @author Raffaele Gravina
* @author Alessia Salmeri
*
* @version 1.3
*/

package spine.payload.codec.tinyos;


import spine.SPINEFunctionConstants;
import spine.datamodel.Feature;
import spine.datamodel.Node;

import spine.datamodel.functions.*;
import spine.exceptions.*;


public class FeatureSpineFunctionReq extends SpineCodec {

	public SpineObject decode(Node node, byte[] payload)throws MethodNotSupportedException{
		throw new MethodNotSupportedException("decode");
	};
    
	public byte[] encode(SpineObject payload) {
		 
		   spine.datamodel.functions.FeatureSpineFunctionReq workPayLoad = (spine.datamodel.functions.FeatureSpineFunctionReq)payload;
			
			int featuresCount = workPayLoad.getFeatures().size();
			
			byte[] data = new byte[1 + 1 + 1 + 1 + 1 + featuresCount*2];
			
			byte activationBinaryFlag = (workPayLoad.getActivationFlag())? (byte)1 : 0;
			
			data[0] = SPINEFunctionConstants.FEATURE; 
			
			data[1] = activationBinaryFlag;
			
			data[2] = (byte)(1 + 1 + featuresCount*2);
						
			data[3] = workPayLoad.getSensor();
			
			data[4] = (byte)featuresCount;
					
			for (int i = 0; i < featuresCount; i++) {
				
				data[(5+i*2)] = ((Feature)workPayLoad.getFeatures().elementAt(i)).getFeatureCode();
				data[(5+i*2)+1] = ((Feature)workPayLoad.getFeatures().elementAt(i)).getChannelBitmask();

			}
			
			return data;		
		}
}

