package com.t2.androidspineexample;


public class BioSensor {
	
	public static final int CONN_ERROR = -1;
	public static final int CONN_IDLE = 0;
	public static final int CONN_PAIRED = 1;
	public static final int CONN_CONNECTING = 2;
	public static final int CONN_CONNECTED = 3;
	
	public String mBTName;
	public String mBTAddress;
	public int mConnectionStatus;
	public Boolean mEnabled;

	/**
	 * A list of names of all of the parameters that this sensor can supply
	 */
	public String mParameterNames = "";
	
	public BioSensor(String btName, String btAddress, Boolean enabled) {
		this.mBTName = btName;
		this.mBTAddress = btAddress;
		this.mEnabled = enabled;
		
		if (btName.startsWith("BH")) {
			this.mParameterNames = "HeartRate, SkinTemp, RespRate";
		}
		if (btName.startsWith("RN42")) {
			this.mParameterNames = "HeartRate, EMG, GSR";
		}
		if (btName.startsWith("TestSensor")) {
			this.mParameterNames = "HeartRate, EMG, GSR, SkinTemp, RespRate";
		}
	}
	

}
