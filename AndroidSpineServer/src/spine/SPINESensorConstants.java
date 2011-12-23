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

package spine;

public class SPINESensorConstants {

	public static final byte ACC_SENSOR = 0x01;
	public static final byte VOLTAGE_SENSOR = 0x02;
	public static final byte GYRO_SENSOR = 0x03;
	public static final byte INTERNAL_TEMPERATURE_SENSOR = 0x04;
	public static final byte EIP_SENSOR = 0x05;
	public static final byte ECG_SENSOR = 0x06;
	public static final byte TEMPERATURE_SENSOR = 0x07;
	public static final byte HUMIDITY_SENSOR = 0x08;
	public static final byte LIGHT_SENSOR = 0x09;
	public static final byte HEARTRATE_SENSOR = 0x0a;
	public static final byte RESPIRATIONRATE_SENSOR = 0x0b;
	public static final byte ZEPHYR_SENSOR = 0x0c;
	public static final byte SHIMMER_GSR_SENSOR = 0x0d;
	public static final byte SHIMMER_EMG_SENSOR = 0x0e;
	public static final byte SHIMMER_ECG_SENSOR = 0x0f;
	public static final byte SHIMMER_MAG_SENSOR = 0x10;
	public static final byte SHIMMER_STRAIN_SENSOR = 0x11;
	
	public static final String ACC_SENSOR_LABEL = "accelerometer";
	public static final String VOLTAGE_SENSOR_LABEL = "voltage";
	public static final String GYRO_SENSOR_LABEL = "gyroscope";
	public static final String INTERNAL_TEMPERATURE_SENSOR_LABEL = "cpu temperature";
	public static final String EIP_SENSOR_LABEL = "Electrical Impedance Pneumography (EIP) breathing";
	public static final String ECG_SENSOR_LABEL = "Electrocardiography (ECG)";
	public static final String TEMPERATURE_SENSOR_LABEL = "Env Temperature";
	public static final String HUMIDITY_SENSOR_LABEL = "Humidity";
	public static final String LIGHT_SENSOR_LABEL = "Light";
	public static final String HEARTRATE_SENSOR_LABEL = "Heartrate";
	public static final String RESPIRATIONRATE_SENSOR_LABEL = "RespirationRate";
	public static final String ZEPHYR_SENSOR_LABEL = "ZephyrDevice";
	
	
	
	public static final byte ALL = 0x0F;				// 1111
	public static final byte NONE = 0x00;				// 0000
	
	public static final byte CH1_ONLY = 0x08;			// 1000
	public static final byte CH1_CH2_ONLY = 0x0C;		// 1100
	public static final byte CH1_CH2_CH3_ONLY = 0x0E;	// 1110
	public static final byte CH1_CH2_CH4_ONLY = 0x0D;	// 1101
	public static final byte CH1_CH3_ONLY = 0xA;		// 1010
	public static final byte CH1_CH3_CH4_ONLY = 0xB;	// 1011
	public static final byte CH1_CH4_ONLY = 0x9;		// 1001
	
	
	public static final byte CH2_ONLY = 0x04;			// 0100
	public static final byte CH2_CH3_ONLY = 0x06;		// 0110
	public static final byte CH2_CH3_CH4_ONLY = 0x07;	// 0111
	public static final byte CH2_CH4_ONLY = 0x05;		// 0101
	
	public static final byte CH3_ONLY = 0x02;			// 0010
	public static final byte CH3_CH4_ONLY = 0x03;		// 0011
	
	public static final byte CH4_ONLY = 0x01;			// 0001
	
	public static final byte NOW = 0x00;				// 00
	public static final byte MILLISEC = 0x01;			// 01
	public static final byte SEC = 0x02;				// 10
	public static final byte MIN = 0x03;				// 11
	
	public static final String NOW_LABEL = "now";				
	public static final String MILLISEC_LABEL = "ms";			
	public static final String SEC_LABEL = "sec";				
	public static final String MIN_LABEL = "min";				
	
	public static final int MAX_VALUE_TYPES = 4;
	
