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
 * This class represents the SPINE Setup Sensor request.
 *  
 *
 * @author Raffaele Gravina
 *
 * @version 1.3
 * 
 * @see spine.SPINESensorConstants
 */

package spine.datamodel.functions;

import spine.SPINESensorConstants;

public class ShimmerNonSpineSetupSensor  implements SpineObject {
	
	private static final long serialVersionUID = 1L;

	public static final byte SHIMMER_COMMAND_STOPPED = 0;
	public static final byte SHIMMER_COMMAND_RUNNING = 1;
	
	private byte sensor = -1; // See SPINESensorConstants
	private byte[] btAddress = new byte[6];
	private int samplingTime = -1;
	private byte command = 0;   // 0 = stopped, 1 = running


	/**
	 * Sets the sensor to setup
	 * 
	 * @param sensor the sensor to setup
	 * 
	 * @see spine.SPINESensorConstants
	 */
	public void setSensor(byte sensor) {
		this.sensor = sensor;
	}
	
	/**
	 * Getter method of the sensor involved in this setup request
	 * 
	 * @return the sensor involved in this setup request
	 * 
	 * @see spine.SPINESensorConstants
	 */
	public byte getSensor() {
		byte sensor;
		sensor=this.sensor;
		return sensor;
	}
	
	
	public byte[] getBtAddress() {
		return btAddress;
	}

	public void setBtAddress(byte[] btAddress) {
		this.btAddress = btAddress;
	}


	public byte getCommand() {
		return command;
	}

	public void setCommand(byte command) {
		this.command = command;
	}

	/**
	 * The hash code is represented by the sensor code
	 * 
	 * @return the sensor code as a hash-code
	 */
	public int hashCode() {
		return this.sensor;
	}
	
	/**
	 * Compares this SpineSetupSensor to the specified object. 
	 * The result is true if and only if the argument is not null and is a SpineSetupSensor object 
	 * with the same sensorCode of this SpineSetupSensor one.
	 *
	 * @param aSpineSetupSensor the object to compare this SpineSetupSensor against.
	 *	  
	 * @return true if the two SpineSetupSensor object are equal; false otherwise.
	 */
	public boolean equals(Object aSpineSetupSensor) {
		if (aSpineSetupSensor == null) return false;
		return this.sensor == ((ShimmerNonSpineSetupSensor)aSpineSetupSensor).sensor;
	}
	
	/**
	 * Returns a string representation of this SpineSetupSensor object.
	 * 
	 * @return the String representation of this SpineSetupSensor object
	 */
	public String toString() {
		String s = "Sensor Setup {";
		
		s += "sensor = " + SPINESensorConstants.sensorCodeToString(sensor) + ", ";
		s += "samplingTime = " + samplingTime + "}";
		
		return s;
	}
	
}
