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

import java.util.Vector;

import spine.SPINEFunctionConstants;
import spine.SPINESensorConstants;
import spine.datamodel.Feature;

public class FeatureSpineFunctionReq extends SpineFunctionReq {

	private static final long serialVersionUID = 1L;
	
	private byte sensor = -1;
	private Vector features = new Vector();
    
	
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
	 * Getter method of the Vector of requested feature in this object.
	 * Note that "removing" features, actually will add those features to the deactivation request
	 * with their channel bitmask XORed with 0xF (Ob11111111). This is due to packet format implementation details.  
	 * 
	 * @return the Vector of the requested feature 
	 */
	public Vector getFeatures() {
		Vector features;
		features = this.features; 
		return features;		
	}
	
	
	/**
	 * Add a new feature to the activation request.
	 * Note that on each request object calling addFeature is mutually exclusive with
	 * removeFeature calls.  
	 * 
	 * @param f the feature to add in this request
	 * 
	 * @see spine.SPINESensorConstants
	 * @see spine.SPINEFunctionConstants
	 */
	public void add(Feature f) {
		this.features.addElement(f);		
	}
	
	/**
	 * Add a new feature to the deactivation request.
	 * 
	 * Note that on each request object calling removeFeature is mutually exclusive with
	 * addFeature calls.  
	 * 
	 * @param f the feature to remove in this request
	 * 
	 * @see spine.SPINESensorConstants
	 * @see spine.SPINEFunctionConstants
	 */
	public void remove(Feature f) {
		f.setChannelBitmask((byte)(f.getChannelBitmask() ^ 0x0F));
		this.features.addElement(f);		
	}
	
	
	/**
	 * 
	 * Returns a string representation of the FeatureSpineFunctionReq object.
	 * 
	 */
	public String toString() {
		String s = "Feature Function ";
		
		s += (this.isActivationRequest)? "Activation {": "Deactivation {";
		
		s += "sensor = " + SPINESensorConstants.sensorCodeToString(sensor) + ", ";
		
		for (int i = 0; i < features.size(); i++) {
			s += "feature = " + SPINEFunctionConstants.functionalityCodeToString(SPINEFunctionConstants.FEATURE, ((Feature)features.elementAt(i)).getFeatureCode()) + ", ";
			s += (this.isActivationRequest)? "channels = " + SPINESensorConstants.channelBitmaskToString(((Feature)features.elementAt(i)).getChannelBitmask()):
											 "channels = " + SPINESensorConstants.channelBitmaskToString((byte)(((Feature)features.elementAt(i)).getChannelBitmask() ^ 0x0F));
			if (i < features.size() - 1)
				s += ", ";
		}
		s += "}";
		
		return s;
	}
	
}
