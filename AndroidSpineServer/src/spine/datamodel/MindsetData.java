package spine.datamodel;

import android.util.Log;


public class MindsetData  extends Data {
	private static final long serialVersionUID = 1L;	
	public static final int NUM_BANDS = 8;	
	
	public int poorSignalStrength;
	public int attention;
	public int meditation;
	public int blinkStrength;
	public int rawSignal;

	/**
	 * Spectral band power data
	 */
	public int[] rawSpectralData = new int[NUM_BANDS];
	
	
	/**
	 * Spectral band power data normalized to 0-100 by ratio to total power
	 */
	public int[] ratioSpectralData = new int[NUM_BANDS];
	
	public static final int DELTA_ID = 0;
	public static final int THETA_ID = 1;
	public static final int LOWALPHA_ID = 2;
	public static final int HIGHALPHA_ID = 3;
	public static final int LOWBETA_ID = 4;
	public static final int HIGHBETA_ID = 5;
	public static final int LOWGAMMA_ID = 6;
	public static final int MIDGAMMA_ID = 7;

	public static final String[] spectralNames = new String[] {"Delta", "Theta", "LowAlpha", "HighAlpha", "LowBeta", "HighBeta", "LowGamma", "MidGamma"};
	
	
	private Node node;
	public byte functionCode;	
	public byte sensorCode;
	public byte exeCode;
	
	public MindsetData() {
		
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

	public int getRawFeature(int feature) {
		if (feature <= NUM_BANDS)
			return this.rawSpectralData[feature];
		else
			return -1;
	}

	/**
	 * @param feature Band of interest
	 * @return Spectral power for specified data normalized to 0-100 by ratio to total power
	 */
	public int getRatioFeature(int feature) {
		if (feature <= NUM_BANDS)
			return this.ratioSpectralData[feature];
		else
			return -1;
	}

	public void updateSpectral(MindsetData d) {

		for (int i = 0; i < NUM_BANDS; i++)	{
			this.rawSpectralData[i] = d.rawSpectralData[i];
			this.ratioSpectralData[i] = d.ratioSpectralData[i];
		}
	}
	
	/**
	 * @param band	Band number to test
	 * @return -1 if more power to left of band, 1 if more power to right of band
	 */
	public int powerTest(int band)
	{
		int powerToLeft = 0;
		int powerToRight = 0;
		for (int i = 0; i < NUM_BANDS; i++)	{
			if (i < band) {
				powerToLeft+= this.ratioSpectralData[i];
			}
			else if (i > band) {
				powerToRight+= this.ratioSpectralData[i];
			}
		}

		return (powerToLeft >= powerToRight) ? -1:1;
	}
	
	public String getLogDataLine() {
		String line = "";							// Comment
		line += this.poorSignalStrength + ", "; 
		line += this.attention + ", "; 
		line += this.meditation + ", "; 		
		for (int i = 0; i < NUM_BANDS; i++)	{
			line += this.ratioSpectralData[i] + ", ";
		}
		line += ", ";								// Visual seperator
		for (int i = 0; i < NUM_BANDS; i++)	{
			line += this.rawSpectralData[i] + ", ";
		}
		return line;
	}
	

	public void logData(){
		Log.i("SensorData", ", " + getLogDataLine());
	}
}
