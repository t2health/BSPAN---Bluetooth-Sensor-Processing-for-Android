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
* This class represents the AlarmData entity.
* It contains the decode method for converting low level Alarm type data into an high level object.
*
* @author Roberta Giannantonio
* @author Philip Kuryloski
* @author Alessia Salmeri
*
* @version 1.3
*/

package spine.datamodel;

import spine.SPINEFunctionConstants;
import spine.SPINESensorConstants;

public class AlarmData extends Data {
	
	private static final long serialVersionUID = 1L;
	
	private byte dataType;
	private byte sensorCode;
	private byte valueType;
	private byte alarmType;
	private int currentValue;
	

	/**
	 * Getter method of the function code
	 * @return the function code
	 */
	public byte getFunctionCode() {
		return functionCode;
	}
		
	/**
	 * Getter method of the data type
	 * @return the data type
	 */
	public byte getDataType() {
		return dataType;
	}	

	/**
	 * Getter method of the sensor code
	 * @return the sensor code
	 */
	public byte getSensorCode() {
		return sensorCode;
	}

	/**
	 * Getter method of the value type
	 * @return the value type
	 */
	public byte getValueType() {
		return valueType;
	}	
	
	/**
	 * Getter method of the alarm type
	 * @return the alarm type
	 */
	public byte getAlarmType() {
		return alarmType;
	}

	/**
	 * Getter method of the current value
	 * @return the current value
	 */
	public int getCurrentValue() {
		return currentValue;
	}
	
	/**
	 * 
	 * Returns a string representation of the Alarm object.
	 * 
	 */
	public String toString() {
		return "From node: {" + this.node.toShortString() + "} - " + SPINEFunctionConstants.ALARM_LABEL  + 
				" on " + SPINEFunctionConstants.functionalityCodeToString(SPINEFunctionConstants.FEATURE,this.dataType) + " on sensor " + SPINESensorConstants.sensorCodeToString(this.sensorCode) + 
				" VALUE " + this.currentValue ;
	}

	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(byte dataType) {
		this.dataType = dataType;
	}

	/**
	 * @param sensorCode the sensorCode to set
	 */
	public void setSensorCode(byte sensorCode) {
		this.sensorCode = sensorCode;
	}

	/**
	 * @param valueType the valueType to set
	 */
	public void setValueType(byte valueType) {
		this.valueType = valueType;
	}

	/**
	 * @param alarmType the alarmType to set
	 */
	public void setAlarmType(byte alarmType) {
		this.alarmType = alarmType;
	}

	/**
	 * @param currentValue the currentValue to set
	 */
	public void setCurrentValue(int currentValue) {
		this.currentValue = currentValue;
	}

}
