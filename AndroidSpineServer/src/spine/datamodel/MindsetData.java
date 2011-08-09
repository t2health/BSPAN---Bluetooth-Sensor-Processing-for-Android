package spine.datamodel;

import com.t2.Constants;
import com.t2.biomap.SharedPref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

	
	public int[] rawWaveData = new int[Constants.RAW_ACCUM_SIZE];
	
	/**
	 * Spectral band power data normalized to 0-100 by ratio to total power
	 */
	public int[] ratioSpectralData = new int[NUM_BANDS];
	public int[] scaledSpectralData = new int[NUM_BANDS];
	public int[] mScaleData = new int[MindsetData.NUM_BANDS];	
	
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
	protected SharedPreferences sharedPref;
	protected static Context context;

	public MindsetData(Context aContext) {
		context = aContext;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);   
		
        mScaleData = SharedPref.getIntValues(
				sharedPref, 
				"BandScales", 
				","
		);
        
        
		if (mScaleData == null) {
			mScaleData = new int[] {1,1,1,1,1,1,1,1};
		}		
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

	public void saveScaleData() {
		if (sharedPref != null && mScaleData != null )
			SharedPref.setIntValues(sharedPref, "BandScales", ",", mScaleData);    	
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

	/**
	 * @param feature Band of interest
	 * @return Spectral power for specified data normalized to 0-100 by ratio to HIGHEST value encountered
	 */
	public int getScaledFeature(int feature) {
		if (feature <= NUM_BANDS)
			return this.scaledSpectralData[feature];
		else
			return -1;
	}

	public String getSpectralName(int band) {
		return spectralNames[band];
	}
	
	public void updateSpectral(MindsetData d) {

		for (int i = 0; i < NUM_BANDS; i++)	{
			rawSpectralData[i] = d.rawSpectralData[i];
			if (rawSpectralData[i] > mScaleData[i]) {
				Log.i("SensorData", "New max for band " + MindsetData.spectralNames[i]  + " = " + rawSpectralData[i]);
				mScaleData[i] = rawSpectralData[i];
			}
			
			if (mScaleData[i] != 0) {
				double scale = (double) rawSpectralData[i] / (double) mScaleData[i];
				scaledSpectralData[i] = (int) (scale * 100);
			}
			else {
				scaledSpectralData[i] = (rawSpectralData[i]);
			}
			this.ratioSpectralData[i] = d.ratioSpectralData[i];

		}
	}
	
//	public void scaleSpectral(MindsetData d, int[] scaleData) {
//
//		for (int i = 0; i < NUM_BANDS; i++)	{
//			this.rawSpectralData[i] = d.rawSpectralData[i];
//			this.ratioSpectralData[i] = d.ratioSpectralData[i];
//		}
//	}
	
	public void updateRawWave(MindsetData d) {

		boolean scale = false;
		
		for (int i = 0; i < Constants.RAW_ACCUM_SIZE; i++)	{

			if (scale) {
				float v = (float) d.rawWaveData[i] + 2048;
				v = v / (float) 409.6;
				int vi = (int) v;
				
				this.rawWaveData[i] = vi;
			}
			else {
				this.rawWaveData[i] = d.rawWaveData[i];
				
			}
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
	
	// This one doesn't print out wave data
	public String getLogDataLine() {
		String line = "";							// Comment
		line += this.poorSignalStrength + ", "; 
		line += this.attention + ", "; 
		line += this.meditation + ", "; 		
		for (int i = 0; i < NUM_BANDS; i++)	{
			line += this.scaledSpectralData[i] + ", ";
//			line += this.ratioSpectralData[i] + ", ";
		}
		line += ", ";								// Visual seperator
		for (int i = 0; i < NUM_BANDS; i++)	{
			line += this.rawSpectralData[i] + ", ";
		}
		
		return line;
	}
	public String getLogDataLine(int execode, boolean saveRawWave) {
		String line = "";							// Comment
		line += this.poorSignalStrength + ", "; 
		line += this.attention + ", "; 
		line += this.meditation + ", "; 		
		for (int i = 0; i < NUM_BANDS; i++)	{
			line += this.scaledSpectralData[i] + ", ";
//			line += this.ratioSpectralData[i] + ", ";
		}
		line += ", ";								// Visual seperator
		for (int i = 0; i < NUM_BANDS; i++)	{
			line += this.rawSpectralData[i] + ", ";
		}

		if (saveRawWave) {
			if (execode == Constants.EXECODE_RAW_ACCUM) {
				for (int i = 0; i < Constants.RAW_ACCUM_SIZE; i++) {
					line += this.rawWaveData[i] + ";";
				}
			}
		}
		
		
		return line;
	}
	

}
