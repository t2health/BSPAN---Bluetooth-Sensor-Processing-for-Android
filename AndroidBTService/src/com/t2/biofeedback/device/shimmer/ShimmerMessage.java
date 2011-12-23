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

package com.t2.biofeedback.device.shimmer;

public class ShimmerMessage {
	private static final long serialVersionUID = 1L;	
	public static final int NUM_AXES = 8;	
	
	public static final int AXIS_X = 0;
	public static final int AXIS_Y = 1;
	public static final int AXIS_Z = 2;	
	
	// Sampling rates
	public static final byte SAMPLING1000HZ =    (byte)1;
	public static final byte SAMPLING500HZ =     (byte)2;
	public static final byte SAMPLING250HZ =     (byte)4;
	public static final byte SAMPLING200HZ =     (byte)5;
	public static final byte SAMPLING166HZ =     (byte)6;
	public static final byte SAMPLING125HZ =     (byte)8;
	public static final byte SAMPLING100HZ =     (byte)0x0a;
	public static final byte SAMPLING50HZ =      (byte)0x14;
	public static final byte SAMPLING10HZ =      (byte)0x64;
	public static final byte SAMPLING4HZ =       (byte) 0xf0;
	public static final byte SAMPLING0HZOFF =    (byte) 0xff;

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
    
    // Sensor enums as reported by the INQUIRYCOMMAND
    public static final byte XAccel      = 0x00;
    public static final byte YAccel      = 0x01;
    public static final byte ZAccel      = 0x02;
    public static final byte XGyro       = 0x03;
    public static final byte YGyro       = 0x04;
    public static final byte ZGyro       = 0x05;
    public static final byte XMag        = 0x06;
    public static final byte YMag        = 0x07;
    public static final byte ZMag        = 0x08;
    public static final byte EcgRaLl     = 0x09;
    public static final byte EcgLaLl     = 0x0A;
    public static final byte GsrRaw      = 0x0B;
    public static final byte GsrRes      = 0x0C;
    public static final byte Emg         = 0x0D;
    public static final byte AnExA0      = 0x0E;
    public static final byte AnExA7      = 0x0F;
    public static final byte StrainHigh  = 0x10;
    public static final byte StrainLow   = 0x11;
    public static final byte HeartRate   = 0x1;

    // Sensor bitmaps for the SETSENSORSCOMMAND
    public static final byte SENSOR0_SensorAccel     = (byte) 0x80;
    public static final byte SENSOR0_SensorGyro      = (byte) 0x40;
    public static final byte SENSOR0_SensorMag       = (byte) 0x20;
    public static final byte SENSOR0_SensorECG       = (byte) 0x10;
    public static final byte SENSOR0_SensorEMG       = (byte) 0x08;
    public static final byte SENSOR0_SensorGSR       = (byte) 0x04;
    public static final byte SENSOR0_SensorAnExA7    = (byte) 0x02;
    public static final byte SENSOR0_SensorAnExA0    = (byte) 0x01;

    public static final byte SENSOR1_SensorStrain    = (byte) 0x80;
    public static final byte SENSOR1_SensorHeart     = (byte) 0x40;    
    
    // Accelerometer ranges for SETACCELRANGECOMMAND
    public static final byte ACCEL_RANGE_RANGE_1_5G = 0;
    public static final byte ACCEL_RANGE_RANGE_2_0G = 1;
    public static final byte ACCEL_RANGE_RANGE_4_0G = 2;
    public static final byte ACCEL_RANGE_RANGE_6_0G = 3;

    // GSR ranges for SETGSRRANGECOMMAND
    public static final byte GSR_RANGE_HW_RES_40K    = 0;
    public static final byte GSR_RANGE_HW_RES_287K   = 1;
    public static final byte GSR_RANGE_HW_RES_1M     = 2;
    public static final byte GSR_RANGE_HW_RES_3M3    = 3;
    public static final byte GSR_RANGE_AUTORANGE     = 4;    
	
	
	public int gsr;
	public int emg;
	public int heartrate;
	public int vUnreg;
	public int vReg;
	public int gsrRange;
	public int SamplingRate;
	public int[] accel = new int[NUM_AXES];
	public int accelRange;
	public int ecgRaLL;
	public int ecgLaLL;

	public byte functionCode;	
	public byte sensorCode;
	public byte packetType;

	
	public ShimmerMessage() {
	}

	public ShimmerMessage(	byte functionCode, byte sensorCode, byte packetType) {
		this.functionCode = functionCode;
		this.sensorCode = sensorCode;
		this.packetType = packetType;
	}	
	
	public String getLogDataLine() {
		String line = "";							// Comment
		return line;
	}

	
	public String getLogDataLineHeader() {
		String line = "";							// Comment
		return line;
	}

}
