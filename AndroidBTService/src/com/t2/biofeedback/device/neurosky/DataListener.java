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

package com.t2.biofeedback.device.neurosky;

/*
 * @(#)DataListener.java    0.9    Jun 04, 2008
 *
 * Copyright (c) 2008 NeuroSky, Inc. All Rights Reserved
 * NEUROSKY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/**
 * @file DataListener.java
 *
 * The DataListener interface defines a callback function 
 * which is called by a ThinkGearStreamParser whenever a valid DataRow 
 * is received in the payload of a ThinkGear Packet.  The DataRow
 * encapsulates a ThinkGear data value (or values).
 * 
 * A DataRow consists of the extended code level and @c code, which 
 * together describe the type of data value(s) contained in the DataRow, 
 * as well as the @c valueBytes[] array, consisting of @c numBytes bytes, 
 * which holds the data value(s) itself/themselves.  Refer to the 
 * ThinkGearDataManual.txt for details on how to interpret the 
 * @c valueBytes[] array based on the @c extendedCodeLevel/code.  
 * 
 * @author Kelvin Soo
 * @version 0.9 Jun 04, 2008 Kelvin Soo
 *   - Initial version.
 */
public interface
DataListener {
	
	/**
	 * Called when the parser has a data row to send to the Spine server 
	 * @param extendedCodeLevel Not currently used
	 * @param code Exe (function) code as devined by the Neurosky protocol
	 * @param numBytes Number of bytest in message
	 * @param valueBytes Message bytes
	 * @param customData Currently not used
	 */
	public void 
	dataValueReceived( int extendedCodeLevel, int code, int numBytes,
					   byte[] valueBytes, Object customData );
}
