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


// Note that we did a little shortcut here. Technically
// the Zephyr device should define it's own Feature type\
// and store batt level, heart rate, resp. rate and skin temp.
// in their own fields. Instead I'm re-using the regular Feature
// class and storing the data in fields chxValue.

// Also, instead of using channels, we probably  want to use
// seperate features instead
public class ZephyrSpineData extends SpineCodec {

	public byte[] encode(SpineObject payload) throws MethodNotSupportedException {
		throw new MethodNotSupportedException("encode");
	};

	private int MAX_MSG_LENGHT = 2500;

	private byte MAX_LABEL_LENGTH = 127;

	public SpineObject decode(Node node, byte[] payload) {

		if (node == null)
			return null;
		
		byte[] dataTmp = new byte[MAX_MSG_LENGHT];
		short dtIndex = 0;
		short pldIndex = 0;

		// functionCode = payload[0];
		byte functionCode = payload[pldIndex++];
		dataTmp[dtIndex++] = functionCode;

		// sensorCode = payload[1];
		byte sensorCode = payload[pldIndex++];
		dataTmp[dtIndex++] = sensorCode;

		// featuresCount = payload[2];
		byte featuresCount = payload[pldIndex++];
		dataTmp[dtIndex++] = featuresCount;

		byte currFeatCode, currSensBitmask;
		byte currFeatLabLenght, currFeatLabLengthBlank;
		for (int i = 0; i < featuresCount; i++) {
			currFeatCode = payload[pldIndex++];
			dataTmp[dtIndex++] = currFeatCode;

			currSensBitmask = payload[pldIndex++];
			dataTmp[dtIndex++] = currSensBitmask;

			// 4 bytes for each channel
			for (int j = 0; j < SPINESensorConstants.MAX_VALUE_TYPES; j++) {
				if (SPINESensorConstants.chPresent(j, currSensBitmask)) {
					dataTmp[dtIndex++] = payload[pldIndex++];
					dataTmp[dtIndex++] = payload[pldIndex++];
					dataTmp[dtIndex++] = payload[pldIndex++];
					dataTmp[dtIndex++] = payload[pldIndex++];

				} else {
					dataTmp[dtIndex++] = 0;
					dataTmp[dtIndex++] = 0;
					dataTmp[dtIndex++] = 0;
					dataTmp[dtIndex++] = 0;
				}
			}

			// featureLabel
			currFeatLabLenght = payload[pldIndex++];
			for (int k = 0; k < currFeatLabLenght; k++) {
				dataTmp[dtIndex++] = payload[pldIndex++];
			}
			// MAX_LABEL_LENGTH bytes for each featureLabel
			currFeatLabLengthBlank = (byte) (MAX_LABEL_LENGTH - currFeatLabLenght);
			for (int z = 0; z < currFeatLabLengthBlank; z++) {
				dataTmp[dtIndex++] = 0;
			}

		}

		FeatureData data = new FeatureData();

		try {

			// set data.node, data.functionCode and data.timestamp
			data.baseInit(node, payload);

			Vector feats = new Vector();

			Feature featureWork;
			byte currBitmask;
			int currCh1Value, currCh2Value, currCh3Value, currCh4Value;
			String currFeatureLabel;
			int blockLength = 18 + MAX_LABEL_LENGTH;

			for (int i = 0; i < featuresCount; i++) {

				currFeatCode = dataTmp[3 + i * blockLength];
				currBitmask = dataTmp[(3 + i * blockLength) + 1];

				currCh1Value = Data.convertFourBytesToInt(dataTmp, (3 + i * blockLength) + 2);
				currCh2Value = Data.convertFourBytesToInt(dataTmp, (3 + i * blockLength) + 6);
				currCh3Value = Data.convertFourBytesToInt(dataTmp, (3 + i * blockLength) + 10);
				currCh4Value = Data.convertFourBytesToInt(dataTmp, (3 + i * blockLength) + 14);

				currFeatureLabel = convertBytesToString(dataTmp, (3 + i * blockLength) + 18);

				featureWork = new Feature(node, SPINEFunctionConstants.FEATURE, currFeatCode, sensorCode, currBitmask, currCh1Value, currCh2Value, currCh3Value, currCh4Value, currFeatureLabel);
				
				feats.addElement(featureWork);

			}

			data.setFeatures((Feature[]) feats.toArray(new Feature[0]));

		} catch (Exception e) {
			e.printStackTrace();
			if (SPINEManager.getLogger().isLoggable(Logger.SEVERE))
				SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
			data = null;
		}

		return data;
	}

	private String convertBytesToString(byte[] bytes, int index) {

		String label = "";

		for (int k = 0; k < MAX_LABEL_LENGTH; k++) {
			if (bytes[index + k] != 0) {
				label = label + (char) bytes[index + k];
			}
		}

		return label;
	}
}
