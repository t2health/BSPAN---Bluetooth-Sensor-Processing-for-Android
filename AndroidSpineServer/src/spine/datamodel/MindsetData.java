package spine.datamodel;

import android.util.Log;


public class MindsetData  extends Data {
	private static final long serialVersionUID = 1L;	
	
	public int poorSignalStrength;
	public int attention;
	public int meditation;
	public int blinkStrength;
	public int rawSignal;
	private byte[] spectralData;
	
	public int delta;
	public int theta;
	public int lowAlpha;
	public int highAlpha;
	public int lowBeta;
	public int highBeta;
	public int lowGamma;
	public int midGamma;

	
	
	public static final int DELTA_ID = 1;
	public static final int THETA_ID = 2;
	public static final int LOWALPHA_ID = 3;
	public static final int HIGHALPHA_ID = 4;
	public static final int LOWBETA_ID = 5;
	public static final int HIGHBETA_ID = 6;
	public static final int LOWGAMMA_ID = 7;
	public static final int MIDGAMMA_ID = 8;
	
	
	
	private Node node;
	public byte functionCode;	
	public byte sensorCode;
	public byte exeCode;
	
	public MindsetData()
	{
		
	}

	public MindsetData(	byte functionCode, byte sensorCode, byte exeCode, 
						byte poorSignalStrength, byte attention, 
						byte meditation, byte blinkStrength) {
		this.functionCode = functionCode;
		this.sensorCode = sensorCode;
		this.exeCode = exeCode;
		
		this.poorSignalStrength = poorSignalStrength;
		this.attention = attention;
		this.meditation = meditation;
		this.blinkStrength = blinkStrength;
	}	

	public int getFeature(long feature)
	{
		switch((int)feature)
		{
		case DELTA_ID:
			return this.delta;
		case THETA_ID:
			return this.theta;
		case LOWALPHA_ID:
			return this.lowAlpha;
		case HIGHALPHA_ID:
			return this.highAlpha;
		case LOWBETA_ID:
			return this.lowBeta;
		case HIGHBETA_ID:
			return this.highBeta;
		case LOWGAMMA_ID:
			return this.lowGamma;
		case MIDGAMMA_ID:
			return this.midGamma;
		}
		return 0;
	}
	
	public void update(MindsetData d)
	{
		this.delta = d.delta;
		this.theta = d.theta;
		this.lowAlpha = d.lowAlpha;
		this.highAlpha = d.highAlpha;
		this.lowBeta = d.lowBeta;
		this.highBeta = d.highBeta;
		this.lowGamma = d.lowGamma;
		this.midGamma = d.midGamma;
		this.attention = d.attention;
		this.meditation = d.meditation;
		this.poorSignalStrength = d.poorSignalStrength;
		this.blinkStrength = d.blinkStrength;
	}
	
	public void logData()
	{
		String line = "";
//		line += this.poorSignalStrength + "\t"; 
//		line += this.attention + "\t"; 
//		line += this.meditation + "\t"; 
//		line += this.theta + "\t"; 
//		line += this.lowAlpha + "\t"; 
//		line += this.highAlpha + "\t"; 
//		line += this.lowBeta+ "\t"; 
//		line += this.highBeta + "\t"; 
//		line += this.lowGamma + "\t"; 
//		line += this.midGamma ; 
		line += "delta= " + this.delta + ", "; 
		line += "theta= " + this.theta + ", "; 
		line += "lowAlpha= " + this.lowAlpha + ", "; 
		line += "highAlpha= " + this.highAlpha + ", "; 
		line += "lowBeta= " + this.lowBeta+ ", "; 
		line += "highBeta= " + this.highBeta + ", "; 
		line += "lowGamma= " + this.lowGamma + ", "; 
		line += "midGamma= " + this.midGamma + ", "; 
		Log.i("SensorData", line);
	}

}
