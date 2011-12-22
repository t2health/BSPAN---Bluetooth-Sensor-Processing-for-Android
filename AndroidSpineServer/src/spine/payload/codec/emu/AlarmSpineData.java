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
 * NodeEmulator SPINE 'Alarm' Data packet payload into a platform independent one.
 * This class is invoked only by the SpineData class, thru the dynamic class loading.
 * 
 * Note that this class is used only internally at the framework.
 * 
 * @author Roberta Giannantonio
 * @author Alessia Salmeri
 *
 * @version 1.3
 */

package spine.payload.codec.emu;

import spine.datamodel.functions.*;

import spine.exceptions.*;

import spine.datamodel.*;

public class AlarmSpineData extends SpineCodec {

	public byte[] encode(SpineObject payload) throws MethodNotSupportedException {
		throw new MethodNotSupportedException("encode");
	};

	public SpineObject decode(Node node, byte[] payload) {

		AlarmData data = new AlarmData();

		// set data.node, data.functionCode e data.timestamp
		data.baseInit(node, payload);
		data.setDataType(payload[2]);
		data.setSensorCode(payload[3]);
		data.setValueType(payload[4]);
		data.setAlarmType(payload[5]);
		data.setCurrentValue(Data.convertFourBytesToInt(payload, 6));
		return data;
	}
}
