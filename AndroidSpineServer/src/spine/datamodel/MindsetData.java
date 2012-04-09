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

import com.t2.Constants;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


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
	public int mTotalSamples = 1;
	
	public static final int DELTA_ID = 0;
	public static final int THETA_ID = 1;
	public static final int LOWALPHA_ID = 2;
	public static final int HIGHALPHA_ID = 3;
	public static final int LOWBETA_ID = 4;
	public static final int HIGHBETA_ID = 5;
	public static final int LOWGAMMA_ID = 6;
	public static final int MIDGAMMA_ID = 7;
	public static final int E_ATTENTION_ID = 8;
	public static final int E_MEDITATION_ID = 9;

	public static final String[] spectralNames = new String[] {
			"Delta", "Theta", "LowAlpha", "HighAlpha", "LowBeta", "HighBeta", "LowGamma", "MidGamma",
			"(e)Attention", "(e)Meditation"};
	
	
	private Node node;
	public byte functionCode;	
	public byte sensorCode;
	public byte exeCode;
	protected SharedPreferences sharedPref;
	protected static Context context;
	long mTotalPower = 0;

	
	
	public MindsetData(Context aContext) {
		context = aContext;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);   
		
//        mScaleData = SharedPref.getIntValues(
//				sharedPref, 
//				"BandScales", 
//				","
//		);
        
        // For now at least let's not use the stored max. The problem is that
        // the max can very quite a bit from session to session based on
        // battery level, different headsets, etc.
        mScaleData = null;
        
//        mTotalSamples = SharedPref.getInt(context, "totalSamples", 1);
        
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
//		if (sharedPref != null && mScaleData != null )
//			SharedPref.setIntValues(sharedPref, "BandScales", ",", mScaleData);
//		
//		SharedPref.getInt(context, "totalSamples", mTotalSamples);
	}
	
	public int getRawFeature(int feature) {
		if (feature <= NUM_BANDS)
			return this.rawSpectralData[feature];
		else
			return -1;
	}

	/**
	 * @param feature Band of interest
	 * @return Spectral power for specified data normalized to 0-100 by xxx
	 */
	public int getFeatureValue(int feature) {
		if (feature < NUM_BANDS) {
			//return this.scaledSpectralData[feature];
			return this.ratioSpectralData[feature];
		}
		else {
			if (feature == E_ATTENTION_ID)
				return attention;
			else if (feature == E_MEDITATION_ID)
				return meditation;
			else
				return -1;
			
		}
	}

	public String getSpectralName(int band) {
		if (band <= E_MEDITATION_ID)
			return spectralNames[band];
		else
			return "error";
	}
	
	public void updateSpectral(MindsetData d) {

		mTotalSamples++;

//		this.attention = d.attention;
//		this.meditation = d.meditation;
//		this.poorSignalStrength = d.poorSignalStrength;
		
		mTotalPower = 0;
		// First get total of all bins (for ratio data)
		for (int i = 0; i < NUM_BANDS; i++)	{
			mTotalPower += d.rawSpectralData[i];
		}		
		
		for (int i = 0; i < NUM_BANDS; i++)	{
			rawSpectralData[i] = d.rawSpectralData[i];

			// Scale the data based on the total power
			if (mTotalPower > 0) {
				double scale = (double) rawSpectralData[i] / (double) mTotalPower;
				ratioSpectralData[i] = (int) (scale * 100);				
			}
			else {
				// Now scale based on total power
				ratioSpectralData[i] = 0;
			}
			
			// for new max of each band
			if (rawSpectralData[i] > mScaleData[i]) {
//				Log.i("SensorData", "New max for band " + MindsetData.spectralNames[i]  + " = " + rawSpectralData[i] + " - old= " + mScaleData[i]);
				mScaleData[i] = rawSpectralData[i];
			}
			
			// Now scale the data based on scale data
			if (mScaleData[i] != 0) {
				double scale = (double) rawSpectralData[i] / (double) mScaleData[i];
				scaledSpectralData[i] = (int) (scale * 100);
			}
			else {
				scaledSpectralData[i] = (rawSpectralData[i]);
			}
			

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
			line += this.getFeatureValue(i) + ", ";
		}

		
		line += ", ";								// Visual seperator
		for (int i = 0; i < NUM_BANDS; i++)	{
			line += this.rawSpectralData[i] + ", ";
		}
		line += "; " + mTotalPower;
		
		return line;
	}
	
	public String getLogDataLine(int execode, boolean saveRawWave) {
		String line = "";							// Comment
		line += this.poorSignalStrength + ", "; 
		line += this.attention + ", "; 
		line += this.meditation + ", "; 		
		for (int i = 0; i < NUM_BANDS; i++)	{
			line += getFeatureValue(i) + ", ";
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
	
	public String getLogDataLineHeader() {
		String line = "SignalStrength,";							// Comment
		line += "Attention, "; 
		line += "Meditation, "; 		
		for (int i = 0; i < NUM_BANDS; i++)	{
			line += spectralNames[i] + ", ";
		}
		line += ", ";								// Visual seperator
		for (int i = 0; i < NUM_BANDS; i++)	{
			line += spectralNames[i] + ", ";
		}
		return line;
	}
	

}
