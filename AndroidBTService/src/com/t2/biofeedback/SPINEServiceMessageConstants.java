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

package com.t2.biofeedback;

public class SPINEServiceMessageConstants {
	// MESSAGE TYPES
	public static final byte ERROR = 0;
	public static final byte WARNING = 1;
	public static final byte ACK = 2;
	public static final byte NOT_SPECIFIED=0x7F;
	public static final byte INFO=3;
	
	// MESSAGE DATAILS
	public static final byte CONNECTION_FAIL = 10;
	public static final byte UNKNOWN_PKT_RECEIVED = 11;
	public static final byte FEATURE_STAT=0x0C;
	public static final byte FEATURE_ENGINE_STAT=0x0D;
	
	// MESSAGE TYPES LABELS
	public static final String ERROR_LABEL = "Error";
	public static final String WARNING_LABEL = "Warning";
	public static final String ACK_LABEL = "Ack";
	public static final String INFO_LABEL = "Info";
	
	// MESSAGE DATAILS LABELS
	public static final String CONNECTION_FAIL_LABEL = "Connection Fail";
	public static final String UNKNOWN_PKT_RECEIVED_LABEL = "Unknown Packet Received";
	public static final String FEATURE_STAT_LABEL = "Performance low-level Feature Calc";
	public static final String FEATURE_ENGINE_STAT_LABEL = "Performance high-level feature calc";
	
	/**
	 * Returns the string label mapped to the given message detail code
	 * 
	 * @return the message detail string label mapped to the given message detail code 
	 */
	public static String messageDetailToString(byte messageType,byte messageDetail) {
		switch(messageType) {
			case SPINEServiceMessageConstants.ERROR: 
				switch(messageDetail) {
					case SPINEServiceMessageConstants.CONNECTION_FAIL: return SPINEServiceMessageConstants.CONNECTION_FAIL_LABEL;
					case SPINEServiceMessageConstants.UNKNOWN_PKT_RECEIVED: return SPINEServiceMessageConstants.UNKNOWN_PKT_RECEIVED_LABEL;
				}
			case SPINEServiceMessageConstants.WARNING: return "" + messageDetail;
			case SPINEServiceMessageConstants.ACK: return "seq# " + messageDetail;
			case SPINEServiceMessageConstants.INFO:
				switch (messageDetail) {
				case SPINEServiceMessageConstants.FEATURE_STAT:return SPINEServiceMessageConstants.FEATURE_STAT_LABEL;
				case SPINEServiceMessageConstants.FEATURE_ENGINE_STAT:return SPINEServiceMessageConstants.FEATURE_ENGINE_STAT_LABEL;
				}
			default: return "UNKNOWN SERVICE MESSAGE " + messageDetail;
		}
	}
	
	/**
	 * Returns the string label mapped to the message type code of the current ServiceMessage object
	 * 
	 * @return the message type string label mapped to the given message type code
	 */
	public static String messageTypeToString(byte messageType) {
		switch(messageType) {
			case SPINEServiceMessageConstants.ERROR: return SPINEServiceMessageConstants.ERROR_LABEL;
			case SPINEServiceMessageConstants.WARNING: return SPINEServiceMessageConstants.WARNING_LABEL;
			case SPINEServiceMessageConstants.ACK: return SPINEServiceMessageConstants.ACK_LABEL;
			case SPINEServiceMessageConstants.INFO:return SPINEServiceMessageConstants.INFO_LABEL;
			default: return "UNKNOWN";
		}
	}
	
	/**
	 * Returns the string label mapped to the service message type code of the current ServiceMessage object
	 * 
	 * @return the service message type string label mapped to the given service message type code
	 */
	public static String serviceMessageTypeToString(byte serviceMessageType) {
		switch (serviceMessageType) {
			case ERROR: return "ServiceError";
			case WARNING: return "ServiceWarning";
			case ACK: return "ServiceAck";
			case INFO: return "ServiceInfo";
			default: return "ServiceNotSpecified";
		}
	}	
}
