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

import jade.util.Logger;
import spine.Properties;
import spine.SPINEManager;
import spine.datamodel.functions.CodecInfo;
import spine.datamodel.functions.SpineObject;

public abstract class Data implements SpineObject {
	
	@SuppressWarnings({ "unchecked", "rawtypes" })

	private static final long serialVersionUID = 1L;
	
	private static final String SPINEDATACODEC_PACKAGE_PREFIX = "spine.payload.codec.";
	private static final String SPINEDATACODEC_PACKAGE = SPINEDATACODEC_PACKAGE_PREFIX + 
				Properties.getDefaultProperties().getProperty(SPINEManager.getPlatform() + "_" + 
						Properties.SPINEDATACODEC_PACKAGE_SUFFIX_KEY) + ".";
	
	protected static CodecInfo codecInformation=null;
	
	protected long timestamp = 0;

	protected Node node = null;
	
	protected byte functionCode = -1;
	
	protected Data() {}
		

	/**
	 * INTERNAL FRAMEWORK USE ONLY
	 */
	public void baseInit(Node node, byte[] payload) {
		timestamp = System.currentTimeMillis();
		this.node = node;
		
		//  Setting functionCode
		try {
			// dynamic class loading of the proper CodecInformation
			if (codecInformation==null){
				Class g = Class.forName(SPINEDATACODEC_PACKAGE + 
				       "CodecInformation");
				codecInformation = (CodecInfo)g.newInstance();
			} 
			functionCode=codecInformation.getFunctionCode(payload);
		} catch (Exception e) { 
			e.printStackTrace();
			if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) 
				SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
		} 	
	}
	
	/**
	 * INTERNAL FRAMEWORK USE ONLY
	 */
	public void setFunctionCode(byte functionCode) {
		this.functionCode = functionCode;
	}
	

	/**
	 * INTERNAL FRAMEWORK USE ONLY
	 */
	public void setNode(Node node) {
		this.node = node;
	}
	

	/**
	 * INTERNAL FRAMEWORK USE ONLY
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;

	}
	
	/**
	 * Getter method of the code of the function generating of the data 
	 * 
	 * @return the code of the function generating of the data
	 */
	public byte getFunctionCode() {
		return this.functionCode;
	}
	
	/**
	 * Getter method of the ID of the node generating the data
	 * 
	 * @return the node of the function generating the data
	 */
	public Node getNode() {
		return this.node;
	}
	
	/**
	 * Getter method of the data creation timestamp
	 * 
	 * @return the data creation timestamp
	 */
	public long getTimestamp() {
		return this.timestamp;
	}
	
	/**
	 * Converts the four following bytes in the array 'bytes' starting from the index 'index' 
	 * into the corresponding integer
	 * 
	 * @param bytes the byte array from where to take the 4 bytes to be converted to an integer 
	 * @param index the starting index on the interested portion to convert
	 * 
	 * @return the converted integer
	 */  
	public static int convertFourBytesToInt(byte[] bytes, int index) {    
		if(bytes.length < 4) return 0;
		
		return ( bytes[index + 3] & 0xFF) 		 |
	           ((bytes[index + 2] & 0xFF) << 8)  |
	           ((bytes[index + 1] & 0xFF) << 16) |
	           ((bytes[index] & 0xFF) << 24);
	}
	
	/**
	 * Converts the two following bytes in the array 'bytes' starting from the index 'index' 
	 * into the corresponding integer
	 * 
	 * @param bytes the byte array from where to take the 2 bytes to be converted to an integer
	 * @param index the starting index on the interested portion to convert
	 * 
	 * @return the converted integer
	 */
	public static int convertTwoBytesToInt(byte[] bytes, int index) {
		if(bytes.length < 2) return 0;
		
		return   (bytes[index + 1] & 0xFF) |
		        ((bytes[index] & 0xFF) << 8);
	}
	
}

