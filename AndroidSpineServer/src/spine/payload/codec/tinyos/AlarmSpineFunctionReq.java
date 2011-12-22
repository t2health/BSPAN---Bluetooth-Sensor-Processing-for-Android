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
* (both activation and deactivation) of type 'Alarm'.
* An application that needs to do an Alarm request, must create a new AlarmSpineFunctionReq
* object for alarm activation, or deactivation.
* 
* This class also implements the encode method of the abstract class SpineFunctionReq that is used internally
* to convert the high level request into an actual SPINE Ota message.     
*
* Note that this class is used only internally at the framework.
*
*
* @author Roberta Giannantonio
* @author Alessia Salmeri
*
* @version 1.3
*/

package spine.payload.codec.tinyos;

import spine.SPINEFunctionConstants;
//import spine.SPINESensorConstants;

import spine.datamodel.Node;
import spine.datamodel.functions.*;
import spine.exceptions.*;


public class AlarmSpineFunctionReq extends SpineCodec {

		public SpineObject decode(Node node, byte[] payload)throws MethodNotSupportedException{
			throw new MethodNotSupportedException("decode");
		};
	    

		public byte[] encode(SpineObject payload) {
			
			spine.datamodel.functions.AlarmSpineFunctionReq workPayLoad = (spine.datamodel.functions.AlarmSpineFunctionReq)payload;
			
			byte[] data = new byte[1 + 1 + 1 + 1 + 1 + 1 + 4 + 4 + 1];
	
			byte activationBinaryFlag = (workPayLoad.getActivationFlag())? (byte)1 : 0;
			
			data[0] = SPINEFunctionConstants.ALARM; 
			
			data[1] = activationBinaryFlag;
			data[2] = (byte)(12);
				
			data[3] = workPayLoad.getDataType();
			data[4] = workPayLoad.getSensor();
			data[5] = workPayLoad.getValueType();
			
			//lower Threshold 			
			data[6] = (byte) (workPayLoad.getLowerThreshold() >> 24);
			data[7] = (byte) (workPayLoad.getLowerThreshold() >> 16);
			data[8] = (byte) (workPayLoad.getLowerThreshold() >> 8);
			data[9] = (byte) (workPayLoad.getLowerThreshold());
			
			//upper Threshold 			
			data[10] = (byte) (workPayLoad.getUpperThreshold() >> 24);
			data[11] = (byte) (workPayLoad.getUpperThreshold() >> 16);
			data[12] = (byte) (workPayLoad.getUpperThreshold()>> 8);
			data[13] = (byte) (workPayLoad.getUpperThreshold());
			
			data[14] = workPayLoad.getAlarmType();
					
			return data;		
		}		
}
