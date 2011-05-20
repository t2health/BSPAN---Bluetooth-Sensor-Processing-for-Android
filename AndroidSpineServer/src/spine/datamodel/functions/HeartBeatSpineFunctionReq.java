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
* (both activation and deactivation) of type 'Heart Beat'.
* An application that needs to do a HeartBeat request, must create a new HeartBeatSpineFunctionReq
* object for alarm activation, or deactivation.
* 
* This class also implements the encode method of the abstract class SpineFunctionReq that is used internally
* to convert the high level request into an actual SPINE Ota message.     
*
*
* @author Alessandro Andreoli
* @author Raffaele Gravina
*
* @version 1.3
*/

package spine.datamodel.functions;

public class HeartBeatSpineFunctionReq extends SpineFunctionReq {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 * Returns a string representation of the HeartBeatSpineFunctionReq object.
	 * 
	 * @return a string representation of this HeartBeatSpineFunctionReq
	 * 
	 */
	public String toString() {
		String s = "Heart-Beat Function Req {";		
		s += (this.isActivationRequest)? "activate" : "deactivate";		
		s += "}";		
		return s;
	}
	
}
