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
* Implementation of SpineSetupFunction responsible of handling setup of the function type 'Feature'
*
* @author Raffaele Gravina
* @author Alessia Salmeri
*
* @version 1.3
*/

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
