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

package spine.datamodel;

import java.util.Arrays;

import spine.SPINEFunctionConstants;
import spine.SPINESensorConstants;

public class BufferedRawData extends Data {
	
	private static final long serialVersionUID = 1L;
	
	private byte sensorCode;
	private byte channelBitmask;
	private byte dataWordLength;
	
	private int[][] values;
	
	/**
	 * Constructor of a BufferedRawData object.
	 * This is used by the lower level components of the framework for creating BufferedRawData objects
	 * from a low level BufferedRawData data packet received by remote nodes. 
	 * 
	 * @param functionCode the function code 
	 * @param sensorCode the sensor code
	 * @param channelBitmask the sensor channels bitmask
	 * @param values the channels values matrix (rows are the samples, columns are the channels)
	 * 
	 * @see spine.SPINEFunctionConstants
	 * @see spine.SPINESensorConstants
	 */
	public BufferedRawData(byte functionCode, byte sensorCode, byte channelBitmask, int[][] values) {

		this.functionCode = functionCode;
		
		this.sensorCode = sensorCode;
		this.channelBitmask = channelBitmask;
		
		this.values = values;
	}
	
	/**
	 * Default Constructor.
	 * 
	 */
	public BufferedRawData() {
		
	}

	/**
	 * Getter method of the node id
	 * @return the node id
	 */
	public byte getFunctionCode() {
		return functionCode;
	}
		
	/**
	 * Getter method of the sensor code
	 * @return the sensor code
	 */
	public byte getSensorCode() {
		return sensorCode;
	}

	/**
	 * Getter method of the sensor channels bitmask
	 * @return the sensor channels bitmask
	 */
	public byte getChannelBitmask() {
		return channelBitmask;
	}

	/**
	 * Getter method of the channels values matrix
	 * @return the channels values matrix: a row for each channel (null if the channel is inactive) and a column for each buffered sampling instant
	 */
	public int[][] getValues() {
		return values;
	}

	/**
	 * 
	 * Setter method of the sensor code
	 * @see spine.SPINESensorConstants
	 */
	public void setSensorCode(byte sensorCode) {
		this.sensorCode = sensorCode;			
	}

	/**
	 * 
	 * Add the given channel to the channel bitmask.
	 * Available channels are CH1, CH2, CH3, CH4
	 * 
	 * @see spine.SPINESensorConstants
	 * 
	 */
	public void addChannelToBitmask(byte channel) {
		switch(channel) {
			case SPINESensorConstants.CH1: this.channelBitmask |= SPINESensorConstants.CH1_ONLY; break; 
			case SPINESensorConstants.CH2: this.channelBitmask |= SPINESensorConstants.CH2_ONLY; break;
			case SPINESensorConstants.CH3: this.channelBitmask |= SPINESensorConstants.CH3_ONLY; break;
			case SPINESensorConstants.CH4: this.channelBitmask |= SPINESensorConstants.CH4_ONLY; break;
		}		
	}
	
	/**
	 * 
	 * Returns a string representation of the Feature object.
	 * 
	 */
	public String toString() {
		return "From node: {" + this.node.toShortString() + "} - " + SPINEFunctionConstants.BUFFERED_RAW_DATA_LABEL + ": " +  
				" on " + SPINESensorConstants.sensorCodeToString(this.sensorCode) + 
				" (now on " + SPINESensorConstants.channelBitmaskToString(this.channelBitmask) + ") " + 
				" - " + SPINESensorConstants.CH1_LABEL + ": "+ Arrays.toString(values[0]) + 
				"; " + SPINESensorConstants.CH2_LABEL + ": "+ Arrays.toString(values[1]) + 
				"; " + SPINESensorConstants.CH3_LABEL + ": "+ Arrays.toString(values[2]) + 
				"; " + SPINESensorConstants.CH4_LABEL + ": "+ Arrays.toString(values[3]);
	}

	/**
	 * @param functionCode the functionCode to set
	 * 
	 * @see spine.SPINEFunctionConstants
	 */
	public void setFunctionCode(byte functionCode) {
		this.functionCode = functionCode;
	}

	/**
	 * @param channelBitmask the channelBitmask to set
	 * 
	 * @see spine.SPINESensorConstants
	 */
	public void setChannelBitmask(byte channelBitmask) {
		this.channelBitmask = channelBitmask;
	}

	/**
	 * Sets the number of bytes each raw-data sample is composed of
	 * 
	 * @param dataWordLength the number of bytes each raw-data sample is composed of
	 */
	public void setDataWordLength(byte dataWordLength) {
		this.dataWordLength = dataWordLength;
	}

	/**
	 * Returns the number of bytes each raw-data sample is composed of
	 */
	public byte getDataWordLength() {
		return dataWordLength;
	}

	/**
	 * @param values the channels values matrix to set
	 */
	public void setValues(int[][] values) {
		this.values = values;
	}		

}
