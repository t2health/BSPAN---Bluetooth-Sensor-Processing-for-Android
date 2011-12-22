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
 *
 *  This class represents the Sensor entity.
 *  It contains a constructor, a toString and getters methods.
 *
 * @author Raffaele Gravina
 *
 * @version 1.3
 */

package spine.datamodel;

import java.io.Serializable;

import spine.SPINESensorConstants;

public class Sensor implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private byte code;
	private byte channelBitmask;
	
	/**
	 * Constructor of a Sensor object.
	 * 
	 * @param code the sensor code
	 * @param channelBitmask the channels bitmask representing by which channels the sensor is composed of
	 * 
	 * @see spine.SPINESensorConstants 
	 */
	public Sensor(byte code, byte channelBitmask) {
		this.code = code;
		this.channelBitmask = channelBitmask; 
	}
	
	/**
	 * Getter method of the sensor code
	 * 
	 * @return the sensor code
	 */
	public byte getCode() {
		return code;
	}

	/**
	 * Getter method of the sensor channels bitmask
	 * 
	 * @return the sensor channels bitmask
	 */
	public byte getChannelBitmask() {
		return channelBitmask;
	}
	
	/**
	 * 
	 * Returns a string representation of the Sensor object.
	 * 
	 * @return a string representation of this Sensor
	 * 
	 */
	public String toString() {
		return SPINESensorConstants.sensorCodeToString(code) + " - " + 
			   SPINESensorConstants.channelBitmaskToString(channelBitmask);
	}
}
