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
* Implementation of SpineSetupFunction responsible of handling setup of the function type 'BufferedRawData'
*
* @author Raffaele Gravina
*
* @version 1.3
*/

package spine.datamodel.functions;

import spine.SPINESensorConstants;

public class BufferedRawDataSpineSetupFunction implements SpineSetupFunction {

	private static final long serialVersionUID = 1L;
	
	private byte sensor = -1;
	private short bufferSize = 0;
	private short shiftSize = 0;

	
	/**
	 * Sets the sensor involved on the current BufferedRawData function request
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
		sensor = this.sensor;
		return sensor;
	}
	
	/**
	 * Sets the size of the raw-data buffer
	 * 
	 * @param bufferSize the size of the buffer to request, expressed in number of samples
	 */
	public void setBufferSize(short bufferSize) {
		this.bufferSize = bufferSize;
	}
	
	/**
	 * Getter method of the buffer size
	 * 
	 * @return the raw-data buffer size
	 */
	public short getBufferSize() {
		short bufferSize;
		bufferSize = this.bufferSize;
		return bufferSize;
	}
	
	
	/**
	 * Sets the shift size (overlap amount in number of samples). 
	 * Set it to 'bufferSize' if you don't need overlap between raw-data transmissions.
	 * An overlap amount less than 'bufferSize' could be useful at application level to be sure
	 * there aren't missing samples.
	 * 
	 * @param shiftSize the overlap amount (ahead shift) over the previous window
	 */
	public void setShiftSize(short shiftSize) {
		this.shiftSize = shiftSize;
	}
	
	/**
	 * Getter method of the overlap amount (ahead shift) over the previous window
	 * 
	 * @return the overlap amount (ahead shift) over the previous window
	 */
	public short getShiftSize() {
		short shiftSize;
		shiftSize = this.shiftSize;
		return shiftSize;
	}
	
	
	/**
	 * 
	 * Returns a string representation of this BufferedRawDataSpineSetupFunction object.
	 * 
	 * @return the String representation of this BufferedRawDataSpineSetupFunction object
	 * 
	 */
	public String toString() {
		String s = "Buffered Raw-Data Setup Function {";
		
		s += "sensor = " + SPINESensorConstants.sensorCodeToString(sensor) + ", ";
		s += "buffer size = " + bufferSize + ", ";
		s += "shift size = " + shiftSize + "}";
		
		return s;
	}
	
}
