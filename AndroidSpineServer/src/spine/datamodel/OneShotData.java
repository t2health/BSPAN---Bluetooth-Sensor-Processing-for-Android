/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
allows dynamic on node configuration for feature extraction and a 
OtA protocol for the management for WSN

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
* This class represents the OneShotData entity.
* It contains the decode method for converting low level One-Shot type data into an high level object.
*
* @author Raffaele Gravina
* @author Philip Kuryloski
*
* @version 1.3
*/

package spine.datamodel;

public class OneShotData extends Data {
	
	private static final long serialVersionUID = 1L;
	
	Feature oneShot = null;		

	/**
	 * 
	 * Returns the one shot feature contained into the OneShot Data message received.
	 * 
	 * @return the one shot feature contained into the OneShot Data message received.
	 * 
	 */
	public Feature getOneShot() {
		return oneShot;
	}

	/**
	 * INTERNAL FRAMEWORK USE ONLY
	 */
	public void setOneShot(Feature oneShot) {
		this.oneShot = oneShot;
	}
	
	/**
	 * 
	 * Returns a string representation of the OneShotData object.
	 * 
	 * @return a string representation of this OneShotData
	 * 
	 */
	public String toString() {
		return "" + oneShot;
	}	
}
