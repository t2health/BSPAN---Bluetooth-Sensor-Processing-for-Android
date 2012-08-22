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

package spine.datamodel.functions;

import spine.SPINESensorConstants;

public class ShimmerNonSpineSetupSensor  implements SpineObject {
	
	private static final long serialVersionUID = 1L;

	public static final byte SHIMMER_COMMAND_STOPPED = 0;
	public static final byte SHIMMER_COMMAND_RUNNING = 1;
	
	// NOTE IF ANY OF THESE CONSTANTS CHANGE THEN THE TABLE AT mCommandTableTranslation MUST CHANGE AS WELL!
	public static final byte SHIMMER_COMMAND_RUNNING_1000HZ_40K = 0 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_500HZ_40K = 1 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_250HZ_40K = 2 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_200HZ_40K = 3 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_166HZ_40K = 4 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_125HZ_40K = 5 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_100HZ_40K = 6 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_50HZ_40K = 7 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_10HZ_40K = 8 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_4HZ_40K = 9 + 2 ;	

	public static final byte SHIMMER_COMMAND_RUNNING_1000HZ_287K = 10 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_500HZ_287K = 11 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_250HZ_287K = 12 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_200HZ_287K = 13 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_166HZ_287K = 14 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_125HZ_287K = 15 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_100HZ_287K = 16 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_50HZ_287K = 17 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_10HZ_287K = 18 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_4HZ_287K = 19 + 2;	

	public static final byte SHIMMER_COMMAND_RUNNING_1000HZ_1M = 20 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_500HZ_1M = 21 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_250HZ_1M = 22 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_200HZ_1M = 23 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_166HZ_1M = 24 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_125HZ_1M = 25 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_100HZ_1M = 26 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_50HZ_1M = 27 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_10HZ_1M = 28 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_4HZ_1M = 29 + 2;	

	public static final byte SHIMMER_COMMAND_RUNNING_1000HZ_3M3 = 30 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_500HZ_3M3 = 31 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_250HZ_3M3 = 32 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_200HZ_3M3 = 33 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_166HZ_3M3 = 34 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_125HZ_3M3 = 35 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_100HZ_3M3 = 36 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_50HZ_3M3 = 37 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_10HZ_3M3 = 38 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_4HZ_3M3 = 39 + 2;	

	public static final byte SHIMMER_COMMAND_RUNNING_1000HZ_AUTORANGE = 40 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_500HZ_AUTORANGE = 41 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_250HZ_AUTORANGE = 42 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_200HZ_AUTORANGE = 43 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_166HZ_AUTORANGE = 44 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_125HZ_AUTORANGE = 45 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_100HZ_AUTORANGE = 46 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_50HZ_AUTORANGE = 47 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_10HZ_AUTORANGE = 48 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_4HZ_AUTORANGE = 49 + 2;
	public static final byte SHIMMER_COMMAND_RUNNING_32HZ_AUTORANGE = 50 + 2;	
	public static final byte SHIMMER_COMMAND_RUNNING_64HZ_AUTORANGE = 51 + 2;	
	
	
	
	
	private byte sensor = -1; // See SPINESensorConstants
	private byte[] btAddress = new byte[6];
	private byte[] btName = new byte[255];
	private int samplingTime = -1;
	private byte command = 0;   // 0 = stopped, 1 = running, 2 enabled, 3 disabled


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

	public byte[] getBtName() {
		return btName;
	}

	public void setBtName(String strBtName) {

		if (strBtName.length() <= 254) {
			btName = strBtName.getBytes();	
		}
		
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
