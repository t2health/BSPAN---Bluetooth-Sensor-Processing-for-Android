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
 * This class represents  Function Code information
 * It contains the logic for extract the function code  from a payload.
 * 
 * Note that this class is only used internally at the framework.   
 * 
 * @author Alessia Salmeri
 *
 * @version 1.3
 */

package spine.payload.codec.android;

import spine.datamodel.functions.CodecInfo;

public class CodecInformation implements CodecInfo {

	public byte getFunctionCode(byte[] payload) {
		return payload[0];
	}

	// New Refactoring ServiceMessage
	public byte getServiceMessageType(byte[] payload) {
		return payload[0];
	}

}
