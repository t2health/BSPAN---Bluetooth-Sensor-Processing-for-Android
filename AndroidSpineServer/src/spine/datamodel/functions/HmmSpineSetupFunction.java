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
* Implementation of SpineSetupFunction responsible of handling setup of the function type 'HmmData'
*
* @author Raffaele Gravina
*
* @version 1.3
*/

package spine.datamodel.functions;

public class HmmSpineSetupFunction implements SpineSetupFunction {

	private static final long serialVersionUID = 1L;
	
	private boolean sendFullMsg = false;

	
	/**
	 * Sets a flag a flag specfying whether we wish to receive detected states packed in a fully filled message
	 * or if a state must be transmitted over-the-air as soon as it has been detected. 
	 * 
	 * @param sendFullMsg set it to true if you wish to get as many detected states buffered into a single message as possible

	 */
	public void setSendFullMsg(boolean sendFullMsg) {
		this.sendFullMsg = sendFullMsg;
	}
	

    /**
	 * 
	 * Returns a string representation of this BufferedRawDataSpineSetupFunction object.
	 * 
	 *@return true if this setup object is requesting a buffered transmission of states detected. false otherwise
	 */
	public boolean isSendFullMsg() {
		return sendFullMsg;
	}
	
	/**
	 * 
	 * Returns a string representation of this HmmSpineSetupFunction object.
	 * 
	 * @return a string representation of this HmmSpineFunctionReq
	 * 
	 */
	public String toString() {
		String s = "Hmm Data Setup Function {";
		
		s += "sendFullMsg = " + sendFullMsg + "}";
		
		return s;
	}
	
}