	public static final byte CH1 = 0x00;
	public static final byte CH2 = 0x01;
	public static final byte CH3 = 0x02;
	public static final byte CH4 = 0x03;
	
	public static final String CH1_LABEL = "ch1";
	public static final String CH2_LABEL = "ch2";
	public static final String CH3_LABEL = "ch3";
	public static final String CH4_LABEL = "ch4";
	
	
	/**
	 *  Returns a human friendly label of the given sensor code
	 * 
	 * @param code numeric code the sensor to convert into a human friendly label
	 * @return human friendly label of the given sensor code
	 */
	public static String sensorCodeToString(byte code) {
		switch (code) {
			case ACC_SENSOR: return ACC_SENSOR_LABEL;
			case VOLTAGE_SENSOR: return VOLTAGE_SENSOR_LABEL;
			case GYRO_SENSOR: return GYRO_SENSOR_LABEL;
			case INTERNAL_TEMPERATURE_SENSOR: return INTERNAL_TEMPERATURE_SENSOR_LABEL;
			case EIP_SENSOR: return EIP_SENSOR_LABEL;
			case ECG_SENSOR: return ECG_SENSOR_LABEL;
			case TEMPERATURE_SENSOR: return TEMPERATURE_SENSOR_LABEL;
			case HUMIDITY_SENSOR: return HUMIDITY_SENSOR_LABEL;
			case LIGHT_SENSOR: return LIGHT_SENSOR_LABEL;
			case HEARTRATE_SENSOR: return HEARTRATE_SENSOR_LABEL;
			case RESPIRATIONRATE_SENSOR: return RESPIRATIONRATE_SENSOR_LABEL;
			case ZEPHYR_SENSOR: return ZEPHYR_SENSOR_LABEL;			
			default: return "?";
		}
	}
	
	/**
	 *  Returns the numeric code of the given sensor string label
	 * 
	 * @param label string label of the sensor
	 * @return numeric code of the given sensor string label
	 */
	public static byte sensorCodeByString(String label) {
		if(label.equals(ACC_SENSOR_LABEL))
			return ACC_SENSOR;
		if(label.equals(VOLTAGE_SENSOR_LABEL))
			return VOLTAGE_SENSOR;
		if(label.equals(GYRO_SENSOR_LABEL))
			return GYRO_SENSOR;
		if(label.equals(INTERNAL_TEMPERATURE_SENSOR_LABEL))
			return INTERNAL_TEMPERATURE_SENSOR;
		if(label.equals(EIP_SENSOR_LABEL))
			return EIP_SENSOR;
		if(label.equals(ECG_SENSOR_LABEL))
			return ECG_SENSOR;
		if(label.equals(TEMPERATURE_SENSOR_LABEL))
			return TEMPERATURE_SENSOR;
		if(label.equals(HUMIDITY_SENSOR_LABEL))
			return HUMIDITY_SENSOR;
		if(label.equals(LIGHT_SENSOR_LABEL))
			return LIGHT_SENSOR;
		if(label.equals(HEARTRATE_SENSOR_LABEL))
			return HEARTRATE_SENSOR;
		if(label.equals(RESPIRATIONRATE_SENSOR_LABEL))
			return RESPIRATIONRATE_SENSOR;
		if(label.equals(ZEPHYR_SENSOR_LABEL))
			return ZEPHYR_SENSOR;
		else 
			return -1;	
	}
	
	/**
	 *  Returns the numeric code of the given channel bitmask
	 * 
	 * @param hasCh1 true if channel1 is enabled in this bitmask; false otherwise
	 * @param hasCh2 true if channel2 is enabled in this bitmask; false otherwise
	 * @param hasCh3 true if channel3 is enabled in this bitmask; false otherwise
	 * @param hasCh4 true if channel4 is enabled in this bitmask; false otherwise
	 * 
	 * @return the numeric code of the given channel bitmask
	 */
	public static byte getValueTypesCodeByBitmask(boolean hasCh1, boolean hasCh2, boolean hasCh3, boolean hasCh4) {
		byte code = 0;
		
		if (hasCh1)
			code |= 0x8;
		if (hasCh2)
			code |= 0x4;
		if (hasCh3)
			code |= 0x2;
		if (hasCh4)
			code |= 0x1;
		
		return code;
	}
	
