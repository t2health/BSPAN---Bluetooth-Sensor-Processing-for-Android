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

import java.io.Serializable;

import spine.SPINEFunctionConstants;
import spine.SPINESensorConstants;
import spine.exceptions.NoSuchChannelException;

public class Feature implements Comparable, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final String CH_NOT_PRESENT_MSG = "Channel is not valued";
	private static final String UNKNOWN_CH_MSG = "Unknown channel";
	
	private Node node;
	
	private String featureLabel;
	
	private byte functionCode;
	private byte featureCode;
	
	private byte sensorCode;
	private byte channelBitmask;
	
	private int ch1Value;
	private int ch2Value;
	private int ch3Value;
	private int ch4Value;
	
	private Integer[] chValues = new Integer[SPINESensorConstants.MAX_VALUE_TYPES];
	
	/**
	 * Default Constructor of a Feature object.
	 *  
	 */
	public Feature() {
		this.functionCode = SPINEFunctionConstants.FEATURE;
	}
	
	/**
	 * Constructor of a Feature object. 
	 * This is used only for convenience when (de)activating features 
	 * through the FeatureSpineFunctionReq class
	 * 
	 * @param featureCode the code of the feature
	 * @param channelBitmask the channels bitmask specifying 
	 * 
	 * @see spine.SPINEFunctionConstants
	 * @see spine.SPINESensorConstants
	 */
	public Feature(byte featureCode, byte channelBitmask) { 
		this.functionCode = SPINEFunctionConstants.FEATURE;
		this.featureCode = featureCode;
		this.channelBitmask = channelBitmask;
		for(int i = 0; i<chValues.length; i++)
			if(SPINESensorConstants.chPresent(i, channelBitmask)) 
				chValues[i] = new Integer(0);
	}
	
	private void init(int nodeID, Node node, byte functionCode, byte featureCode, byte sensorCode, 
					  byte channelBitmask, int ch1Value, int ch2Value, int ch3Value, int ch4Value, String featureLabel) {
		
		this.functionCode = SPINEFunctionConstants.FEATURE;
		
		this.node = node;

		this.functionCode = functionCode;
		this.featureCode = featureCode;
		
		this.sensorCode = sensorCode;
		this.channelBitmask = channelBitmask;
		
		this.ch1Value = ch1Value;
		this.ch2Value = ch2Value;
		this.ch3Value = ch3Value;
		this.ch4Value = ch4Value;
		
		if (SPINESensorConstants.chPresent(SPINESensorConstants.CH1, this.channelBitmask))
			chValues[0] = new Integer(this.ch1Value); 
		if (SPINESensorConstants.chPresent(SPINESensorConstants.CH2, this.channelBitmask))
			chValues[1] = new Integer(this.ch2Value); 
		if (SPINESensorConstants.chPresent(SPINESensorConstants.CH3, this.channelBitmask))
			chValues[2] = new Integer(this.ch3Value); 
		if (SPINESensorConstants.chPresent(SPINESensorConstants.CH4, this.channelBitmask))
			chValues[3] = new Integer(this.ch4Value); 
		
		this.featureLabel = featureLabel;
	}
	
	/**
	 * Constructor of a Feature object.
	 * This is used by the lower level components of the framework for creating Feature objects
	 * from a low level Feature data packet received by remote nodes. 
	 * 
	 * @param node the node 
	 * @param functionCode the function code 
	 * @param featureCode the feature code
	 * @param sensorCode the sensor code
	 * @param channelBitmask the sensor channels bitmask
	 * @param ch1Value the first feature channel value
	 * @param ch2Value the first feature channel value
	 * @param ch3Value the first feature channel value
	 * @param ch4Value the first feature channel value
	 * 
	 * @see spine.SPINEFunctionConstants
	 * @see spine.SPINESensorConstants
	 */
	public Feature(Node node, byte functionCode, byte featureCode, byte sensorCode, byte channelBitmask, int ch1Value, int ch2Value, int ch3Value, int ch4Value) {		
		init(node.getPhysicalID().getAsInt(), node, functionCode, featureCode, sensorCode, channelBitmask, ch1Value, ch2Value, ch3Value, ch4Value, null);
	}
	
	
	/**
	 * Constructor of a Feature object.
	 * This is used by the lower level components of the framework for creating Feature objects
	 * from a low level Feature data packet received by remote nodes. 
	 * 
	 * @param node the node 
	 * @param functionCode the function code 
	 * @param featureCode the feature code
	 * @param sensorCode the sensor code
	 * @param channelBitmask the sensor channels bitmask
	 * @param ch1Value the first feature channel value
	 * @param ch2Value the first feature channel value
	 * @param ch3Value the first feature channel value
	 * @param ch4Value the first feature channel value
	 * 
	 * @see spine.SPINEFunctionConstants
	 * @see spine.SPINESensorConstants
	 */
	public Feature(Node node, byte functionCode, byte featureCode, byte sensorCode, byte channelBitmask, int ch1Value, int ch2Value, int ch3Value, int ch4Value, String featureLabel) {
		init(node.getPhysicalID().getAsInt(), node, functionCode, featureCode, sensorCode, channelBitmask, ch1Value, ch2Value, ch3Value, ch4Value, featureLabel);
	}
	
	/**
	 * Getter method of the node issuing this Feature 
	 * @return the node  
	 */
	public Node getNode() {
		return this.node;
	}
	
	/**
	 * Getter method of the node id
	 * @return the node id
	 */
	public byte getFunctionCode() {
		return functionCode;
	}
		
	/**
	 * Getter method of the feature code
	 * @return the feature code
	 */
	public byte getFeatureCode() {
		return featureCode;
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
	 * Returns an Integer array of the values. 
	 * CH1 is located in the first element of the array, CH2 in the second one, and so on.
	 * If CHi is not active for this feature, the (i-1)th element is null.
	 * 
	 * @return all the values as an array of Integer
	 */
	public Integer[] getValues() {
		return this.chValues;
	}
	
	/**
	 * Returns the value of the given channel or 
	 * throws a spine.NoSuchChannelException if the given channel is not known or not valued for this Feature
	 * 
	 * @param chCode the channel code 
	 * 
	 * @return the value of the given channel 
	 * 
	 * @throws spine.NoSuchChannelException
	 * 
	 * @see spine.SPINESensorConstants
	 */
	public int getValue(byte chCode) {
		if (chCode != SPINESensorConstants.CH1 && chCode != SPINESensorConstants.CH2 &&
				chCode != SPINESensorConstants.CH3 && chCode != SPINESensorConstants.CH4)
			throw new NoSuchChannelException(UNKNOWN_CH_MSG + " (" + chCode + ")");
		
		// here we know the chCode exists 
		if (!SPINESensorConstants.chPresent(chCode, this.channelBitmask))
				throw new NoSuchChannelException(CH_NOT_PRESENT_MSG + " (" + SPINESensorConstants.channelCodeToString(chCode) + ")");
		
		// here we know the chCode exists and  it's active in this feature
		switch (chCode) {
			case SPINESensorConstants.CH1: return ch1Value;
			case SPINESensorConstants.CH2: return ch2Value;
			case SPINESensorConstants.CH3: return ch3Value;
			case SPINESensorConstants.CH4: return ch4Value;
			default: return 0;
		}
	}

	/**
	 * Getter method of the value of the first feature channel
	 * @return the value of the first feature channel
	 */
	public int getCh1Value() {
		return ch1Value;
	}

	/**
	 * Getter method of the value of the second feature channel
	 * @return the value of the second feature channel
	 */
	public int getCh2Value() {
		return ch2Value;
	}

	/**
	 * Getter method of the value of the third feature channel
	 * @return the value of the third feature channel
	 */
	public int getCh3Value() {
		return ch3Value;
	}
	
	/**
	 * Getter method of the value of the fourth feature channel
	 * @return the value of the fourth feature channel
	 */
	public int getCh4Value() {
		return ch4Value;
	}
	
	/**
	 * Getter method of the value of the feature label
	 * @return the value of the feature label
	 */
	public String getFeatureLabel() {
		return featureLabel;
	}
	
	
	/**
	 * 
	 * Setter method of the node 
	 * 
	 */
	public void setNode(Node node) {
		this.node = node;	
	}

	/**
	 * 
	 * Setter method of the feature code
	 * 
	 */
	public void setFeatureCode(byte featureCode) {
		this.featureCode = featureCode;	
	}

	/**
	 * 
	 * Setter method of the sensor code
	 * 
	 */
	public void setSensorCode(byte sensorCode) {
		this.sensorCode = sensorCode;			
	}	
	
	/**
	 * 
	 * Setter method of the feature label
	 * 
	 */
	public void setFeatureLabel(String featureLabel) {
		this.featureLabel = featureLabel;			
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
			case SPINESensorConstants.CH1: 
				this.channelBitmask |= SPINESensorConstants.CH1_ONLY; 
				this.chValues[0] = new Integer(this.ch1Value);
				break; 
			case SPINESensorConstants.CH2: 
				this.channelBitmask |= SPINESensorConstants.CH2_ONLY;
				this.chValues[1] = new Integer(this.ch2Value);
				break;
			case SPINESensorConstants.CH3: 
				this.channelBitmask |= SPINESensorConstants.CH3_ONLY;
				this.chValues[2] = new Integer(this.ch3Value);
				break;
			case SPINESensorConstants.CH4: 
				this.channelBitmask |= SPINESensorConstants.CH4_ONLY;
				this.chValues[3] = new Integer(this.ch4Value);
				break;
		}		
	}
	
	/**
	 * @param functionCode the functionCode to set
	 */
	public void setFunctionCode(byte functionCode) {
		this.functionCode = functionCode;
	}

	/**
	 * @param channelBitmask the channelBitmask to set
	 */
	public void setChannelBitmask(byte channelBitmask) {
		this.channelBitmask = channelBitmask;
	}

	/**
	 * @param ch1Value the ch1Value to set
	 */
	public void setCh1Value(int ch1Value) {
		this.ch1Value = ch1Value;
		chValues[0] = new Integer(this.ch1Value); 
	}

	/**
	 * @param ch2Value the ch2Value to set
	 */
	public void setCh2Value(int ch2Value) {
		this.ch2Value = ch2Value;
		chValues[1] = new Integer(this.ch2Value);
	}

	/**
	 * @param ch3Value the ch3Value to set
	 */
	public void setCh3Value(int ch3Value) {
		this.ch3Value = ch3Value;
		chValues[2] = new Integer(this.ch3Value);
	}

	/**
	 * @param ch4Value the ch4Value to set
	 */
	public void setCh4Value(int ch4Value) {
		this.ch4Value = ch4Value;
		chValues[3] = new Integer(this.ch4Value);
	}

	/**
	 * Compares this Feature to another Object. 
	 * If the Object is a Feature, this function compares hierarchically (and in the following order) 
	 * the physical id, the sensorCode, the featureCode and the channelBitmask. 
	 * Otherwise, it throws a ClassCastException (as Strings are comparable only to other Strings).
	 * 
	 * @param o the Object to be compared.
	 * 
	 * @return the value 0 if the argument is a Feature is "equal" to this Feature; 
	 * 			-1 if this Feature "precede" the argument o;
	 * 			+1 otherwise.  
	 */
	public int compareTo(Object o) {
		Feature f = (Feature)o;
		
		if (this.node.getPhysicalID().getAsInt() < f.node.getPhysicalID().getAsInt()) return -1;
		if (this.node.getPhysicalID().getAsInt() > f.node.getPhysicalID().getAsInt()) return 1;
		if (this.node.getPhysicalID().getAsInt() == f.node.getPhysicalID().getAsInt()) {
			if (this.sensorCode < f.sensorCode) return -1;
			if (this.sensorCode > f.sensorCode) return 1;
			if (this.sensorCode == f.sensorCode) {
				if (this.featureCode < f.featureCode) return -1;
				if (this.featureCode > f.featureCode) return 1;
				if (this.featureCode == f.featureCode) {
					if (this.channelBitmask < f.channelBitmask) return -1;
					if (this.channelBitmask > f.channelBitmask) return 1;
					if (this.channelBitmask == f.channelBitmask) return 0;
				}
			}
		}
		
		return 0;
	}
	
	/**
	 * Creates and returns a copy of this Feature.
	 * 
	 * @return an exact clone of this Feature instance.
	 */
	public Object clone() {
		Feature clone = new Feature(this.node, this.functionCode, this.featureCode, this.sensorCode, this.channelBitmask, 
									this.ch1Value, this.ch2Value, this.ch3Value, this.ch4Value, this.featureLabel);
		return clone;
	}	
	
	/**
	 * 
	 * Returns a string representation of the Feature object.
	 * 
	 * @return a string representation of this Feature
	 * 
	 */
	public String toString() {
		
		String info = "From node: {" + this.node.toShortString() + "} - " + SPINEFunctionConstants.FEATURE_LABEL + ": " + SPINEFunctionConstants.functionalityCodeToString(this.functionCode, this.featureCode) + 
					  " on " + SPINESensorConstants.sensorCodeToString(this.sensorCode) + 
					  " (now on " + SPINESensorConstants.channelBitmaskToString(this.channelBitmask) + ") " + 
					  " - " + SPINESensorConstants.CH1_LABEL + ": "+ this.ch1Value + 
					  "; " + SPINESensorConstants.CH2_LABEL + ": "+ this.ch2Value + 
					  "; " + SPINESensorConstants.CH3_LABEL + ": "+ this.ch3Value + 
					  "; " + SPINESensorConstants.CH4_LABEL + ": "+ this.ch4Value;
		
		if (this.featureLabel != null)
			info += " (" + this.featureLabel + ") ";
		
		return info;
	}
}