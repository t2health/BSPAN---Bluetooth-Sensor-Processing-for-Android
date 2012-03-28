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

import android.content.Context;



public class ShimmerData  extends Data {
	private static final long serialVersionUID = 1L;	
	public static final int NUM_AXES = 8;	
	
	public static final int AXIS_X = 0;
	public static final int AXIS_Y = 1;
	public static final int AXIS_Z = 2;	
	
    // GSR Range
	public static final byte HW_RES_40K    = 0;
	public static final byte HW_RES_287K   = 1;
	public static final byte HW_RES_1M     = 2;
	public static final byte HW_RES_3M3    = 3;
	public static final byte AUTORANGE     = 4;
	
	// Sampling rates
	public static final byte SAMPLING1000HZ =    1;
	public static final byte SAMPLING500HZ =     2;
	public static final byte SAMPLING250HZ =     4;
	public static final byte SAMPLING200HZ =     5;
	public static final byte SAMPLING166HZ =     6;
	public static final byte SAMPLING125HZ =     8;
	public static final byte SAMPLING100HZ =     10;
	public static final byte SAMPLING50HZ =      20;
	public static final byte SAMPLING10HZ =      100;
	public static final byte SAMPLING0HZOFF =    25;

	// packet types
    public static final byte DATAPACKET                  = 0X00;
    public static final byte INQUIRYCOMMAND              = 0X01;
    public static final byte INQUIRYRESPONSE             = 0X02;
    public static final byte GETSAMPLINGRATECOMMAND      = 0X03;
    public static final byte SAMPLINGRATERESPONSE        = 0X04;
    public static final byte SETSAMPLINGRATECOMMAND      = 0X05;
    public static final byte TOGGLELEDCOMMAND            = 0X06;
    public static final byte STARTSTREAMINGCOMMAND       = 0X07;
    public static final byte SETSENSORSCOMMAND           = 0X08;
    public static final byte SETACCELRANGECOMMAND        = 0X09;
    public static final byte ACCELRANGERESPONSE          = 0X0A;
    public static final byte GETACCELRANGECOMMAND        = 0X0B;
    public static final byte SET5VREGULATORCOMMAND       = 0X0C;
    public static final byte SETPOWERMUXCOMMAND          = 0X0D;
    public static final byte SETCONFIGSETUPBYTE0COMMAND  = 0X0E;
    public static final byte CONFIGSETUPBYTE0RESPONSE    = 0X0F;
    public static final byte GETCONFIGSETUPBYTE0COMMAND  = 0X10;
    public static final byte SETACCELCALIBRATIONCOMMAND  = 0X11;
    public static final byte ACCELCALIBRATIONRESPONSE    = 0X12;
    public static final byte GETACCELCALIBRATIONCOMMAND  = 0X13;
    public static final byte SETGYROCALIBRATIONCOMMAND   = 0X14;
    public static final byte GYROCALIBRATIONRESPONSE     = 0X15;
    public static final byte GETGYROCALIBRATIONCOMMAND   = 0X16;
    public static final byte SETMAGCALIBRATIONCOMMAND    = 0X17;
    public static final byte MAGCALIBRATIONRESPONSE      = 0X18;
    public static final byte GETMAGCALIBRATIONCOMMAND    = 0X19;
    public static final byte STOPSTREAMINGCOMMAND        = 0X20;
    public static final byte SETGSRRANGECOMMAND          = 0X21;
    public static final byte GSRRANGERESPONSE            = 0X22;
    public static final byte GETGSRRANGECOMMAND          = 0X23;
    public static final byte GETSHIMMERVERSIONCOMMAND    = 0X24;
    public static final byte SHIMMERVERSIONRESPONSE      = 0X25;
    public static final byte ACKCOMMANDPROCESSED         = (byte) 0XFF;	
	
	
	public int timestamp;
	public int gsr;
	public int emg;
	public int ecg;
	public int vUnreg;
	public int vReg;
	public int gsrRange;
	public int SamplingRate;
	public int[] accel = new int[NUM_AXES];
	public int accelRange;
	public int ecgRaLL;
	public int ecgLaLL;
	

	
	private Node node;
	public byte functionCode;	
	public byte sensorCode;
	public byte packetType;

	
	public ShimmerData(Context aContext) {
	}

	public ShimmerData(	byte functionCode, byte sensorCode, byte packetType) {
		this.functionCode = functionCode;
		this.sensorCode = sensorCode;
		this.packetType = packetType;
	}	
	
	public String getLogDataLine() {
		String line = "";							// Comment
		line += this.timestamp + ", ";
		line += this.accel[AXIS_X] + ", ";
		line += this.gsrRange + ", ";
		line += this.gsr;
		return line;
	}

	
	public String getLogDataLineHeader() {
		String line = "timestamp, accelX, range, gsr";							// Comment
		return line;
	}

}
