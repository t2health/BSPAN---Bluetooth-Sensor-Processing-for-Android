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

import jade.util.Logger;

import java.util.Vector;

import spine.SPINEFunctionConstants;
import spine.SPINEManager;
import spine.datamodel.Node;
import spine.datamodel.Sensor;
import spine.datamodel.functions.*;
import spine.exceptions.*;

public class ServiceAdvertisement extends SpineCodec {

	private final static String FUNCTION_CLASSNAME_PREFIX = "spine.datamodel.functions.";

	private final static String FUNCTION_CLASSNAME_SUFFIX = "Function";

	public byte[] encode(SpineObject payload) throws MethodNotSupportedException {
		throw new MethodNotSupportedException("encode");
	};

	/**
	 * Decompress Service Advertisement packet payload into Node object
	 * 
	 * @param payload
	 *            the low level byte array containing the payload of the Spine
	 *            Service Advertisement packet to parse (decompress)
	 * @return Node object.
	 */

	public SpineObject decode(Node node, byte[] payload) {

		Vector sensorsList = new Vector();
		Vector functionsList = new Vector(); 

		byte sensorsNr = payload[0];

		for (int i = 0; i < sensorsNr; i++) {
			sensorsList.addElement(new Sensor(payload[1 + i * 2], payload[1 + i * 2 + 1]));
		}
		// set functionsList
		int functionsListSize = payload[1 + sensorsNr * 2];
		int parseOfst = 1 + sensorsNr * 2 + 1;
		while (parseOfst < (functionsListSize + 1 + sensorsNr * 2 + 1)) {
			byte functionCode = payload[parseOfst++];
			byte fParamSize = payload[parseOfst++];
			byte[] fParams = new byte[fParamSize];

			System.arraycopy(payload, parseOfst, fParams, 0, fParamSize);
			parseOfst += fParamSize;

			try {
				Class c = Class.forName(FUNCTION_CLASSNAME_PREFIX + SPINEFunctionConstants.functionCodeToString(functionCode) + FUNCTION_CLASSNAME_SUFFIX);
				Function currFunction = (Function) c.newInstance();
				currFunction.init(fParams);
				functionsList.addElement(currFunction);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				if (SPINEManager.getLogger().isLoggable(Logger.SEVERE))
					SPINEManager.getLogger().log(Logger.INFO, e.getMessage());
			} catch (InstantiationException e) {
				e.printStackTrace();
				if (SPINEManager.getLogger().isLoggable(Logger.SEVERE))
					SPINEManager.getLogger().log(Logger.INFO, e.getMessage());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				if (SPINEManager.getLogger().isLoggable(Logger.SEVERE))
					SPINEManager.getLogger().log(Logger.INFO, e.getMessage());
			} catch (BadFunctionSpecException e) {
				e.printStackTrace();
				if (SPINEManager.getLogger().isLoggable(Logger.SEVERE))
					SPINEManager.getLogger().log(Logger.INFO, e.getMessage());
			}
		}
		node.setFunctionsList(functionsList);
		node.setSensorsList(sensorsList);

		return node;
	}
}
