package spine.datamodel;

import android.content.Context;



public class ShimmerData  extends Data {
	private static final long serialVersionUID = 1L;	
	public static final int NUM_AXES = 8;	
	
	public static final int AXIS_X = 0;
	public static final int AXIS_Y = 1;
	public static final int AXIS_Z = 2;	
	
	// Sampling rates
	public static int SAMPLING1000HZ =    1;
	public static int SAMPLING500HZ =     2;
	public static int SAMPLING250HZ =     4;
	public static int SAMPLING200HZ =     5;
	public static int SAMPLING166HZ =     6;
	public static int SAMPLING125HZ =     8;
	public static int SAMPLING100HZ =     10;
	public static int SAMPLING50HZ =      20;
	public static int SAMPLING10HZ =      100;
	public static int SAMPLING0HZOFF =    25;

	// packet types
    public static int DATAPACKET                  = 0X00;
    public static int INQUIRYCOMMAND              = 0X01;
    public static int INQUIRYRESPONSE             = 0X02;
    public static int GETSAMPLINGRATECOMMAND      = 0X03;
    public static int SAMPLINGRATERESPONSE        = 0X04;
    public static int SETSAMPLINGRATECOMMAND      = 0X05;
    public static int TOGGLELEDCOMMAND            = 0X06;
    public static int STARTSTREAMINGCOMMAND       = 0X07;
    public static int SETSENSORSCOMMAND           = 0X08;
    public static int SETACCELRANGECOMMAND        = 0X09;
    public static int ACCELRANGERESPONSE          = 0X0A;
    public static int GETACCELRANGECOMMAND        = 0X0B;
    public static int SET5VREGULATORCOMMAND       = 0X0C;
    public static int SETPOWERMUXCOMMAND          = 0X0D;
    public static int SETCONFIGSETUPBYTE0COMMAND  = 0X0E;
    public static int CONFIGSETUPBYTE0RESPONSE    = 0X0F;
    public static int GETCONFIGSETUPBYTE0COMMAND  = 0X10;
    public static int SETACCELCALIBRATIONCOMMAND  = 0X11;
    public static int ACCELCALIBRATIONRESPONSE    = 0X12;
    public static int GETACCELCALIBRATIONCOMMAND  = 0X13;
    public static int SETGYROCALIBRATIONCOMMAND   = 0X14;
    public static int GYROCALIBRATIONRESPONSE     = 0X15;
    public static int GETGYROCALIBRATIONCOMMAND   = 0X16;
    public static int SETMAGCALIBRATIONCOMMAND    = 0X17;
    public static int MAGCALIBRATIONRESPONSE      = 0X18;
    public static int GETMAGCALIBRATIONCOMMAND    = 0X19;
    public static int STOPSTREAMINGCOMMAND        = 0X20;
    public static int SETGSRRANGECOMMAND          = 0X21;
    public static int GSRRANGERESPONSE            = 0X22;
    public static int GETGSRRANGECOMMAND          = 0X23;
    public static int GETSHIMMERVERSIONCOMMAND    = 0X24;
    public static int SHIMMERVERSIONRESPONSE      = 0X25;
    public static int ACKCOMMANDPROCESSED         = 0XF;
	
	
	
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
		return line;
	}

	
	public String getLogDataLineHeader() {
		String line = "";							// Comment
		return line;
	}

}
