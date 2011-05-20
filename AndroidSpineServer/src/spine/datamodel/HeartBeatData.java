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
* This class represents the HeartBeatData entity.
* It contains the decode method for converting low level Heart-Beat type data into an high level object.
*
* @author Alessandro Andreoli
* @author Raffaele Gravina
*
* @version 1.3
*/

package spine.datamodel;

import spine.SPINEFunctionConstants;

public class HeartBeatData extends Data {
	
	private static final long serialVersionUID = 1L;
	
	private int bpm;
		
	/**
	 * Getter method of the BPM (beat per minute)
	 * 
	 * @return the BPM
	 */
	public int getBPM() {
		return this.bpm;
	}

	/**
	 * Setter method of the BPM (beat per minute)
	 * 
	 * @param bpm the bpm (beat per minute)  to set
	 */
	public void setBPM(int bpm) {
		this.bpm = bpm;
	}
	
	/**
	 * 
	 * Returns a string representation of the HeartBeat object.
	 * 
	 * @return a string representation of this HeartBeat 
	 * 
	 */
	public String toString() {
		return "From node: {" + this.node.toShortString() + "} - " + SPINEFunctionConstants.HEARTBEAT_LABEL + 
				" update: "	+ this.bpm;
	}
}
