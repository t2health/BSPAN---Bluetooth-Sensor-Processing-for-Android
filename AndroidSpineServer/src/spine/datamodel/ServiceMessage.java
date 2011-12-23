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
