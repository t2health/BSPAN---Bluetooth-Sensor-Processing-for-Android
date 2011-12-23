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

public class SpineSetupSensor  implements SpineObject {
	
	private static final long serialVersionUID = 1L;
	
	private byte sensor = -1;
	private byte timeScale = -1;
	private int samplingTime = -1;


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
	
	
	/**
	 * Sets the time scale of the sampling time.	  
	 * 
	 * @param timeScale the time scale
	 * 
	 * @see spine.SPINESensorConstants for the defined time-scales 
	 */
	public void setTimeScale(byte timeScale) {
		this.timeScale = timeScale;	
	}
	
	/**
	 * Getter method of the time scale
	 * 
	 * @return the time scale of this setup-sensor request
	 * 
	 * @see spine.SPINESensorConstants for the defined time-scales
	 */
	public byte getTimeScale() {
		byte timeScale;
		timeScale=this.timeScale;
		return timeScale;
	}
	
	
	/**
	 * Set the absolute value of the sampling time (the actual sampling interval depends on the time scale)
	 * 
	 * @param samplingTime the value of the sampling time
	 *
	 * @see spine.SPINESensorConstants for the defined time-scales
	 */
	public void setSamplingTime(int samplingTime) {
		this.samplingTime = samplingTime;
	}
	
	/**
	 * Getter method of the sampling time in number of 'timeScale' units
	 * 
	 * @return samplingTime the value of the sampling time of this setup-sensor request
	 */
	public int getSamplingTime() {
		int samplingTime;
		samplingTime=this.samplingTime;
		return samplingTime;
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
		return this.sensor == ((SpineSetupSensor)aSpineSetupSensor).sensor;
	}
	
	/**
	 * Returns a string representation of this SpineSetupSensor object.
	 * 
	 * @return the String representation of this SpineSetupSensor object
	 */
	public String toString() {
		String s = "Sensor Setup {";
		
		s += "sensor = " + SPINESensorConstants.sensorCodeToString(sensor) + ", ";
		s += "timeScale = " + SPINESensorConstants.timeScaleToString(timeScale) + ", ";
		s += "samplingTime = " + samplingTime + "}";
		
		return s;
	}
	
}
