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
	
	public void 
	dataValueReceived( int extendedCodeLevel, int code, int numBytes,
					   byte[] valueBytes, Object customData );
}
