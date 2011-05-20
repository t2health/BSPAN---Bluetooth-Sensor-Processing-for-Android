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
* This class represents the FeatureData entity.
* It contains the decode method for converting low level Feature type data into an high level object.
*
* @author Raffaele Gravina
* @author Philip Kuryloski
*
* @version 1.3
*/

package spine.datamodel;

public class FeatureData extends Data {

	private static final long serialVersionUID = 1L;
	
	Feature[] features;
			
	/**
	 * Getter method of the features array
	 * @return the array of Feature objects of this FeatureData
	 */
	public Feature[] getFeatures() {
	    return features;
	}
	
	/**
	 * Setter method of the features array
	 * @param features the array of Feature objects of this FeatureData
	 */
	public void setFeatures(Feature[] features) {
		this.features = features;
	}
		
	/**
	 * 
	 * Returns a string representation of the Feature object.
	 * 
	 * @return a string representation of this Feature
	 * 
	 */
	public String toString() {
		String s = "Feature set received from Node {" + this.node.toShortString() + "} :\n";
		
		for (int i=0; i<features.length; i++) {
			s += "\t" + features[i] + "\n";
		}
		
		return s;
	}
	
}
