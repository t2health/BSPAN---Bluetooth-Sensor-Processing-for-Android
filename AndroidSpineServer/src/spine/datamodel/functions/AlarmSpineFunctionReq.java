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

import spine.SPINEFunctionConstants;
import spine.SPINESensorConstants;

public class AlarmSpineFunctionReq extends SpineFunctionReq {
	
	private static final long serialVersionUID = 1L;
	
	private byte sensor = -1;
	private byte dataType = -1;
	private byte valueType = -1;
	private int lowerThreshold = 0;	
	private int upperThreshold = 0;
	private byte alarmType;

	/**
	 * Set the data type (e.g. the feature code) involved in the request
	 * 
	 * @param dataType the code of the feature that the alarm engine should monitor
	 * 
	 * @see spine.SPINEFunctionConstants
	 */
	public void setDataType(byte dataType) {
		this.dataType  = dataType;		
	}
	

	public byte getDataType() {
		byte dataType;
		dataType=this.dataType;	
		return dataType;
	}
	
	/**
	 * Set the sensor involved in the request
	 * 
	 * @param sensor the code of the sensor
	 * 
	 * @see spine.SPINESensorConstants 
	 */	
	public void setSensor(byte sensor) {
		this.sensor  = sensor;		
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
	 * Set the value type (channel bitmask) involved in the request
	 * 
	 * @param ValueType the channels that the alarm engine should monitor
	 * 
	 * @see spine.SPINESensorConstants 
	 */		
	public void setValueType(byte ValueType) {
		this.valueType  = ValueType;		
	}	
	
	/**
	 * Getter method of the value type (channel bitmask)
	 * 
	 * @return the value type (channel bitmask)
	 * 
	 * @see spine.SPINESensorConstants 
	 */	
	public byte getValueType() {
		byte valueType;
		valueType=this.valueType;
		return valueType;
	}	

	/**
	 * Set the lower threshold for the alarms
	 * 
	 * User must guarantee that the lower threshold value is smaller than the higher threshold value
	 * 
	 * @param setLowerThreshold lower threshold (LT) for alarms
	 * the value will be sent back from the sensor if either one of the following is true:
	 * alarmType=BELOW_THRESHOLD_LABEL & value  < LT
	 * alarmType=IN_BETWEEN_THRESHOLDS_LABEL & LT<=value<=UT
	 * if alarmType=OUT_OF_THRESHOLDS_LABEL & value<= LT | value>=UT
	 *  
	 */	
	public void setLowerThreshold(int setLowerThreshold) {
		this.lowerThreshold  = setLowerThreshold;		
	}
	
	/**
	 * Getter method of the lower threshold for these alarms
	 * 
	 * @return the lower threshold for these alarms
	 */	
	public int getLowerThreshold() {
		int lowerThreshold;
		lowerThreshold=this.lowerThreshold;
		return lowerThreshold;
	}
	
	/**
	 * Set the upper threshold for the alarms
	 * 
	 * User must guarantee that the upper threshold value is greater than the lower threshold value
	 * 
	 * @param setUpperThreshold upper threshold (UT) for alarms
	 * the value will be sent back from the sensor if either one of the following is true:
	 * alarmType=ABOVE_THRESHOLD_LABEL & value>UT
	 * alarmType=IN_BETWEEN_THRESHOLDS_LABEL & LT<=value<=UT
	 * if alarmType=OUT_OF_THRESHOLDS_LABEL & value<= LT | value>=UT
	 * 
	 */	
	public void setUpperThreshold(int setUpperThreshold) {
		this.upperThreshold  = setUpperThreshold;		
	}	
	
	
	/**
	 * Getter method of the upper threshold for these alarms
	 * 
	 * @return the upper threshold for these alarms
	 */	
	public int getUpperThreshold() {
		int upperThreshold;
		upperThreshold=this.upperThreshold;
		return upperThreshold;
	}	
	
	/**
	 * Set the alarm type 
	 * 
	 * @param AlarmType kind of alarm to set the alarm engine
	 * 
	 * @see spine.SPINEFunctionConstants
	 */		
	public void setAlarmType(byte AlarmType) {
		this.alarmType  = AlarmType;		
	}	
	
	/**
	 * Getter method of the alarm type 
	 * 
	 * @return the alarm type
	 * 
	 * @see spine.SPINEFunctionConstants
	 */	
	public byte getAlarmType() {
		byte alarmType;
		alarmType=this.alarmType;
		return alarmType;
	}	
	
		
	/**
	 * 
	 * Returns a string representation of the AlarmSpineFunctionReq object.
	 * 
	 */
	public String toString() {
		String s = "Alarm Function ";
		
		s += (this.isActivationRequest)? "Activation {": "Deactivation {";
		
		s += "sensor = " + SPINESensorConstants.sensorCodeToString(sensor) + ", ";
		s += "dataType = " + SPINEFunctionConstants.functionalityCodeToString(SPINEFunctionConstants.FEATURE, dataType) + ", ";
		s += "valueType = " + SPINESensorConstants.channelBitmaskToString(valueType) + ", ";
		s += "lowerThreshold = " + lowerThreshold + ", ";
		s += "upperThreshold = " + upperThreshold + ", ";
		s += "alarmType = " + SPINEFunctionConstants.functionalityCodeToString(SPINEFunctionConstants.ALARM, alarmType) + "}";
		
		return s;
	}
	
}
