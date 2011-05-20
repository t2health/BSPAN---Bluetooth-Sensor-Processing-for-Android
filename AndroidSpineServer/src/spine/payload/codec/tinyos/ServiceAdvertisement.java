/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
allows dynamic on node configuration for feature extraction and a 
OtA protocol for the management for WSN

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
 * TinyOS SPINE Service Advertisement packet payload into a platform independent one.
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
import spine.datamodel.Node;
import spine.datamodel.Sensor;
import spine.datamodel.functions.*;
import spine.exceptions.*;

public class ServiceAdvertisement extends SpineCodec {

	private final static String FUNCTION_CLASSNAME_PREFIX = "spine.datamodel.functions.";
	private final static String FUNCTION_CLASSNAME_SUFFIX = "Function";

	// private Vector sensorsList = new Vector(); // <values:Sensor>
	// private Vector functionsList = new Vector(); // <values:Function>

	public byte[] encode(SpineObject payload)
			throws MethodNotSupportedException {
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

		Vector sensorsList = new Vector(); // <values:Sensor>
		Vector functionsList = new Vector(); // <values:Function>

		byte sensorsNr = payload[0];
		byte librariesListSize = payload[1 + sensorsNr];
		byte[] data = new byte[1 + sensorsNr * 2 + 1 + librariesListSize];

		// set data array
		data[0] = sensorsNr;

		for (int i = 0; i < sensorsNr; i++) {
			data[1 + i * 2] = (byte) ((payload[1 + i] & 0xFF) >> 4);
			data[(1 + i * 2) + 1] = (byte) (payload[1 + i] & 0x0F);
		}

		data[1 + sensorsNr * 2] = librariesListSize;

		for (int i = 0; i < librariesListSize; i++)
			data[(1 + sensorsNr * 2) + 1 + i] = payload[1 + sensorsNr + 1 + i];

		// set sensorsList
		sensorsNr = data[0];
		for (int i = 0; i < sensorsNr; i++)
			sensorsList.addElement(new Sensor(data[1 + i * 2],
					data[1 + i * 2 + 1]));

		// set functionsList
		int functionsListSize = data[1 + sensorsNr * 2];
		int parseOfst = 1 + sensorsNr * 2 + 1;
		while (parseOfst < (functionsListSize + 1 + sensorsNr * 2 + 1)) {
			byte functionCode = data[parseOfst++];
			byte fParamSize = data[parseOfst++];
			byte[] fParams = new byte[fParamSize];

			System.arraycopy(data, parseOfst, fParams, 0, fParamSize);
			parseOfst += fParamSize;

			try {
				Class c = Class.forName(FUNCTION_CLASSNAME_PREFIX
						+ SPINEFunctionConstants
								.functionCodeToString(functionCode)
						+ FUNCTION_CLASSNAME_SUFFIX);
				Function currFunction = (Function) c.newInstance();
				currFunction.init(fParams);
				functionsList.addElement(currFunction);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) 
					SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
			} catch (InstantiationException e) {
				e.printStackTrace();
				if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) 
					SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				if (SPINEManager.getLogger().isLoggable(Logger.SEVERE))
					SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
			} catch (BadFunctionSpecException e) {
				e.printStackTrace();
				if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) 
					SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
			}
		}

		node.setFunctionsList(functionsList);
		node.setSensorsList(sensorsList);

		return node;
	}

}
