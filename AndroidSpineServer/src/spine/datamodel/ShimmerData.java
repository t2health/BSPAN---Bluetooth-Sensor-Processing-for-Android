package spine.datamodel;

import android.content.Context;



public class ShimmerData  extends Data {
	private static final long serialVersionUID = 1L;	
	public static final int NUM_AXES = 8;	
	
	public static final int AXIS_X = 0;
	public static final int AXIS_Y = 1;
	public static final int AXIS_Z = 2;	
	
	// Sampling rates
	public static byte SAMPLING1000HZ =    1;
	public static byte SAMPLING500HZ =     2;
	public static byte SAMPLING250HZ =     4;
	public static byte SAMPLING200HZ =     5;
	public static byte SAMPLING166HZ =     6;
	public static byte SAMPLING125HZ =     8;
	public static byte SAMPLING100HZ =     10;
	public static byte SAMPLING50HZ =      20;
	public static byte SAMPLING10HZ =      100;
	public static byte SAMPLING0HZOFF =    25;

	// packet types
    public static byte DATAPACKET                  = 0X00;
    public static byte INQUIRYCOMMAND              = 0X01;
    public static byte INQUIRYRESPONSE             = 0X02;
    public static byte GETSAMPLINGRATECOMMAND      = 0X03;
    public static byte SAMPLINGRATERESPONSE        = 0X04;
    public static byte SETSAMPLINGRATECOMMAND      = 0X05;
    public static byte TOGGLELEDCOMMAND            = 0X06;
    public static byte STARTSTREAMINGCOMMAND       = 0X07;
    public static byte SETSENSORSCOMMAND           = 0X08;
    public static byte SETACCELRANGECOMMAND        = 0X09;
    public static byte ACCELRANGERESPONSE          = 0X0A;
    public static byte GETACCELRANGECOMMAND        = 0X0B;
    public static byte SET5VREGULATORCOMMAND       = 0X0C;
    public static byte SETPOWERMUXCOMMAND          = 0X0D;
    public static byte SETCONFIGSETUPBYTE0COMMAND  = 0X0E;
    public static byte CONFIGSETUPBYTE0RESPONSE    = 0X0F;
    public static byte GETCONFIGSETUPBYTE0COMMAND  = 0X10;
    public static byte SETACCELCALIBRATIONCOMMAND  = 0X11;
    public static byte ACCELCALIBRATIONRESPONSE    = 0X12;
    public static byte GETACCELCALIBRATIONCOMMAND  = 0X13;
    public static byte SETGYROCALIBRATIONCOMMAND   = 0X14;
    public static byte GYROCALIBRATIONRESPONSE     = 0X15;
    public static byte GETGYROCALIBRATIONCOMMAND   = 0X16;
    public static byte SETMAGCALIBRATIONCOMMAND    = 0X17;
    public static byte MAGCALIBRATIONRESPONSE      = 0X18;
    public static byte GETMAGCALIBRATIONCOMMAND    = 0X19;
    public static byte STOPSTREAMINGCOMMAND        = 0X20;
    public static byte SETGSRRANGECOMMAND          = 0X21;
    public static byte GSRRANGERESPONSE            = 0X22;
    public static byte GETGSRRANGECOMMAND          = 0X23;
    public static byte GETSHIMMERVERSIONCOMMAND    = 0X24;
    public static byte SHIMMERVERSIONRESPONSE      = 0X25;
    public static byte ACKCOMMANDPROCESSED         = (byte) 0XFF;	
	
	
	public int timestamp;
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
		line += this.gsr + "\n";
		return line;
	}

	
	public String getLogDataLineHeader() {
		String line = "timestamp, accelX, gsr";							// Comment
		return line;
	}

}
