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

package com.t2;

/**
 * Static global constants for the Android Spine Server
 * @author scott.coleman
 *
 */
public class Constants {
	public static final String TAG = "BFDemo";
	public static final int RESERVED_ADDRESS_ZEPHYR = -15;   // 0xfff1
	public static final int RESERVED_ADDRESS_MINDSET = -14;  // 0xfff2
	public static final int RESERVED_ADDRESS_SHIMMER = -13;  // 0xfff3
	public static final int RESERVED_ADDRESS_ARDUINO = 1;
	
	
	public static final int DATA_TYPE_HEARTRATE = 1;
	public static final int DATA_TYPE_MEDITATION = 2;
	public static final int DATA_TYPE_ATTENTION = 3;
	public static final int DATA_SIGNAL_STRENGTH = 4;	
	public static final int DATA_ZEPHYR_BATTLEVEL = 5;	
	public static final int DATA_ZEPHYR_HEARTRATE = 6;	
	public static final int DATA_ZEPHYR_RESPRATE = 7;	
	public static final int DATA_ZEPHYR_SKINTEMP = 8;	

	public static final byte EXECODE_POOR_SIG_QUALITY = 2;
	public static final byte EXECODE_ATTENTION = 4;
	public static final byte EXECODE_MEDITATION = 5;
	public static final byte EXECODE_BLINK_STRENGTH = 0x16;
	public static final byte EXECODE_RAW_WAVE = (byte) 0x80;
	public static final byte EXECODE_SPECTRAL = (byte) 0x83;
	public static final byte EXECODE_RAW_ACCUM = (byte) 0x90;				// Special T2 code for gaterhing 1 second of raw data and sending it all together
	public static final int RAW_ACCUM_SIZE = 512;				// Size of raw wave data accumulator
	

	
}
