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

package spine.payload.codec.emu;

import spine.SPINESensorConstants;
import spine.datamodel.functions.*;
import spine.exceptions.*;

import spine.datamodel.*;

public class BufferedRawDataSpineData extends SpineCodec {

	public byte[] encode(SpineObject payload) throws MethodNotSupportedException {
		throw new MethodNotSupportedException("encode");
	};

	public SpineObject decode(Node node, byte[] payload) {

		BufferedRawData data = new BufferedRawData();

		data.baseInit(node, payload);
		// data.setFunctionCode(SPINEFunctionConstants.BUFFERED_RAW_DATA);

		short pldIndex = 2;

		byte sensorCode = (byte) (payload[pldIndex] >> 4);
		data.setSensorCode(sensorCode);

		byte channelBitmask = (byte) (payload[pldIndex++] & 0x0F);
		data.setChannelBitmask(channelBitmask);

		byte dataWordLength = payload[pldIndex++];
		data.setDataWordLength(dataWordLength);

		pldIndex++; // skip MSB of the bufferSize because it will be always 0
		int bufferSize = payload[pldIndex++];

		int[][] values = new int[SPINESensorConstants.MAX_VALUE_TYPES][];

		byte[] dataTmp = new byte[4];
		for (int i = 0; i < SPINESensorConstants.MAX_VALUE_TYPES; i++) {
			if (SPINESensorConstants.chPresent(i, channelBitmask)) {
				values[i] = new int[bufferSize];
				for (int j = bufferSize - 1; j >= 0; j--) {
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
