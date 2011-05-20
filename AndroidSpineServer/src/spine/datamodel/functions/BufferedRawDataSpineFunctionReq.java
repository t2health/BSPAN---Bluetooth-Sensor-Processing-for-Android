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


public class BufferedRawDataSpineFunctionReq extends SpineFunctionReq {

	private static final long serialVersionUID = 1L;
	
	private byte sensor = -1;
	private byte channelsBitmask = 0;
	
	
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
	 * Getter method of the sensor involved in this request
	 * 
	 * @return the sensor involved in this request
	 * 
	 * @see spine.SPINESensorConstants
	 */
	public byte getSensor() {
		byte sensor;
		sensor = this.sensor;
		return sensor;
	}
	
	
	/**
	 * Sets the sensor involved on the current BufferedRawData function request
	 * 
	 * @param channelsBitmask the channels bitmask describing the sensor channels to return
	 */
	public void setChannelsBitmask(byte channelsBitmask) {
		this.channelsBitmask = channelsBitmask;
	}

	/**
	 * Getter method of the sensor channels bitmask
	 * 
	 * @return the sensor channels bitmask
	 */
	public byte getChannelsBitmask() {
		return channelsBitmask;
	}
	
	
	/**
	 * 
	 * Returns a string representation of the BufferedRawDataSpineFunctionReq object.
	 * 
	 * @return the String representation of this BufferedRawDataSpineFunctionReq object
	 * 
	 */
	public String toString() {
		String s = "Buffered Raw-Data Function Req {";		
		s += (this.isActivationRequest)? "activate " : "deactivate ";		
		s += "sensor = " + SPINESensorConstants.sensorCodeToString(sensor) + ", ";		
		s += "chs bitmask = " + SPINESensorConstants.channelBitmaskToString(channelsBitmask) + "}";
		return s;
	}
	
}
