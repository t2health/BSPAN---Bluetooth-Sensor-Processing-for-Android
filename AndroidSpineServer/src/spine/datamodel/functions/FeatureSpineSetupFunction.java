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


public class FeatureSpineSetupFunction implements SpineSetupFunction {

	private static final long serialVersionUID = 1L;
	
	private byte sensor = -1;
	private short windowSize = 0;
	private short shiftSize = 0;

	
	/**
	 * Sets the sensor involved on the current Feature function setup request
	 * Note that a Feature function setup request is always made on a 'per sensor' basis.
	 * To activate features over different sensors, 
	 * it's necessary to do a Feature function setup request per each sensor and then
	 * to activate the required features on the involved sensors.
	 * 
	 * @param sensor the sensor code
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
	 * Sets the size of the window over which computes the features 
	 * that will eventually activated thru a Feature Spine Function Req
	 * 
	 * @param windowSize the window size expressed in number of samples
	 */
	public void setWindowSize(short windowSize) {
		this.windowSize = windowSize;
	}
	

	/**
	 * Getter method of the size of the window over which the features will be computed
	 * 
	 * @return the size of the window involved in this setup request
	 */
	public short getWindowSize() {
		short windowSize;
		windowSize=this.windowSize;
		return windowSize;
	}
	
	
	/**
	 * Sets the shift size on the window over which computes the features 
	 * that will eventually activated thru a Feature Spine Function Req
	 * 
	 * @param shiftSize the number of samples (ahead shift) to wait before a new feature computation - 
	 * 		  the overlap would be (windowSize-shiftSize) samples
	 */
	public void setShiftSize(short shiftSize) {
		this.shiftSize = shiftSize;
	}
	

	/**
	 * Getter method of the shift size involved in this setup request
	 * 
	 * @return the shift size involved in this setup request
	 */
	public short getShiftSize() {
		short shiftSize;
		shiftSize=this.shiftSize;
		return shiftSize;
	}
	
	
	/**
	 * 
	 * Returns a string representation of the FeatureSpineSetupFunction object.
	 * 
	 */
	public String toString() {
		String s = "Feature Function Setup {";
		
		s += "sensor = " + SPINESensorConstants.sensorCodeToString(sensor) + ", ";
		s += "window = " + windowSize + ", ";
		s += "shift = " + shiftSize + "}";
		
		return s;
	}
	
}
