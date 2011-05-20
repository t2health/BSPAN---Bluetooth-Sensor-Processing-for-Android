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
* 
* Objects of this class are used for expressing at high level function requests 
* (both activation and deactivation) of type 'Feature'.
* An application that needs to do a Feature request, must create a new FeatureSpineFunctionReq
* object, set on it the sensor involved and use the addFeature one or more times 
* (currently, up to 7 add are supported per each request) 
* for features activation, or the removeFeature (currently, up to 7 remove are supported per each request) 
* for features deactivation.
* 
* This class also implements the encode method of the abstract class SpineFunctionReq that is used internally
* to convert the high level request into an actual SPINE Ota message.     
*
* @author Raffaele Gravina
*
* @version 1.3
*/

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
