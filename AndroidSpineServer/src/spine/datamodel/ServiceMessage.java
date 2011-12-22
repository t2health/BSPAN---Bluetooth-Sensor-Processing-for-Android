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
* This class represents the ServiceMessage entity.
* It contains a constructor, toString and getters methods.
*
* @author Raffaele Gravina
*
* @version 1.3
*/

package spine.datamodel;

import spine.SPINEServiceMessageConstants;
import spine.datamodel.functions.SpineObject;

public class ServiceMessage implements SpineObject {

	private static final long serialVersionUID = 1L;

	protected Node node = null;
	
	protected int nodeID = -1;
	protected byte messageType = -1;
	protected byte messageDetail = -1;
	

	/**
	 * Default constructor of a ServiceMessage object.
	 */
	public ServiceMessage() {}

	/**
	 * Getter method for the message type attribute 
	 * 
	 * @return the message type code
	 * 
	 */
	public byte getMessageType() {
		return messageType;
	}

	/**
	 * Getter method for the message detail attribute 
	 * 
	 * @return the message detail code
	 * 
	 */
	public byte getMessageDetail() {
		return messageDetail;
	}
	
	/**
	 * INTERNAL FRAMEWORK USE ONLY
	 */
	public void setMessageDetail(byte messageDetail) {
		this.messageDetail = messageDetail;
	}
	
	/**
	 * INTERNAL FRAMEWORK USE ONLY
	 */
	public void setMessageType(byte messageType) {
		this.messageType = messageType;
	}
	
	/**
	 * INTERNAL FRAMEWORK USE ONLY
	 */
	public void setNode(Node node) {
		this.node = node;
	}
	
	/**
	 * Getter method of the node issuing this service message 
	 * 
	 * @return the Node issuing this service message 
	 * 
	 */
	public Node getNode() {
		return this.node;
	}
	
	/**
	 * Currently, does nothing!
	 * 
	 */
	protected String parse(){
		return "";
	}

	/**
	 * 
	 * Returns a string representation of the ServiceMessage object.
	 * 
	 * @return a string representation of this ServiceMessage
	 * 
	 */
	public String toString() {
		return "Svc Msg from {" + this.node.toShortString() + "} - " + 
					SPINEServiceMessageConstants.messageTypeToString(this.messageType) + ":" + 
					SPINEServiceMessageConstants.messageDetailToString(this.messageType,this.messageDetail);
	}
	
}