	/**
	 * Returns a human friendly label of the given channel bitmask code
	 * 
	 * @param code the numeric code of the given channel bitmask
	 * 
	 * @return human friendly label of the given channel bitmask code
	 */
	public static String channelBitmaskToString(byte code) {
		
		switch (code) {
			case ALL: return "ch1, ch2, ch3, ch4";
			case NONE: return "none";
			
			case CH1_ONLY: return "ch1";
			case CH1_CH2_ONLY: return "ch1, ch2";
			case CH1_CH2_CH3_ONLY: return "ch1, ch2, ch3";
			case CH1_CH2_CH4_ONLY: return "ch1, ch2, ch4";
			case CH1_CH3_ONLY: return "ch1, ch3";
			case CH1_CH3_CH4_ONLY: return "ch1, ch3, ch4";
			case CH1_CH4_ONLY: return "ch1, ch4";
			
			case CH2_ONLY: return "ch2";
			case CH2_CH3_ONLY: return "ch2, ch3";
			case CH2_CH3_CH4_ONLY: return "ch2, ch3, ch4";
			case CH2_CH4_ONLY: return "ch2, ch4";
			
			case CH3_ONLY: return "ch3";
			case CH3_CH4_ONLY: return "ch3, ch4";
			
			case CH4_ONLY: return "ch4";
			default: return "?";
		}
	}
	
	/**
	 * Returns a human friendly label of the channel code
	 * 
	 * @param code the numeric code of the given channel 
	 * 
	 * @return human friendly label of the given channel code
	 */
	public static String channelCodeToString(byte code) {		
		switch (code) {
			case CH1: return CH1_LABEL;
			case CH2: return CH2_LABEL;
			case CH3: return CH3_LABEL;
			case CH4: return CH4_LABEL;
			default: return ""+code;
		}
	}
	
	/**
	 * Returns a human friendly label of the time scale code
	 * 
	 * @param code the numeric code of the given time scale 
	 * 
	 * @return human friendly label of the given time scale code
	 */
	public static String timeScaleToString(byte code) {
		switch (code) {
			case NOW: return NOW_LABEL;
			case MILLISEC: return MILLISEC_LABEL;
			case SEC: return SEC_LABEL;
			case MIN: return MIN_LABEL;
			default: return "?";
		}
	}
	
	/**
	 *  Returns the numeric code of the given time scale label
	 * 
	 * @param label the time scale label to be returned as its numeric code
	 * 
	 * @return the numeric code of the given time scale label
	 */
	public static byte timeScaleByString(String label) {
		if(label.equals(NOW_LABEL))
			return NOW;
		if(label.equals(MILLISEC_LABEL))
			return MILLISEC;
		if(label.equals(SEC_LABEL))
			return SEC;
		if(label.equals(MIN_LABEL))
			return MIN;
		else 
			return -1;
	}

	/**
	 *  Checks wether the given channel is present in the given bitmask
	 * 
	 * @param chID the channel id to be checked. NOTE: 0, 1, 2, 3 will indicate ch1, ch2, ch3, ch4 respectively
	 * @param channelBitmask the numeric code of the given channel bitmask
	 * 
	 * @return true if the given channel is enabled (present) in the given bitmask; false otherwise
	 */
	public static boolean chPresent(int chID, byte channelBitmask) {
		return (( (channelBitmask>>(MAX_VALUE_TYPES - (chID+1))) & 0x01 ) == 1);
	}
	
	/**
	 * Returns the number of of channels that enabled in the given channel bitmask
	 * 
	 * @param channelBitmask the numeric code of the given channel bitmask
	 * 
	 * @return the number of channels enabled in this channel bitmask
	 */
	public static int countChannelsInBitmask(byte channelBitmask) {
		int temp = channelBitmask; 
		int result = 0; 
		while (temp > 0) { 
			result += temp & 0x01; 
			temp = temp >> 1; 
		} 
		return result; 
	}	
	
}
