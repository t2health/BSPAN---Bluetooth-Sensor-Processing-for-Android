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

public class HmmData extends Data {
	
	private static final long serialVersionUID = 1L;
	
	private int[] states;
	
	/**
	 * Constructor of a HmmData object.
	 * This is used by the lower level components of the framework for creating HmmData objects
	 * from a low level HMM data packet received by remote nodes. 
	 * 
	 * @param functionCode the function code 
	 * @param states the array containing a sequence of states detected by the on-node HMM classifier.
	 * 
	 */
	public HmmData(byte functionCode, int[] states) {

		this.functionCode = functionCode;
		
		this.states = states;
	}
	
	/**
	 * Default Constructor.
	 * 
	 */
	public HmmData() {}

	/**
	 * Getter method of the function code
	 * 
	 * @return the function code
	 */
	public byte getFunctionCode() {
		return functionCode;
	}
		
	/**
	 * Getter method of the states list
	 * 
	 * @return the states list
	 */
	public int[] getStates() {
		return states;
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
	 * Setter method for the states of the hmm classification
	 * 
	 * @param states the states of the hmm classification
	 */
	public void setStates(int[] states) {
		this.states = states;
	}		
	
	/**
	 * 
	 * Returns a string representation of the HMMData object.
	 * 
	 * @return a string representation of this HMMData
	 * 
	 */
	public String toString() {
		return "From node: {" + this.node.toShortString() + "} - " + SPINEFunctionConstants.HMM_LABEL + ": " +  
				" on " + SPINESensorConstants.ACC_SENSOR_LABEL + 
				" - STATES: " + Arrays.toString(states);
				//" - STATES: " + hacked(states);
	}
	
	/*String hacked(int[]states){
		String s = "";
		for(int i=0;i<states.length;i+=2) {
			String b1 = "" + Integer.toHexString(states[i]);
			if(b1.length() > 2) b1 = b1.substring(b1.length()-2,b1.length());
			String b2 = "" + Integer.toHexString(states[i+1]);
			if(b2.length() > 2) b2 = b2.substring(b2.length()-2,b2.length());
			if(b2.length()==1) b2 = "0"+b2;
			String temp = b1 + "" + b2;
			s += Integer.parseInt(temp, 16) + ", ";
		}
		
		return s;
	}*/

}
